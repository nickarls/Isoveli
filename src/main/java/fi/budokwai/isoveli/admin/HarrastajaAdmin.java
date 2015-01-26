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
import fi.budokwai.isoveli.malli.Henkil�;
import fi.budokwai.isoveli.malli.Osoite;
import fi.budokwai.isoveli.malli.Perhe;
import fi.budokwai.isoveli.malli.Rooli;
import fi.budokwai.isoveli.malli.Sopimus;
import fi.budokwai.isoveli.malli.Sopimustyyppi;
import fi.budokwai.isoveli.malli.Sukupuoli;
import fi.budokwai.isoveli.malli.Vy�arvo;
import fi.budokwai.isoveli.malli.Vy�koe;
import fi.budokwai.isoveli.util.Loggaaja;
import fi.budokwai.isoveli.util.Muuttui;

@Named
@SessionScoped
@Stateful
public class HarrastajaAdmin extends Perustoiminnallisuus
{
   private List<Harrastaja> harrastajat;
   private List<Rooli> roolit;
   private List<Vy�arvo> vy�arvot;
   private List<Perhe> perheet;

   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   private TabSet tabi;

   private Harrastaja harrastaja;
   private Sopimus sopimus;
   private Vy�koe vy�koe;

   private RowStateMap harrastajaRSM = new RowStateMap();
   private RowStateMap vy�koeRSM = new RowStateMap();
   private RowStateMap sopimusRSM = new RowStateMap();

   @Inject
   private Loggaaja loggaaja;

   @Inject
   @Muuttui
   private Event<EntityManager> emMuuttui;

