package fi.budokwai.isoveli.util;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import fi.budokwai.isoveli.malli.Henkil�;
import fi.budokwai.isoveli.malli.Loki;

@Stateful
@RequestScoped
public class Loggaaja
{
   @PersistenceContext
   private EntityManager entityManager;

   @Inject
   @Kirjautunut
   private Instance<Henkil�> k�ytt�j�;

   @Produces
   @Named
   public List<Loki> getLokirivit()
   {
      return entityManager.createNamedQuery("lokitapahtumat", Loki.class).getResultList();
   }

   public void loggaa(String mit�)
   {
      Loki loki = new Loki();
      loki.setKuka(k�ytt�j�.get().getNimi());
      loki.setKoska(new Date());
      loki.setMit�(mit�);
      entityManager.persist(loki);
   }
}
