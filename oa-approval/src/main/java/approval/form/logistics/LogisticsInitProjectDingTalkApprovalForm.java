package approval.form.logistics;

import com.wyyt.scp.biz.api.vo.sys.FileRecordVo;
import com.wyyt.scp.biz.approval.ApprovalBusinessTypeEnum;
import com.wyyt.scp.biz.approval.dingtalk.annotation.DingTalkFormComponent;
import com.wyyt.scp.biz.approval.form.DingTalkApprovalFormEngine;
import lombok.Data;

import java.util.List;

/**
 * 合同物流-立项审批钉钉表单
 *
 * @author: chenyu
 * @Date: 2023/09/26 09:54
 */
@Data
public class LogisticsInitProjectDingTalkApprovalForm implements DingTalkApprovalFormEngine {

    @DingTalkFormComponent("呈报部门")
    private String deptName;

    @DingTalkFormComponent("经办人")
    private String bizPerson;

    @DingTalkFormComponent("项目名称")
    private String projectName;

    @DingTalkFormComponent("客户名称")
    private String customerName;

    @DingTalkFormComponent("业务说明")
    private String businessDescription;

    @DingTalkFormComponent("附件")
    private List<FileRecordVo> fileList;

    @Override
    public ApprovalBusinessTypeEnum getBusinessApprovalTypeEnum() {
        return ApprovalBusinessTypeEnum.LOGISTICS_INIT_PROJECT;
    }
}
