package fi.budokwai.isoveli.admin;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

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
import javax.validation.ConstraintViolation;

import org.icefaces.ace.component.datetimeentry.DateTimeEntry;
import org.icefaces.ace.component.tabset.TabSet;
import org.icefaces.ace.event.SelectEvent;
import org.icefaces.ace.model.table.RowStateMap;

import fi.budokwai.isoveli.IsoveliPoikkeus;
import fi.budokwai.isoveli.malli.BlobData;
import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Henkilö;
import fi.budokwai.isoveli.malli.Osoite;
import fi.budokwai.isoveli.malli.Perhe;
import fi.budokwai.isoveli.malli.Sopimus;
import fi.budokwai.isoveli.malli.Sopimuslasku;
import fi.budokwai.isoveli.malli.Sopimustarkistus;
import fi.budokwai.isoveli.malli.Sopimustyyppi;
import fi.budokwai.isoveli.malli.Sukupuoli;
import fi.budokwai.isoveli.malli.Vyöarvo;
import fi.budokwai.isoveli.malli.Vyökoe;
import fi.budokwai.isoveli.util.DateUtil;
import fi.budokwai.isoveli.util.Loggaaja;
import fi.budokwai.isoveli.util.Muuttui;
import fi.budokwai.isoveli.util.Tilapäiskortti;
import fi.budokwai.isoveli.util.Tulostaja;
import fi.budokwai.isoveli.util.Vyökoehelper;

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
   private Vyökoe vyökoe;
   private boolean arkiiviMoodi;

   private RowStateMap harrastajaRSM = new RowStateMap();
   private RowStateMap vyökoeRSM = new RowStateMap();
   private RowStateMap sopimusRSM = new RowStateMap();

   @Inject
   private EntityManager entityManager;

   @Inject
   private Vyökoehelper vyökoehelper;

   @Inject
   private List<Sopimustyyppi> sopimustyypit;

   @Inject
   private Loggaaja loggaaja;

   @Inject
   private Tulostaja tulostaja;

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
      perheet = null;
      harrastaja = null;
      sopimus = null;
      vyökoe = null;
      harrastajaRSM = new RowStateMap();
      vyökoeRSM = new RowStateMap();
      sopimusRSM = new RowStateMap();
      emMuuttui.fire(entityManager);
   }

   public void huoltajanPuhelinMuuttui(AjaxBehaviorEvent e)
   {
      harrastaja.setIce(harrastaja.getHuoltaja().getYhteystiedot().getPuhelinnumero());
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
      koeaika.setTreenikertojaJäljellä(koeaikaSopimustyyppi.getOletusTreenikerrat());
      koeaika.setUmpeutuu(DateUtil.kuukausienPäästä(koeaikaSopimustyyppi.getOletusKuukaudetVoimassa()));
      return koeaika;
   }

   public void huoltajaMuuttui(AjaxBehaviorEvent e)
   {
      Henkilö henkilö = harrastaja.getHuoltaja();
      if (henkilö == null)
      {
         harrastaja.setIce(null);
      } else if (henkilö.getYhteystiedot() != null)
      {
         harrastaja.setIce(henkilö.getYhteystiedot().getPuhelinnumero());
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
      perheet = null;
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
      perheet = null;
      loggaaja.loggaa("Lisäsi perheen harrastajalle '%s'", harrastaja);
   }

   @Produces
   @Named
   public List<Perhe> getPerheet()
   {
      if (perheet == null)
      {
         perheet = entityManager.createNamedQuery("perheet", Perhe.class).getResultList();
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
         harrastajat = entityManager.createNamedQuery("harrastajat", Harrastaja.class).getResultList();
      }
      return harrastajat;
   }

   @Produces
   @Named
   public List<SelectItem> getHarrastajatSI()
   {
      return getHarrastajat().stream().map(h -> new SelectItem(h.getNimi())).collect(Collectors.toList());
   }

   @Produces
   @Named
   public Vyökoe getVyökoe()
   {
      return vyökoe;
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

   public void tallennaHarrastaja()
   {
      if (!validointiOK())
      {
         return;
      }
      harrastaja.siivoa();
      if (harrastaja.getPerhe() != null)
      {
         harrastaja.setPerhe(entityManager.merge(harrastaja.getPerhe()));
      }
      harrastaja = entityManager.merge(harrastaja);
      entityManager.flush();
      harrastajaRSM.get(harrastaja).setSelected(true);
      harrastajat = null;
      perheet = null;
      info("Harrastaja tallennettu");
      loggaaja.loggaa("Tallensi harrastajan '%s'", harrastaja);
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

   public void tallennaVyökoe()
   {
      if (vyökoe.isTallentamaton())
      {
         harrastaja.lisääVyökoe(vyökoe);
      }
      if (!validointiOK())
      {
         return;
      }
      harrastaja = entityManager.merge(harrastaja);
      entityManager.flush();
      vyökoeRSM.get(vyökoe).setSelected(true);
      info("Vyökoe tallennettu");
      harrastajat = null;
      loggaaja.loggaa("Tallensi harrastajan '%s' vyökokeen '%s'", harrastaja, vyökoe);
   }

   private boolean validointiOK()
   {
      Set<ConstraintViolation<Harrastaja>> validointivirheet = validator.validate(harrastaja);
      validointivirheet.forEach(v -> {
         virhe(v.getMessage());
      });
      return validointivirheet.isEmpty();
   }

   public void tallennaSopimus()
   {
      harrastaja = entityManager.merge(harrastaja);
      if (sopimus.isTallentamaton())
      {
         harrastaja.lisääSopimus(sopimus);
      }
      entityManager.flush();
      sopimusRSM.get(sopimus).setSelected(true);
      harrastajat = null;
      info("Sopimus tallennettu");
      loggaaja.loggaa("Tallensi harrastajan '%s' sopimuksen '%s'", harrastaja, sopimus);

   }

   public void poistaVyökoe()
   {
      harrastaja.getVyökokeet().remove(vyökoe);
      if (vyökoe.isTallentamaton())
      {
         vyökoe = null;
         return;
      }
      harrastaja = entityManager.merge(harrastaja);
      entityManager.flush();
      harrastajat = null;
      vyökoe = null;
      info("Vyökoe poistettu");
      loggaaja.loggaa("Poisti harrastajan '%s' vyökokeen '%s'", harrastaja, vyökoe);
   }

   public void poistaSopimus()
   {
      if (sopimus.isTallentamaton())
      {
         sopimus = null;
         return;
      }
      tarkistaSopimuskäyttö();
      harrastaja.getSopimukset().remove(sopimus);
      harrastaja = entityManager.merge(harrastaja);
      entityManager.flush();
      harrastajat = null;
      sopimus = null;
      info("Sopimus poistettu");
      loggaaja.loggaa("Poisti harrastajan '%s' sopimuksen '%s'", harrastaja, sopimus);
   }

   private void tarkistaSopimuskäyttö()
   {
      List<Sopimuslasku> käyttö = entityManager
         .createQuery("select sl from Sopimuslasku sl where sl.sopimus=:sopimus", Sopimuslasku.class)
         .setParameter("sopimus", sopimus).getResultList();
      if (!käyttö.isEmpty())
      {
         StringJoiner stringJoiner = new StringJoiner(", ");
         käyttö.forEach(sl -> {
            stringJoiner.add(sl.getJakso());
         });
         String viesti = String.format("Sopimuksella on sopimuslaskuja ja sitä ei voi poistaa (%dkpl: %s...)",
            käyttö.size(), stringJoiner.toString());
         throw new IsoveliPoikkeus(viesti);
      }
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
         String lukumäärä = String.format("%d", samaSyntymäpäivä.size() + 1);
         String jäsennumero = String.format("%s-%s", päivämäärä, lukumäärä);
         harrastaja.setJäsennumero(jäsennumero);
      }
      if (Harrastaja.ikä(val) < 4)
      {
         virhe("Onko ikä alle 4 tarkoituksella?");
      }
   }

   public void lisääHuoltaja()
   {
      harrastaja = entityManager.merge(harrastaja);
      harrastaja.lisääHuoltaja();
      entityManager.persist(harrastaja);
      entityManager.flush();
      perheet = null;
      fokusoi("form:huoltajan_etunimi");
      loggaaja.loggaa("Lisäsi harrastajalle '%s' huoltajan", harrastaja);
   }

   public void lisääHarrastaja()
   {
      resetoi();
      // tabi.setSelectedIndex(0);
      harrastaja = new Harrastaja();
      info("Uusi harrastaja alustettu");
      fokusoi("form:etunimi");
      loggaaja.loggaa("Lisäsi harrastajan");
   }

   public void lisääSopimus()
   {
      sopimusRSM.setAllSelected(false);
      sopimus = new Sopimus(harrastaja, ehdotaSopimusTyyppiä());
      if (sopimus.getTyyppi() != null)
      {
         sopimus.asetaPäättymispäivä();
      }
      sopimus.setMaksuväli(sopimus.getTyyppi().getOletusMaksuväli());
      info("Uusi sopimus alustettu");
      loggaaja.loggaa("Lisäsi harrastajalle '%s' sopimuksen", harrastaja);
   }

   private Sopimustyyppi ehdotaSopimusTyyppiä()
   {
      Optional<Sopimustyyppi> ehdotus = null;
      if (!harrastaja.getSopimukset().stream().filter(s -> s.getTyyppi().isJäsenmaksutyyppi()).findFirst().isPresent())
      {
         ehdotus = sopimustyypit.stream().filter(s -> s.isJäsenmaksutyyppi()).findFirst();
      } else if (!harrastaja.getSopimukset().stream().filter(s -> s.getTyyppi().isHarjoittelumaksutyyppi()).findFirst()
         .isPresent())
      {
         ehdotus = sopimustyypit.stream().filter(s -> s.isHarjoittelumaksutyyppi() && s.sopiiHarrastajalle(harrastaja))
            .findFirst();
      }
      return (ehdotus == null || !ehdotus.isPresent()) ? null : ehdotus.get();
   }

   public void lisääVyökoe()
   {
      vyökoe = new Vyökoe();
      Vyöarvo seuraavaVyöarvo = vyökoehelper.haeSeuraavaVyöarvo(harrastaja);
      vyökoe.setVyöarvo(seuraavaVyöarvo);
      vyökoeRSM.setAllSelected(false);
      info("Uusi vyökoe alustettu");
      loggaaja.loggaa("Lisäsi harrastajalle '%s' vyökokeen", harrastaja);
   }

   public void sopimustyyppiMuuttui()
   {
      sopimus.setTreenikertojaTilattu(sopimus.getTyyppi().getOletusTreenikerrat());
      sopimus.setTreenikertojaJäljellä(sopimus.getTreenikertojaTilattu());
      sopimus.setMaksuväli(sopimus.getTyyppi().getOletusMaksuväli());
      sopimus.asetaPäättymispäivä();
      List<Sopimustarkistus> tarkistukset = sopimus.tarkista();
      tarkistukset.forEach(t -> virhe(t.getViesti()));
   }

   public void harrastajaValittu(SelectEvent e)
   {
      harrastaja = (Harrastaja) e.getObject();
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

   public void setHarrastaja(Harrastaja harrastaja)
   {
      this.harrastaja = harrastaja;
   }

   public void setSopimus(Sopimus sopimus)
   {
      this.sopimus = sopimus;
   }

   public void setVyökoe(Vyökoe vyökoe)
   {
      this.vyökoe = vyökoe;
   }

   public boolean isArkiiviMoodi()
   {
      return arkiiviMoodi;
   }

   public void setArkiiviMoodi(boolean arkiiviMoodi)
   {
      this.arkiiviMoodi = arkiiviMoodi;
   }

   public void tulostaTilapäisenMateriaalit() throws IOException
   {
      tulostaTilapäinenHarjoittelukortti();
      tulostaMateriaali("säännöt");
      tulostaMateriaali("hinnasto");
      tulostaMateriaali("aikataulu");
      tulostaMateriaali("yhteystiedot");
   }

   private void tulostaMateriaali(String avain)
   {
      List<BlobData> materiaali = entityManager.createNamedQuery("nimetty_blobdata", BlobData.class)
         .setParameter("nimi", avain).getResultList();
      if (materiaali.isEmpty())
      {
         return;
      }
      tulostaja.tulostaTiedosto(materiaali.iterator().next());
   }

   private void tulostaTilapäinenHarjoittelukortti() throws IOException
   {
      byte[] kuva = Tilapäiskortti.teeKortti(harrastaja.getNimi(), harrastaja.getId() + 10000 + "");
      tulostaja.tulostaTiedosto(BlobData.PDF("kortti", kuva));
   }

}
