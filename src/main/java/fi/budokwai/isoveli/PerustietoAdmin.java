package fi.budokwai.isoveli;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.icefaces.ace.event.SelectEvent;
import org.icefaces.ace.model.table.RowStateMap;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Henkil�;
import fi.budokwai.isoveli.malli.Rooli;
import fi.budokwai.isoveli.malli.Sopimus;
import fi.budokwai.isoveli.malli.Sopimustyyppi;
import fi.budokwai.isoveli.malli.Treeni;
import fi.budokwai.isoveli.malli.Treenisessio;
import fi.budokwai.isoveli.malli.Treenityyppi;
import fi.budokwai.isoveli.malli.Viikonp�iv�;
import fi.budokwai.isoveli.malli.Vy�arvo;

@Named
@SessionScoped
@Stateful
public class PerustietoAdmin extends Perustoiminnallisuus
{
   private Rooli rooli;
   private Vy�arvo vy�arvo;
   private Treenityyppi treenityyppi;
   private Treeni treeni;
   private Sopimustyyppi sopimustyyppi;

   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   private List<Rooli> roolit;
   private List<Vy�arvo> vy�arvot;
   private List<Treenityyppi> treenityypit;
   private List<Harrastaja> vy�arvoK�ytt�;
   private List<Henkil�> rooliK�ytt�;
   private List<Treeni> treenityyppiK�ytt�;
   private List<Treenisessio> treeniK�ytt�;
   private List<Sopimus> sopimustyyppiK�ytt�;
   private List<Harrastaja> treenivet�j�t;
   private List<Harrastaja> kaikkivet�j�t;
   private List<Treeni> treenit;
   private List<Sopimustyyppi> sopimustyypit;

   private RowStateMap treenityyppiRSM = new RowStateMap();
   private RowStateMap vy�arvoRSM = new RowStateMap();
   private RowStateMap rooliRSM = new RowStateMap();
   private RowStateMap treeniRSM = new RowStateMap();
   private RowStateMap sopimustyyppiRSM = new RowStateMap();

   @PostConstruct
   public void alusta()
   {
      haeRoolit();
      haeVy�arvot();
      haeTreenityypit();
      haeTreenit();
      haeSopimustyypit();
   }

   @Produces
   @Named
   public Collection<Treeni> getTreenit()
   {
      return treenit;
   }

   @Produces
   @Named
   public List<Harrastaja> getVet�j�t()
   {
      if (treeni == null)
      {
         return Collections.emptyList();
      }
      if (kaikkivet�j�t == null)
      {
         kaikkivet�j�t = entityManager.createNamedQuery("treenivet�j�t", Harrastaja.class).getResultList();
         List<Harrastaja> tulos = new ArrayList<Harrastaja>();
         for (Harrastaja harrastaja : kaikkivet�j�t)
         {
            if (harrastaja.isTreenienVet�j�())
            {
               tulos.add(harrastaja);
            }
         }
         kaikkivet�j�t = tulos;
      }
      if (treenivet�j�t == null)
      {
         treenivet�j�t = new ArrayList<Harrastaja>();
         treenivet�j�t.addAll(kaikkivet�j�t);
         treenivet�j�t.removeAll(treeni.getVet�j�t());
      }
      return treenivet�j�t;
   }

   @Produces
   @Named
   public Treeni getTreeni()
   {
      return treeni;
   }

