package approval.form.logistics;

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
public class LogisticsResultReportInfoFormDingTalk implements DingTalkApprovalFormEngine {
    @DingTalkFormComponent("呈报部门")
    private String deptName;


    @DingTalkFormComponent("经办人")
    private String bizPerson;

    @DingTalkFormComponent(value = "关联招标审批单", componentType = ComponentType.RelateField)
    private Long inviteTenderId ;

    @DingTalkFormComponent("项目名称")
    private String projectName;

    @DingTalkFormComponent("类型 1物流合作方 2供应商")
    private String inviteTenderType;


    @DingTalkFormComponent("运输品类")
    private String transportCategory;

    @DingTalkFormComponent("预计运输量")
    private String expectTransportMeasure;

    @DingTalkFormComponent("运输方式")
    private String transportType;


    @DingTalkFormComponent("中标企业名称")
    private String winningBidderCompanyName;

    @DingTalkFormComponent("说明")
    private String explanation;

    @DingTalkFormComponent("附件列表")
    private List<FileRecordVo> fileList;

    @Override
    public ApprovalBusinessTypeEnum getBusinessApprovalTypeEnum() {
        return ApprovalBusinessTypeEnum.LOGISTICS_RESULT_REPORT_MANAGEMENT;
    }
}
