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
 * 钉钉审批表单关联表
 * </p>
 *
 * @author wangfarui
 * @since 2023-08-02
 */
@Data
@TableName("dd_approval_form_rel")
@ApiModel(value = "DdApprovalFormRel对象", description = "钉钉审批表单关联表")
public class DdApprovalFormRel {

    @ApiModelProperty("主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("租户id")
    private Long tenantId;

    @ApiModelProperty("scm表单类型code(ApprovalFormEnum)")
    private Integer typeCode;

    @ApiModelProperty("scm表单类型名称")
    private String typeName;

    @ApiModelProperty("钉钉表单模板Code")
    private String processCode;

    @ApiModelProperty("表单版本号")
    private String formVersion;

    @ApiModelProperty("创建人")
    private Long createById;

    @ApiModelProperty("创建时间")
    private Date createTime;

}
