package approval.dingtalk.util;

import cn.hutool.crypto.digest.MD5;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.dingtalkworkflow_1_0.models.FormComponentProps;
import com.aliyun.dingtalkworkflow_1_0.models.StartProcessInstanceRequest;
import com.wyyt.scp.biz.api.dto.AbstractFileDto;
import com.wyyt.scp.biz.approval.dingtalk.DingTalkApprovalService;
import com.wyyt.scp.biz.approval.dingtalk.annotation.DingTalkFormComponent;
import com.wyyt.scp.biz.approval.dingtalk.client.DingTalkStorageClient;
import com.wyyt.scp.biz.approval.dingtalk.model.ComponentType;
import com.wyyt.scp.biz.approval.form.DingTalkApprovalFormEngine;
import com.wyyt.scp.biz.core.exception.BizException;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.aliyun.dingtalkstorage_1_0.models.CommitFileResponseBody.CommitFileResponseBodyDentry;
import static com.aliyun.dingtalkworkflow_1_0.models.StartProcessInstanceRequest.StartProcessInstanceRequestFormComponentValues;

/**
 * 钉钉表单工具
 *
 * @author wangfarui
 * @since 2023/8/3
 */
public abstract class DingTalkFormUtil {

    private static final MD5 MD5_HELPER = MD5.create();

    private static DingTalkApprovalService dingTalkApprovalService;

    /**
     * 生成表单控件
     */
    public static com.aliyun.dingtalkworkflow_1_0.models.FormComponent generateFormComponent(Field field) {
        DingTalkFormComponent formComponent = field.getAnnotation(DingTalkFormComponent.class);
        if (formComponent == null) return null;
        // 钉钉表单控件
        com.aliyun.dingtalkworkflow_1_0.models.FormComponent dingTalkFormComponent = new com.aliyun.dingtalkworkflow_1_0.models.FormComponent();
        FormComponentProps formComponentProps = new FormComponentProps()
                .setComponentId(determineComponentId(formComponent))
                .setLabel(formComponent.value())
                .setRequired(formComponent.required());
        // 自动识别类型
        if (ComponentType.AUTO.equals(formComponent.componentType())) {
            // 表格控件
            if (Collection.class.isAssignableFrom(field.getType())) {
                if (dealWithTableFieldComponent(field, dingTalkFormComponent, formComponentProps)) {
                    return null;
                }
            }
            // 附件控件
            else if (AbstractFileDto.class.isAssignableFrom(field.getType())) {
                dingTalkFormComponent.setComponentType(ComponentType.DDAttachment.name());
            }
            // 默认单行输入框控件
            else {
                dingTalkFormComponent.setComponentType(ComponentType.TextField.name());
            }
        }
        // 表格控件类型
        else if (ComponentType.TableField.equals(formComponent.componentType())) {
            if (dealWithTableFieldComponent(field, dingTalkFormComponent, formComponentProps)) {
                return null;
            }
        }
        // 指定表单控件类型
        else {
            dingTalkFormComponent.setComponentType(formComponent.componentType().name());
        }
        return dingTalkFormComponent.setProps(formComponentProps);
    }

    /**
     * 生成默认表单控件
     */
    public static List<com.aliyun.dingtalkworkflow_1_0.models.FormComponent> generateDefaultFormComponents() {
        return Collections.emptyList();
//        FormComponentProps formComponentProps = new FormComponentProps()
//                .setLabel(DingTalkCommonConstant.SELF_RELATE_FIELD_NAME)
//                .setRequired(false);
//        com.aliyun.dingtalkworkflow_1_0.models.FormComponent formComponent = new com.aliyun.dingtalkworkflow_1_0.models.FormComponent()
//                .setComponentType(ComponentType.RelateField.name())
//                .setProps(formComponentProps);
//        return Collections.singletonList(formComponent);
    }

