package approval.form.logistics;

import com.wyyt.scp.biz.api.vo.sys.FileRecordVo;
import com.wyyt.scp.biz.approval.ApprovalBusinessTypeEnum;
import com.wyyt.scp.biz.approval.dingtalk.annotation.DingTalkFormComponent;
import com.wyyt.scp.biz.approval.form.DingTalkApprovalFormEngine;
import lombok.Data;

import java.util.List;

/**
 * @author xielu
 * @date 2023-10-08 14:55
 */
@Data
public class LogisticsDownContractFormDingTalk implements DingTalkApprovalFormEngine {
    @DingTalkFormComponent(value = "所在部门")
    private String deptName;

    @DingTalkFormComponent(value = "经办人")
    private String createName;

    @DingTalkFormComponent(value = "项目名称")
    private String projectName;

    @DingTalkFormComponent(value = "合同编号")
    private String contractNumber;

    @DingTalkFormComponent(value = "合同名称")
    private String contractName;

    @DingTalkFormComponent(value = "合同类型")
    private String contractType = "托运合同";

    @DingTalkFormComponent(value = "运输品类")
    private String transportCategory;

    @DingTalkFormComponent(value = "合同说明(新增)")
    private String remark;

    @DingTalkFormComponent(value = "执行状态")
    private String executeStatus = "拟执行";

    @DingTalkFormComponent(value = "附件")
    private List<FileRecordVo> fileList;

    @Override
    public ApprovalBusinessTypeEnum getBusinessApprovalTypeEnum() {
        return ApprovalBusinessTypeEnum.LOGISTICS_DOWN_CONTRACT;
    }
}
