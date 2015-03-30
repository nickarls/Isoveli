package fi.budokwai.isoveli.malli.validointi;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class Ik‰rajaj‰rjestysValidator extends PerusValidator implements ConstraintValidator<Ik‰rajaj‰rjestys, Object>
{
   private String alaraja;
   private String yl‰raja;

   @Override
   public void initialize(Ik‰rajaj‰rjestys constraintAnnotation)
   {
      alaraja = constraintAnnotation.yl‰raja();
      yl‰raja = constraintAnnotation.alaraja();
   }

   @Override
   public boolean isValid(Object olio, ConstraintValidatorContext context)
   {
      context.disableDefaultConstraintViolation();
      Integer alaIk‰ = lueRaja(olio, alaraja);
      Integer yl‰Ik‰ = lueRaja(olio, yl‰raja);
      if (alaIk‰ != null && yl‰Ik‰ != null && (yl‰Ik‰ > alaIk‰))
      {
         context.buildConstraintViolationWithTemplate(
            String.format("Yl‰ik‰raja %d ei voi olla alempi kun alaraja %d", yl‰Ik‰, alaIk‰)).addConstraintViolation();
         return false;
      }
      return true;
   }

}
