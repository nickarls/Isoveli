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
import fi.budokwai.isoveli.malli.Henkilö;
import fi.budokwai.isoveli.malli.Rooli;
import fi.budokwai.isoveli.malli.Sopimus;
import fi.budokwai.isoveli.malli.Sopimustyyppi;
import fi.budokwai.isoveli.malli.Treeni;
import fi.budokwai.isoveli.malli.Treenisessio;
import fi.budokwai.isoveli.malli.Treenityyppi;
import fi.budokwai.isoveli.malli.Viikonpäivä;
import fi.budokwai.isoveli.malli.Vyöarvo;

@Named
@SessionScoped
@Stateful
public class PerustietoAdmin extends Perustoiminnallisuus
{
   private Rooli rooli;
   private Vyöarvo vyöarvo;
   private Treenityyppi treenityyppi;
   private Treeni treeni;
   private Sopimustyyppi sopimustyyppi;

   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   private List<Rooli> roolit;
   private List<Vyöarvo> vyöarvot;
   private List<Treenityyppi> treenityypit;
   private List<Harrastaja> vyöarvoKäyttö;
   private List<Henkilö> rooliKäyttö;
   private List<Treeni> treenityyppiKäyttö;
   private List<Treenisessio> treeniKäyttö;
   private List<Sopimus> sopimustyyppiKäyttö;
   private List<Harrastaja> treenivetäjät;
   private List<Harrastaja> kaikkivetäjät;
   private List<Treeni> treenit;
   private List<Sopimustyyppi> sopimustyypit;

   private RowStateMap treenityyppiRSM = new RowStateMap();
   private RowStateMap vyöarvoRSM = new RowStateMap();
   private RowStateMap rooliRSM = new RowStateMap();
   private RowStateMap treeniRSM = new RowStateMap();
   private RowStateMap sopimustyyppiRSM = new RowStateMap();

   @PostConstruct
   public void alusta()
   {
      haeRoolit();
      haeVyöarvot();
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
   public List<Harrastaja> getVetäjät()
   {
      if (treeni == null)
      {
         return Collections.emptyList();
      }
      if (kaikkivetäjät == null)
      {
         kaikkivetäjät = entityManager.createNamedQuery("treenivetäjät", Harrastaja.class).getResultList();
         List<Harrastaja> tulos = new ArrayList<Harrastaja>();
         for (Harrastaja harrastaja : kaikkivetäjät)
         {
            if (harrastaja.isTreenienVetäjä())
            {
               tulos.add(harrastaja);
            }
         }
         kaikkivetäjät = tulos;
      }
      if (treenivetäjät == null)
      {
         treenivetäjät = new ArrayList<Harrastaja>();
         treenivetäjät.addAll(kaikkivetäjät);
         treenivetäjät.removeAll(treeni.getVetäjät());
      }
      return treenivetäjät;
   }

   @Produces
   @Named
   public Treeni getTreeni()
   {
      return treeni;
   }

   @Produces
   @Named
   public List<SelectItem> getViikonpäivät()
   {
      List<SelectItem> tulos = new ArrayList<SelectItem>();
      for (Viikonpäivä viikonpaiva : Viikonpäivä.values())
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

   public void piilotaVyöarvo()
   {
      vyöarvo = null;
   }

   public void piilotaTreenityyppi()
   {
      treenityyppi = null;
   }

   public void piilotaTreeni()
   {
      treeni = null;
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

   public void lisääTreeni()
   {
      treeni = new Treeni();
      treenivetäjät = null;
      treeniKäyttö = null;
      treeniRSM.setAllSelected(false);
      info("Uusi treeni alustettu");
   }

   public void lisääTreenityyppi()
   {
      treenityyppi = new Treenityyppi();
      treenityyppiRSM = new RowStateMap();
      treenityyppiKäyttö = null;
      info("Uusi treenityyppi alustettu");
   }

   public void lisääSopimustyyppi()
   {
      sopimustyyppi = new Sopimustyyppi();
      sopimustyyppiRSM = new RowStateMap();
      sopimustyyppiKäyttö = null;
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

   public void poistaTreeni()
   {
      entityManager.remove(treeni);
      haeTreenit();
      info("Treeni poistettu");
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

   public void poistaSopimustyyppi()
   {
      entityManager.remove(sopimustyyppi);
      haeSopimustyypit();
      info("Sopimustyyppi poistettu");
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
   public List<Treenisessio> getTreeniKäyttö()
   {
      if (treeni == null || !treeni.isPoistettavissa())
      {
         return Collections.emptyList();
      }
      if (treeniKäyttö == null)
      {
         treeniKäyttö = entityManager.createNamedQuery("treenikäyttö", Treenisessio.class)
            .setParameter("treeni", treeni).getResultList();
      }
      return treeniKäyttö;
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
   public List<Sopimus> getSopimustyyppiKäyttö()
   {
      if (sopimustyyppi == null || !sopimustyyppi.isPoistettavissa())
      {
         return Collections.emptyList();
      }
      if (sopimustyyppiKäyttö == null)
      {
         sopimustyyppiKäyttö = entityManager.createNamedQuery("sopimustyyppikäyttö", Sopimus.class)
            .setParameter("sopimustyyppi", sopimustyyppi).getResultList();
      }
      return sopimustyyppiKäyttö;
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

   public void treeniValittu(SelectEvent e)
   {
      treeni = (Treeni) e.getObject();
      treenivetäjät = null;
      treeniKäyttö = null;
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

   public void sopimustyyppiValittu(SelectEvent e)
   {
      sopimustyyppi = (Sopimustyyppi) e.getObject();
      sopimustyyppiKäyttö = null;
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
