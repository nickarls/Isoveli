package fi.budokwai.isoveli;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class Asetuslataaja
{
   @PersistenceContext
   private EntityManager entityManager;

   @Produces
   @SessionScoped
   public Asetukset getAsetukset()
   {
      return entityManager.find(Asetukset.class, "Budokwai");
   }
}
