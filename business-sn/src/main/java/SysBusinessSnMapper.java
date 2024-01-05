import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 系统业务编号表 Mapper层
 *
 * @author wangfarui
 * @since 2022/8/4
 */
@Mapper
public interface SysBusinessSnMapper extends BaseMapper<SysBusinessSn> {

    void incrementBusinessSn(@Param("tenantId") String tenantId, @Param("businessType") Integer businessType, @Param("businessDate") String businessDate);
}
