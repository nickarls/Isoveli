package fi.budokwai.isoveli.admin;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.icefaces.ace.component.datetimeentry.DateTimeEntry;
import org.icefaces.ace.component.tabset.TabSet;
import org.icefaces.ace.event.SelectEvent;
import org.icefaces.ace.model.table.RowStateMap;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Henkilö;
import fi.budokwai.isoveli.malli.Osoite;
import fi.budokwai.isoveli.malli.Perhe;
import fi.budokwai.isoveli.malli.Rooli;
import fi.budokwai.isoveli.malli.Sopimus;
import fi.budokwai.isoveli.malli.Sopimustyyppi;
import fi.budokwai.isoveli.malli.Sukupuoli;
import fi.budokwai.isoveli.malli.Vyöarvo;
import fi.budokwai.isoveli.malli.Vyökoe;
import fi.budokwai.isoveli.util.Loggaaja;
import fi.budokwai.isoveli.util.Muuttui;

@Named
@SessionScoped
@Stateful
public class HarrastajaAdmin extends Perustoiminnallisuus
{
   private List<Harrastaja> harrastajat;
   private List<Rooli> roolit;
   private List<Vyöarvo> vyöarvot;
   private List<Perhe> perheet;

   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   private TabSet tabi;

   private Harrastaja harrastaja;
   private Sopimus sopimus;
   private Vyökoe vyökoe;

   private RowStateMap harrastajaRSM = new RowStateMap();
   private RowStateMap vyökoeRSM = new RowStateMap();
   private RowStateMap sopimusRSM = new RowStateMap();

   @Inject
   private Loggaaja loggaaja;

   @Inject
   @Muuttui
   private Event<EntityManager> emMuuttui;

   public void päätabiMuuttui(ValueChangeEvent e)
   {
      int uusiTabi = (int) e.getNewValue();
      switch (uusiTabi)
      {
      case 0:
         resetoi();
      }
   }

   private void resetoi()
   {
      entityManager.clear();
      harrastajat = null;
      roolit = null;
      vyöarvot = null;
      perheet = null;
      harrastaja = null;
      sopimus = null;
      vyökoe = null;
      harrastajaRSM = new RowStateMap();
      vyökoeRSM = new RowStateMap();
      sopimusRSM = new RowStateMap();
      emMuuttui.fire(entityManager);
   }

   public void tilapäisyysMuuttui(AjaxBehaviorEvent e)
   {
      if (harrastaja.isTilapäinen())
      {
         harrastaja.muutaVakioksi();
         harrastaja.getSopimukset().add(teeKoeaikaSopimus());
      } else
      {
         harrastaja.muutaTilapäiseksi();
      }
   }

