package approval.dingtalk.model;

import com.wyyt.scp.biz.approval.form.DingTalkApprovalFormEngine;
import com.wyyt.scp.biz.core.utils.UserUtils;
import lombok.Data;

/**
 * 钉钉审批表单流程实例对象
 *
 * @author wangfarui
 * @since 2022/11/11
 */
@Data
public class DingTalkApprovalFormInstance {

    /**
     * scm业务id
     * <p>非空</p>
     */
    private Long businessId;

    /**
     * 审批流程表单模板对象
     * <p>非空</p>
     */
    private DingTalkApprovalFormEngine approvalForm;

    /**
     * scm租户id
     * <p>非空字段，为空时默认从UserUtils获取</p>
     */
    private Long tenantId;

    /**
     * scm操作人id
     * <p>非空字段，为空时默认从UserUtils获取</p>
     */
    private Long userId;

    /**
     * 钉钉部门id
     * <br>
     * TODO 钉钉存在多部门时，需要指定。后期根据产品需求决定是手动指定还是默认指定
     */
    private Long departmentId;

    /**
     * 自身关联审批单
     */
    private boolean selfRelateField = true;

    public DingTalkApprovalFormInstance() {
    }

    public DingTalkApprovalFormInstance(Long businessId, DingTalkApprovalFormEngine approvalForm,
                                        Long tenantId, Long userId, Long departmentId) {
        this.businessId = businessId;
        this.approvalForm = approvalForm;
        this.tenantId = tenantId;
        this.userId = userId;
        this.departmentId = departmentId;
    }

    public Long getTenantId() {
        if (this.tenantId == null) {
            this.tenantId = UserUtils.getTenantId();
        }
        if (this.tenantId == null) {
            throw new IllegalArgumentException("tenantId 不能为空");
        }
        return this.tenantId;
    }

    public Long getUserId() {
        return getUserId(true);
    }

    public Long getUserId(boolean required) {
        if (this.userId == null) {
            this.userId = UserUtils.getUserId();
        }
        if (this.userId == null && required) {
            throw new IllegalArgumentException("userId 不能为空");
        }
        return this.userId;
    }

    public void checkParamValid() {
        if (this.businessId == null) {
            throw new IllegalArgumentException("业务id不能为空");
        }
        if (this.approvalForm == null) {
            throw new IllegalArgumentException("审批流程表单模板对象不能为空");
        }
        if (this.approvalForm.getBusinessApprovalTypeEnum() == null) {
            throw new IllegalArgumentException("业务审批类型不能为空");
        }
    }

    public static ApprovalFormInstanceBuilder builder() {
        return new ApprovalFormInstanceBuilder();
    }

    public static class ApprovalFormInstanceBuilder {

        private Long businessId;

        private DingTalkApprovalFormEngine approvalForm;

        private Long tenantId;

        private Long userId;

        private Long departmentId;

        public ApprovalFormInstanceBuilder businessId(Long businessId) {
            this.businessId = businessId;
            return this;
        }

        public ApprovalFormInstanceBuilder approvalForm(DingTalkApprovalFormEngine approvalForm) {
            this.approvalForm = approvalForm;
            return this;
        }

        public ApprovalFormInstanceBuilder tenantId(Long tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public ApprovalFormInstanceBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public ApprovalFormInstanceBuilder departmentId(Long departmentId) {
            this.departmentId = departmentId;
            return this;
        }

        public DingTalkApprovalFormInstance build() {
            return new DingTalkApprovalFormInstance(businessId, approvalForm, tenantId, userId, departmentId);
        }
    }
}
