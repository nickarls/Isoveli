package fi.budokwai.isoveli.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.icefaces.ace.event.SelectEvent;
import org.icefaces.ace.model.table.RowStateMap;

import fi.budokwai.isoveli.IsoveliPoikkeus;
import fi.budokwai.isoveli.malli.BlobData;
import fi.budokwai.isoveli.malli.Lasku;
import fi.budokwai.isoveli.malli.Osoite;
import fi.budokwai.isoveli.malli.Sopimus;
import fi.budokwai.isoveli.util.Lasku2PDF;

@Stateful
@SessionScoped
@Named
public class LaskutusAdmin extends Perustoiminnallisuus
{
   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

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
      tilasuodatukset.add(new SelectItem("X", "Mitätöity", "Mitätöity", false, false, false));
   }

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

   public void laskutaSopimukset()
   {
      List<Sopimus> laskuttamattomatSopimukset = entityManager.createNamedQuery("laskuttamattomat_sopimukset",
         Sopimus.class).getResultList();
      Map<Osoite, List<Sopimus>> sopimuksetPerOsoite = laskuttamattomatSopimukset.stream().collect(
         Collectors.groupingBy(sopimus -> sopimus.getHarrastaja().isAlaikäinen() ? sopimus.getHarrastaja().getOsoite()
            : sopimus.getHarrastaja().getPerhe().getOsoite()));
      sopimuksetPerOsoite.keySet().forEach(osoite -> {
         List<Sopimus> sopimukset = sopimuksetPerOsoite.get(osoite);
         Lasku lasku = new Lasku(sopimukset);
         entityManager.persist(lasku);
         byte[] pdf = null;
         try
         {
            pdf = teePdfLasku(lasku);
         } catch (Exception e)
         {
            throw new IsoveliPoikkeus("Laskun luonti epäonnistui", e);
         }
         lasku.setPdf(BlobData.PDF(String.format("lasku-%d", lasku.getId()), pdf));
         entityManager.persist(lasku);
         sopimukset.forEach(sopimus -> {
            entityManager.persist(sopimus);
         });
         entityManager.flush();
      });
      haeLaskuttamattomat();
      info("Muodosti %d sopimuksesta %d laskua", laskuttamattomatSopimukset.size(), sopimuksetPerOsoite.keySet().size());
   }

   private byte[] teePdfLasku(Lasku lasku) throws Exception
   {
      Optional<BlobData> mallit = entityManager.createNamedQuery("blobdata", BlobData.class)
         .setParameter("nimi", "laskupohja").getResultList().stream().findFirst();
      if (!mallit.isPresent())
      {
         throw new IsoveliPoikkeus("Laskumallia ei löytynyt");
      }
      byte[] malli = mallit.get().getTieto();
      return new Lasku2PDF(malli, lasku).muodosta();
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
      laskuttamattomat = entityManager.createNamedQuery("laskuttamattomat_sopimukset", Sopimus.class).getResultList();
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
      lasku.getLaskurivit().forEach(l -> {
         l.getSopimus().setLaskurivi(null);
         l.setSopimus(null);
         entityManager.persist(l);
      });
      entityManager.remove(lasku);
      info("Lasku poistettu");
      laskut = null;
      haeLaskut();
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
}
