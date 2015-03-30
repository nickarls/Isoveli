package fi.budokwai.isoveli.malli.validointi;

import java.lang.reflect.InvocationTargetException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;

import fi.budokwai.isoveli.malli.Vyökoetilaisuus;

public class IkärajajärjestysValidator implements ConstraintValidator<Ikärajajärjestys, Vyökoetilaisuus>
{
   private String alaraja;
   private String yläraja;

   @Override
   public void initialize(Ikärajajärjestys constraintAnnotation)
   {
      alaraja = constraintAnnotation.yläraja();
      yläraja = constraintAnnotation.alaraja();
   }

   @Override
   public boolean isValid(Vyökoetilaisuus vyökoetilaisuus, ConstraintValidatorContext context)
   {
      context.disableDefaultConstraintViolation();
      Integer alaIkä = lueRaja(vyökoetilaisuus, alaraja);
      Integer yläIkä = lueRaja(vyökoetilaisuus, yläraja);
      if (alaIkä != null && yläIkä != null && (yläIkä > alaIkä))
      {
         context.buildConstraintViolationWithTemplate(
            String.format("Yläikäraja %d ei voi olla alempi kun alaraja %d", yläIkä, alaIkä)).addConstraintViolation();
         return false;
      }
      return true;
   }

   private Integer lueRaja(Vyökoetilaisuus vyökoetilaisuus, String kenttä)
   {
      String arvo = null;
      try
      {
         arvo = BeanUtils.getProperty(vyökoetilaisuus, kenttä);
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
