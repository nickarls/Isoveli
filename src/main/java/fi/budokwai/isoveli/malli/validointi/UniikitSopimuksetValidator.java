package fi.budokwai.isoveli.malli.validointi;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Sopimustyyppi;

public class UniikitSopimuksetValidator implements ConstraintValidator<UniikitSopimukset, Harrastaja>
{

   @Override
   public void initialize(UniikitSopimukset constraintAnnotation)
   {
   }

   @Override
   public boolean isValid(Harrastaja harrastaja, ConstraintValidatorContext context)
   {
      Set<Sopimustyyppi> sopimustyyppit = new HashSet<>();
      List<Sopimustyyppi> aktiivitSopimustyypit = harrastaja.getSopimukset().stream().filter(s -> !s.isArkistoitu())
         .map(s -> s.getTyyppi()).collect(Collectors.toList());
      for (Sopimustyyppi sopimustyyppi : aktiivitSopimustyypit)
      {
         if (sopimustyyppit.contains(sopimustyyppi))
         {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
               String.format("Harrastajalla on jo aktiivinen sopimustyyppi %s", sopimustyyppi.getNimi()))
               .addConstraintViolation();
            return false;
         }
         sopimustyyppit.add(sopimustyyppi);
      }
      return true;
   }

}
