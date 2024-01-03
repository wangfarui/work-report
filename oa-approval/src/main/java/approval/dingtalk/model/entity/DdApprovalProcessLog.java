package approval.dingtalk.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 钉钉审批流程日志表
 *
 * @author wangfarui
 * @since 2023/8/28
 */
@Data
public class DdApprovalProcessLog {

    @ApiModelProperty("主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("租户id")
    private Long tenantId;

    @ApiModelProperty("审批业务关联id(dd_approval_business_rel)")
    private Long businessRelId;

    @ApiModelProperty("事件id")
    private String eventId;

    @ApiModelProperty("任务id")
    private Long taskId;

    @ApiModelProperty("流程类型 ApprovalProcessType")
    private Integer processType;

    @ApiModelProperty("操作人id")
    private String staffId;

    @ApiModelProperty("操作人名称")
    private String staffName;

    @ApiModelProperty("流程内容")
    private String processContent;

    @ApiModelProperty("创建时间（审批时间）")
    private Date createTime;
}
