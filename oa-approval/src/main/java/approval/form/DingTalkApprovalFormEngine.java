package approval.form;

import cn.hutool.crypto.digest.MD5;
import com.aliyun.dingtalkworkflow_1_0.models.FormComponent;
import com.wyyt.scp.biz.approval.ApprovalBusinessTypeEnum;
import com.wyyt.scp.biz.approval.dingtalk.config.DingTalkApprovalConfig;
import com.wyyt.scp.biz.approval.dingtalk.util.DingTalkFormUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.aliyun.dingtalkworkflow_1_0.models.StartProcessInstanceRequest.StartProcessInstanceRequestFormComponentValues;

/**
 * 审批表单Engine
 *
 * @author wangfarui
 * @since 2022/11/11
 */
public interface DingTalkApprovalFormEngine {

    /**
     * 获取业务审批类型枚举
     */
    ApprovalBusinessTypeEnum getBusinessApprovalTypeEnum();

    /**
     * 表单版本
     */
    default String version() {
        return DingTalkApprovalConfig.getFormVersion(getBusinessApprovalTypeEnum(), () -> {
            Field[] fields = this.getClass().getDeclaredFields();
            long versionSum = 0L;
            for (Field field : fields) {
                int version = DingTalkFormUtil.computeComponentVersion(field);
                versionSum += version;
            }
            return MD5.create().digestHex16(String.valueOf(versionSum));
        });
    }

    default List<FormComponent> buildFormComponent() {
        Field[] fields = this.getClass().getDeclaredFields();
        List<FormComponent> defaultFormComponents = DingTalkFormUtil.generateDefaultFormComponents();
        List<FormComponent> list = new ArrayList<>(fields.length + defaultFormComponents.size());
        for (Field field : fields) {
            FormComponent formComponent = DingTalkFormUtil.generateFormComponent(field);
            if (formComponent != null) {
                list.add(formComponent);
            }
        }
        // 默认表单控件置于最后
        if (!defaultFormComponents.isEmpty()) {
            list.addAll(defaultFormComponents);
        }
        if (list.isEmpty()) {
            throw new IllegalStateException("表单组件的控件元素不能为空");
        }
        return list;
    }

    default List<StartProcessInstanceRequestFormComponentValues> buildFormComponentValues() {
        Field[] fields = this.getClass().getDeclaredFields();
        List<StartProcessInstanceRequestFormComponentValues> list = new ArrayList<>(fields.length);
        for (Field field : fields) {
            StartProcessInstanceRequestFormComponentValues formComponentValues = DingTalkFormUtil.generateFormComponentValues(field, this);
            if (formComponentValues != null) {
                list.add(formComponentValues);
            }
        }
        return list;
    }
}
