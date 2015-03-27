package fi.budokwai.isoveli;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import fi.budokwai.isoveli.admin.Perustoiminnallisuus;
import java.io.Serializable;

@SessionScoped
@Named
public class Asetuslataaja extends Perustoiminnallisuus implements Serializable
{
   private static final long serialVersionUID = 1L;

   @Inject
   private EntityManager entityManager;

   private Asetukset asetukset;

   @Produces
   @Model
   public Asetukset getAsetukset()
   {
      if (asetukset == null)
      {
         asetukset = entityManager.find(Asetukset.class, "Budokwai");
      }
      return asetukset;
   }

   public void tallennaAsetukset()
   {
      asetukset = entityManager.merge(asetukset);
      loggaaja.loggaa("Tallensi asetukset");
      info("Asetukset tallennettu");
   }
}
