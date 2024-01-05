import org.springframework.beans.BeansException;

/**
 * 系统业务编号工具类
 *
 * @author wangfarui
 * @since 2023/1/17
 */
public abstract class SysBusinessSnUtil {

    /**
     * 全局租户id
     * <p>根据业务类型，生成系统全局唯一编码</p>
     */
    public static final Long GLOBAL_TENANT_ID = -1L;

    private static volatile SysBusinessSnDao SYS_BUSINESS_SN_DAO;

    private static SysBusinessSnDao getSysBusinessSnDao() {
        if (SYS_BUSINESS_SN_DAO == null) {
            synchronized (SysBusinessSnUtil.class) {
                if (SYS_BUSINESS_SN_DAO == null) {
                    try {
                        SYS_BUSINESS_SN_DAO = SpringUtil.getBean(SysBusinessSnDao.class);
                    } catch (BeansException e) {
                        throw new IllegalStateException("SysBusinessSnDao Bean 获取异常");
                    }
                }
            }
        }
        return SYS_BUSINESS_SN_DAO;
    }

    /**
     * 获取业务编号
     *
     * @param sysBusinessSnEnum 业务类型枚举
     * @return 租户下唯一的业务编号
     */
    public static String getBusinessSn(SysBusinessSnEnum sysBusinessSnEnum) {
        return getSysBusinessSnDao().generateBusinessSnByEnum(sysBusinessSnEnum, null);
    }

    /**
     * 获取业务编号
     *
     * @param sysBusinessSnEnum 业务类型枚举
     * @param tenantId          租户id
     * @return 指定tenantId下唯一的业务编号
     */
    public static String getBusinessSn(SysBusinessSnEnum sysBusinessSnEnum, Long tenantId) {
        return getSysBusinessSnDao().generateBusinessSnByEnum(sysBusinessSnEnum, tenantId);
    }

    /**
     * 生成全局唯一业务编号
     *
     * @param sysBusinessSnEnum 业务类型枚举
     * @return 全局唯一的业务编号
     */
    public static String getGlobalBusinessSn(SysBusinessSnEnum sysBusinessSnEnum) {
        return getSysBusinessSnDao().generateBusinessSnByEnum(sysBusinessSnEnum, GLOBAL_TENANT_ID);
    }

    /**
     * 获取业务编号
     * <p>返回结果：prefix + yyyyMMdd + 001</p>
     * <p>001序列根据prefix唯一，但不会与{@link SysBusinessSnEnum}冲突</p>
     *
     * @param prefix 业务编号前缀
     * @return 租户下唯一的业务编号
     */
    public static String getBusinessSn(String prefix) {
        return getSysBusinessSnDao().generateBusinessSnByPrefix(prefix, null);
    }

    /**
     * 获取业务编号
     *
     * @param prefix   业务编号前缀
     * @param tenantId 租户id
     * @return 指定tenantId下唯一的业务编号
     */
    public static String getBusinessSn(String prefix, Long tenantId) {
        return getSysBusinessSnDao().generateBusinessSnByPrefix(prefix, tenantId);
    }

    /**
     * 获取业务编号
     *
     * @param prefix 业务编号前缀
     * @return 全局唯一的业务编号
     */
    public static String getGlobalBusinessSn(String prefix) {
        return getSysBusinessSnDao().generateBusinessSnByPrefix(prefix, GLOBAL_TENANT_ID);
    }
}
