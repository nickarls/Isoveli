package fi.budokwai.isoveli.malli.validointi;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import fi.budokwai.isoveli.malli.Vyökoe;
import fi.budokwai.isoveli.util.DateUtil;

public class VyökoeSyntymänJälkeenValidator implements ConstraintValidator<VyökoeSyntymänJälkeen, Vyökoe>
{

   @Override
   public void initialize(VyökoeSyntymänJälkeen constraintAnnotation)
   {
   }

   @Override
   public boolean isValid(Vyökoe vyökoe, ConstraintValidatorContext context)
   {
      if (DateUtil.onkoAiemmin(vyökoe.getPäivä(), vyökoe.getHarrastaja().getSyntynyt()))
      {
         context.disableDefaultConstraintViolation();
         context.buildConstraintViolationWithTemplate(
            String.format("Harrastaja on syntynyt %s, hän ei ole voinut suorittaa vyöarvon %s %s",
               DateUtil.päiväTekstiksi(vyökoe.getHarrastaja().getSyntynyt()), vyökoe.getVyöarvo().getNimi(),
               DateUtil.päiväTekstiksi(vyökoe.getPäivä()))).addConstraintViolation();
         return false;
      }
      return true;
   }

}
