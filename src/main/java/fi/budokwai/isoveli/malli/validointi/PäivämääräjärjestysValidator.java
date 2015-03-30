package fi.budokwai.isoveli.malli.validointi;

import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import fi.budokwai.isoveli.util.DateUtil;

public class P‰iv‰m‰‰r‰j‰rjestysValidator extends PerusValidator implements
   ConstraintValidator<P‰iv‰m‰‰r‰j‰rjestys, Object>
{
   private String alaraja;
   private String yl‰raja;
   private boolean aika;

   @Override
   public void initialize(P‰iv‰m‰‰r‰j‰rjestys constraintAnnotation)
   {
      alaraja = constraintAnnotation.alaraja();
      yl‰raja = constraintAnnotation.yl‰raja();
      aika = constraintAnnotation.aika();
   }

   @Override
   public boolean isValid(Object olio, ConstraintValidatorContext context)
   {
      Date alaP‰iv‰ = lueRaja(olio, alaraja);
      Date yl‰P‰iv‰ = lueRaja(olio, yl‰raja);
      if (alaP‰iv‰ != null && yl‰P‰iv‰ != null && !tarkistusOK(alaP‰iv‰, yl‰P‰iv‰))
      {
         return false;
      }
      return true;
   }

   private boolean tarkistusOK(Date alaP‰iv‰, Date yl‰P‰iv‰)
   {
      boolean x = aika ? DateUtil.onkoAikaAiemmin(alaP‰iv‰, yl‰P‰iv‰) : DateUtil.onkoAiemmin(alaP‰iv‰, yl‰P‰iv‰);
      return x;
   }

}
