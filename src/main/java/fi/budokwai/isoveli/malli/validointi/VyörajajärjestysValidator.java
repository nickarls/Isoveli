package fi.budokwai.isoveli.malli.validointi;

import java.lang.reflect.InvocationTargetException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.PropertyUtils;

import fi.budokwai.isoveli.malli.Vy�arvo;
import fi.budokwai.isoveli.malli.Vy�koetilaisuus;

public class Vy�rajaj�rjestysValidator implements ConstraintValidator<Vy�rajaj�rjestys, Vy�koetilaisuus>
{
   private String alaraja;
   private String yl�raja;

   @Override
   public void initialize(Vy�rajaj�rjestys constraintAnnotation)
   {
      alaraja = constraintAnnotation.yl�raja();
      yl�raja = constraintAnnotation.alaraja();
   }

   @Override
   public boolean isValid(Vy�koetilaisuus vy�koetilaisuus, ConstraintValidatorContext context)
   {
      context.disableDefaultConstraintViolation();
      Vy�arvo alaVy� = lueRaja(vy�koetilaisuus, alaraja);
      Vy�arvo yl�Vy� = lueRaja(vy�koetilaisuus, yl�raja);
      if (alaVy� != null && yl�Vy� != null && (alaVy�.getJ�rjestys() < yl�Vy�.getJ�rjestys()))
      {
         String viesti = String.format("Yl�vy�raja %s ei voi olla alempi kun alaraja %s", alaVy�.getNimi(),
            yl�Vy�.getNimi());
         context.buildConstraintViolationWithTemplate(viesti).addConstraintViolation();
         return false;
      }
      return true;
   }

   private Vy�arvo lueRaja(Vy�koetilaisuus vy�koetilaisuus, String kentt�)
   {
      try
      {
         return (Vy�arvo) PropertyUtils.getProperty(vy�koetilaisuus, kentt�);
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
      {
         return Vy�arvo.EI_OOTA;
      }
   }

}
