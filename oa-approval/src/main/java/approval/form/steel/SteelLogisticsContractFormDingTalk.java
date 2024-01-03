package approval.form.steel;

import com.wyyt.scp.biz.api.vo.sys.FileRecordVo;
import com.wyyt.scp.biz.approval.ApprovalBusinessTypeEnum;
import com.wyyt.scp.biz.approval.dingtalk.annotation.DingTalkFormComponent;
import com.wyyt.scp.biz.approval.form.DingTalkApprovalFormEngine;
import lombok.Data;

import java.util.List;

/**
 * @Author: 佳辉
 * @Description: $
 * @Date 2023-08-21 11:27
 */
@Data
public class SteelLogisticsContractFormDingTalk implements DingTalkApprovalFormEngine {

    @DingTalkFormComponent(value = "所在部门")
    private String deptName;

    @DingTalkFormComponent(value = "经办人")
    private String bizPerson;

    @DingTalkFormComponent(value = "项目名称")
    private String projectName;

    @DingTalkFormComponent(value = "合同编号")
    private String logisticsBusinessNumber;

    @DingTalkFormComponent(value = "合同名称")
    private String logisticsContractName;

    @DingTalkFormComponent(value = "合同类型")
    private String contractType = "物流合同";

    @DingTalkFormComponent(value = "合同说明")
    private String remark;

    @DingTalkFormComponent(value = "执行状态")
    private String executeStatus = "拟执行";

    @DingTalkFormComponent(value = "附件")
    private List<FileRecordVo> fileList;


    @Override
    public ApprovalBusinessTypeEnum getBusinessApprovalTypeEnum() {
        return ApprovalBusinessTypeEnum.STEEL_LOGISTICS_CONTRACT;
    }
}
