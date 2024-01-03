package approval.form.coal;

import com.wyyt.scp.biz.api.vo.sys.FileRecordVo;
import com.wyyt.scp.biz.approval.ApprovalBusinessTypeEnum;
import com.wyyt.scp.biz.approval.dingtalk.annotation.DingTalkFormComponent;
import com.wyyt.scp.biz.approval.dingtalk.model.ComponentType;
import com.wyyt.scp.biz.approval.form.DingTalkApprovalFormEngine;
import lombok.Data;

import java.util.List;

/**
 * @Author: 佳辉
 * @Description: $
 * @Date 2023-08-17 14:37
 */
@Data
public class CoalTenderFormDingTalk implements DingTalkApprovalFormEngine {


    @DingTalkFormComponent(value = "呈报部门")
    private String deptName;

    @DingTalkFormComponent(value = "经办人")
    private String createName;

    @DingTalkFormComponent(value = "关联项目审批单",componentType = ComponentType.RelateField)
    private Long projectId ;

    @DingTalkFormComponent(value = "项目名称")
    private String projectName;

    @DingTalkFormComponent(value = "客户名称")
    private String customerName;

    @DingTalkFormComponent(value = "投标保证金")
    private String isMargin;

    @DingTalkFormComponent(value = "项目投标概况")
    private String tenderRemake;

    @DingTalkFormComponent("附件")
    private List<FileRecordVo> fileList;

    @Override
    public ApprovalBusinessTypeEnum getBusinessApprovalTypeEnum() {
        return ApprovalBusinessTypeEnum.TENDER;
    }
}
