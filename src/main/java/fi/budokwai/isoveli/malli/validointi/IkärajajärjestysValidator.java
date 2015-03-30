package fi.budokwai.isoveli.malli.validointi;

import java.lang.reflect.InvocationTargetException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;

import fi.budokwai.isoveli.malli.Vy�koetilaisuus;

public class Ik�rajaj�rjestysValidator implements ConstraintValidator<Ik�rajaj�rjestys, Vy�koetilaisuus>
{
   private String alaraja;
   private String yl�raja;

   @Override
   public void initialize(Ik�rajaj�rjestys constraintAnnotation)
   {
      alaraja = constraintAnnotation.yl�raja();
      yl�raja = constraintAnnotation.alaraja();
   }

   @Override
   public boolean isValid(Vy�koetilaisuus vy�koetilaisuus, ConstraintValidatorContext context)
   {
      context.disableDefaultConstraintViolation();
      Integer alaIk� = lueRaja(vy�koetilaisuus, alaraja);
      Integer yl�Ik� = lueRaja(vy�koetilaisuus, yl�raja);
      if (alaIk� != null && yl�Ik� != null && (yl�Ik� > alaIk�))
      {
         context.buildConstraintViolationWithTemplate(
            String.format("Yl�ik�raja %d ei voi olla alempi kun alaraja %d", yl�Ik�, alaIk�)).addConstraintViolation();
         return false;
      }
      return true;
   }

   private Integer lueRaja(Vy�koetilaisuus vy�koetilaisuus, String kentt�)
   {
      String arvo = null;
      try
      {
         arvo = BeanUtils.getProperty(vy�koetilaisuus, kentt�);
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
      {
         return null;
      }
      if (arvo == null)
      {
         return null;
      }
      return Integer.parseInt(arvo);
   }

}
