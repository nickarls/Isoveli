package fi.budokwai.isoveli.malli.validointi;

import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import fi.budokwai.isoveli.util.DateUtil;

public class PäivämääräjärjestysValidator extends PerusValidator implements
   ConstraintValidator<Päivämääräjärjestys, Object>
{
   private String alaraja;
   private String yläraja;
   private boolean aika;

   @Override
   public void initialize(Päivämääräjärjestys constraintAnnotation)
   {
      alaraja = constraintAnnotation.alaraja();
      yläraja = constraintAnnotation.yläraja();
      aika = constraintAnnotation.aika();
   }

   @Override
   public boolean isValid(Object olio, ConstraintValidatorContext context)
   {
      Date alaPäivä = lueRaja(olio, alaraja);
      Date yläPäivä = lueRaja(olio, yläraja);
      if (alaPäivä != null && yläPäivä != null && !tarkistusOK(alaPäivä, yläPäivä))
      {
         return false;
      }
      return true;
   }

   private boolean tarkistusOK(Date alaPäivä, Date yläPäivä)
   {
      boolean x = aika ? DateUtil.onkoAikaAiemmin(alaPäivä, yläPäivä) : DateUtil.onkoAiemmin(alaPäivä, yläPäivä);
      return x;
   }

}
