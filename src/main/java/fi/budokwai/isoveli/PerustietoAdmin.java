package fi.budokwai.isoveli;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.icefaces.ace.event.SelectEvent;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Henkilö;
import fi.budokwai.isoveli.malli.Rooli;
import fi.budokwai.isoveli.malli.Vyöarvo;

@Named
@SessionScoped
@Stateful
public class PerustietoAdmin extends Perustoiminnallisuus
{
   private Rooli rooli;
   private Vyöarvo vyöarvo;

   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   private List<Rooli> roolit;
   private List<Vyöarvo> vyöarvot;
   private List<Harrastaja> vyöarvoKäyttö;
   private List<Henkilö> rooliKäyttö;

   @PostConstruct
   public void alusta()
   {
      haeRoolit();
      haeVyöarvot();
   }

   @Produces
   @Named
   public List<Rooli> getKaikkiRoolit()
   {
      return roolit;
   }

   @Produces
   @Named
   public List<Vyöarvo> getKaikkiVyöarvot()
   {
      return vyöarvot;
   }

   @Produces
   @Named
   public Rooli getRooli()
   {
      return rooli;
   }

   @Produces
   @Named
   public Vyöarvo getVyöarvo()
   {
      return vyöarvo;
   }

   public void peruutaMuutos()
   {
      rooli = null;
      vyöarvo = null;
      virhe("Muutokset peruttu");
   }

   public void piilotaRooli()
   {
      rooli = null;
   }

   public void piilotaVyöarvo()
   {
      vyöarvo = null;
   }

   public void lisääRooli()
   {
      rooli = new Rooli();
      info("Uusi rooli alustettu");
   }

   public void lisääVyöarvo()
   {
      vyöarvo = new Vyöarvo();
      info("Uusi vyöarvo alustettu");
   }

   public void tallennaRooli()
   {
      entityManager.persist(rooli);
      haeRoolit();
      info("Rooli tallennettu");
   }

   public void tallennaVyöarvo()
   {
      entityManager.persist(vyöarvo);
      haeVyöarvot();
      info("Vyöarvo tallennettu");
   }

   private void haeRoolit()
   {
      roolit = entityManager.createNamedQuery("roolit", Rooli.class).getResultList();
   }

   private void haeVyöarvot()
   {
      vyöarvot = entityManager.createNamedQuery("vyöarvot", Vyöarvo.class).getResultList();
   }

   public void poistaRooli()
   {
      entityManager.remove(rooli);
      haeRoolit();
      info("Rooli poistettu");
   }

   public void poistaVyöarvo()
   {
      entityManager.remove(vyöarvo);
      haeVyöarvot();
      info("Vyöarvo poistettu");
   }

   @Produces
   @Named
   public List<Henkilö> getRooliKäyttö()
   {
      if (rooli == null || !rooli.isPoistettavissa())
      {
         return Collections.emptyList();
      }
      if (rooliKäyttö == null)
      {
         rooliKäyttö = entityManager.createNamedQuery("roolikäyttö", Henkilö.class).setParameter("rooli", rooli)
            .getResultList();
      }
      return rooliKäyttö;
   }

   @Produces
   @Named
   public List<Harrastaja> getVyöarvoKäyttö()
   {
      if (vyöarvo == null || !vyöarvo.isPoistettavissa())
      {
         return Collections.emptyList();
      }
      if (vyöarvoKäyttö == null)
      {
         vyöarvoKäyttö = entityManager.createNamedQuery("vyöarvokäyttö", Harrastaja.class)
            .setParameter("vyöarvo", vyöarvo).getResultList();
      }
      return vyöarvoKäyttö;
   }

   public void rooliValittu(SelectEvent e)
   {
      rooli = (Rooli) e.getObject();
      rooliKäyttö = null;
   }

   public void vyöarvoValittu(SelectEvent e)
   {
      vyöarvo = (Vyöarvo) e.getObject();
      vyöarvoKäyttö = null;
   }

}
