package approval.form.settle;

import com.wyyt.scp.biz.api.vo.sys.FileRecordVo;
import com.wyyt.scp.biz.approval.ApprovalBusinessTypeEnum;
import com.wyyt.scp.biz.approval.dingtalk.annotation.DingTalkFormComponent;
import com.wyyt.scp.biz.approval.dingtalk.model.ComponentType;
import com.wyyt.scp.biz.approval.form.DingTalkApprovalFormEngine;
import lombok.Data;

import java.util.List;

/**
 * 保证金付款申请表单
 *
 * @author: chenyu
 * @Date: 2023/09/05 09:47
 */
@Data
public class DepositPaymentRequestDingTalkApprovalForm implements DingTalkApprovalFormEngine {

    @DingTalkFormComponent(value = "关联合同审批单", componentType = ComponentType.RelateField)
    private Long relateBiddingId;

    @DingTalkFormComponent("经办人")
    private String bizPerson;

    @DingTalkFormComponent("项目名称")
    private String projectName;

    @DingTalkFormComponent("保证金类型")
    private String depositType;

    @DingTalkFormComponent("业务类型")
    private String bizType="往来支出类";

    @DingTalkFormComponent("本次申请支付")
    private String applyAmount;

    @DingTalkFormComponent("付款方式")
    private String payType;

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
        return ApprovalBusinessTypeEnum.DEPOSIT_PAYMENT_REQUEST;
    }
}