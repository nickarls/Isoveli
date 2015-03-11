package fi.budokwai.isoveli.admin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

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

import org.icefaces.ace.component.datetimeentry.DateTimeEntry;
import org.icefaces.ace.component.tabset.TabSet;
import org.icefaces.ace.event.SelectEvent;
import org.icefaces.ace.model.table.RowStateMap;

import fi.budokwai.isoveli.IsoveliPoikkeus;
import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Henkil�;
import fi.budokwai.isoveli.malli.Osoite;
import fi.budokwai.isoveli.malli.Perhe;
import fi.budokwai.isoveli.malli.Sopimus;
import fi.budokwai.isoveli.malli.Sopimuslasku;
import fi.budokwai.isoveli.malli.Sopimustarkistus;
import fi.budokwai.isoveli.malli.Sopimustyyppi;
import fi.budokwai.isoveli.malli.Sukupuoli;
import fi.budokwai.isoveli.malli.Vy�arvo;
import fi.budokwai.isoveli.malli.Vy�koe;
import fi.budokwai.isoveli.util.DateUtil;
import fi.budokwai.isoveli.util.Loggaaja;
import fi.budokwai.isoveli.util.Muuttui;
import fi.budokwai.isoveli.util.Vy�koehelper;

@Named
@SessionScoped
@Stateful
public class HarrastajaAdmin extends Perustoiminnallisuus
{
   private List<Harrastaja> harrastajat;
   private List<Perhe> perheet;
   private TabSet tabi;
   private Harrastaja harrastaja;
   private Sopimus sopimus;
   private Vy�koe vy�koe;

   private RowStateMap harrastajaRSM = new RowStateMap();
   private RowStateMap vy�koeRSM = new RowStateMap();
   private RowStateMap sopimusRSM = new RowStateMap();

   @Inject
   private EntityManager entityManager;

   @Inject
   private Vy�koehelper vy�koehelper;

