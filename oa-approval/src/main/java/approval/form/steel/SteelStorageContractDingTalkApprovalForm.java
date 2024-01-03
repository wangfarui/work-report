package approval.form.steel;

import com.wyyt.scp.biz.api.vo.sys.FileRecordVo;
import com.wyyt.scp.biz.approval.ApprovalBusinessTypeEnum;
import com.wyyt.scp.biz.approval.dingtalk.annotation.DingTalkFormComponent;
import com.wyyt.scp.biz.approval.form.DingTalkApprovalFormEngine;
import lombok.Data;

import java.util.List;

/**
 * 钢材仓储合同钉钉表单
 *
 * @author: chenyu
 * @Date: 2023/11/01 16:56
 */
@Data
public class SteelStorageContractDingTalkApprovalForm implements DingTalkApprovalFormEngine {

    @DingTalkFormComponent("所在部门")
    private String deptName;

    @DingTalkFormComponent("经办人")
    private String bizPerson;

    @DingTalkFormComponent("合同编号")
    private String contractNumber;

    @DingTalkFormComponent("合同名称")
    private String contractName;

    @DingTalkFormComponent("仓储合作方")
    private String storageName;

    @DingTalkFormComponent("附件")
    private List<FileRecordVo> fileList;

    @Override
    public ApprovalBusinessTypeEnum getBusinessApprovalTypeEnum() {
        return ApprovalBusinessTypeEnum.STEEL_STORAGE_CONTRACT;
    }
}
