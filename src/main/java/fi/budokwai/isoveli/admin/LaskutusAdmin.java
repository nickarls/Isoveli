package fi.budokwai.isoveli.admin;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.event.ValueChangeEvent;
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
import fi.budokwai.isoveli.util.Util;

@Stateful
@SessionScoped
@Named
public class LaskutusAdmin extends Perustoiminnallisuus
{
   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   private RowStateMap laskuttamattomatRSM = new RowStateMap();
   private RowStateMap maksamattomatRSM = new RowStateMap();
   private RowStateMap myöhästyneetRSM = new RowStateMap();
   private RowStateMap maksetutRSM = new RowStateMap();

   private List<Sopimus> laskuttamattomat;
   private List<Lasku> maksamattomat;
   private List<Lasku> myöhästyneet;
   private List<Lasku> maksetut;

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
   public List<Lasku> getMyöhästyneet()
   {
      if (myöhästyneet == null)
      {
         haeMyöhästyneet();
      }
      return myöhästyneet;
   }

   @Produces
   @Named
   public List<Lasku> getMaksamattomat()
   {
      if (maksamattomat == null)
      {
         haeMaksamattomat();
      }
      return maksamattomat;
   }

   @Produces
   @Named
   public List<Lasku> getMaksetut()
   {
      if (maksetut == null)
      {
         haeMaksetut();
      }
      return maksetut;
   }

   private void haeLaskuttamattomat()
   {
      laskuttamattomat = entityManager.createNamedQuery("laskuttamattomat_sopimukset", Sopimus.class).getResultList();
   }

   private void haeMyöhästyneet()
   {
      myöhästyneet = entityManager.createNamedQuery("myöhästyneet_laskut", Lasku.class)
         .setParameter("tänään", Util.tänään()).getResultList();
   }

   private void haeMaksamattomat()
   {
      maksamattomat = entityManager.createNamedQuery("maksamattomat_laskut", Lasku.class)
         .setParameter("tänään", Util.tänään()).getResultList();
   }

   private void haeMaksetut()
   {
      maksetut = entityManager.createNamedQuery("maksetut_laskut", Lasku.class).getResultList();
   }

   public RowStateMap getLaskuttamattomatRSM()
   {
      return laskuttamattomatRSM;
   }

   public void setLaskuttamattomatRSM(RowStateMap laskuttamattomatRSM)
   {
      this.laskuttamattomatRSM = laskuttamattomatRSM;
   }

   public RowStateMap getMaksamattomatRSM()
   {
      return maksamattomatRSM;
   }

   public void setMaksamattomatRSM(RowStateMap maksamattomatRSM)
   {
      this.maksamattomatRSM = maksamattomatRSM;
   }

   public RowStateMap getMyöhästyneetRSM()
   {
      return myöhästyneetRSM;
   }

   public void setMyöhästyneetRSM(RowStateMap myöhästyneetRSM)
   {
      this.myöhästyneetRSM = myöhästyneetRSM;
   }

   public RowStateMap getMaksetutRSM()
   {
      return maksetutRSM;
   }

   public void setMaksetutRSM(RowStateMap maksetutRSM)
   {
      this.maksetutRSM = maksetutRSM;
   }

   public void tabiMuuttui(ValueChangeEvent e)
   {
      int uusiTabi = (int) e.getNewValue();
      switch (uusiTabi)
      {
      case 0:
         myöhästyneet = null;
      case 1:
         maksamattomat = null;
      case 2:
         laskuttamattomat = null;
      case 3:
         maksetut = null;
      }
   }

   public void laskuValittu(SelectEvent e)
   {
   }
}
