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
import fi.budokwai.isoveli.malli.Henkilö;
import fi.budokwai.isoveli.malli.Rooli;
import fi.budokwai.isoveli.malli.Sopimus;
import fi.budokwai.isoveli.malli.Sopimustyyppi;
import fi.budokwai.isoveli.malli.Treeni;
import fi.budokwai.isoveli.malli.Treenisessio;
import fi.budokwai.isoveli.malli.Treenityyppi;
import fi.budokwai.isoveli.malli.Viikonpäivä;
import fi.budokwai.isoveli.malli.Vyöarvo;
import fi.budokwai.isoveli.malli.Vyökoe;
import fi.budokwai.isoveli.util.DateUtil;
import fi.budokwai.isoveli.util.Loggaaja;
import fi.budokwai.isoveli.util.Muuttui;

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

   @Inject
   private EntityManager entityManager;

   private List<Rooli> roolit;
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

   @Inject
   private Loggaaja loggaaja;

   @Inject
   @Muuttui
   private Event<Vyöarvo> vyöarvoMuuttui;

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
   public List<Harrastaja> getVetäjät()
   {
      if (treeni == null)
      {
         return new ArrayList<Harrastaja>();
      }
      if (kaikkivetäjät == null)
      {
         kaikkivetäjät = entityManager.createNamedQuery("treenivetäjät", Harrastaja.class).getResultList();
         kaikkivetäjät = kaikkivetäjät.stream().filter(h -> h.isTreenienVetäjä()).collect(Collectors.toList());
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
      if (treeni.isRajatRistissä())
      {
         throw new IsoveliPoikkeus("Alaikäraja ei voi olla korkeampi kun yläraja");
      }
      if (treeni.isVyörajatRistissä())
      {
         throw new IsoveliPoikkeus("Vyöalaraja ei voi olla korkeampi kun yläraja");
      }
      if (treeni.isVoimassaoloRistissä())
      {
         throw new IsoveliPoikkeus("Alkamispäivä ei voi olla päättymispäivän jälkeen");
      }
      if (treeni.isAjankohtaRistissä())
      {
         throw new IsoveliPoikkeus("Treeni ei voi päättyä ennen kun se alkaa");
      }
      treeni = entityManager.merge(treeni);
      entityManager.flush();
      treeniRSM.get(treeni).setSelected(true);
      treenit = null;
      info("Treeni tallennettu");
      loggaaja.loggaa(String.format("Tallensi treenin %s", treeni));
   }

   public void tallennaVyöarvo()
   {
      vyöarvo = entityManager.merge(vyöarvo);
      entityManager.flush();
      vyöarvoRSM.get(vyöarvo).setSelected(true);
      vyöarvoMuuttui.fire(vyöarvo);
      info("Vyöarvo tallennettu");
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
      tarkistaRoolikäyttö();
      rooli = entityManager.merge(rooli);
      entityManager.remove(rooli);
      entityManager.flush();
      roolit = null;
      info("Rooli poistettu");
   }

   private void tarkistaRoolikäyttö()
   {
      List<Henkilö> käyttö = entityManager
         .createQuery("select h from Henkilö h where :rooli member of h.roolit", Henkilö.class)
         .setParameter("rooli", rooli).getResultList();
      if (!käyttö.isEmpty())
      {
         StringJoiner stringJoiner = new StringJoiner(", ");
         käyttö.forEach(h -> stringJoiner.add(h.getNimi()));
         String viesti = String.format("Rooli on käytössä ja sitä ei voi poistaa (%dkpl: %s...)", käyttö.size(),
            stringJoiner.toString());
         throw new IsoveliPoikkeus(viesti);
      }
   }

   public void poistaTreeni()
   {
      tarkistaTreenikäyttö();
      treeni = entityManager.merge(treeni);
      entityManager.remove(treeni);
      entityManager.flush();
      treenit = null;
      info("Treeni poistettu");
   }

   private void tarkistaTreenikäyttö()
   {
      List<Treenisessio> käyttö = entityManager
         .createQuery("select ts from Treenisessio ts where ts.treeni=:treeni", Treenisessio.class)
         .setParameter("treeni", treeni).getResultList();
      if (!käyttö.isEmpty())
      {
         StringJoiner stringJoiner = new StringJoiner(", ");
         käyttö.forEach(ts -> stringJoiner.add(DateUtil.päiväTekstiksi(ts.getPäivä())));
         String viesti = String.format("Treeni on käytössä ja sitä ei voi poistaa (%dkpl: %s...)", käyttö.size(),
            stringJoiner.toString());
         throw new IsoveliPoikkeus(viesti);
      }
   }

   public void poistaVyöarvo()
   {
      tarkistaVyöarvoKäyttö();
      vyöarvo = entityManager.merge(vyöarvo);
      entityManager.remove(vyöarvo);
      entityManager.flush();
      vyöarvoMuuttui.fire(vyöarvo);
      info("Vyöarvo poistettu");
   }

   private void tarkistaVyöarvoKäyttö()
   {
      List<Vyökoe> käyttö = entityManager
         .createQuery("select vk from Vyökoe vk where vk.vyöarvo=:vyöarvo", Vyökoe.class)
         .setParameter("vyöarvo", vyöarvo).getResultList();
      if (!käyttö.isEmpty())
      {
         StringJoiner stringJoiner = new StringJoiner(", ");
         käyttö.forEach(v -> stringJoiner.add(v.getHarrastaja().getNimi()));
         String viesti = String.format("Vyöarvo on käytössä ja sitä ei voi poistaa (%dkpl: %s...)", käyttö.size(),
            stringJoiner.toString());
         throw new IsoveliPoikkeus(viesti);
      }
   }

   public void poistaTreenityyppi()
   {
      tarkistaTreenityyppiKäyttö();
      treenityyppi = entityManager.merge(treenityyppi);
      entityManager.remove(treenityyppi);
      entityManager.flush();
      treenityypit = null;
      info("Treenityyppi poistettu");
   }

   private void tarkistaTreenityyppiKäyttö()
   {
      List<Treeni> käyttö = entityManager.createQuery("select t from Treeni t where t.tyyppi=:tyyppi", Treeni.class)
         .setParameter("tyyppi", treenityyppi).getResultList();
      if (!käyttö.isEmpty())
      {
         StringJoiner stringJoiner = new StringJoiner(", ");
         käyttö.forEach(t -> stringJoiner.add(t.getNimi()));
         String viesti = String.format("Treenityyppi on käytössä ja sitä ei voi poistaa (%dkpl: %s...)", käyttö.size(),
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
   public List<Henkilö> getRooliKäyttö()
   {
      if (rooli == null || !rooli.isPoistettavissa())
      {
         return new ArrayList<Henkilö>();
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
         return new ArrayList<Treenisessio>();
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
         return new ArrayList<Harrastaja>();
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
         return new ArrayList<Sopimus>();
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
         return new ArrayList<Treeni>();
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

   public void setVyöarvo(Vyöarvo vyöarvo)
   {
      this.vyöarvo = vyöarvo;
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
