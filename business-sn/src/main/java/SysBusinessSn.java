import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 系统业务编号表
 *
 * @author wangfarui
 * @since 2022/8/4
 */
@TableName("sys_business_sn")
@Data
public class SysBusinessSn {

    /**
     * 主键id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 业务类型
     *
     * @see com.wyyt.scp.collaboration.enums.SysBusinessSnEnum#getCode()
     */
    @SuppressWarnings("all")
    private Integer businessType;

    /**
     * 业务日期
     */
    private Date businessDate;

    /**
     * 业务编号 (一般根据 业务类型+业务日期 唯一)
     */
    private Integer businessSn;

}
