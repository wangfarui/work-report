package approval.form.steel;

import com.wyyt.scp.biz.api.vo.sys.FileRecordVo;
import com.wyyt.scp.biz.approval.ApprovalBusinessTypeEnum;
import com.wyyt.scp.biz.approval.dingtalk.annotation.DingTalkFormComponent;
import com.wyyt.scp.biz.approval.form.DingTalkApprovalFormEngine;
import lombok.Data;

import java.util.List;

/**
 * 钢材补充协议 审批表单
 *
 * @author wangfarui
 * @since 2023/11/2
 */
@Data
public class SteelAgreementDingTalkApprovalForm implements DingTalkApprovalFormEngine {

    @DingTalkFormComponent("呈报部门")
    private String deptName;

    @DingTalkFormComponent("经办人")
    private String bizPerson;

    @DingTalkFormComponent("项目名称")
    private String projectName;

    @DingTalkFormComponent("协议编号")
    private String agreementNumber;

    @DingTalkFormComponent("协议名称")
    private String agreementName;

    @DingTalkFormComponent(value = "合同类型")
    private String contractType="补充协议";

    @DingTalkFormComponent("协议说明")
    private String remark;

    @DingTalkFormComponent(value = "执行状态")
    private String executeStatus="拟执行";

    @DingTalkFormComponent(value = "附件")
    private List<FileRecordVo> fileList;

    @Override
    public ApprovalBusinessTypeEnum getBusinessApprovalTypeEnum() {
        return ApprovalBusinessTypeEnum.STEEL_AGREEMENT;
    }
}
