package approval.form.coal;

import com.wyyt.scp.biz.api.vo.sys.FileRecordVo;
import com.wyyt.scp.biz.approval.ApprovalBusinessTypeEnum;
import com.wyyt.scp.biz.approval.dingtalk.annotation.DingTalkFormComponent;
import com.wyyt.scp.biz.approval.dingtalk.model.ComponentType;
import com.wyyt.scp.biz.approval.form.DingTalkApprovalFormEngine;
import lombok.Data;

import java.util.List;

@Data
public class CoalInviteTenderFormDingTalk implements DingTalkApprovalFormEngine {

    @DingTalkFormComponent(value = "呈报部门")
    private String deptName;

    @DingTalkFormComponent(value = "经办人")
    private String createName;

    @DingTalkFormComponent(value = "关联项目审批单",componentType = ComponentType.RelateField)
    private Long projectId ;

    @DingTalkFormComponent(value = "项目名称")
    private String projectName;

    @DingTalkFormComponent(value = "招标对象")
    private String inviteTenderTypeName;

    @DingTalkFormComponent(value = "招标方案说明")
    private String planDescription;

    @DingTalkFormComponent("附件")
    private List<FileRecordVo> fileList;


    @Override
    public ApprovalBusinessTypeEnum getBusinessApprovalTypeEnum() {
        return ApprovalBusinessTypeEnum.INVITETENDER;
    }

}
