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
import fi.budokwai.isoveli.malli.Henkil�;
import fi.budokwai.isoveli.malli.Rooli;
import fi.budokwai.isoveli.malli.Treeni;
import fi.budokwai.isoveli.malli.Treenityyppi;
import fi.budokwai.isoveli.malli.Vy�arvo;

@Named
@SessionScoped
@Stateful
public class PerustietoAdmin extends Perustoiminnallisuus
{
   private Rooli rooli;
   private Vy�arvo vy�arvo;
   private Treenityyppi treenityyppi;

   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   private List<Rooli> roolit;
   private List<Vy�arvo> vy�arvot;
   private List<Treenityyppi> treenityypit;
   private List<Harrastaja> vy�arvoK�ytt�;
   private List<Henkil�> rooliK�ytt�;
   private List<Treeni> treenityyppiK�ytt�;

   @PostConstruct
   public void alusta()
   {
      haeRoolit();
      haeVy�arvot();
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
   public List<Vy�arvo> getKaikkiVy�arvot()
   {
      return vy�arvot;
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
   public Vy�arvo getVy�arvo()
   {
      return vy�arvo;
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
      vy�arvo = null;
      treenityyppi = null;
      virhe("Muutokset peruttu");
   }

   public void piilotaRooli()
   {
      rooli = null;
   }

   public void piilotaVy�arvo()
   {
      vy�arvo = null;
   }

   public void piilotaTreenityyppi()
   {
      treenityyppi = null;
   }

   public void lis��Rooli()
   {
      rooli = new Rooli();
      info("Uusi rooli alustettu");
   }

   public void lis��Vy�arvo()
   {
      vy�arvo = new Vy�arvo();
      info("Uusi vy�arvo alustettu");
   }

   public void lis��Treenityyppi()
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

   public void tallennaVy�arvo()
   {
      entityManager.persist(vy�arvo);
      haeVy�arvot();
      info("Vy�arvo tallennettu");
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

   private void haeVy�arvot()
   {
      vy�arvot = entityManager.createNamedQuery("vy�arvot", Vy�arvo.class).getResultList();
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

   public void poistaVy�arvo()
   {
      entityManager.remove(vy�arvo);
      haeVy�arvot();
      info("Vy�arvo poistettu");
   }

   public void poistaTreenityyppi()
   {
      entityManager.remove(treenityyppi);
      haeTreenityypit();
      info("Treenityyppi poistettu");
   }

   @Produces
   @Named
   public List<Henkil�> getRooliK�ytt�()
   {
      if (rooli == null || !rooli.isPoistettavissa())
      {
         return Collections.emptyList();
      }
      if (rooliK�ytt� == null)
      {
         rooliK�ytt� = entityManager.createNamedQuery("roolik�ytt�", Henkil�.class).setParameter("rooli", rooli)
            .getResultList();
      }
      return rooliK�ytt�;
   }

   @Produces
   @Named
   public List<Harrastaja> getVy�arvoK�ytt�()
   {
      if (vy�arvo == null || !vy�arvo.isPoistettavissa())
      {
         return Collections.emptyList();
      }
      if (vy�arvoK�ytt� == null)
      {
         vy�arvoK�ytt� = entityManager.createNamedQuery("vy�arvok�ytt�", Harrastaja.class)
            .setParameter("vy�arvo", vy�arvo).getResultList();
      }
      return vy�arvoK�ytt�;
   }

   @Produces
   @Named
   public List<Treeni> getTreenityyppiK�ytt�()
   {
      if (treenityyppi == null || !treenityyppi.isPoistettavissa())
      {
         return Collections.emptyList();
      }
      if (treenityyppiK�ytt� == null)
      {
         treenityyppiK�ytt� = entityManager.createNamedQuery("treenityyppik�ytt�", Treeni.class)
            .setParameter("treenityyppi", treenityyppi).getResultList();
      }
      return treenityyppiK�ytt�;
   }

   public void rooliValittu(SelectEvent e)
   {
      rooli = (Rooli) e.getObject();
      rooliK�ytt� = null;
   }

   public void vy�arvoValittu(SelectEvent e)
   {
      vy�arvo = (Vy�arvo) e.getObject();
      vy�arvoK�ytt� = null;
   }
   
   public void treenityyppiValittu(SelectEvent e)
   {
      treenityyppi = (Treenityyppi) e.getObject();
      treenityyppiK�ytt� = null;
   }   

}
