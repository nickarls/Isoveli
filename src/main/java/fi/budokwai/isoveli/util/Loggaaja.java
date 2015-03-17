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

import fi.budokwai.isoveli.malli.Henkilö;
import fi.budokwai.isoveli.malli.Loki;

@Stateful
@RequestScoped
public class Loggaaja
{
   @Inject
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

   public void loggaa(String teksti, Object... parametrit)
   {
      Loki loki = new Loki();
      String kuka = null;
      try
      {
         kuka = käyttäjä.get().getNimi();
      } catch (Exception e)
      {
         kuka = "Isoveli";
      }
      loki.setKuka(kuka);
      loki.setKoska(new Date());
      loki.setMitä(String.format(teksti, parametrit));
      entityManager.persist(loki);
   }
}
