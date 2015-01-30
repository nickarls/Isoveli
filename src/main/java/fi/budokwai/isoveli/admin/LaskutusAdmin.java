package fi.budokwai.isoveli.admin;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.icefaces.ace.event.SelectEvent;
import org.icefaces.ace.model.table.RowStateMap;

import fi.budokwai.isoveli.Asetukset;
import fi.budokwai.isoveli.IsoveliPoikkeus;
import fi.budokwai.isoveli.malli.BlobData;
import fi.budokwai.isoveli.malli.Henkil�;
import fi.budokwai.isoveli.malli.Lasku;
import fi.budokwai.isoveli.malli.Laskurivi;
import fi.budokwai.isoveli.malli.Osoite;
import fi.budokwai.isoveli.malli.Sopimus;
import fi.budokwai.isoveli.malli.Sopimuslasku;
import fi.budokwai.isoveli.util.Lasku2PDF;
import fi.budokwai.isoveli.util.Loggaaja;
import fi.budokwai.isoveli.util.MailManager;
import fi.budokwai.isoveli.util.Util;
import fi.budokwai.isoveli.util.Zippaaja;

@Stateful
@SessionScoped
@Named
public class LaskutusAdmin extends Perustoiminnallisuus
{
   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   @Inject
   private MailManager mailManager;

   @Inject
   private Loggaaja loggaaja;

   private RowStateMap laskuRSM = new RowStateMap();
   private RowStateMap laskuttamattomatRSM = new RowStateMap();
   private List<Sopimus> laskuttamattomat;
   private List<Lasku> laskut;
   private Lasku lasku;
   private Sopimus sopimus;
   private List<SelectItem> tilasuodatukset;

   @PostConstruct
   public void init()
   {
      tilasuodatukset = new ArrayList<SelectItem>();
      tilasuodatukset.add(new SelectItem("", "Kaikki", "Kaikki", false, false, true));
      tilasuodatukset.add(new SelectItem("A", "Avoin", "Avoin", false, false, false));
      tilasuodatukset.add(new SelectItem("M", "Maksettu", "Maksettu", false, false, false));
      tilasuodatukset.add(new SelectItem("X", "Mit�t�ity", "Mit�t�ity", false, false, false));
   }

   @Inject
   private Asetukset asetukset;

   @Produces
   @Named
   public Lasku getLasku()
   {
      return lasku;
   }

   @Produces
   @Named
   public Sopimus getLaskuttamatonSopimus()
   {
      return sopimus;
   }

   @Produces
   @Named
   public List<SelectItem> getTilasuodatukset()
   {
      return tilasuodatukset;
   }

   public void l�het�Laskut()
   {
      List<Lasku> laskuttamattomat = entityManager.createNamedQuery("laskuttamattomat_laskut", Lasku.class)
         .getResultList();
      laskuttamattomat.forEach(lasku -> {
         try
         {
            mailManager.l�het�S�hk�posti(lasku.getHenkil�().getYhteystiedot().getS�hk�posti(), "Lasku", "Liitteen�",
               lasku.getPdf());
            lasku.setLaskutettu(true);
            entityManager.persist(lasku);
         } catch (IsoveliPoikkeus e)
         {

         }
      });
   }

   public void laskutaSopimukset()
   {
      List<Sopimus> sopimukset = entityManager.createNamedQuery("laskuttamattomat_sopimukset", Sopimus.class)
         .setParameter("nyt", Util.t�n��n()).getResultList();
      sopimukset.addAll(entityManager.createNamedQuery("uudet_sopimukset", Sopimus.class).getResultList());
      Map<Osoite, List<Sopimus>> sopimuksetPerOsoite = sopimukset.stream().collect(
         Collectors.groupingBy(sopimus -> sopimus.getLaskutusosoite()));
      sopimuksetPerOsoite.values().forEach(osoitteenSopimukset -> teeLaskuOsoitteelle(osoitteenSopimukset));
      haeLaskuttamattomat();
      info("Muodosti %d sopimuksesta %d laskua", sopimukset.size(), sopimuksetPerOsoite.keySet().size());
      loggaaja.loggaa("Suoritti laskutusajon");
   }

   private void teeLaskuOsoitteelle(List<Sopimus> sopimukset)
   {
      Henkil� henkil� = haeLaskunVastaanottaja(sopimukset);
      Lasku lasku = new Lasku(henkil�);
      sopimukset.forEach(sopimus -> {
         do
         {
            Sopimuslasku sopimuslasku = new Sopimuslasku(sopimus);
            Laskurivi laskurivi = new Laskurivi(sopimuslasku);
            lasku.lis��Rivi(laskurivi);
         } while (!sopimus.valmiiksiLaskutettu());
      });
      entityManager.persist(lasku);
      byte[] pdf = teePdfLasku(lasku);
      lasku.setPdf(BlobData.PDF(String.format("lasku-%d", lasku.getId()), pdf));
      entityManager.persist(lasku);
      loggaaja.loggaa(String.format("Teki laskun henkil�lle %s", henkil�.getNimi()));
   }

