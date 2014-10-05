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
import fi.budokwai.isoveli.malli.Vy�arvo;

@Named
@SessionScoped
@Stateful
public class PerustietoAdmin extends Perustoiminnallisuus
{
   private Rooli rooli;
   private Vy�arvo vy�arvo;

   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   private List<Rooli> roolit;
   private List<Vy�arvo> vy�arvot;
   private List<Harrastaja> vy�arvoK�ytt�;
   private List<Henkil�> rooliK�ytt�;

   @PostConstruct
   public void alusta()
   {
      haeRoolit();
      haeVy�arvot();
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

   public void peruutaMuutos()
   {
      rooli = null;
      vy�arvo = null;
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

   private void haeRoolit()
   {
      roolit = entityManager.createNamedQuery("roolit", Rooli.class).getResultList();
   }

   private void haeVy�arvot()
   {
      vy�arvot = entityManager.createNamedQuery("vy�arvot", Vy�arvo.class).getResultList();
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

}
