package fi.budokwai.isoveli.malli.validointi;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;

public class PerusValidator
{
   @SuppressWarnings("unchecked")
   protected <T> T lueRaja(Object olio, String kentt�)
   {
      try
      {
         return (T) PropertyUtils.getProperty(olio, kentt�);
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
      {
         return null;
      }
   }

}
