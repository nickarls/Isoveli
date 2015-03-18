package fi.budokwai.isoveli.malli.validointi;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Vy�koe;
import fi.budokwai.isoveli.util.DateUtil;

public class Vy�koeJ�rjestysValidator implements ConstraintValidator<Vy�koej�rjestys, Harrastaja>
{

   @Override
   public void initialize(Vy�koej�rjestys constraintAnnotation)
   {
   }

   @Override
   public boolean isValid(Harrastaja harrastaja, ConstraintValidatorContext context)
   {
      Vy�koe edellinenKoe = Vy�koe.EI_OOTA;
      for (Vy�koe vy�koe : harrastaja.getVy�kokeet())
      {
         if (edellinenKoe != Vy�koe.EI_OOTA && !DateUtil.onkoAiemmin(edellinenKoe.getP�iv�(), vy�koe.getP�iv�()))
         {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
               String.format("%s (%s) ei voi olla suoritettuna aikaisemmin kun %s (%s)", vy�koe.getVy�arvo().getNimi(),
                  DateUtil.p�iv�Tekstiksi(vy�koe.getP�iv�()), edellinenKoe.getVy�arvo().getNimi(),
                  DateUtil.p�iv�Tekstiksi(edellinenKoe.getP�iv�()))).addConstraintViolation();
            return false;
         }
         edellinenKoe = vy�koe;
      }
      return true;
   }

}
