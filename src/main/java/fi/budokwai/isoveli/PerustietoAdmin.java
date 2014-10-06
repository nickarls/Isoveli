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
import org.icefaces.ace.model.table.RowStateMap;
import org.icefaces.ace.model.tree.NodeStateMap;

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

   private RowStateMap treenityyppiRSM = new RowStateMap();
   private RowStateMap vyöarvoRSM = new RowStateMap();
   private RowStateMap rooliRSM = new RowStateMap();

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

   public void peruutaRoolimuutos()
   {
      if (rooli.isPoistettavissa())
      {
         entityManager.refresh(rooli);
      } else
      {
         rooli = null;
      }
      virhe("Muutokset peruttu");
   }   
   
   public void peruutaTreenityyppimuutos()
   {
      if (treenityyppi.isPoistettavissa())
      {
         entityManager.refresh(treenityyppi);
      } else
      {
         treenityyppi = null;
      }
      virhe("Muutokset peruttu");
   }

   public void peruutaVyöarvomuutos()
   {
      if (vyöarvo.isPoistettavissa())
      {
         entityManager.refresh(vyöarvo);
      } else
      {
         vyöarvo = null;
      }
      virhe("Muutokset peruttu");
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
      rooliRSM = new RowStateMap();
      rooliKäyttö = null;
      info("Uusi rooli alustettu");
   }

   public void lisääVyöarvo()
   {
      vyöarvo = new Vyöarvo();
      vyöarvoRSM = new RowStateMap();
      vyöarvoKäyttö = null;
      info("Uusi vyöarvo alustettu");
   }

   public void lisääTreenityyppi()
   {
      treenityyppi = new Treenityyppi();
      treenityyppiRSM = new RowStateMap();
      treenityyppiKäyttö = null;
      info("Uusi treenityyppi alustettu");
   }

   public void tallennaRooli()
   {
      entityManager.persist(rooli);
      rooliRSM.get(rooli).setSelected(true);
      haeRoolit();
      info("Rooli tallennettu");
   }

   public void tallennaVyöarvo()
   {
      entityManager.persist(vyöarvo);
      vyöarvoRSM.get(vyöarvo).setSelected(true);
      haeVyöarvot();
      info("Vyöarvo tallennettu");
   }

   public void tallennaTreenityyppi()
   {
      entityManager.persist(treenityyppi);
      treenityyppiRSM.get(treenityyppi).setSelected(true);
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

   public RowStateMap getTreenityyppiRSM()
   {
      return treenityyppiRSM;
   }

   public void setTreenityyppiRSM(RowStateMap treenityyppiRSM)
   {
      this.treenityyppiRSM = treenityyppiRSM;
   }

   public RowStateMap getVyöarvoRSM()
   {
      return vyöarvoRSM;
   }

   public void setVyöarvoRSM(RowStateMap vyöarvoRSM)
   {
      this.vyöarvoRSM = vyöarvoRSM;
   }

   public RowStateMap getRooliRSM()
   {
      return rooliRSM;
   }

   public void setRooliRSM(RowStateMap rooliRSM)
   {
      this.rooliRSM = rooliRSM;
   }

}
