package approval.form.settle;

import com.wyyt.scp.biz.approval.ApprovalBusinessTypeEnum;
import com.wyyt.scp.biz.approval.dingtalk.annotation.DingTalkFormComponent;
import com.wyyt.scp.biz.approval.dingtalk.model.ComponentType;
import com.wyyt.scp.biz.approval.form.DingTalkApprovalFormEngine;
import lombok.Data;

/**
 * @author xielu
 * @date 2023-09-11 14:53
 */
@Data
public class PaymentRequestDingTalkApprovalForm implements DingTalkApprovalFormEngine {

    @DingTalkFormComponent(value = "关联审批单", componentType = ComponentType.RelateField)
    private Long relateBiddingId;

    @DingTalkFormComponent("经办人")
    private String bizPerson;

    @DingTalkFormComponent("合同名称")
    private String contractName;

    @DingTalkFormComponent("合同编号")
    private String contractNo;

    @DingTalkFormComponent("业务类型")
    private String bizType="成本支出类";

    @DingTalkFormComponent("本合同累计付款")
    private String totalPayAmount;

    @DingTalkFormComponent("本次申请支付")
    private String applyAmount;

    @DingTalkFormComponent("付款方式")
    private String payType;

    @DingTalkFormComponent("要求付款日期")
    private String claimDate;

    @DingTalkFormComponent("收款单位")
    private String unitName;

    @DingTalkFormComponent("银行账号")
    private String unitAccount;

    @DingTalkFormComponent("开户行名称")
    private String unitBank;

    @DingTalkFormComponent("备注")
    private String remark;


    @Override
    public ApprovalBusinessTypeEnum getBusinessApprovalTypeEnum() {
        return ApprovalBusinessTypeEnum.PAYMENT_REQUEST;
    }
}
