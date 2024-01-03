package approval;

import com.google.common.base.Preconditions;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.NonNull;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * 审批回调的后置处理器
 *
 * @author wangfarui
 * @since 2023/12/13
 */
public abstract class ApprovalCallbackProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        for (Method method : findAllMethod(clazz)) {
            processMethod(bean, method);
        }
        return bean;
    }

    public void checkArgument(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Preconditions.checkArgument(parameterTypes.length == 1,
                "Invalid number of parameters: %s for method: %s, should be 1", parameterTypes.length,
                method);
        Preconditions.checkArgument(BusinessApprovalCallbackEvent.class.isAssignableFrom(parameterTypes[0]),
                "Invalid parameter type: %s for method: %s, should be BusinessApprovalCallbackEvent", parameterTypes[0],
                method);
        ReflectionUtils.makeAccessible(method);
    }

    protected void processMethod(Object bean, Method method) {

    }

    private List<Method> findAllMethod(Class<?> clazz) {
        final List<Method> res = new LinkedList<>();
        ReflectionUtils.doWithMethods(clazz, res::add);
        return res;
    }
}
