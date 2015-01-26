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

import fi.budokwai.isoveli.malli.Henkilö;
import fi.budokwai.isoveli.malli.Loki;

@Stateful
@RequestScoped
public class Loggaaja
{
   @PersistenceContext
   private EntityManager entityManager;

   @Inject
   @Kirjautunut
   private Instance<Henkilö> käyttäjä;

   @Produces
   @Named
   public List<Loki> getLokirivit()
   {
      return entityManager.createNamedQuery("lokitapahtumat", Loki.class).getResultList();
   }

   public void loggaa(String mitä)
   {
      Loki loki = new Loki();
      loki.setKuka(käyttäjä.get().getNimi());
      loki.setKoska(new Date());
      loki.setMitä(mitä);
      entityManager.persist(loki);
   }
}