    /**
     * 生成表单控件值
     */
    @SuppressWarnings("unchecked")
    public static StartProcessInstanceRequestFormComponentValues generateFormComponentValues(Field field, DingTalkApprovalFormEngine approvalForm) {
        DingTalkFormComponent dingTalkFormComponent = field.getAnnotation(DingTalkFormComponent.class);
        if (dingTalkFormComponent == null) return null;

        StartProcessInstanceRequestFormComponentValues formComponentValues = new StartProcessInstanceRequestFormComponentValues()
                .setId(determineComponentId(dingTalkFormComponent))
                .setName(dingTalkFormComponent.value())
                .setComponentType(determineComponentType(field).name());

        field.setAccessible(true);
        Object o;
        try {
            o = field.get(approvalForm);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("无法访问字段[" + field.getName() + "]", e);
        }
        if (o == null) {
            if (dingTalkFormComponent.required()) {
                throw illegalComponent(dingTalkFormComponent);
            }
            return null;
        }
        // 指定componentType的数据解析
        ComponentType componentType = dingTalkFormComponent.componentType();
        // 关联审批单
        if (componentType.equals(ComponentType.RelateField)) {
            List<Long> businessIdList;
            // 校验是否为指定的Long或List<Long>
            if (o instanceof List) {
                Class<?> genericsClass = getGenericsClass(field);
                if (genericsClass == null || !Long.class.isAssignableFrom(genericsClass)) {
                    throw new BizException("钉钉审批表单控件类型为[RelateField]时，仅支持Long和List<Long>类型字段!");
                }
                businessIdList = (List<Long>) o;
            } else if (o instanceof Long) {
                businessIdList = Collections.singletonList((Long) o);
            } else {
                throw new BizException("钉钉审批表单控件类型为[RelateField]时，仅支持Long和List<Long>类型字段!");
            }
            if (dingTalkApprovalService == null) {
                dingTalkApprovalService = SpringUtil.getBean(DingTalkApprovalService.class);
            }
            return dingTalkApprovalService.getApprovalRelatedForm(formComponentValues, businessIdList);
        }
        // 集合 setDetails
        if (o instanceof Collection) {
            Collection<Object> o1 = (Collection<Object>) o;
            if (o1.isEmpty()) {
                formComponentValues.setValue(JSON.toJSONString(Collections.emptyList()));
            } else {
                Object oType = o1.stream().findAny().get();
                boolean anyMatch = Arrays.stream(oType.getClass().getDeclaredFields()).anyMatch(t ->
                        t.isAnnotationPresent(DingTalkFormComponent.class)
                );
                // 集合中为非自定义表单控件对象, 例如List<AbstractFileDto>
                if (!anyMatch) {
                    if (oType instanceof AbstractFileDto) {
                        List<JSONObject> fileList = new ArrayList<>(o1.size());
                        for (Object o2 : o1) {
                            JSONObject attachmentJson = uploadFileDto((AbstractFileDto) o2);
                            fileList.add(attachmentJson);
                        }
                        formComponentValues.setValue(JSON.toJSONString(fileList));
                        return formComponentValues;
                    } else {
                        throw new IllegalArgumentException("不支持除List<AbstractFileDto>以外的集合对象");
                    }
                } else {
                    List<StartProcessInstanceRequest.StartProcessInstanceRequestFormComponentValuesDetails> detailsList = new ArrayList<>();
                    List<List<StartProcessInstanceRequest.StartProcessInstanceRequestFormComponentValuesDetails>> valueList = new ArrayList<>(o1.size());
                    for (Object o2 : o1) {
                        Field[] fields = o2.getClass().getDeclaredFields();
                        List<StartProcessInstanceRequest.StartProcessInstanceRequestFormComponentValuesDetails> details = new ArrayList<>(fields.length);
                        for (Field f : fields) {
                            DingTalkFormComponent fc = f.getAnnotation(DingTalkFormComponent.class);
                            if (fc == null) continue;
                            f.setAccessible(true);
                            Object o3;
                            try {
                                o3 = f.get(o2);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException("无法访问字段[" + f.getName() + "]", e);
                            }
                            StartProcessInstanceRequest.StartProcessInstanceRequestFormComponentValuesDetails formComponentValue = new StartProcessInstanceRequest.StartProcessInstanceRequestFormComponentValuesDetails()
                                    .setName(fc.value());
                            formComponentValue.setValue(formatObjectStr(o3, fc));
                            details.add(formComponentValue);
                            detailsList.add(formComponentValue);
                        }
                        valueList.add(details);
                    }
                    formComponentValues.setValue(JSON.toJSONString(valueList));
                    formComponentValues.setDetails(detailsList);
                }
            }
        }
        // 其他都是非集合 setValue
        else if (o instanceof Date) {
            formComponentValues.setValue(formatObjectStr(o, dingTalkFormComponent));
        } else if (o instanceof AbstractFileDto) {
            JSONObject attachmentJson = uploadFileDto((AbstractFileDto) o);
            formComponentValues.setValue(JSON.toJSONString(Collections.singletonList(attachmentJson)));
        } else {
            formComponentValues.setValue(formatObjectStr(o, dingTalkFormComponent));
        }

        return formComponentValues;
    }

    /**
     * 确定表单控件id
     */
    public static String determineComponentId(DingTalkFormComponent dingTalkFormComponent) {
        if (StringUtils.hasText(dingTalkFormComponent.id())) {
            return dingTalkFormComponent.id();
        }
        return MD5_HELPER.digestHex16(dingTalkFormComponent.value());
    }

    /**
     * 计算表单控件版本号
     */
    public static int computeComponentVersion(Field field) {
        DingTalkFormComponent dingTalkFormComponent = field.getAnnotation(DingTalkFormComponent.class);
        if (dingTalkFormComponent == null) {
            return 0;
        }
        String sb = dingTalkFormComponent.value() +
                dingTalkFormComponent.id() +
                dingTalkFormComponent.componentType().name() +
                dingTalkFormComponent.required() +
                dingTalkFormComponent.pattern() +
                field.getType().getName().hashCode();
        String hex16 = MD5_HELPER.digestHex16(sb);
        return hex16.hashCode();
    }

