import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 系统业务编号表 Manager层
 *
 * @author wangfarui
 * @since 2022/8/4
 */
@Component
public class SysBusinessSnDao extends ServiceImpl<SysBusinessSnMapper, SysBusinessSn> {

    /**
     * 业务编号格式（保留3位数）
     */
    private static final DecimalFormat BUSINESS_SN_3 = new DecimalFormat("000");

    /**
     * 业务日期格式（用于sql条件匹配查询）
     */
    private static final SimpleDateFormat BUSINESS_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 业务编号默认日期格式
     */
    private static final SimpleDateFormat BUSINESS_SN_DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    @Resource
    private PlatformTransactionManager transactionManager;

    @PostConstruct
    public void init() {
        // 校验业务编号枚举是否合规
        SysBusinessSnEnum.checkCodeValidation();
    }

    /**
     * 根据业务编号枚举的全量规则 生成业务编号
     *
     * @param sysBusinessSnEnum 业务编号枚举
     * @return 全量业务编号
     */
    public String generateBusinessSnByEnum(SysBusinessSnEnum sysBusinessSnEnum, Long companyId) {
        DecimalFormat decimalFormat = this.generateDynamicNumber(sysBusinessSnEnum.getPlaces());
        return this.generateDefaultFullBusinessSnByCustomized(sysBusinessSnEnum, sysBusinessSnEnum.getDateFormat(), decimalFormat, companyId);
    }

    /**
     * 生成默认全量业务编号 (自然数编号是3位)
     * <p>生成的业务编号规则为: {@link SysBusinessSnEnum#getPrefix()} + yyyyMMdd + xxx
     *
     * @param sysBusinessSnEnum 业务类型枚举
     * @return 全量业务编号
     */
    public String generateDefaultFullBusinessSn3(SysBusinessSnEnum sysBusinessSnEnum) {
        return this.generateDefaultFullBusinessSnByDecimalFormat(sysBusinessSnEnum, BUSINESS_SN_3);
    }

    /**
     * 生成默认全量业务编号 (自然数编号是x位)
     * <p>生成的业务编号规则为: {@link SysBusinessSnEnum#getPrefix()} + yyyyMMdd + x * x
     *
     * @param sysBusinessSnEnum 业务类型枚举
     * @param x                 自然数的位数
     * @return 全量业务编号
     */
    public String generateDefaultFullBusinessSnX(SysBusinessSnEnum sysBusinessSnEnum, int x) {
        return this.generateDefaultFullBusinessSnByDecimalFormat(sysBusinessSnEnum, this.generateDynamicNumber(x));
    }

    /**
     * 生成默认全量业务编号 (自然数编号根据自定义格式决定)
     * <p>生成的业务编号规则为: {@link SysBusinessSnEnum#getPrefix()} + yyyyMMdd + (decimalFormat.format(sn))
     *
     * @param sysBusinessSnEnum 业务类型枚举
     * @param decimalFormat     自然数的格式
     * @return 全量业务编号
     */
    public String generateDefaultFullBusinessSnByDecimalFormat(SysBusinessSnEnum sysBusinessSnEnum, DecimalFormat decimalFormat) {
        return this.generateDefaultFullBusinessSnByCustomized(sysBusinessSnEnum, "yyyyMMdd", decimalFormat, null);
    }

    /**
     * 自定义生成默认全量业务编号 (年月日和自然数编号根据自定义格式决定)
     * <p>生成的业务编号规则为: {@link SysBusinessSnEnum#getPrefix()} + (dateFormat) + (decimalFormat.format(sn))
     *
     * @param sysBusinessSnEnum 业务类型枚举
     * @param decimalFormat     自然数的格式
     * @param dateFormat        日期格式
     * @return 全量业务编号
     */
    public String generateDefaultFullBusinessSnByCustomized(SysBusinessSnEnum sysBusinessSnEnum, String dateFormat,
                                                            DecimalFormat decimalFormat, Long companyId) {
        Date date = new Date();
        StringBuilder sb = new StringBuilder(sysBusinessSnEnum.getPrefix());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        sb.append(simpleDateFormat.format(date));

        if (sysBusinessSnEnum.getRandomLen() > 0) {
            String randomNumbers = RandomUtil.randomNumbers(sysBusinessSnEnum.getRandomLen());
            sb.append(randomNumbers);
        }

        Integer sn = this.getBusinessSnByType(sysBusinessSnEnum.getCode(), date, companyId);

        return sb.append(decimalFormat.format(sn)).toString();
    }

    /**
     * 根据业务前缀生成业务编号
     *
     * @param prefix 业务前缀
     * @return 业务编号
     */
    public String generateBusinessSnByPrefix(String prefix, Long companyId) {
        StringBuilder sb = new StringBuilder(prefix);
        if (StringUtils.isBlank(prefix)) {
            prefix = "UNKNOWN";
        } else {
            sb.append(prefix);
        }
        Date date = new Date();
        sb.append(BUSINESS_SN_DEFAULT_DATE_FORMAT.format(date));

        Integer businessType = this.getBusinessType(prefix);
        Integer sn = this.getBusinessSnByType(businessType, date, companyId);

        return sb.append(BUSINESS_SN_3.format(sn)).toString();
    }

    /**
     * 根据业务类型和业务日期 获取业务编号
     * <p>同时数据库中对应业务编号自增</p>
     *
     * @param businessType 业务类型 {@link SysBusinessSnEnum#getCode()}
     * @param date         业务日期
     * @return 业务编号
     */
    public Integer getBusinessSnByType(Integer businessType, Date date, Long companyId) {
        // 拿取到真正存储到数据库的tenantId, 不能为null
        String realTenantId;
        if (companyId == null) {
            Long tenantId = UserUtils.getTenantId();
            if (tenantId == null) {
                throw new IllegalStateException("token信息异常");
            }
            realTenantId = tenantId.toString();
        } else {
            realTenantId = companyId.toString();
        }
        String dateStr = BUSINESS_DATE_FORMAT.format(date);

        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        baseMapper.incrementBusinessSn(realTenantId, businessType, dateStr);

        SysBusinessSn entity = lambdaQuery()
                .eq(SysBusinessSn::getTenantId, realTenantId)
                .eq(SysBusinessSn::getBusinessType, businessType)
                .eq(SysBusinessSn::getBusinessDate, dateStr)
                .select(SysBusinessSn::getBusinessSn)
                .last("limit 1")
                .one();

        transactionManager.commit(transactionStatus);

        return entity.getBusinessSn();
    }

    private DecimalFormat generateDynamicNumber(int x) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < x; i++) {
            sb.append("0");
        }
        return new DecimalFormat(sb.toString());
    }

    private Integer getBusinessType(String prefix) {
        int businessType = prefix.hashCode();
        if (businessType >= 0 && businessType <= 1024) {
            return businessType + 1024;
        }
        return businessType;
    }
}
