package approval.dingtalk.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 钉钉审批流程日志附件表
 *
 * @author wangfarui
 * @since 2023/8/28
 */
@Data
public class DdApprovalProcessLogAttachment {

    @ApiModelProperty("主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("租户id")
    private Long tenantId;

    @ApiModelProperty("钉钉审批流程日志id")
    private Long logId;

    @ApiModelProperty("附件名称")
    private String fileName;

    @ApiModelProperty("附件地址URL")
    private String fileUrl;

    @ApiModelProperty("附件类型")
    private String fileType;
}
