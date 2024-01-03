package approval.form.logistics;

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
public class LogisticsFrameContractFormDingTalk implements DingTalkApprovalFormEngine {

    @DingTalkFormComponent(value = "所在部门")
    private String deptName;

    @DingTalkFormComponent(value = "经办人")
    private String createName;

    @DingTalkFormComponent(value = "合同编号")
    private String logisticsBusinessNumber;

    @DingTalkFormComponent(value = "合同名称")
    private String logisticsContractName;

    @DingTalkFormComponent(value = "合同说明(新增)")
    private String remark;

    @DingTalkFormComponent(value = "执行状态")
    private String executeStatus = "拟执行";

    @DingTalkFormComponent(value = "附件")
    private List<FileRecordVo> fileList;


    @Override
    public ApprovalBusinessTypeEnum getBusinessApprovalTypeEnum() {
        return ApprovalBusinessTypeEnum.COAL_LOGISTICS_FRAME_CONTRACT;
    }
}
