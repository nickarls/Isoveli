package fi.budokwai.isoveli.malli.validointi;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Vy�arvo;
import fi.budokwai.isoveli.malli.Vy�koe;

public class UniikitVy�kokeetValidator implements ConstraintValidator<UniikitVy�kokeet, Harrastaja>
{

   @Override
   public void initialize(UniikitVy�kokeet constraintAnnotation)
   {
   }

   @Override
   public boolean isValid(Harrastaja harrastaja, ConstraintValidatorContext context)
   {
      Set<Vy�arvo> vy�arvot = new HashSet<Vy�arvo>();
      for (Vy�koe vy�koe : harrastaja.getVy�kokeet())
      {
         if (vy�arvot.contains(vy�koe.getVy�arvo()))
         {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
               String.format("Harrastajalla on jo vy�arvo %s", vy�koe.getVy�arvo().getNimi())).addConstraintViolation();
            return false;
         }
         vy�arvot.add(vy�koe.getVy�arvo());
      }
      return true;
   }

}
