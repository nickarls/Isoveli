package fi.budokwai.isoveli;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;

public class Asetuslataaja
{
   @Inject
   private EntityManager entityManager;

   @Produces
   @SessionScoped
   public Asetukset getAsetukset()
   {
      return entityManager.find(Asetukset.class, "Budokwai");
   }
}
