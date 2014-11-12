package fi.budokwai.isoveli.util;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.apache.deltaspike.core.api.exception.control.event.ExceptionToCatchEvent;
import org.apache.deltaspike.core.api.provider.BeanManagerProvider;


public class DSInterceptorIntegration
{
   @AroundInvoke
   public Object catchExceptions(InvocationContext invocationContext) throws Exception
   {
      try
      {
         return invocationContext.proceed();
      } catch (Exception exception)
      {
         BeanManagerProvider.getInstance().getBeanManager().fireEvent(new ExceptionToCatchEvent(exception));
         return null;
      }
   }
}
