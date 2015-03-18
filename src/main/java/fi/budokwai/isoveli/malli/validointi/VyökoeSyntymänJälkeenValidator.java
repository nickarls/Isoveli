package fi.budokwai.isoveli.malli.validointi;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import fi.budokwai.isoveli.malli.Vy�koe;
import fi.budokwai.isoveli.util.DateUtil;

public class Vy�koeSyntym�nJ�lkeenValidator implements ConstraintValidator<Vy�koeSyntym�nJ�lkeen, Vy�koe>
{

   @Override
   public void initialize(Vy�koeSyntym�nJ�lkeen constraintAnnotation)
   {
   }

   @Override
   public boolean isValid(Vy�koe vy�koe, ConstraintValidatorContext context)
   {
      if (DateUtil.onkoAiemmin(vy�koe.getP�iv�(), vy�koe.getHarrastaja().getSyntynyt()))
      {
         context.disableDefaultConstraintViolation();
         context.buildConstraintViolationWithTemplate(
            String.format("Harrastaja on syntynyt %s, h�n ei ole voinut suorittaa vy�arvon %s %s",
               DateUtil.p�iv�Tekstiksi(vy�koe.getHarrastaja().getSyntynyt()), vy�koe.getVy�arvo().getNimi(),
               DateUtil.p�iv�Tekstiksi(vy�koe.getP�iv�()))).addConstraintViolation();
         return false;
      }
      return true;
   }

}
