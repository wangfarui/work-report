package approval.form.settle;

import com.wyyt.scp.biz.api.vo.sys.FileRecordVo;
import com.wyyt.scp.biz.approval.ApprovalBusinessTypeEnum;
import com.wyyt.scp.biz.approval.dingtalk.annotation.DingTalkFormComponent;
import com.wyyt.scp.biz.approval.dingtalk.model.ComponentType;
import com.wyyt.scp.biz.approval.form.DingTalkApprovalFormEngine;
import lombok.Data;

import java.util.List;

/**
 * 预付款申请单钉钉表单
 *
 * @author: chenyu
 * @Date: 2023/09/12 15:40
 */
@Data
public class AdvancePaymentDingTalkApprovalForm implements DingTalkApprovalFormEngine {

    @DingTalkFormComponent(value = "关联合同审批单", componentType = ComponentType.RelateField)
    private Long relateContractId;

    @DingTalkFormComponent("经办人")
    private String bizPerson;

    @DingTalkFormComponent("项目名称")
    private String projectName;

    @DingTalkFormComponent("合同名称")
    private String contractName;

    @DingTalkFormComponent("业务名称")
    private String businessName = "成本支出类";

    @DingTalkFormComponent("支出金额")
    private String amount;

    @DingTalkFormComponent("收款单位")
    private String unitName;

    @DingTalkFormComponent("银行账号")
    private String unitAccount;

    @DingTalkFormComponent("开户行名称")
    private String unitBank;

    @DingTalkFormComponent("备注")
    private String remark;

    @DingTalkFormComponent(value = "附件")
    private List<FileRecordVo> fileList;

    @Override
    public ApprovalBusinessTypeEnum getBusinessApprovalTypeEnum() {
        return ApprovalBusinessTypeEnum.ADVANCE_PAYMENT;
    }
}
