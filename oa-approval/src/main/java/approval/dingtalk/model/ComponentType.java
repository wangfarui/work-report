package approval.dingtalk.model;

/**
 * 表单控件类型
 *
 * @author wangfarui
 * @since 2023/8/2
 */
public enum ComponentType {

    /**
     * 自动，默认类型
     * <p>自动识别控件类型，支持单行输入框、表格、附件</p>
     */
    AUTO,
    TextField, // 单行输入框
    TextareaField, // 多行输入框
    TableField, // 表格
    DDAttachment, // 附件
    RelateField, // 关联审批单
}
