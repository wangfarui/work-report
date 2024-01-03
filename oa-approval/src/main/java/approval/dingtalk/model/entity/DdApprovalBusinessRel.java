package approval.dingtalk.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * <p>
 * 钉钉审批业务关联表
 * </p>
 *
 * @author wangfarui
 * @since 2023-08-02
 */
@Data
@TableName("dd_approval_business_rel")
@ApiModel(value = "DdApprovalBusinessRel对象", description = "钉钉审批业务关联表")
public class DdApprovalBusinessRel {

    @ApiModelProperty("主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("租户id")
    private Long tenantId;

    @ApiModelProperty("业务表id")
    private Long businessId;

    @ApiModelProperty("流程实例id（钉钉创建的审批流程实例id）")
    private String instanceId;

    @ApiModelProperty("流程实例哈希值")
    private Integer instanceHash;

    @ApiModelProperty("流程实例审批编号")
    private String instanceBusinessId;

    @ApiModelProperty("流程实例标题")
    private String instanceTitle;

    @ApiModelProperty("表单关联id（dd_approval_form_rel）")
    private Long formRelId;

    @ApiModelProperty("scm表单类型code")
    private Integer typeCode;

    @ApiModelProperty("scm表单类型名称")
    private String typeName;

    @ApiModelProperty("钉钉表单模板Code")
    private String processCode;

    @ApiModelProperty("钉钉审核状态 （1.审核中，2.审核通过，3.审核拒绝, 4.审核撤销）")
    private Integer auditStatus;

    @ApiModelProperty("流程完结时间")
    private Date finishTime;

    @ApiModelProperty("创建人")
    private Long createById;

    @ApiModelProperty("创建时间")
    private Date createTime;

}
