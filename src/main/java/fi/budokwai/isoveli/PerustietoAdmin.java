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
import fi.budokwai.isoveli.malli.Treeni;
import fi.budokwai.isoveli.malli.Treenityyppi;
import fi.budokwai.isoveli.malli.Vyöarvo;

@Named
@SessionScoped
@Stateful
public class PerustietoAdmin extends Perustoiminnallisuus
{
   private Rooli rooli;
   private Vyöarvo vyöarvo;
   private Treenityyppi treenityyppi;

   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   private List<Rooli> roolit;
   private List<Vyöarvo> vyöarvot;
   private List<Treenityyppi> treenityypit;
   private List<Harrastaja> vyöarvoKäyttö;
   private List<Henkilö> rooliKäyttö;
   private List<Treeni> treenityyppiKäyttö;

   @PostConstruct
   public void alusta()
   {
      haeRoolit();
      haeVyöarvot();
      haeTreenityypit();
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
   public List<Treenityyppi> getKaikkiTreenityypit()
   {
      return treenityypit;
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

   @Produces
   @Named
   public Treenityyppi getTreenityyppi()
   {
      return treenityyppi;
   }

   public void peruutaMuutos()
   {
      rooli = null;
      vyöarvo = null;
      treenityyppi = null;
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

   public void piilotaTreenityyppi()
   {
      treenityyppi = null;
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

   public void lisääTreenityyppi()
   {
      treenityyppi = new Treenityyppi();
      info("Uusi treenityyppi alustettu");
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

   public void tallennaTreenityyppi()
   {
      entityManager.persist(treenityyppi);
      haeTreenityypit();
      info("Treenityyppi tallennettu");
   }

   private void haeRoolit()
   {
      roolit = entityManager.createNamedQuery("roolit", Rooli.class).getResultList();
   }

   private void haeVyöarvot()
   {
      vyöarvot = entityManager.createNamedQuery("vyöarvot", Vyöarvo.class).getResultList();
   }

   private void haeTreenityypit()
   {
      treenityypit = entityManager.createNamedQuery("treenityypit", Treenityyppi.class).getResultList();
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

   public void poistaTreenityyppi()
   {
      entityManager.remove(treenityyppi);
      haeTreenityypit();
      info("Treenityyppi poistettu");
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

   @Produces
   @Named
   public List<Treeni> getTreenityyppiKäyttö()
   {
      if (treenityyppi == null || !treenityyppi.isPoistettavissa())
      {
         return Collections.emptyList();
      }
      if (treenityyppiKäyttö == null)
      {
         treenityyppiKäyttö = entityManager.createNamedQuery("treenityyppikäyttö", Treeni.class)
            .setParameter("treenityyppi", treenityyppi).getResultList();
      }
      return treenityyppiKäyttö;
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
   
   public void treenityyppiValittu(SelectEvent e)
   {
      treenityyppi = (Treenityyppi) e.getObject();
      treenityyppiKäyttö = null;
   }   

}
