package fi.budokwai.isoveli.malli.validointi;

import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.util.DateUtil;

public class TaukotarkistusValidator implements ConstraintValidator<Taukotarkistus, Harrastaja>
{

   @Override
   public void initialize(Taukotarkistus constraintAnnotation)
   {
   }

   @Override
   public boolean isValid(Harrastaja harrastaja, ConstraintValidatorContext context)
   {
      context.disableDefaultConstraintViolation();
      if (harrastaja.getTauko().isAvoin())
      {
         context.buildConstraintViolationWithTemplate(
            String.format("Sek‰ tauon alkamis- ett‰ p‰‰ttymisp‰iv‰m‰‰r‰ annettava")).addConstraintViolation();
         return false;
      }
      if (harrastaja.getTauko().isRajatRistiss‰())
      {
         context.buildConstraintViolationWithTemplate(String.format("Tauko ei voi loppua ennen kun se alkaa"))
            .addConstraintViolation();
         return false;
      }
      Date taukoAlkaa = harrastaja.getTauko().getAlkaa();
      if (taukoAlkaa != null && DateUtil.onkoAiemmin(taukoAlkaa, harrastaja.getSyntynyt()))
      {
         context.buildConstraintViolationWithTemplate(
            String.format("Tauko ei voi alkaa ennen kun harrastaja on syntynyt")).addConstraintViolation();
         return false;
      }
      Date taukoP‰‰ttyy = harrastaja.getTauko().getP‰‰ttyy();
      if (taukoP‰‰ttyy != null && DateUtil.onkoAiemmin(taukoP‰‰ttyy, harrastaja.getSyntynyt()))
      {
         context.buildConstraintViolationWithTemplate(
            String.format("Tauko ei voi p‰‰tty‰ ennen kun harrastaja on syntynyt")).addConstraintViolation();
         return false;
      }
      return true;
   }

}
