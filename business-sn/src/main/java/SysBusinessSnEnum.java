import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * 系统业务编号 枚举类
 *
 * @author wangfarui
 * @since 2022/8/4
 */
@Getter
public enum SysBusinessSnEnum {

    DEMO(1, "DEMO", 4, "yyyyMMdd", 0, "示例");

    /**
     * 业务类型（针对 sys_business_sn 表的业务类型）
     * <p>枚举内唯一</p>
     * <p>code允许范围为[0, 1024]</p>
     */
    private final int code;

    /**
     * 业务编号的前缀（可为空）
     */
    private final String prefix;

    /**
     * 自然数的位数，例如4就是0001
     * <p>自然数超过位数的范围时，正常显示自然数</p>
     */
    private final int places;

    /**
     * 日期格式
     */
    private final String dateFormat;

    /**
     * 随机数字长度
     * <p>randomLen <= 0时，表示无随机数</p>
     */
    private final int randomLen;

    /**
     * 业务解释
     */
    private final String text;

    SysBusinessSnEnum(int code, String prefix, int places, String dateFormat, int randomLen, String text) {
        if (code < 0 || code > 1024) {
            throw new IllegalArgumentException("非法的code值");
        }
        this.code = code;
        this.prefix = prefix;
        this.places = places;
        this.dateFormat = dateFormat;
        this.randomLen = randomLen;
        this.text = text;
    }

    public static void checkCodeValidation() {
        Set<Integer> codeSet = new HashSet<>();
        SysBusinessSnEnum[] sysBusinessSnEnums = values();
        for (SysBusinessSnEnum sysBusinessSnEnum : sysBusinessSnEnums) {
            if (!codeSet.add(sysBusinessSnEnum.code)) {
                throw new IllegalArgumentException("SysBusinessSnEnum code 不能重复!");
            }
        }
    }
}
