package fi.budokwai.isoveli.malli.validointi;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Vyökoe;
import fi.budokwai.isoveli.util.DateUtil;

public class VyökoeJärjestysValidator implements ConstraintValidator<Vyökoejärjestys, Harrastaja>
{

   @Override
   public void initialize(Vyökoejärjestys constraintAnnotation)
   {
   }

   @Override
   public boolean isValid(Harrastaja harrastaja, ConstraintValidatorContext context)
   {
      Vyökoe edellinenKoe = Vyökoe.EI_OOTA;
      for (Vyökoe vyökoe : harrastaja.getVyökokeet())
      {
         if (edellinenKoe != Vyökoe.EI_OOTA && !DateUtil.onkoAiemmin(edellinenKoe.getPäivä(), vyökoe.getPäivä()))
         {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
               String.format("%s (%s) ei voi olla suoritettuna aikaisemmin kun %s (%s)", vyökoe.getVyöarvo().getNimi(),
                  DateUtil.päiväTekstiksi(vyökoe.getPäivä()), edellinenKoe.getVyöarvo().getNimi(),
                  DateUtil.päiväTekstiksi(edellinenKoe.getPäivä()))).addConstraintViolation();
            return false;
         }
         edellinenKoe = vyökoe;
      }
      return true;
   }

}
