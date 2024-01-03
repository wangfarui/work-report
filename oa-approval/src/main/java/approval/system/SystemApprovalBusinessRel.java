package approval.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wyyt.scp.biz.approval.ApprovalBusinessTypeEnum;
import com.wyyt.scp.biz.approval.ApprovalInstanceStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 系统审批业务关联表
 *
 * @author wangfarui
 * @since 2023/12/12
 */
@Data
@TableName("system_approval_business_rel")
@ApiModel(value = "SystemApprovalBusinessRel对象", description = "系统审批业务关联表")
public class SystemApprovalBusinessRel {

    @ApiModelProperty("主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("租户id")
    private Long tenantId;

    @ApiModelProperty("业务表id")
    private Long businessId;

    /**
     * @see ApprovalBusinessTypeEnum#getCode()
     */
    @ApiModelProperty("scm表单类型code")
    private Integer typeCode;

    @ApiModelProperty("scm表单类型名称")
    private String typeName;

    @ApiModelProperty("wyys模板编号")
    private String templateCode;

    @ApiModelProperty("流程实例id（物易云商创建的审批流程id）")
    private Long flowId;

    @ApiModelProperty("流程实例编号（物易云商创建的审批流程编号）")
    private String flowSn;

    /**
     * @see ApprovalInstanceStatusEnum#getCode()
     */
    @ApiModelProperty("审批实例状态 （1审批中, 2已通过, 3已拒绝, 4已撤销）")
    private Integer auditStatus;

    @ApiModelProperty("业务发起日期")
    private Date businessDate;

    @ApiModelProperty("流程完结时间")
    private Date finishTime;

    @ApiModelProperty("创建人")
    private Long createById;

    @ApiModelProperty("创建时间")
    private Date createTime;

}
