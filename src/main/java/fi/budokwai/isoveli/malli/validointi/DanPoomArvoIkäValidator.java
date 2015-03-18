package fi.budokwai.isoveli.malli.validointi;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import fi.budokwai.isoveli.malli.Vyökoe;
import fi.budokwai.isoveli.util.DateUtil;

public class DanPoomArvoIkäValidator implements ConstraintValidator<DanPoomArvoIkä, Vyökoe>
{

   @Override
   public void initialize(DanPoomArvoIkä constraintAnnotation)
   {
   }

   @Override
   public boolean isValid(Vyökoe vyökoe, ConstraintValidatorContext context)
   {
      long ikäVyökokeessa = DateUtil.vuosiaVälissä(vyökoe.getHarrastaja().getSyntynyt(), vyökoe.getPäivä());
      context.disableDefaultConstraintViolation();
      if (ikäVyökokeessa < 16 && vyökoe.getVyöarvo().isDan())
      {
         context.buildConstraintViolationWithTemplate(
            String.format("Harrastaja oli %s %d vuotta, tarkoitit varmaan poom-arvoa etkä %s?",
               DateUtil.päiväTekstiksi(vyökoe.getPäivä()), ikäVyökokeessa, vyökoe.getVyöarvo().getNimi()))
            .addConstraintViolation();
         return false;
      }
      if (ikäVyökokeessa > 16 && vyökoe.getVyöarvo().isPoom())
      {
         context.buildConstraintViolationWithTemplate(
            String.format("Harrastaja oli %s %d vuotta, tarkoitit varmaan dan-arvoa etkä %s?",
               DateUtil.päiväTekstiksi(vyökoe.getPäivä()), ikäVyökokeessa, vyökoe.getVyöarvo().getNimi()))
            .addConstraintViolation();
         return false;
      }
      return true;
   }

}
