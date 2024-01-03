package approval.form.coal;

import com.wyyt.scp.biz.api.vo.sys.FileRecordVo;
import com.wyyt.scp.biz.approval.ApprovalBusinessTypeEnum;
import com.wyyt.scp.biz.approval.dingtalk.annotation.DingTalkFormComponent;
import com.wyyt.scp.biz.approval.form.DingTalkApprovalFormEngine;
import lombok.Data;

import java.util.List;


/**
 * 立项项目审批表单
 *
 * @author: chenyu
 * @Date: 2023/08/23 09:46
 */
@Data
public class CoalProjectDingTalkApprovalForm implements DingTalkApprovalFormEngine {

    @DingTalkFormComponent("呈报部门")
    private String deptName;

    @DingTalkFormComponent("经办人")
    private String bizPerson;

    @DingTalkFormComponent("项目名称")
    private String projectName;

    @DingTalkFormComponent("客户名称")
    private String customerName;

    @DingTalkFormComponent("预计月供应量")
    private String estimateSupply;

    @DingTalkFormComponent("业务说明")
    private String businessDescription;

    @DingTalkFormComponent("附件")
    private List<FileRecordVo> fileList;

    @Override
    public ApprovalBusinessTypeEnum getBusinessApprovalTypeEnum() {
        return ApprovalBusinessTypeEnum.COAL_PROJECT;
    }
}
