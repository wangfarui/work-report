package approval.dingtalk.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 钉钉用户关联表
 * </p>
 *
 * @author wangfarui
 * @since 2023-08-02
 */
@Data
@TableName("dd_user_rel")
@ApiModel(value = "DdUserRel对象", description = "钉钉用户关联表")
public class DdUserRel {

    @ApiModelProperty("主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("租户id")
    private Long tenantId;

    @ApiModelProperty("用户手机号")
    private String userMobile;

    @ApiModelProperty("scm用户id")
    private Long scmUserId;

    @ApiModelProperty("钉钉用户id")
    private String ddUserId;

    @ApiModelProperty("钉钉用户名称")
    private String ddUserName;

    @ApiModelProperty("钉钉用户部门id")
    private Long ddDeptId;

}
