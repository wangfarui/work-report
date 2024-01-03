package approval.form.demo;

import com.wyyt.scp.biz.api.vo.bas.oms.FileDto;
import com.wyyt.scp.biz.approval.ApprovalBusinessTypeEnum;
import com.wyyt.scp.biz.approval.dingtalk.annotation.DingTalkFormComponent;
import com.wyyt.scp.biz.approval.dingtalk.model.ComponentType;
import com.wyyt.scp.biz.approval.form.DingTalkApprovalFormEngine;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 示例审批表单
 *
 * @author wangfarui
 * @since 2023/8/2
 */
@Data
public class DemoDingTalkApprovalForm implements DingTalkApprovalFormEngine {

    @DingTalkFormComponent(value = "名称", required = true)
    private String projectName;

    @DingTalkFormComponent(value = "创建时间", pattern = "yyyy-MM-dd")
    private Date createTime;

    @DingTalkFormComponent("项目明细")
    private List<ProjectDetail> detailList;

    @DingTalkFormComponent("附件")
    private FileDto fileDto;

    @DingTalkFormComponent("附件2")
    private List<FileDto> fileDtoList;

    @DingTalkFormComponent(value = "关联项目", componentType = ComponentType.RelateField)
    private Long projectId;

    @DingTalkFormComponent(value = "关联合同", componentType = ComponentType.RelateField)
    private List<Long> contractIdList;

    @Override
    public ApprovalBusinessTypeEnum getBusinessApprovalTypeEnum() {
        return ApprovalBusinessTypeEnum.DEMO;
    }

    @Data
    public static class ProjectDetail {

        @DingTalkFormComponent("编号")
        private Long code;

        /**
         * 默认不做精度控制，可以使用String自己控制精度
         */
        @DingTalkFormComponent("吨位")
        private BigDecimal unit;

        @DingTalkFormComponent("金额")
        private BigDecimal money;

        @DingTalkFormComponent("创建日期")
        private Date creatTime;
    }
}