    private static JSONObject uploadFileDto(AbstractFileDto fileDto) {
        CommitFileResponseBodyDentry fileDentry = DingTalkStorageClient.uploadFileDto(fileDto);
        // 封装附件组件的值
        JSONObject attachmentJson = new JSONObject();
        attachmentJson.put("fileId", fileDentry.getId());
        attachmentJson.put("fileName", fileDentry.getName());
        attachmentJson.put("fileType", fileDentry.getExtension());
        attachmentJson.put("spaceId", fileDentry.getSpaceId());
        attachmentJson.put("fileSize", fileDentry.getSize());
        return attachmentJson;
    }

    private static ComponentType determineComponentType(Field field) {
        DingTalkFormComponent dingTalkFormComponent = field.getAnnotation(DingTalkFormComponent.class);
        if (!ComponentType.AUTO.equals(dingTalkFormComponent.componentType())) {
            return dingTalkFormComponent.componentType();
        }

        Class<?> type = field.getType();
        if (Collection.class.isAssignableFrom(type)) {
            Class<?> genericsClass = getGenericsClass(field);
            if (genericsClass != null && AbstractFileDto.class.isAssignableFrom(genericsClass)) {
                return ComponentType.DDAttachment;
            }
            return ComponentType.TableField;
        }
        if (AbstractFileDto.class.isAssignableFrom(type)) {
            return ComponentType.DDAttachment;
        }
        return ComponentType.TextField;

    }

    private static boolean dealWithTableFieldComponent(Field field,
                                                       com.aliyun.dingtalkworkflow_1_0.models.FormComponent dingTalkFormComponent,
                                                       FormComponentProps formComponentProps) {
        Class<?> genericsClass = getGenericsClass(field);
        if (genericsClass == null) {
            return true;
        }
        if (Collection.class.isAssignableFrom(genericsClass)) {
            throw new IllegalArgumentException("不支持多层集合");
        }
        // 附件集合，默认为附件组件类型
        if (AbstractFileDto.class.isAssignableFrom(genericsClass)) {
            dingTalkFormComponent.setComponentType(ComponentType.DDAttachment.name());
            return false;
        }
        Field[] genericsFields = genericsClass.getDeclaredFields();
        List<com.aliyun.dingtalkworkflow_1_0.models.FormComponent> genericsFormComponent = new ArrayList<>(genericsFields.length);
        for (Field genericsField : genericsFields) {
            com.aliyun.dingtalkworkflow_1_0.models.FormComponent component = generateFormComponent(genericsField);
            if (component != null) {
                verifyTableComponentType(component);
                genericsFormComponent.add(component);
            }
        }
        if (genericsFormComponent.isEmpty()) {
            return true;
        }
        dingTalkFormComponent.setComponentType(ComponentType.TableField.name());
        dingTalkFormComponent.setChildren(genericsFormComponent);
        formComponentProps.setTableViewMode("table");

        return false;
    }

    private static void verifyTableComponentType(com.aliyun.dingtalkworkflow_1_0.models.FormComponent formComponent) {
        String componentType = formComponent.getComponentType();
        if (!componentType.equalsIgnoreCase(ComponentType.TextField.name())
                && !componentType.equalsIgnoreCase(ComponentType.TextareaField.name())) {
            throw new IllegalArgumentException("明细控件目前只支持: 单行输入框、多行输入框。");
        }
    }

    private static Class<?> getGenericsClass(Field field) {
        field.setAccessible(true);
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) genericType;
            return (Class<?>) type.getActualTypeArguments()[0];
        }
        return null;
    }

    /**
     * 格式化对象 转为字符串
     *
     * @param o             对象
     * @param dingTalkFormComponent 表单控件
     * @return 字符串
     */
    private static String formatObjectStr(Object o, DingTalkFormComponent dingTalkFormComponent) {
        if (o == null) {
            if (dingTalkFormComponent.required()) {
                throw illegalComponent(dingTalkFormComponent);
            }
            return "";
        }

        if (o instanceof Date) {
            SimpleDateFormat sdf = new SimpleDateFormat(dingTalkFormComponent.pattern());
            return sdf.format(o);
        }

        String s = o.toString();
        if (!StringUtils.hasText(s)) {
            if (dingTalkFormComponent.required()) {
                throw illegalComponent(dingTalkFormComponent);
            }
            return "";
        }
        return s;
    }

    private static IllegalArgumentException illegalComponent(DingTalkFormComponent dingTalkFormComponent) {
        return new IllegalArgumentException(String.format("表单控件[%s]为必填项，不能为空", dingTalkFormComponent.value()));
    }

}