   private Sopimus teeKoeaikaSopimus()
   {
      Sopimus koeaika = new Sopimus();
      koeaika.setHarrastaja(harrastaja);
      Sopimustyyppi koeaikaSopimustyyppi = (Sopimustyyppi) entityManager.createNamedQuery("koeaikasopimus")
         .getSingleResult();
      koeaika.setTyyppi(koeaikaSopimustyyppi);
      koeaika.setTreenikertoja(koeaikaSopimustyyppi.getOletusTreenikerrat());
      LocalDate umpeutuu = LocalDate.now();
      umpeutuu = umpeutuu.plus(koeaikaSopimustyyppi.getOletusKuukaudetVoimassa(), ChronoUnit.MONTHS);
      koeaika.setUmpeutuu(Date.from(umpeutuu.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
      return koeaika;
   }

   public void perheMuuttui(ValueChangeEvent e)
   {
      Perhe uusiPerhe = (Perhe) e.getNewValue();
      if (uusiPerhe == null)
      {
         harrastaja.setOsoite(new Osoite());
      } else
      {
         harrastaja.setOsoite(null);
         if (uusiPerhe.getHuoltajat().size() > 0)
         {
            harrastaja.setHuoltaja(uusiPerhe.getHuoltajat().iterator().next());
         }
      }
      harrastaja.setOsoiteMuuttunut(true);
      fokusoi("form:osoite");
   }

   public void lisääPerhe()
   {
      Perhe perhe = new Perhe();
      perhe.setNimi(harrastaja.getSukunimi());
      perhe.getPerheenjäsenet().add(harrastaja);
      perhe.setOsoite(harrastaja.getOsoite());
      harrastaja.setOsoite(null);
      harrastaja.setPerhe(perhe);
      entityManager.persist(perhe);
      entityManager.flush();
      virkistäPerheet();
   }

   @PostConstruct
   public void init()
   {
      ((Session) entityManager.getDelegate()).setFlushMode(FlushMode.MANUAL);
      haeVyöarvot();
      haePerheet();
      haeHarrastajat();
   }

   @Produces
   @Named
   public List<Perhe> getPerheet()
   {
      if (perheet == null)
      {
         haePerheet();
      }
      return perheet;
   }

   @Produces
   @Named
   public Sopimus getSopimus()
   {
      return sopimus;
   }

   @Produces
   @Named
   public List<Harrastaja> getHarrastajat()
   {
      if (harrastajat == null)
      {
         haeHarrastajat();
      }
      return harrastajat;
   }

   @Produces
   @Named
   public Vyökoe getVyökoe()
   {
      return vyökoe;
   }

   @Produces
   @Named
   public List<Rooli> getRoolit()
   {
      if (harrastaja == null)
      {
         return new ArrayList<Rooli>();
      }
      if (roolit == null)
      {
         roolit = entityManager.createNamedQuery("roolit", Rooli.class).getResultList();
         roolit.removeAll(harrastaja.getRoolit());
      }
      return roolit;
   }

   @Produces
   @Named
   public List<SelectItem> getSukupuolet()
   {
      List<SelectItem> tulos = new ArrayList<SelectItem>();
      for (Sukupuoli sukupuoli : Sukupuoli.values())
      {
         tulos.add(new SelectItem(sukupuoli, sukupuoli.getNimi()));
      }
      return tulos;
   }

   @Produces
   @Named
   public Harrastaja getHarrastaja()
   {
      return harrastaja;
   }

   private void haeHarrastajat()
   {
      harrastajat = entityManager.createNamedQuery("harrastajat", Harrastaja.class).getResultList();
   }

   private void haePerheet()
   {
      perheet = entityManager.createNamedQuery("perheet", Perhe.class).getResultList();
   }

   private void haeVyöarvot()
   {
      vyöarvot = entityManager.createNamedQuery("vyöarvot", Vyöarvo.class).getResultList();
   }

   private void virkistäPerheet()
   {
      haePerheet();
      perheet.forEach(p -> entityManager.refresh(p));
   }

   public void tallennaHarrastaja()
   {
      if (löytyySamanniminenHarrastaja())
      {
         virhe("Samanniminen harrastaja löytyy jo");
         return;
      }
      loggaaja.loggaa(String.format("Tallensi harrastajan %s", harrastaja));
      harrastaja.siivoa();
      entityManager.persist(harrastaja);
      entityManager.flush();
      harrastajaRSM.get(harrastaja).setSelected(true);
      if (harrastaja.isOsoiteMuuttunut())
      {
         poistaTyhjätPerheetJaOsoitteet();
      }
      harrastaja.setOsoiteMuuttunut(false);
      virkistäPerheet();
      haeHarrastajat();
      info("Harrastaja tallennettu");
   }

   private boolean löytyySamanniminenHarrastaja()
   {
      List<Henkilö> vanhat = entityManager.createNamedQuery("samanniminen_käyttäjä", Henkilö.class)
         .setParameter("etunimi", harrastaja.getEtunimi()).setParameter("sukunimi", harrastaja.getSukunimi())
         .setParameter("id", harrastaja.getId()).getResultList();
      return vanhat.size() > 0;
   }

   private void poistaTyhjätPerheetJaOsoitteet()
   {
      // entityManager.createNamedQuery("poista_turhat_huoltajat").executeUpdate();
      // entityManager.createNativeQuery("delete from henkilo h where not exist(select 1 from harrastaja ha where ha.huoltaja=h.id)").executeUpdate();
      entityManager.createNamedQuery("poista_tyhjät_perheet").executeUpdate();
      entityManager.createNamedQuery("poista_tyhjät_osoitteet").executeUpdate();
   }

   public void peruutaPerustietomuutos()
   {
      if (harrastaja.isPoistettavissa())
      {
         harrastaja.siivoa();
         entityManager.refresh(harrastaja);
      } else
      {
         harrastaja = null;
      }
      virhe("Muutokset peruttu");
   }

   public void peruutaSopimusmuutos()
   {
      if (sopimus.isPoistettavissa())
      {
         entityManager.refresh(sopimus);
      } else
      {
         sopimus = null;
      }
      virhe("Muutokset peruttu");
   }

   public void peruutaVyökoemuutos()
   {
      if (vyökoe.isPoistettavissa())
      {
         entityManager.refresh(vyökoe);
      } else
      {
         vyökoe = null;
      }
      virhe("Muutokset peruttu");
   }

   public void poistaHarrastaja()
   {
      entityManager.remove(harrastaja);
      entityManager.flush();
      harrastaja = null;
      haeHarrastajat();
      virkistäPerheet();
      harrastajaRSM.setAllSelected(false);
      info("Harrastaja poistettu");
   }

   public void tallennaVyökoe()
   {
      if (!harrastaja.getVyökokeet().contains(vyökoe))
      {
         vyökoe.setHarrastaja(harrastaja);
         harrastaja.getVyökokeet().add(vyökoe);
      }
      entityManager.persist(harrastaja);
      entityManager.flush();
      vyökoeRSM.get(vyökoe).setSelected(true);
      harrastaja.getVyökokeet().sort(
         (v1, v2) -> Integer.valueOf(v1.getVyöarvo().getJärjestys()).compareTo(
            Integer.valueOf(v2.getVyöarvo().getJärjestys())));
      info("Vyökoe tallennettu");
   }

   public void tallennaSopimus()
   {
      if (!harrastaja.getSopimukset().contains(sopimus))
      {
         sopimus.setHarrastaja(harrastaja);
         harrastaja.getSopimukset().add(sopimus);
      }
      entityManager.persist(harrastaja);
      entityManager.flush();
      sopimusRSM.get(sopimus).setSelected(true);
      info("Sopimus tallennettu");
   }

   public void poistaVyökoe()
   {
      harrastaja.getVyökokeet().remove(vyökoe);
      entityManager.persist(harrastaja);
      entityManager.flush();
      info("Vyökoe poistettu");
   }

   public void poistaSopimus()
   {
      harrastaja.getSopimukset().remove(sopimus);
      entityManager.persist(harrastaja);
      entityManager.flush();
      info("Sopimus poistettu");
   }

   public void piilotaSopimus()
   {
      sopimus = null;
   }

   public void piilotaVyökoe()
   {
      vyökoe = null;
   }

   public void piilotaHarrastaja()
   {
      harrastaja = null;
   }

   public void syntymäaikaMuuttui(AjaxBehaviorEvent e)
   {
      DateTimeEntry dte = (DateTimeEntry) e.getComponent();
      Date val = (Date) dte.getValue();
      if (!Harrastaja.alaikäinen(val))
      {
         harrastaja.setHuoltaja(null);
      }
      if (harrastaja.getJäsennumero() == null)
      {
         List<Harrastaja> samaSyntymäpäivä = entityManager.createNamedQuery("sama_syntymäpäivä", Harrastaja.class)
            .setParameter("päivä", val).getResultList();
         String päivämäärä = new SimpleDateFormat("YYYYMMdd").format(val);
         String lukumäärä = String.format("%03d", samaSyntymäpäivä.size() + 1);
         String korttinumero = String.format("%s%s", päivämäärä, lukumäärä);
         harrastaja.setKorttinumero(korttinumero);
      }
   }

   public void lisääHuoltaja()
   {
      Henkilö huoltaja = new Henkilö();
      Perhe perhe = harrastaja.getPerhe();
      if (perhe == null)
      {
         perhe = new Perhe();
         perhe.setNimi(harrastaja.getSukunimi());
         harrastaja.setPerhe(perhe);
      }
      perhe.getPerheenjäsenet().add(huoltaja);
      huoltaja.setSukunimi(harrastaja.getSukunimi());
      huoltaja.setPerhe(perhe);
      harrastaja.setHuoltaja(huoltaja);
      perheet.add(perhe);
      fokusoi("form:huoltajan_etunimi");
   }

   public void lisääHarrastaja()
   {
      resetoi();
      tabi.setSelectedIndex(0);
      harrastaja = new Harrastaja();
      info("Uusi harrastaja alustettu");
      fokusoi("form:etunimi");
   }

   public void lisääSopimus()
   {
      sopimus = new Sopimus();
      sopimusRSM.setAllSelected(false);
      info("Uusi sopimus alustettu");
   }

   public void lisääVyökoe()
   {
      vyökoe = new Vyökoe();
      Vyöarvo seuraavaVyöarvo = vyöarvot.iterator().next();
      if (!harrastaja.getVyökokeet().isEmpty())
      {
         seuraavaVyöarvo = vyöarvot.get(vyöarvot.indexOf(harrastaja.getTuoreinVyöarvo()) + 1);
      }
      vyökoe.setVyöarvo(seuraavaVyöarvo);
      vyökoe.setPäivä(new Date());
      vyökoe.setHarrastaja(harrastaja);
      vyökoeRSM.setAllSelected(false);
      info("Uusi vyökoe alustettu");
   }

   public void sopimustyyppiMuuttui()
   {
      sopimus.setTreenikertoja(sopimus.getTyyppi().getOletusTreenikerrat());
      sopimus.setMaksuväli(sopimus.getTyyppi().getOletusMaksuväli());
      if (sopimus.getTyyppi().getOletusKuukaudetVoimassa() > 0)
      {
         sopimus.setUmpeutuu(Date.from(LocalDate.now()
            .plus(sopimus.getTyyppi().getOletusKuukaudetVoimassa(), ChronoUnit.MONTHS).atStartOfDay()
            .atZone(ZoneId.systemDefault()).toInstant()));
      } else
      {
         sopimus.setUmpeutuu(null);
      }
      if (sopimus.getTyyppi().isJäsenmaksu())
      {
         LocalDate päivä = LocalDate.now();
         päivä = päivä.with(TemporalAdjusters.lastDayOfYear());
         sopimus.setUmpeutuu(Date.from(päivä.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
      }
   }

   public void harrastajaValittu(SelectEvent e)
   {
      harrastaja = (Harrastaja) e.getObject();
      roolit = null;
      vyökoe = null;
      sopimus = null;
      vyökoeRSM = new RowStateMap();
      sopimusRSM = new RowStateMap();
   }

   public void vyökoeValittu(SelectEvent e)
   {
      vyökoe = (Vyökoe) e.getObject();
   }

   public void sopimusValittu(SelectEvent e)
   {
      sopimus = (Sopimus) e.getObject();
   }

   public RowStateMap getVyökoeRSM()
   {
      return vyökoeRSM;
   }

   public void setVyökoeRSM(RowStateMap vyökoeRSM)
   {
      this.vyökoeRSM = vyökoeRSM;
   }

   public RowStateMap getSopimusRSM()
   {
      return sopimusRSM;
   }

   public void setSopimusRSM(RowStateMap sopimusRSM)
   {
      this.sopimusRSM = sopimusRSM;
   }

   public RowStateMap getHarrastajaRSM()
   {
      return harrastajaRSM;
   }

   public void setHarrastajaRSM(RowStateMap harrastajaRSM)
   {
      this.harrastajaRSM = harrastajaRSM;
   }

   public TabSet getTabi()
   {
      return tabi;
   }

   public void setTabi(TabSet tabi)
   {
      this.tabi = tabi;
   }

}
