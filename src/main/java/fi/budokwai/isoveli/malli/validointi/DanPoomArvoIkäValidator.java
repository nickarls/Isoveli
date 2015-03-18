package fi.budokwai.isoveli.malli.validointi;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import fi.budokwai.isoveli.malli.Vy�koe;
import fi.budokwai.isoveli.util.DateUtil;

public class DanPoomArvoIk�Validator implements ConstraintValidator<DanPoomArvoIk�, Vy�koe>
{

   @Override
   public void initialize(DanPoomArvoIk� constraintAnnotation)
   {
   }

   @Override
   public boolean isValid(Vy�koe vy�koe, ConstraintValidatorContext context)
   {
      long ik�Vy�kokeessa = DateUtil.vuosiaV�liss�(vy�koe.getHarrastaja().getSyntynyt(), vy�koe.getP�iv�());
      context.disableDefaultConstraintViolation();
      if (ik�Vy�kokeessa < 16 && vy�koe.getVy�arvo().isDan())
      {
         context.buildConstraintViolationWithTemplate(
            String.format("Harrastaja oli %s %d vuotta, tarkoitit varmaan poom-arvoa etk� %s?",
               DateUtil.p�iv�Tekstiksi(vy�koe.getP�iv�()), ik�Vy�kokeessa, vy�koe.getVy�arvo().getNimi()))
            .addConstraintViolation();
         return false;
      }
      if (ik�Vy�kokeessa > 16 && vy�koe.getVy�arvo().isPoom())
      {
         context.buildConstraintViolationWithTemplate(
            String.format("Harrastaja oli %s %d vuotta, tarkoitit varmaan dan-arvoa etk� %s?",
               DateUtil.p�iv�Tekstiksi(vy�koe.getP�iv�()), ik�Vy�kokeessa, vy�koe.getVy�arvo().getNimi()))
            .addConstraintViolation();
         return false;
      }
      return true;
   }

}
