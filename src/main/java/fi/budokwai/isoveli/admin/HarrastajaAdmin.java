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
import fi.budokwai.isoveli.util.Tilap�iskortti;
import fi.budokwai.isoveli.util.Tulostaja;
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
   private boolean arkiiviMoodi;

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
   private Tulostaja tulostaja;

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
      koeaika.setTreenikertojaJ�ljell�(koeaikaSopimustyyppi.getOletusTreenikerrat());
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
      perheet = null;
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

   public void tallennaVy�koe()
   {
      if (vy�koe.isTallentamaton())
      {
         harrastaja.lis��Vy�koe(vy�koe);
      }
      if (!validointiOK())
      {
         return;
      }
      harrastaja = entityManager.merge(harrastaja);
      entityManager.flush();
      vy�koeRSM.get(vy�koe).setSelected(true);
      info("Vy�koe tallennettu");
      harrastajat = null;
      loggaaja.loggaa("Tallensi harrastajan '%s' vy�kokeen '%s'", harrastaja, vy�koe);
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
         harrastaja.lis��Sopimus(sopimus);
      }
      entityManager.flush();
      sopimusRSM.get(sopimus).setSelected(true);
      harrastajat = null;
      info("Sopimus tallennettu");
      loggaaja.loggaa("Tallensi harrastajan '%s' sopimuksen '%s'", harrastaja, sopimus);

   }

   public void poistaVy�koe()
   {
      harrastaja.getVy�kokeet().remove(vy�koe);
      if (vy�koe.isTallentamaton())
      {
         vy�koe = null;
         return;
      }
      harrastaja = entityManager.merge(harrastaja);
      entityManager.flush();
      harrastajat = null;
      vy�koe = null;
      info("Vy�koe poistettu");
      loggaaja.loggaa("Poisti harrastajan '%s' vy�kokeen '%s'", harrastaja, vy�koe);
   }

   public void poistaSopimus()
   {
      if (sopimus.isTallentamaton())
      {
         sopimus = null;
         return;
      }
      tarkistaSopimusk�ytt�();
      harrastaja.getSopimukset().remove(sopimus);
      harrastaja = entityManager.merge(harrastaja);
      entityManager.flush();
      harrastajat = null;
      sopimus = null;
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
      harrastaja = entityManager.merge(harrastaja);
      harrastaja.lis��Huoltaja();
      entityManager.persist(harrastaja);
      entityManager.flush();
      perheet = null;
      fokusoi("form:huoltajan_etunimi");
      loggaaja.loggaa("Lis�si harrastajalle '%s' huoltajan", harrastaja);
   }

   public void lis��Harrastaja()
   {
      resetoi();
      // tabi.setSelectedIndex(0);
      harrastaja = new Harrastaja();
      info("Uusi harrastaja alustettu");
      fokusoi("form:etunimi");
      loggaaja.loggaa("Lis�si harrastajan");
   }

   public void lis��Sopimus()
   {
      sopimusRSM.setAllSelected(false);
      sopimus = new Sopimus(harrastaja, ehdotaSopimusTyyppi�());
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
      Vy�arvo seuraavaVy�arvo = vy�koehelper.haeSeuraavaVy�arvo(harrastaja);
      vy�koe.setVy�arvo(seuraavaVy�arvo);
      vy�koeRSM.setAllSelected(false);
      info("Uusi vy�koe alustettu");
      loggaaja.loggaa("Lis�si harrastajalle '%s' vy�kokeen", harrastaja);
   }

   public void sopimustyyppiMuuttui()
   {
      sopimus.setTreenikertojaTilattu(sopimus.getTyyppi().getOletusTreenikerrat());
      sopimus.setTreenikertojaJ�ljell�(sopimus.getTreenikertojaTilattu());
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

   public boolean isArkiiviMoodi()
   {
      return arkiiviMoodi;
   }

   public void setArkiiviMoodi(boolean arkiiviMoodi)
   {
      this.arkiiviMoodi = arkiiviMoodi;
   }

   public void tulostaTilap�isenMateriaalit() throws IOException
   {
      tulostaTilap�inenHarjoittelukortti();
      tulostaMateriaali("s��nn�t");
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

   private void tulostaTilap�inenHarjoittelukortti() throws IOException
   {
      byte[] kuva = Tilap�iskortti.teeKortti(harrastaja.getNimi(), harrastaja.getId() + 10000 + "");
      tulostaja.tulostaTiedosto(BlobData.PDF("kortti", kuva));
   }

}
