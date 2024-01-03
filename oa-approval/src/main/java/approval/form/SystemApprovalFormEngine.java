package approval.form;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.wyyt.open.platform.client.module.wyys.flow.request.FlowInstanceCreateByNewRequest;
import com.wyyt.scp.biz.api.dto.AbstractFileDto;
import com.wyyt.scp.biz.approval.system.SystemFormComponent;
import com.wyyt.scp.biz.core.utils.FilePrefixUtil;
import com.wyyt.scp.biz.core.utils.InnerStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 审批流程表单模板对象
 *
 * @author wangfarui
 * @since 2022/11/11
 */
public interface SystemApprovalFormEngine {

    @SuppressWarnings("unchecked")
    default List<FlowInstanceCreateByNewRequest.FormControlValueRequest> buildFormControlValues() {
        Field[] fields = this.getClass().getDeclaredFields();
        List<FlowInstanceCreateByNewRequest.FormControlValueRequest> list = new ArrayList<>(fields.length);
        for (Field field : fields) {
            SystemFormComponent systemFormComponent = field.getAnnotation(SystemFormComponent.class);
            if (systemFormComponent == null) continue;
            FlowInstanceCreateByNewRequest.FormControlValueRequest request = new FlowInstanceCreateByNewRequest.FormControlValueRequest();
            request.setName(systemFormComponent.value());
            boolean required = systemFormComponent.required();
            try {
                field.setAccessible(true);
                Object o = field.get(this);
                if (o == null) {
                    request.setValue(required ? InnerStringUtils.NOT_EMPTY_STR : InnerStringUtils.EMPTY_STR);
                    continue;
                }
                // 表格
                if (o instanceof Collection) {
                    Collection<Object> o1 = (Collection<Object>) o;
                    if (!o1.isEmpty()) {
                        Object o2 = o1.stream().findAny().get();
                        // 行集合
                        List<Object> rowList = new ArrayList<>(o1.size());
                        if (o2 instanceof String) {
                            for (Object o3 : o1) {
                                FlowInstanceCreateByNewRequest.FormControlValueRequest formField = new FlowInstanceCreateByNewRequest.FormControlValueRequest();
                                formField.setName(StringUtils.isNotBlank(systemFormComponent.subName()) ? systemFormComponent.subName() : systemFormComponent.value());
                                formField.setValue(InnerStringUtils.formatObjectStr(o3, required));
                                rowList.add(Collections.singletonList(formField));
                            }
                            request.setValue(JSONUtil.toJsonStr(rowList));
                        }
                        else if (o2 instanceof AbstractFileDto) {
                            for (Object o3 : o1) {
                                AbstractFileDto o4 = (AbstractFileDto) o3;
                                Map<String, String> map = new HashMap<>();
                                map.put("name", o4.getFileName());
                                map.put("url", FilePrefixUtil.appendUrlPrefix(o4.getFileUrl()));
                                rowList.add(map);
                            }
                            request.setValue(JSONUtil.toJsonStr(rowList));
                        } else if (o2 instanceof Collection) {
                            throw new IllegalArgumentException("数据格式错误, 参考package-info.java文件描述");
                        } else {
                            for (Object o3 : o1) {
                                Field[] f1 = o3.getClass().getDeclaredFields();
                                List<FlowInstanceCreateByNewRequest.FormControlValueRequest> row = new ArrayList<>(f1.length);
                                for (Field f : f1) {
                                    SystemFormComponent a = f.getAnnotation(SystemFormComponent.class);
                                    if (a == null) continue;
                                    f.setAccessible(true);
                                    FlowInstanceCreateByNewRequest.FormControlValueRequest formField = new FlowInstanceCreateByNewRequest.FormControlValueRequest();
                                    formField.setName(a.value());
                                    Object o4 = f.get(o3);
                                    if (o4 instanceof Date) {
                                        o4 = DateUtil.format((Date) o4, a.pattern());
                                    }
                                    formField.setValue(InnerStringUtils.formatObjectStr(o4, a.required()));
                                    row.add(formField);
                                }
                                rowList.add(row);
                            }
                            request.setValue(JSONUtil.toJsonStr(rowList));
                        }
                    } else {
                        request.setValue(required ? InnerStringUtils.NOT_EMPTY_STR : InnerStringUtils.EMPTY_STR);
                    }
                }
                // 单行文本、多行文本、数值、金额
                else if (o instanceof String) {
                    request.setValue(InnerStringUtils.formatObjectStr(o, required));
                }
                // 日期
                else if (o instanceof Date) {
                    String format = DateUtil.format((Date) o, systemFormComponent.pattern());
                    request.setValue(InnerStringUtils.formatObjectStr(format, required));
                }
                // 单个图片、附件等其他
                else if (o instanceof AbstractFileDto) {
                    AbstractFileDto o4 = (AbstractFileDto) o;
                    Map<String, String> map = new HashMap<>();
                    map.put("name", o4.getFileName());
                    map.put("url", FilePrefixUtil.appendUrlPrefix(o4.getFileUrl()));
                    request.setValue(JSONUtil.toJsonStr(Collections.singletonList(map)));
                }
                // 其他
                else {
                    request.setValue(JSONUtil.toJsonStr(Collections.singletonList(o)));
                }
            } catch (Exception e) {
                throw new RuntimeException("构建审批流程表单模板对象失败", e);
            } finally {
                list.add(request);
            }
        }
        return list;
    }
}
