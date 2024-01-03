package approval.form.coal;

import com.wyyt.scp.biz.api.vo.sys.FileRecordVo;
import com.wyyt.scp.biz.approval.ApprovalBusinessTypeEnum;
import com.wyyt.scp.biz.approval.dingtalk.annotation.DingTalkFormComponent;
import com.wyyt.scp.biz.approval.dingtalk.model.ComponentType;
import com.wyyt.scp.biz.approval.form.DingTalkApprovalFormEngine;
import lombok.Data;

import java.util.List;

/**
 * @author xielu
 * @date 2023-08-22 11:19
 */
@Data
public class CoalResultReportInfoFormDingTalk implements DingTalkApprovalFormEngine {
    @DingTalkFormComponent("呈报部门")
    private String deptName;


    @DingTalkFormComponent("经办人")
    private String bizPerson;

    @DingTalkFormComponent(value = "关联招标审批单", componentType = ComponentType.RelateField)
    private Long inviteTenderId ;

    @DingTalkFormComponent("项目名称")
    private String projectName;

    @DingTalkFormComponent("招标对象")
    private String inviteTenderType;

    @DingTalkFormComponent("中标企业")
    private String winningBidderCompanyName;

    @DingTalkFormComponent(value = "招标推荐说明")
    private String explanation;

    @DingTalkFormComponent("附件")
    private List<FileRecordVo> fileList;

    @Override
    public ApprovalBusinessTypeEnum getBusinessApprovalTypeEnum() {
        return ApprovalBusinessTypeEnum.RESULT_REPORT;
    }
}
