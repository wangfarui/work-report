package approval.system;

import com.wyyt.scp.biz.approval.ApprovalBusinessTypeEnum;
import com.wyyt.scp.biz.approval.form.SystemApprovalFormEngine;
import com.wyyt.scp.biz.core.utils.UserUtils;
import lombok.Data;

import java.util.Date;

/**
 * 系统审批表单流程实例对象
 *
 * @author wangfarui
 * @since 2022/11/11
 */
@Data
public class SystemApprovalFormInstance {

    /**
     * scm业务id
     * <p>非空</p>
     */
    private Long businessId;

    /**
     * 审批业务类型
     * <p>非空</p>
     */
    private ApprovalBusinessTypeEnum businessTypeEnum;

    /**
     * 审批流程表单模板对象
     * <p>非空</p>
     */
    private SystemApprovalFormEngine approvalForm;

    /**
     * 业务发起日期
     */
    private Date businessDate;

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
     * 部门id
     * <p>存在多部门时，必传</p>
     */
    private Long departmentId;

    public SystemApprovalFormInstance() {
    }

    public SystemApprovalFormInstance(Long businessId, ApprovalBusinessTypeEnum businessTypeEnum,
                                      SystemApprovalFormEngine approvalForm, Date businessDate,
                                      Long tenantId, Long userId, Long departmentId) {
        this.businessId = businessId;
        this.businessTypeEnum = businessTypeEnum;
        this.approvalForm = approvalForm;
        this.businessDate = businessDate;
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
        if (this.businessTypeEnum == null) {
            throw new IllegalArgumentException("审批业务类型不能为空");
        }
    }

    public static SystemApprovalFormInstanceBuilder builder() {
        return new SystemApprovalFormInstanceBuilder();
    }

    public static class SystemApprovalFormInstanceBuilder {

        private Long businessId;

        private ApprovalBusinessTypeEnum businessTypeEnum;

        private SystemApprovalFormEngine approvalForm;

        private Date businessDate;

        private Long tenantId;

        private Long userId;

        private Long departmentId;

        public SystemApprovalFormInstanceBuilder businessId(Long businessId) {
            this.businessId = businessId;
            return this;
        }

        public SystemApprovalFormInstanceBuilder businessTypeEnum(ApprovalBusinessTypeEnum businessTypeEnum) {
            this.businessTypeEnum = businessTypeEnum;
            return this;
        }

        public SystemApprovalFormInstanceBuilder approvalForm(SystemApprovalFormEngine approvalForm) {
            this.approvalForm = approvalForm;
            return this;
        }

        public SystemApprovalFormInstanceBuilder businessDate(Date businessDate) {
            this.businessDate = businessDate;
            return this;
        }

        public SystemApprovalFormInstanceBuilder tenantId(Long tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public SystemApprovalFormInstanceBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public SystemApprovalFormInstanceBuilder departmentId(Long departmentId) {
            this.departmentId = departmentId;
            return this;
        }

        public SystemApprovalFormInstance build() {
            return new SystemApprovalFormInstance(businessId, businessTypeEnum, approvalForm, businessDate, tenantId, userId, departmentId);
        }
    }
}
