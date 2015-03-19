package fi.budokwai.isoveli.malli.validointi;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import fi.budokwai.isoveli.malli.Henkil�;

public class UniikkiHenkil�Validator implements ConstraintValidator<UniikkiHenkil�, Henkil�>
{

   @PersistenceUnit
   private EntityManagerFactory emf;

   @Override
   public void initialize(UniikkiHenkil� constraintAnnotation)
   {
   }

   @Override
   public boolean isValid(Henkil� henkil�, ConstraintValidatorContext context)
   {
      if (emf == null)
      {
         return true;
      }
      List<Henkil�> h = emf
         .createEntityManager()
         .createQuery("select h from Henkil� h where h.etunimi=:etunimi and h.sukunimi=:sukunimi and h.id <> :id",
            Henkil�.class).setParameter("etunimi", henkil�.getEtunimi())
         .setParameter("sukunimi", henkil�.getSukunimi()).setParameter("id", henkil�.getId()).getResultList();
      if (!h.isEmpty())
      {
         context.disableDefaultConstraintViolation();
         context.buildConstraintViolationWithTemplate(String.format("Samanniminen henkil� '%s' on jo olemassa", henkil�.getNimi()))
            .addConstraintViolation();
         return false;
      }
      return true;
   }

}