   @Produces
   @Named
   public List<SelectItem> getViikonp�iv�t()
   {
      List<SelectItem> tulos = new ArrayList<SelectItem>();
      for (Viikonp�iv� viikonpaiva : Viikonp�iv�.values())
      {
         tulos.add(new SelectItem(viikonpaiva, viikonpaiva.toString()));
      }
      return tulos;
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
   public List<Sopimustyyppi> getKaikkiSopimustyypit()
   {
      return sopimustyypit;
   }

   @Produces
   @Named
   public Sopimustyyppi getSopimustyyppi()
   {
      return sopimustyyppi;
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

   @Produces
   @Named
   public Collection<Treenityyppi> getTreenityypit()
   {
      if (treenityypit == null)
      {
         treenityypit = entityManager.createNamedQuery("treenityypit", Treenityyppi.class).getResultList();
      }
      return treenityypit;
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

   public void peruutaTreenimuutos()
   {
      if (treeni.isPoistettavissa())
      {
         entityManager.refresh(treeni);
      } else
      {
         treeni = null;
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

   public void peruutaVy�arvomuutos()
   {
      if (vy�arvo.isPoistettavissa())
      {
         entityManager.refresh(vy�arvo);
      } else
      {
         vy�arvo = null;
      }
      virhe("Muutokset peruttu");
   }

   public void peruutaSopimustyyppimuutos()
   {
      if (sopimustyyppi.isPoistettavissa())
      {
         entityManager.refresh(sopimustyyppi);
      } else
      {
         sopimustyyppi = null;
      }
      virhe("Muutokset peruttu");
   }

   public void piilotaRooli()
   {
      rooli = null;
   }

   public void piilotaSopimustyyppi()
   {
      sopimustyyppi = null;
   }

   public void piilotaVy�arvo()
   {
      vy�arvo = null;
   }

   public void piilotaTreenityyppi()
   {
      treenityyppi = null;
   }

   public void piilotaTreeni()
   {
      treeni = null;
   }

   public void lis��Rooli()
   {
      rooli = new Rooli();
      rooliRSM = new RowStateMap();
      rooliK�ytt� = null;
      info("Uusi rooli alustettu");
   }

   public void lis��Vy�arvo()
   {
      vy�arvo = new Vy�arvo();
      vy�arvoRSM = new RowStateMap();
      vy�arvoK�ytt� = null;
      info("Uusi vy�arvo alustettu");
   }

   public void lis��Treeni()
   {
      treeni = new Treeni();
      treenivet�j�t = null;
      treeniK�ytt� = null;
      treeniRSM.setAllSelected(false);
      info("Uusi treeni alustettu");
   }

   public void lis��Treenityyppi()
   {
      treenityyppi = new Treenityyppi();
      treenityyppiRSM = new RowStateMap();
      treenityyppiK�ytt� = null;
      info("Uusi treenityyppi alustettu");
   }

   public void lis��Sopimustyyppi()
   {
      sopimustyyppi = new Sopimustyyppi();
      sopimustyyppiRSM = new RowStateMap();
      sopimustyyppiK�ytt� = null;
      info("Uusi sopimustyyppi alustettu");
   }

   public void tallennaRooli()
   {
      entityManager.persist(rooli);
      rooliRSM.get(rooli).setSelected(true);
      haeRoolit();
      info("Rooli tallennettu");
   }

   public void tallennaSopimustyyppi()
   {
      entityManager.persist(sopimustyyppi);
      rooliRSM.get(sopimustyyppi).setSelected(true);
      haeSopimustyypit();
      info("Sopimustyyppi tallennettu");
   }

   public void tallennaTreeni()
   {
      entityManager.persist(treeni);
      treeniRSM.get(treeni).setSelected(true);
      haeTreenit();
      info("Treeni tallennettu");
   }

   public void tallennaVy�arvo()
   {
      entityManager.persist(vy�arvo);
      vy�arvoRSM.get(vy�arvo).setSelected(true);
      haeVy�arvot();
      info("Vy�arvo tallennettu");
   }

   public void tallennaTreenityyppi()
   {
      entityManager.persist(treenityyppi);
      treenityyppiRSM.get(treenityyppi).setSelected(true);
      haeTreenityypit();
      info("Treenityyppi tallennettu");
   }

   private void haeTreenit()
   {
      treenit = entityManager.createNamedQuery("treenit", Treeni.class).getResultList();
   }

   private void haeSopimustyypit()
   {
      sopimustyypit = entityManager.createNamedQuery("sopimustyypit", Sopimustyyppi.class).getResultList();
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

   public void poistaTreeni()
   {
      entityManager.remove(treeni);
      haeTreenit();
      info("Treeni poistettu");
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

   public void poistaSopimustyyppi()
   {
      entityManager.remove(sopimustyyppi);
      haeSopimustyypit();
      info("Sopimustyyppi poistettu");
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
   public List<Treenisessio> getTreeniK�ytt�()
   {
      if (treeni == null || !treeni.isPoistettavissa())
      {
         return Collections.emptyList();
      }
      if (treeniK�ytt� == null)
      {
         treeniK�ytt� = entityManager.createNamedQuery("treenik�ytt�", Treenisessio.class)
            .setParameter("treeni", treeni).getResultList();
      }
      return treeniK�ytt�;
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
   public List<Sopimus> getSopimustyyppiK�ytt�()
   {
      if (sopimustyyppi == null || !sopimustyyppi.isPoistettavissa())
      {
         return Collections.emptyList();
      }
      if (sopimustyyppiK�ytt� == null)
      {
         sopimustyyppiK�ytt� = entityManager.createNamedQuery("sopimustyyppik�ytt�", Sopimus.class)
            .setParameter("sopimustyyppi", sopimustyyppi).getResultList();
      }
      return sopimustyyppiK�ytt�;
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

   public void treeniValittu(SelectEvent e)
   {
      treeni = (Treeni) e.getObject();
      treenivet�j�t = null;
      treeniK�ytt� = null;
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

   public void sopimustyyppiValittu(SelectEvent e)
   {
      sopimustyyppi = (Sopimustyyppi) e.getObject();
      sopimustyyppiK�ytt� = null;
   }

   public RowStateMap getTreenityyppiRSM()
   {
      return treenityyppiRSM;
   }

   public void setTreenityyppiRSM(RowStateMap treenityyppiRSM)
   {
      this.treenityyppiRSM = treenityyppiRSM;
   }

   public RowStateMap getVy�arvoRSM()
   {
      return vy�arvoRSM;
   }

   public void setVy�arvoRSM(RowStateMap vy�arvoRSM)
   {
      this.vy�arvoRSM = vy�arvoRSM;
   }

   public RowStateMap getRooliRSM()
   {
      return rooliRSM;
   }

   public void setRooliRSM(RowStateMap rooliRSM)
   {
      this.rooliRSM = rooliRSM;
   }

   public RowStateMap getTreeniRSM()
   {
      return treeniRSM;
   }

   public void setTreeniRSM(RowStateMap treeniRSM)
   {
      this.treeniRSM = treeniRSM;
   }

   public RowStateMap getSopimustyyppiRSM()
   {
      return sopimustyyppiRSM;
   }

   public void setSopimustyyppiRSM(RowStateMap sopimustyyppiRSM)
   {
      this.sopimustyyppiRSM = sopimustyyppiRSM;
   }

}