   @Inject
   private List<Sopimustyyppi> sopimustyypit;

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
      perheet = null;
      harrastaja = null;
      sopimus = null;
      vy�koe = null;
      harrastajaRSM = new RowStateMap();
      vy�koeRSM = new RowStateMap();
      sopimusRSM = new RowStateMap();
      emMuuttui.fire(entityManager);
   }

   public void huoltajanPuhelinMuuttui(AjaxBehaviorEvent e)
   {
      harrastaja.setIce(harrastaja.getHuoltaja().getYhteystiedot().getPuhelinnumero());
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
      koeaika.setUmpeutuu(DateUtil.kuukausienP��st�(koeaikaSopimustyyppi.getOletusKuukaudetVoimassa()));
      return koeaika;
   }

   public void huoltajaMuuttui(AjaxBehaviorEvent e)
   {
      Henkil� henkil� = harrastaja.getHuoltaja();
      if (henkil� == null)
      {
         harrastaja.setIce(null);
      } else if (henkil�.getYhteystiedot() != null)
      {
         harrastaja.setIce(henkil�.getYhteystiedot().getPuhelinnumero());
      }
   }

   public void perheMuuttui(AjaxBehaviorEvent e)
   {
      Perhe uusiPerhe = harrastaja.getPerhe();
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
         harrastaja.setIce(uusiPerhe.getOletusICE());
      }
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
      perheet = null;
      loggaaja.loggaa("Lis�si perheen harrastajalle '%s'", harrastaja);
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
      if (harrastaja != null && harrastaja.getPerhe() != null && !harrastaja.getPerhe().isTallennettu())
      {
         perheet.add(harrastaja.getPerhe());
      }
   }

   public void tallennaHarrastaja()
   {
      if (l�ytyySamanniminenHarrastaja())
      {
         throw new IsoveliPoikkeus("Samanniminen harrastaja/henkil� l�ytyy jo");
      }
      if (harrastaja.getTauko().isAvoin())
      {
         throw new IsoveliPoikkeus("Sek� tauon alkamis- ett� p��ttymisp�iv�m��r� annettava");
      }
      if (harrastaja.getTauko().isRajatRistiss�())
      {
         throw new IsoveliPoikkeus("Tauko ei voi loppua ennen kun se alkaa");
      }
      harrastaja.siivoa();
      if (harrastaja.getPerhe() != null)
      {
         entityManager.merge(harrastaja.getPerhe().getOsoite());
         perheet = null;
      }
      harrastaja = entityManager.merge(harrastaja);
      entityManager.flush();
      harrastajaRSM.get(harrastaja).setSelected(true);
      harrastajat = null;
      info("Harrastaja tallennettu");
      loggaaja.loggaa("Tallensi harrastajan '%s'", harrastaja);
   }

   private boolean l�ytyySamanniminenHarrastaja()
   {
      List<Henkil�> vanhat = entityManager.createNamedQuery("samanniminen_k�ytt�j�", Henkil�.class)
         .setParameter("etunimi", harrastaja.getEtunimi()).setParameter("sukunimi", harrastaja.getSukunimi())
         .setParameter("id", harrastaja.getId()).getResultList();
      return harrastaja.isTallentamaton() && vanhat.size() > 0;
   }

   public void poistaHarrastaja()
   {
      harrastaja.poistotarkistus();
      harrastaja = entityManager.merge(harrastaja);
      entityManager.remove(harrastaja);
      entityManager.flush();
      harrastaja = null;
      harrastajat = null;
      harrastajaRSM.setAllSelected(false);
      info("Harrastaja poistettu");
      loggaaja.loggaa("Poisti harrastajan '%s'", harrastaja);
   }

   public void tallennaVy�koe()
   {
      harrastaja = entityManager.merge(harrastaja);
      entityManager.flush();
      vy�koeRSM.get(vy�koe).setSelected(true);
      info("Vy�koe tallennettu");
      harrastajat = null;
      loggaaja.loggaa("Tallensi harrastajan '%s' vy�kokeen '%s'", harrastaja, vy�koe);
   }

   public void tallennaSopimus()
   {
      harrastaja = entityManager.merge(harrastaja);
      entityManager.flush();
      sopimusRSM.get(sopimus).setSelected(true);
      harrastajat = null;
      info("Sopimus tallennettu");
      loggaaja.loggaa("Tallensi harrastajan '%s' sopimuksen '%s'", harrastaja, sopimus);
      
   }

   public void poistaVy�koe()
   {
      harrastaja.getVy�kokeet().remove(vy�koe);
      harrastaja = entityManager.merge(harrastaja);
      entityManager.flush();
      harrastajat = null;
      info("Vy�koe poistettu");
      loggaaja.loggaa("Poisti harrastajan '%s' vy�kokeen '%s'", harrastaja, vy�koe);
   }

   public void poistaSopimus()
   {
      tarkistaSopimusk�ytt�();
      harrastaja.getSopimukset().remove(sopimus);
      harrastaja = entityManager.merge(harrastaja);
      entityManager.flush();
      harrastajat = null;
      info("Sopimus poistettu");
      loggaaja.loggaa("Poisti harrastajan '%s' sopimuksen '%s'", harrastaja, sopimus);
   }

   private void tarkistaSopimusk�ytt�()
   {
      List<Sopimuslasku> k�ytt� = entityManager
         .createQuery("select sl from Sopimuslasku sl where sl.sopimus=:sopimus", Sopimuslasku.class)
         .setParameter("sopimus", sopimus).getResultList();
      if (!k�ytt�.isEmpty())
      {
         StringJoiner stringJoiner = new StringJoiner(", ");
         k�ytt�.forEach(sl -> {
            stringJoiner.add(sl.getJakso());
         });
         String viesti = String.format("Sopimuksella on sopimuslaskuja ja sit� ei voi poistaa (%dkpl: %s...)",
            k�ytt�.size(), stringJoiner.toString());
         throw new IsoveliPoikkeus(viesti);
      }
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
         String lukum��r� = String.format("%d", samaSyntym�p�iv�.size() + 1);
         String j�sennumero = String.format("%s-%s", p�iv�m��r�, lukum��r�);
         harrastaja.setJ�sennumero(j�sennumero);
      }
      if (Harrastaja.ik�(val) < 4)
      {
         virhe("Onko ik� alle 4 tarkoituksella?");
      }
   }

   public void lis��Huoltaja()
   {
      Henkil� huoltaja = new Henkil�();
      Perhe perhe = harrastaja.getPerhe();
      perhe.getPerheenj�senet().add(huoltaja);
      huoltaja.setSukunimi(harrastaja.getSukunimi());
      huoltaja.setEtunimi("Huoltaja");
      huoltaja.setPerhe(perhe);
      harrastaja.setHuoltaja(huoltaja);
      entityManager.persist(huoltaja);
      entityManager.persist(perhe);
      perheet = null;
      fokusoi("form:huoltajan_etunimi");
      loggaaja.loggaa("Lis�si harrastajalle '%s' huoltajan", harrastaja);
   }

   public void lis��Harrastaja()
   {
      resetoi();
//      tabi.setSelectedIndex(0);
      harrastaja = new Harrastaja();
      info("Uusi harrastaja alustettu");
      fokusoi("form:etunimi");
      loggaaja.loggaa("Lis�si harrastajan");
   }

   public void lis��Sopimus()
   {
      sopimusRSM.setAllSelected(false);
      Sopimus sopimus = harrastaja.lis��Sopimus(new Sopimus(ehdotaSopimusTyyppi�()));
      if (sopimus.getTyyppi() != null)
      {
         sopimus.asetaP��ttymisp�iv�();
      }
      sopimus.setMaksuv�li(sopimus.getTyyppi().getOletusMaksuv�li());
      info("Uusi sopimus alustettu");
      loggaaja.loggaa("Lis�si harrastajalle '%s' sopimuksen", harrastaja);
   }

   private Sopimustyyppi ehdotaSopimusTyyppi�()
   {
      Optional<Sopimustyyppi> ehdotus = null;
      if (!harrastaja.getSopimukset().stream().filter(s -> s.getTyyppi().isJ�senmaksutyyppi()).findFirst().isPresent())
      {
         ehdotus = sopimustyypit.stream().filter(s -> s.isJ�senmaksutyyppi()).findFirst();
      } else if (!harrastaja.getSopimukset().stream().filter(s -> s.getTyyppi().isHarjoittelumaksutyyppi()).findFirst()
         .isPresent())
      {
         ehdotus = sopimustyypit.stream().filter(s -> s.isHarjoittelumaksutyyppi() && s.sopiiHarrastajalle(harrastaja))
            .findFirst();
      }
      return (ehdotus == null || !ehdotus.isPresent()) ? null : ehdotus.get();
   }

   public void lis��Vy�koe()
   {
      vy�koe = new Vy�koe();
      Vy�arvo seuraavaVy�arvo = vy�koehelper.haeSeuraavaVy�arvo(harrastaja.getTuoreinVy�arvo());
      vy�koe.setVy�arvo(seuraavaVy�arvo);
      harrastaja.lis��Vy�koe(vy�koe);
      vy�koeRSM.setAllSelected(false);
      info("Uusi vy�koe alustettu");
      loggaaja.loggaa("Lis�si harrastajalle '%s' vy�kokeen", harrastaja);
   }

   public void sopimustyyppiMuuttui()
   {
      sopimus.setTreenikertoja(sopimus.getTyyppi().getOletusTreenikerrat());
      sopimus.setMaksuv�li(sopimus.getTyyppi().getOletusMaksuv�li());
      sopimus.asetaP��ttymisp�iv�();
      List<Sopimustarkistus> tarkistukset = sopimus.tarkista();
      tarkistukset.forEach(t -> virhe(t.getViesti()));
   }

   public void harrastajaValittu(SelectEvent e)
   {
      harrastaja = (Harrastaja) e.getObject();
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

   public void setHarrastaja(Harrastaja harrastaja)
   {
      this.harrastaja = harrastaja;
   }

   public void setSopimus(Sopimus sopimus)
   {
      this.sopimus = sopimus;
   }

   public void setVy�koe(Vy�koe vy�koe)
   {
      this.vy�koe = vy�koe;
   }

}
