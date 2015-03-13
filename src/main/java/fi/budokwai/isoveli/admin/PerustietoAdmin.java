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

   public void lis��Rooli()
   {
      rooli = new Rooli();
      rooliRSM = new RowStateMap();
      info("Uusi rooli alustettu");
   }

   public void lis��Vy�arvo()
   {
      vy�arvo = new Vy�arvo();
      vy�arvoRSM = new RowStateMap();
      info("Uusi vy�arvo alustettu");
   }

   public void lis��Treeni()
   {
      treeni = new Treeni();
      treenivet�j�t = null;
      treeniRSM.setAllSelected(false);
      info("Uusi treeni alustettu");
   }

   public void lis��Treenityyppi()
   {
      treenityyppi = new Treenityyppi();
      treenityyppiRSM = new RowStateMap();
      info("Uusi treenityyppi alustettu");
   }

   public void lis��Sopimustyyppi()
   {
      sopimustyyppi = new Sopimustyyppi();
      sopimustyyppiRSM = new RowStateMap();
      info("Uusi sopimustyyppi alustettu");
   }

   public void tallennaRooli()
   {
      rooli = entityManager.merge(rooli);
      entityManager.flush();
      rooliRSM.get(rooli).setSelected(true);
      roolit = null;
      info("Rooli tallennettu");
      loggaaja.loggaa("Tallensi roolin '%s'", rooli);
   }

   public void tallennaSopimustyyppi()
   {
      sopimustyyppi = entityManager.merge(sopimustyyppi);
      entityManager.flush();
      rooliRSM.get(sopimustyyppi).setSelected(true);
      sopimustyypit = null;
      info("Sopimustyyppi tallennettu");
      loggaaja.loggaa("Tallensi sopimustyypin '%s'", sopimustyyppi);
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
      loggaaja.loggaa("Tallensi treenin '%s'", treeni);
   }

   public void tallennaVy�arvo()
   {
      vy�arvo = entityManager.merge(vy�arvo);
      entityManager.flush();
      vy�arvoRSM.get(vy�arvo).setSelected(true);
      vy�arvoMuuttui.fire(vy�arvo);
      info("Vy�arvo tallennettu");
      loggaaja.loggaa("Tallensi vy�arvon '%s'", vy�arvo);
   }

   public void tallennaTreenityyppi()
   {
      treenityyppi = entityManager.merge(treenityyppi);
      entityManager.flush();
      treenityyppiRSM.get(treenityyppi).setSelected(true);
      treenityypit = null;
      info("Treenityyppi tallennettu");
      loggaaja.loggaa("Tallensi treenityyin '%s'", treenityyppi);
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
      if (rooli.isTallentamaton())
      {
         rooli = null;
         return;
      }
      tarkistaRoolik�ytt�();
      rooli = entityManager.merge(rooli);
      entityManager.remove(rooli);
      entityManager.flush();
      roolit = null;
      rooli = null;
      info("Rooli poistettu");
      loggaaja.loggaa("Poisti roolin '%s'", rooli);
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
      if (treeni.isTallentamaton())
      {
         treeni = null;
         return;
      }
      tarkistaTreenik�ytt�();
      treeni = entityManager.merge(treeni);
      entityManager.remove(treeni);
      entityManager.flush();
      treenit = null;
      treeni = null;
      info("Treeni poistettu");
      loggaaja.loggaa("Poisti treenin '%s'", treeni);
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
      if (vy�arvo.isTallentamaton())
      {
         vy�arvo = null;
         return;
      }
      tarkistaVy�arvoK�ytt�();
      vy�arvo = entityManager.merge(vy�arvo);
      entityManager.remove(vy�arvo);
      entityManager.flush();
      vy�arvoMuuttui.fire(vy�arvo);
      vy�arvo = null;
      info("Vy�arvo poistettu");
      loggaaja.loggaa("Poisti vy�arvon '%s'", vy�arvo);
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
      if (treenityyppi.isTallentamaton())
      {
         treenityyppi = null;
         return;
      }
      tarkistaTreenityyppiK�ytt�();
      treenityyppi = entityManager.merge(treenityyppi);
      entityManager.remove(treenityyppi);
      entityManager.flush();
      treenityypit = null;
      treenityyppi = null;
      info("Treenityyppi poistettu");
      loggaaja.loggaa("Poisti treenityypin '%s'", treenityyppi);
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
      if (sopimustyyppi.isTallentamaton())
      {
         sopimustyyppi = null;
         return;
      }
      tarkistaSopimustyyppik�ytt�();
      sopimustyyppi = entityManager.merge(sopimustyyppi);
      entityManager.remove(sopimustyyppi);
      entityManager.flush();
      sopimustyypit = null;
      sopimustyyppi = null;
      info("Sopimustyyppi poistettu");
      loggaaja.loggaa("Poisti sopimustyypin '%s'", sopimustyyppi);
   }

   private void tarkistaSopimustyyppik�ytt�()
   {
      List<Sopimus> k�ytt� = entityManager.createQuery("select s from Sopimus s where s.tyyppi=:tyyppi", Sopimus.class)
         .setParameter("tyyppi", sopimustyyppi).getResultList();
      if (!k�ytt�.isEmpty())
      {
         StringJoiner stringJoiner = new StringJoiner(", ");
         k�ytt�.forEach(s -> stringJoiner.add(s.getHarrastaja().getNimi()));
         String viesti = String.format("Sopimustyyppi on k�yt�ss� ja sit� ei voi poistaa (%dkpl: %s...)",
            k�ytt�.size(), stringJoiner.toString());
         throw new IsoveliPoikkeus(viesti);
      }
   }

   public void rooliValittu(SelectEvent e)
   {
      rooli = (Rooli) e.getObject();
   }

   public void treeniValittu(SelectEvent e)
   {
      treeni = (Treeni) e.getObject();
      treenivet�j�t = null;
   }

   public void vy�arvoValittu(SelectEvent e)
   {
      vy�arvo = (Vy�arvo) e.getObject();
   }

   public void treenityyppiValittu(SelectEvent e)
   {
      treenityyppi = (Treenityyppi) e.getObject();
   }

   public void sopimustyyppiValittu(SelectEvent e)
   {
      sopimustyyppi = (Sopimustyyppi) e.getObject();
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

   public void setSopimustyyppi(Sopimustyyppi sopimustyyppi)
   {
      this.sopimustyyppi = sopimustyyppi;
   }

}
