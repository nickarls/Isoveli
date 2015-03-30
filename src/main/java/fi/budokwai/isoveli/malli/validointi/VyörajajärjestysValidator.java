package fi.budokwai.isoveli.malli.validointi;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import fi.budokwai.isoveli.malli.Vyöarvo;

public class VyörajajärjestysValidator extends PerusValidator implements ConstraintValidator<Vyörajajärjestys, Object>
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
   public boolean isValid(Object olio, ConstraintValidatorContext context)
   {
      context.disableDefaultConstraintViolation();
      Vyöarvo alaVyö = lueRaja(olio, alaraja);
      Vyöarvo yläVyö = lueRaja(olio, yläraja);
      if (alaVyö != null && yläVyö != null && (alaVyö.getJärjestys() < yläVyö.getJärjestys()))
      {
         String viesti = String.format("Ylävyöraja %s ei voi olla alempi kun alaraja %s", alaVyö.getNimi(),
            yläVyö.getNimi());
         context.buildConstraintViolationWithTemplate(viesti).addConstraintViolation();
         return false;
      }
      return true;
   }

}
