package fi.budokwai.isoveli.malli.validointi;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Vyöarvo;
import fi.budokwai.isoveli.malli.Vyökoe;

public class UniikitVyökokeetValidator implements ConstraintValidator<UniikitVyökokeet, Harrastaja>
{

   @Override
   public void initialize(UniikitVyökokeet constraintAnnotation)
   {
   }

   @Override
   public boolean isValid(Harrastaja harrastaja, ConstraintValidatorContext context)
   {
      Set<Vyöarvo> vyöarvot = new HashSet<Vyöarvo>();
      for (Vyökoe vyökoe : harrastaja.getVyökokeet())
      {
         if (vyöarvot.contains(vyökoe.getVyöarvo()))
         {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
               String.format("Harrastajalla on jo vyöarvo %s", vyökoe.getVyöarvo().getNimi())).addConstraintViolation();
            return false;
         }
         vyöarvot.add(vyökoe.getVyöarvo());
      }
      return true;
   }

}
