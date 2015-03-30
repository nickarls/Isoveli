package fi.budokwai.isoveli.malli.validointi;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import fi.budokwai.isoveli.malli.Vy�arvo;

public class Vy�rajaj�rjestysValidator extends PerusValidator implements ConstraintValidator<Vy�rajaj�rjestys, Object>
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
   public boolean isValid(Object olio, ConstraintValidatorContext context)
   {
      context.disableDefaultConstraintViolation();
      Vy�arvo alaVy� = lueRaja(olio, alaraja);
      Vy�arvo yl�Vy� = lueRaja(olio, yl�raja);
      if (alaVy� != null && yl�Vy� != null && (alaVy�.getJ�rjestys() < yl�Vy�.getJ�rjestys()))
      {
         String viesti = String.format("Yl�vy�raja %s ei voi olla alempi kun alaraja %s", alaVy�.getNimi(),
            yl�Vy�.getNimi());
         context.buildConstraintViolationWithTemplate(viesti).addConstraintViolation();
         return false;
      }
      return true;
   }

}
