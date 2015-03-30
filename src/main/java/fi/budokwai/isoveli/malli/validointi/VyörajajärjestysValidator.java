package fi.budokwai.isoveli.malli.validointi;

import java.lang.reflect.InvocationTargetException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.PropertyUtils;

import fi.budokwai.isoveli.malli.Vyöarvo;
import fi.budokwai.isoveli.malli.Vyökoetilaisuus;

public class VyörajajärjestysValidator implements ConstraintValidator<Vyörajajärjestys, Vyökoetilaisuus>
{
   private String alaraja;
   private String yläraja;

   @Override
   public void initialize(Vyörajajärjestys constraintAnnotation)
   {
      alaraja = constraintAnnotation.yläraja();
      yläraja = constraintAnnotation.alaraja();
   }

   @Override
   public boolean isValid(Vyökoetilaisuus vyökoetilaisuus, ConstraintValidatorContext context)
   {
      context.disableDefaultConstraintViolation();
      Vyöarvo alaVyö = lueRaja(vyökoetilaisuus, alaraja);
      Vyöarvo yläVyö = lueRaja(vyökoetilaisuus, yläraja);
      if (alaVyö != null && yläVyö != null && (alaVyö.getJärjestys() < yläVyö.getJärjestys()))
      {
         String viesti = String.format("Ylävyöraja %s ei voi olla alempi kun alaraja %s", alaVyö.getNimi(),
            yläVyö.getNimi());
         context.buildConstraintViolationWithTemplate(viesti).addConstraintViolation();
         return false;
      }
      return true;
   }

   private Vyöarvo lueRaja(Vyökoetilaisuus vyökoetilaisuus, String kenttä)
   {
      try
      {
         return (Vyöarvo) PropertyUtils.getProperty(vyökoetilaisuus, kenttä);
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
      {
         return Vyöarvo.EI_OOTA;
      }
   }

}
