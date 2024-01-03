package approval.form.coal;

import com.wyyt.scp.biz.api.vo.sys.FileRecordVo;
import com.wyyt.scp.biz.approval.ApprovalBusinessTypeEnum;
import com.wyyt.scp.biz.approval.dingtalk.annotation.DingTalkFormComponent;
import com.wyyt.scp.biz.approval.dingtalk.model.ComponentType;
import com.wyyt.scp.biz.approval.form.DingTalkApprovalFormEngine;
import lombok.Data;

import java.util.List;

@Data
public class CoalSaleContractFormDingTalk implements DingTalkApprovalFormEngine {

    @DingTalkFormComponent(value = "所在部门")
    private String deptName;

    @DingTalkFormComponent(value = "经办人")
    private String bizPerson;

    @DingTalkFormComponent(value = "关联审批单", componentType = ComponentType.RelateField)
    private List<Long> sourceContractIdList;

    @DingTalkFormComponent(value = "项目名称")
    private String projectName;

    @DingTalkFormComponent(value = "合同编号")
    private String contractNumber;

    @DingTalkFormComponent(value = "合同名称")
    private String contractName;

    @DingTalkFormComponent(value = "合同类型")
    private String contractType;

    @DingTalkFormComponent(value = "合同说明(新增)", componentType = ComponentType.TextareaField)
    private String remark;

    @DingTalkFormComponent(value = "执行状态")
    private String executeStatus;

    @DingTalkFormComponent(value = "附件")
    private List<FileRecordVo> fileList;

    @Override
    public ApprovalBusinessTypeEnum getBusinessApprovalTypeEnum() {
        return ApprovalBusinessTypeEnum.SALE_CONTRACT;
    }

}