   public void testaa() throws IOException
   {
      Lasku lasku = entityManager.find(Lasku.class, 1);
      byte[] data = teePdfLasku(lasku);
      FileOutputStream out = new FileOutputStream("c:/temp/lasku.pdf");
      out.write(data);
      out.flush();
      out.close();
   }

   private Henkil� haeLaskunVastaanottaja(List<Sopimus> sopimukset)
   {
      List<Henkil�> huoltajat = sopimukset.stream().filter(sopimus -> sopimus.getHarrastaja().getHuoltaja() != null)
         .map(sopimus -> sopimus.getHarrastaja().getHuoltaja()).collect(Collectors.toList());
      if (!huoltajat.isEmpty())
      {
         return huoltajat.stream().findFirst().get();
      }
      List<Henkil�> t�ysiik�iset = sopimukset.stream().map(sopimus -> sopimus.getHarrastaja())
         .filter(harrastaja -> !harrastaja.isAlaik�inen()).collect(Collectors.toList());
      if (!t�ysiik�iset.isEmpty())
      {
         return t�ysiik�iset.stream().findFirst().get();
      }
      return sopimukset.stream().findFirst().get().getHarrastaja();
   }

   private byte[] teePdfLasku(Lasku lasku)
   {
      byte[] malli = null;
      Optional<BlobData> mallit = entityManager.createNamedQuery("nimetty_blobdata", BlobData.class)
         .setParameter("nimi", "laskupohja").getResultList().stream().findFirst();
      if (mallit.isPresent())
      {
         malli = mallit.get().getTieto();
      } else
      {
         ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
         try
         {
            Path path = Paths.get(ec.getResource("/WEB-INF/classes/laskupohja.pdf").toURI());
            malli = Files.readAllBytes(path);
         } catch (IOException | URISyntaxException e)
         {
            throw new IsoveliPoikkeus("Mallin lukeminen ep�onnistui", e);
         }
      }
      return new Lasku2PDF(malli, lasku, asetukset).muodosta();
   }

   @Produces
   @Named
   public List<Sopimus> getLaskuttamattomat()
   {
      if (laskuttamattomat == null)
      {
         haeLaskuttamattomat();
      }
      return laskuttamattomat;
   }

   @Produces
   @Named
   public List<Lasku> getLaskut()
   {
      if (laskut == null)
      {
         haeLaskut();
      }
      return laskut;
   }

   private void haeLaskut()
   {
      laskut = entityManager.createNamedQuery("laskut", Lasku.class).getResultList();
   }

   private void haeLaskuttamattomat()
   {
      laskuttamattomat = entityManager.createNamedQuery("laskuttamattomat_sopimukset", Sopimus.class)
         .setParameter("nyt", Util.t�n��n()).getResultList();
      laskuttamattomat.addAll(entityManager.createNamedQuery("uudet_sopimukset", Sopimus.class).getResultList());
   }

   public void tabiMuuttui(ValueChangeEvent e)
   {
      int uusiTabi = (int) e.getNewValue();
      switch (uusiTabi)
      {
      case 0:
         laskut = null;
      case 1:
         laskuttamattomat = null;
      }
      entityManager.clear();
   }

   public void laskuValittu(SelectEvent e)
   {
      lasku = (Lasku) e.getObject();
   }

   public void sopimusValittu(SelectEvent e)
   {
      sopimus = (Sopimus) e.getObject();
   }

   public void piilotaSopimus()
   {
      sopimus = null;
   }

   public void piilotaLasku()
   {
      lasku = null;
   }

   public void poistaLasku()
   {
      // lasku.getLaskurivit().forEach(l -> {
      // l.getSopimus().setLaskurivi(null);
      // l.setSopimus(null);
      // entityManager.persist(l);
      // });
      // entityManager.remove(lasku);
      // info("Lasku poistettu");
      // laskut = null;
      // haeLaskut();
   }

   public RowStateMap getLaskuRSM()
   {
      return laskuRSM;
   }

   public void setLaskuRSM(RowStateMap laskuRSM)
   {
      this.laskuRSM = laskuRSM;
   }

   public RowStateMap getLaskuttamattomatRSM()
   {
      return laskuttamattomatRSM;
   }

   public void setLaskuttamattomatRSM(RowStateMap laskuttamattomatRSM)
   {
      this.laskuttamattomatRSM = laskuttamattomatRSM;
   }

   public BlobData zippaaLaskuttamattomat()
   {
      List<Lasku> laskuttamattomat = entityManager.createNamedQuery("laskuttamattomat_laskut", Lasku.class)
         .getResultList();
      Zippaaja zippaaja = new Zippaaja();
      laskuttamattomat.forEach(lasku -> {
         zippaaja.lis��ZipTiedostoon(String.format("%s.pdf", lasku.getHenkil�().getNimi()), lasku.getPdf().getTieto());
      });
      return zippaaja.haeZipTiedosto("laskuttamattomat");
   }
}
