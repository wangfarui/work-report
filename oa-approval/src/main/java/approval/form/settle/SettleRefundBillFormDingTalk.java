package approval.form.settle;

import com.wyyt.scp.biz.approval.ApprovalBusinessTypeEnum;
import com.wyyt.scp.biz.approval.dingtalk.annotation.DingTalkFormComponent;
import com.wyyt.scp.biz.approval.form.DingTalkApprovalFormEngine;
import lombok.Data;

/**
 * @Author: 佳辉
 * @Description: $
 * @Date 2023-11-24 16:33
 */
@Data
public class SettleRefundBillFormDingTalk implements DingTalkApprovalFormEngine {


    @DingTalkFormComponent("所在部门")
    private String deptName;

    @DingTalkFormComponent("经办人")
    private String bizPerson;

    @DingTalkFormComponent("退款金额")
    private String applyAmount;

    @DingTalkFormComponent("收款单位")
    private String unitName;

    @DingTalkFormComponent("银行账号")
    private String payAccount;


    @DingTalkFormComponent("开户行名称")
    private String payBankText;

    @DingTalkFormComponent("退款说明")
    private String refundExplain;


    @Override
    public ApprovalBusinessTypeEnum getBusinessApprovalTypeEnum() {
        return ApprovalBusinessTypeEnum.SETTLE_REFUND_BILL;
    }
}
