package approval.form.steel;

import com.wyyt.scp.biz.api.vo.sys.FileRecordVo;
import com.wyyt.scp.biz.approval.ApprovalBusinessTypeEnum;
import com.wyyt.scp.biz.approval.dingtalk.annotation.DingTalkFormComponent;
import com.wyyt.scp.biz.approval.form.DingTalkApprovalFormEngine;
import lombok.Data;

import java.util.List;

/**
 * 钢材立项钉钉表单
 *
 * @author: chenyu
 * @Date: 2023/11/01 16:53
 */
@Data
public class SteelProjectDingTalkApprovalForm implements DingTalkApprovalFormEngine {

    @DingTalkFormComponent("呈报部门")
    private String deptName;

    @DingTalkFormComponent("经办人")
    private String bizPerson;

    @DingTalkFormComponent("项目名称")
    private String projectName;

    @DingTalkFormComponent("供应商名称")
    private String supplierName;

    @DingTalkFormComponent("业务说明")
    private String businessDescription;

    @DingTalkFormComponent("附件")
    private List<FileRecordVo> fileList;

    @Override
    public ApprovalBusinessTypeEnum getBusinessApprovalTypeEnum() {
        return ApprovalBusinessTypeEnum.STEEL_PROJECT;
    }
}