   public void p��tabiMuuttui(ValueChangeEvent e)
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
      vy�arvot = null;
      perheet = null;
      harrastaja = null;
      sopimus = null;
      vy�koe = null;
      harrastajaRSM = new RowStateMap();
      vy�koeRSM = new RowStateMap();
      sopimusRSM = new RowStateMap();
      emMuuttui.fire(entityManager);
   }

   public void tilap�isyysMuuttui(AjaxBehaviorEvent e)
   {
      if (harrastaja.isTilap�inen())
      {
         harrastaja.muutaVakioksi();
         harrastaja.getSopimukset().add(teeKoeaikaSopimus());
      } else
      {
         harrastaja.muutaTilap�iseksi();
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

   public void lis��Perhe()
   {
      Perhe perhe = new Perhe();
      perhe.setNimi(harrastaja.getSukunimi());
      perhe.getPerheenj�senet().add(harrastaja);
      perhe.setOsoite(harrastaja.getOsoite());
      harrastaja.setOsoite(null);
      harrastaja.setPerhe(perhe);
      entityManager.persist(perhe);
      entityManager.flush();
      virkist�Perheet();
   }

   @PostConstruct
   public void init()
   {
      ((Session) entityManager.getDelegate()).setFlushMode(FlushMode.MANUAL);
      haeVy�arvot();
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
   public Vy�koe getVy�koe()
   {
      return vy�koe;
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

   private void haeVy�arvot()
   {
      vy�arvot = entityManager.createNamedQuery("vy�arvot", Vy�arvo.class).getResultList();
   }

   private void virkist�Perheet()
   {
      haePerheet();
      perheet.forEach(p -> entityManager.refresh(p));
   }

   public void tallennaHarrastaja()
   {
      if (l�ytyySamanniminenHarrastaja())
      {
         virhe("Samanniminen harrastaja l�ytyy jo");
         return;
      }
      loggaaja.loggaa(String.format("Tallensi harrastajan %s", harrastaja));
      harrastaja.siivoa();
      entityManager.persist(harrastaja);
      entityManager.flush();
      harrastajaRSM.get(harrastaja).setSelected(true);
      if (harrastaja.isOsoiteMuuttunut())
      {
         poistaTyhj�tPerheetJaOsoitteet();
      }
      harrastaja.setOsoiteMuuttunut(false);
      virkist�Perheet();
      haeHarrastajat();
      info("Harrastaja tallennettu");
   }

   private boolean l�ytyySamanniminenHarrastaja()
   {
      List<Henkil�> vanhat = entityManager.createNamedQuery("samanniminen_k�ytt�j�", Henkil�.class)
         .setParameter("etunimi", harrastaja.getEtunimi()).setParameter("sukunimi", harrastaja.getSukunimi())
         .setParameter("id", harrastaja.getId()).getResultList();
      return vanhat.size() > 0;
   }

   private void poistaTyhj�tPerheetJaOsoitteet()
   {
      // entityManager.createNamedQuery("poista_turhat_huoltajat").executeUpdate();
      // entityManager.createNativeQuery("delete from henkilo h where not exist(select 1 from harrastaja ha where ha.huoltaja=h.id)").executeUpdate();
      entityManager.createNamedQuery("poista_tyhj�t_perheet").executeUpdate();
      entityManager.createNamedQuery("poista_tyhj�t_osoitteet").executeUpdate();
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

   public void peruutaVy�koemuutos()
   {
      if (vy�koe.isPoistettavissa())
      {
         entityManager.refresh(vy�koe);
      } else
      {
         vy�koe = null;
      }
      virhe("Muutokset peruttu");
   }

   public void poistaHarrastaja()
   {
      entityManager.remove(harrastaja);
      entityManager.flush();
      harrastaja = null;
      haeHarrastajat();
      virkist�Perheet();
      harrastajaRSM.setAllSelected(false);
      info("Harrastaja poistettu");
   }

   public void tallennaVy�koe()
   {
      if (!harrastaja.getVy�kokeet().contains(vy�koe))
      {
         vy�koe.setHarrastaja(harrastaja);
         harrastaja.getVy�kokeet().add(vy�koe);
      }
      entityManager.persist(harrastaja);
      entityManager.flush();
      vy�koeRSM.get(vy�koe).setSelected(true);
      harrastaja.getVy�kokeet().sort(
         (v1, v2) -> Integer.valueOf(v1.getVy�arvo().getJ�rjestys()).compareTo(
            Integer.valueOf(v2.getVy�arvo().getJ�rjestys())));
      info("Vy�koe tallennettu");
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

   public void poistaVy�koe()
   {
      harrastaja.getVy�kokeet().remove(vy�koe);
      entityManager.persist(harrastaja);
      entityManager.flush();
      info("Vy�koe poistettu");
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

   public void piilotaVy�koe()
   {
      vy�koe = null;
   }

   public void piilotaHarrastaja()
   {
      harrastaja = null;
   }

   public void syntym�aikaMuuttui(AjaxBehaviorEvent e)
   {
      DateTimeEntry dte = (DateTimeEntry) e.getComponent();
      Date val = (Date) dte.getValue();
      if (!Harrastaja.alaik�inen(val))
      {
         harrastaja.setHuoltaja(null);
      }
      if (harrastaja.getJ�sennumero() == null)
      {
         List<Harrastaja> samaSyntym�p�iv� = entityManager.createNamedQuery("sama_syntym�p�iv�", Harrastaja.class)
            .setParameter("p�iv�", val).getResultList();
         String p�iv�m��r� = new SimpleDateFormat("YYYYMMdd").format(val);
         String lukum��r� = String.format("%03d", samaSyntym�p�iv�.size() + 1);
         String korttinumero = String.format("%s%s", p�iv�m��r�, lukum��r�);
         harrastaja.setKorttinumero(korttinumero);
      }
   }

   public void lis��Huoltaja()
   {
      Henkil� huoltaja = new Henkil�();
      Perhe perhe = harrastaja.getPerhe();
      if (perhe == null)
      {
         perhe = new Perhe();
         perhe.setNimi(harrastaja.getSukunimi());
         harrastaja.setPerhe(perhe);
      }
      perhe.getPerheenj�senet().add(huoltaja);
      huoltaja.setSukunimi(harrastaja.getSukunimi());
      huoltaja.setPerhe(perhe);
      harrastaja.setHuoltaja(huoltaja);
      perheet.add(perhe);
      fokusoi("form:huoltajan_etunimi");
   }

   public void lis��Harrastaja()
   {
      resetoi();
      tabi.setSelectedIndex(0);
      harrastaja = new Harrastaja();
      info("Uusi harrastaja alustettu");
      fokusoi("form:etunimi");
   }

   public void lis��Sopimus()
   {
      sopimus = new Sopimus();
      sopimusRSM.setAllSelected(false);
      info("Uusi sopimus alustettu");
   }

   public void lis��Vy�koe()
   {
      vy�koe = new Vy�koe();
      Vy�arvo seuraavaVy�arvo = vy�arvot.iterator().next();
      if (!harrastaja.getVy�kokeet().isEmpty())
      {
         seuraavaVy�arvo = vy�arvot.get(vy�arvot.indexOf(harrastaja.getTuoreinVy�arvo()) + 1);
      }
      vy�koe.setVy�arvo(seuraavaVy�arvo);
      vy�koe.setP�iv�(new Date());
      vy�koe.setHarrastaja(harrastaja);
      vy�koeRSM.setAllSelected(false);
      info("Uusi vy�koe alustettu");
   }

   public void sopimustyyppiMuuttui()
   {
      sopimus.setTreenikertoja(sopimus.getTyyppi().getOletusTreenikerrat());
      sopimus.setMaksuv�li(sopimus.getTyyppi().getOletusMaksuv�li());
      if (sopimus.getTyyppi().getOletusKuukaudetVoimassa() > 0)
      {
         sopimus.setUmpeutuu(Date.from(LocalDate.now()
            .plus(sopimus.getTyyppi().getOletusKuukaudetVoimassa(), ChronoUnit.MONTHS).atStartOfDay()
            .atZone(ZoneId.systemDefault()).toInstant()));
      } else
      {
         sopimus.setUmpeutuu(null);
      }
      if (sopimus.getTyyppi().isJ�senmaksu())
      {
         LocalDate p�iv� = LocalDate.now();
         p�iv� = p�iv�.with(TemporalAdjusters.lastDayOfYear());
         sopimus.setUmpeutuu(Date.from(p�iv�.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
      }
   }

   public void harrastajaValittu(SelectEvent e)
   {
      harrastaja = (Harrastaja) e.getObject();
      roolit = null;
      vy�koe = null;
      sopimus = null;
      vy�koeRSM = new RowStateMap();
      sopimusRSM = new RowStateMap();
   }

   public void vy�koeValittu(SelectEvent e)
   {
      vy�koe = (Vy�koe) e.getObject();
   }

   public void sopimusValittu(SelectEvent e)
   {
      sopimus = (Sopimus) e.getObject();
   }

   public RowStateMap getVy�koeRSM()
   {
      return vy�koeRSM;
   }

   public void setVy�koeRSM(RowStateMap vy�koeRSM)
   {
      this.vy�koeRSM = vy�koeRSM;
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
