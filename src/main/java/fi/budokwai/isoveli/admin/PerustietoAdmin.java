package fi.budokwai.isoveli.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.icefaces.ace.event.SelectEvent;
import org.icefaces.ace.model.table.RowStateMap;

import fi.budokwai.isoveli.IsoveliPoikkeus;
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
import fi.budokwai.isoveli.malli.Vy�koe;
import fi.budokwai.isoveli.util.DateUtil;
import fi.budokwai.isoveli.util.Loggaaja;
import fi.budokwai.isoveli.util.Muuttui;

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

   @Inject
   private EntityManager entityManager;

   private List<Rooli> roolit;
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

   @Inject
   private Loggaaja loggaaja;

   @Inject
   @Muuttui
   private Event<Vy�arvo> vy�arvoMuuttui;

   @PostConstruct
   public void alusta()
   {
      haeRoolit();
      haeTreenityypit();
      haeTreenit();
      haeSopimustyypit();
   }

   @Produces
   @Named
   public List<Rooli> getRoolit()
   {
      if (roolit == null)
      {
         haeRoolit();
      }
      return roolit;
   }

   @Produces
   @Named
   public List<Treeni> getTreenit()
   {
      if (treenit == null)
      {
         haeTreenit();
      }
      return treenit;
   }

   @Produces
   @Named
   public List<Treenityyppi> getTreenityypit()
   {
      if (treenityypit == null)
      {
         haeTreenityypit();
      }
      return treenityypit;
   }

   @Produces
   @Named
   public List<Sopimustyyppi> getSopimustyypit()
   {
      if (sopimustyypit == null)
      {
         haeSopimustyypit();
      }
      return sopimustyypit;
   }

   @Produces
   @Named
   public List<Harrastaja> getVet�j�t()
   {
      if (treeni == null)
      {
         return new ArrayList<Harrastaja>();
      }
      if (kaikkivet�j�t == null)
      {
         kaikkivet�j�t = entityManager.createNamedQuery("treenivet�j�t", Harrastaja.class).getResultList();
         kaikkivet�j�t = kaikkivet�j�t.stream().filter(h -> h.isTreenienVet�j�()).collect(Collectors.toList());
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

   // @Produces
   // @Named
   // public List<Rooli> getKaikkiRoolit()
   // {
   // return roolit;
   // }
   //
   // @Produces
   // @Named
   // public List<Treenityyppi> getKaikkiTreenityypit()
   // {
   // return treenityypit;
   // }
   //
   // @Produces
   // @Named
   // public List<Sopimustyyppi> getKaikkiSopimustyypit()
   // {
   // return sopimustyypit;
   // }

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
      rooli = entityManager.merge(rooli);
      entityManager.flush();
      rooliRSM.get(rooli).setSelected(true);
      roolit = null;
      info("Rooli tallennettu");
   }

   public void tallennaSopimustyyppi()
   {
      sopimustyyppi = entityManager.merge(sopimustyyppi);
      rooliRSM.get(sopimustyyppi).setSelected(true);
      sopimustyypit = null;
      info("Sopimustyyppi tallennettu");
   }

   public void tallennaTreeni()
   {
      if (treeni.isRajatRistiss�())
      {
         throw new IsoveliPoikkeus("Alaik�raja ei voi olla korkeampi kun yl�raja");
      }
      if (treeni.isVy�rajatRistiss�())
      {
         throw new IsoveliPoikkeus("Vy�alaraja ei voi olla korkeampi kun yl�raja");
      }
      if (treeni.isVoimassaoloRistiss�())
      {
         throw new IsoveliPoikkeus("Alkamisp�iv� ei voi olla p��ttymisp�iv�n j�lkeen");
      }
      if (treeni.isAjankohtaRistiss�())
      {
         throw new IsoveliPoikkeus("Treeni ei voi p��tty� ennen kun se alkaa");
      }
      treeni = entityManager.merge(treeni);
      entityManager.flush();
      treeniRSM.get(treeni).setSelected(true);
      treenit = null;
      info("Treeni tallennettu");
      loggaaja.loggaa(String.format("Tallensi treenin %s", treeni));
   }

   public void tallennaVy�arvo()
   {
      vy�arvo = entityManager.merge(vy�arvo);
      entityManager.flush();
      vy�arvoRSM.get(vy�arvo).setSelected(true);
      vy�arvoMuuttui.fire(vy�arvo);
      info("Vy�arvo tallennettu");
   }

   public void tallennaTreenityyppi()
   {
      treenityyppi = entityManager.merge(treenityyppi);
      entityManager.flush();
      treenityyppiRSM.get(treenityyppi).setSelected(true);
      treenityypit = null;
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

   private void haeTreenityypit()
   {
      treenityypit = entityManager.createNamedQuery("treenityypit", Treenityyppi.class).getResultList();
   }

   public void poistaRooli()
   {
      tarkistaRoolik�ytt�();
      rooli = entityManager.merge(rooli);
      entityManager.remove(rooli);
      entityManager.flush();
      roolit = null;
      info("Rooli poistettu");
   }

   private void tarkistaRoolik�ytt�()
   {
      List<Henkil�> k�ytt� = entityManager
         .createQuery("select h from Henkil� h where :rooli member of h.roolit", Henkil�.class)
         .setParameter("rooli", rooli).getResultList();
      if (!k�ytt�.isEmpty())
      {
         StringJoiner stringJoiner = new StringJoiner(", ");
         k�ytt�.forEach(h -> stringJoiner.add(h.getNimi()));
         String viesti = String.format("Rooli on k�yt�ss� ja sit� ei voi poistaa (%dkpl: %s...)", k�ytt�.size(),
            stringJoiner.toString());
         throw new IsoveliPoikkeus(viesti);
      }
   }

   public void poistaTreeni()
   {
      tarkistaTreenik�ytt�();
      treeni = entityManager.merge(treeni);
      entityManager.remove(treeni);
      entityManager.flush();
      treenit = null;
      info("Treeni poistettu");
   }

   private void tarkistaTreenik�ytt�()
   {
      List<Treenisessio> k�ytt� = entityManager
         .createQuery("select ts from Treenisessio ts where ts.treeni=:treeni", Treenisessio.class)
         .setParameter("treeni", treeni).getResultList();
      if (!k�ytt�.isEmpty())
      {
         StringJoiner stringJoiner = new StringJoiner(", ");
         k�ytt�.forEach(ts -> stringJoiner.add(DateUtil.p�iv�Tekstiksi(ts.getP�iv�())));
         String viesti = String.format("Treeni on k�yt�ss� ja sit� ei voi poistaa (%dkpl: %s...)", k�ytt�.size(),
            stringJoiner.toString());
         throw new IsoveliPoikkeus(viesti);
      }
   }

   public void poistaVy�arvo()
   {
      tarkistaVy�arvoK�ytt�();
      vy�arvo = entityManager.merge(vy�arvo);
      entityManager.remove(vy�arvo);
      entityManager.flush();
      vy�arvoMuuttui.fire(vy�arvo);
      info("Vy�arvo poistettu");
   }

   private void tarkistaVy�arvoK�ytt�()
   {
      List<Vy�koe> k�ytt� = entityManager
         .createQuery("select vk from Vy�koe vk where vk.vy�arvo=:vy�arvo", Vy�koe.class)
         .setParameter("vy�arvo", vy�arvo).getResultList();
      if (!k�ytt�.isEmpty())
      {
         StringJoiner stringJoiner = new StringJoiner(", ");
         k�ytt�.forEach(v -> stringJoiner.add(v.getHarrastaja().getNimi()));
         String viesti = String.format("Vy�arvo on k�yt�ss� ja sit� ei voi poistaa (%dkpl: %s...)", k�ytt�.size(),
            stringJoiner.toString());
         throw new IsoveliPoikkeus(viesti);
      }
   }

   public void poistaTreenityyppi()
   {
      tarkistaTreenityyppiK�ytt�();
      treenityyppi = entityManager.merge(treenityyppi);
      entityManager.remove(treenityyppi);
      entityManager.flush();
      treenityypit = null;
      info("Treenityyppi poistettu");
   }

   private void tarkistaTreenityyppiK�ytt�()
   {
      List<Treeni> k�ytt� = entityManager.createQuery("select t from Treeni t where t.tyyppi=:tyyppi", Treeni.class)
         .setParameter("tyyppi", treenityyppi).getResultList();
      if (!k�ytt�.isEmpty())
      {
         StringJoiner stringJoiner = new StringJoiner(", ");
         k�ytt�.forEach(t -> stringJoiner.add(t.getNimi()));
         String viesti = String.format("Treenityyppi on k�yt�ss� ja sit� ei voi poistaa (%dkpl: %s...)", k�ytt�.size(),
            stringJoiner.toString());
         throw new IsoveliPoikkeus(viesti);
      }
   }

   public void poistaSopimustyyppi()
   {
      sopimustyyppi = entityManager.merge(sopimustyyppi);
      entityManager.remove(sopimustyyppi);
      sopimustyypit = null;
      info("Sopimustyyppi poistettu");
   }

   @Produces
   @Named
   public List<Henkil�> getRooliK�ytt�()
   {
      if (rooli == null || !rooli.isPoistettavissa())
      {
         return new ArrayList<Henkil�>();
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
         return new ArrayList<Treenisessio>();
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
         return new ArrayList<Harrastaja>();
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
         return new ArrayList<Sopimus>();
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
         return new ArrayList<Treeni>();
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

   public void setVy�arvo(Vy�arvo vy�arvo)
   {
      this.vy�arvo = vy�arvo;
   }

   public void setTreenityyppi(Treenityyppi treenityyppi)
   {
      this.treenityyppi = treenityyppi;
   }

   public void setTreeni(Treeni treeni)
   {
      this.treeni = treeni;
   }

   public void setRooli(Rooli rooli)
   {
      this.rooli = rooli;
   }

}
