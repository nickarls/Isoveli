package fi.budokwai.isoveli.malli.validointi;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import fi.budokwai.isoveli.malli.Henkilö;

public class UniikkiHenkilöValidator implements ConstraintValidator<UniikkiHenkilö, Henkilö>
{

   @PersistenceUnit
   private EntityManagerFactory emf;

   @Override
   public void initialize(UniikkiHenkilö constraintAnnotation)
   {
   }

   @Override
   public boolean isValid(Henkilö henkilö, ConstraintValidatorContext context)
   {
      if (emf == null)
      {
         return true;
      }
      List<Henkilö> h = emf
         .createEntityManager()
         .createQuery("select h from Henkilö h where h.etunimi=:etunimi and h.sukunimi=:sukunimi and h.id <> :id",
            Henkilö.class).setParameter("etunimi", henkilö.getEtunimi())
         .setParameter("sukunimi", henkilö.getSukunimi()).setParameter("id", henkilö.getId()).getResultList();
      if (!h.isEmpty())
      {
         context.disableDefaultConstraintViolation();
         context.buildConstraintViolationWithTemplate(String.format("Samanniminen henkilö '%s' on jo olemassa", henkilö.getNimi()))
            .addConstraintViolation();
         return false;
      }
      return true;
   }

}
