package fi.budokwai.isoveli;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.icefaces.ace.component.tabset.TabSet;
import org.icefaces.ace.event.SelectEvent;
import org.icefaces.ace.model.chart.GaugeSeries;
import org.icefaces.ace.model.table.RowStateMap;

import fi.budokwai.isoveli.admin.Perustoiminnallisuus;
import fi.budokwai.isoveli.malli.BlobData;
import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Henkilö;
import fi.budokwai.isoveli.malli.JäljelläVyökokeeseen;
import fi.budokwai.isoveli.malli.Tiedostotyyppi;
import fi.budokwai.isoveli.malli.Vyöarvo;
import fi.budokwai.isoveli.malli.Vyökoe;
import fi.budokwai.isoveli.util.Kirjautunut;

@Stateful
@SessionScoped
@Named
public class Käyttäjäylläpito extends Perustoiminnallisuus
{
   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   @Inject
   @Kirjautunut
   private Henkilö itse;

   private Henkilö ylläpidettävä;

   private List<Vyöarvo> vyöarvot;
   private RowStateMap vyökoeRSM = new RowStateMap();
   private Vyökoe vyökoe;
   private TabSet tabi;

   @PostConstruct
   public void init()
   {
      vyöarvot = entityManager.createNamedQuery("vyöarvot", Vyöarvo.class).getResultList();
      itse = entityManager.merge(itse);
      ylläpidettävä = itse;
   }

   public void esifokus()
   {
      if (FacesContext.getCurrentInstance().isPostback())
      {
         return;
      }
      if (itse.isHarrastaja() && !itse.isAlaikäinen() && !((Harrastaja) itse).isSopimuksetOK())
      {
         tabi.setSelectedIndex(3);
      }
   }

   @Produces
   @Named
   public Henkilö getKohde()
   {
      return ylläpidettävä;
   }

   @Produces
   @Named
   public List<Henkilö> getYlläpidettävät()
   {
      List<Henkilö> huollettavat = itse.getHuollettavat();
      huollettavat.add(0, itse);
      return huollettavat;
   }

   @Produces
   @Named
   public Vyökoe getOmaVyökoe()
   {
      return vyökoe;
   }

   @Produces
   @Named
   public List<Vyöarvo> getVyöarvot()
   {
      return vyöarvot;
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

   public void tallennaVyökoe()
   {
      Harrastaja harrastaja = (Harrastaja) itse;
      if (!harrastaja.getVyökokeet().contains(vyökoe))
      {
         vyökoe.setHarrastaja(harrastaja);
         harrastaja.getVyökokeet().add(vyökoe);
         harrastaja.getVyökokeet().sort(
            (v1, v2) -> Integer.valueOf(v1.getVyöarvo().getJärjestys()).compareTo(
               Integer.valueOf(v2.getVyöarvo().getJärjestys())));
      }
      entityManager.persist(itse);
      vyökoeRSM.get(vyökoe).setSelected(true);
      vyökoe = null;
      info("Vyökoe tallennettu");
   }

   public void poistaVyökoe()
   {
      Harrastaja harrastaja = (Harrastaja) itse;
      harrastaja.getVyökokeet().remove(vyökoe);
      harrastaja = entityManager.merge(harrastaja);
      entityManager.flush();
      info("Vyökoe poistettu");
   }

   public void lisääVyökoe()
   {
      Harrastaja harrastaja = (Harrastaja) itse;
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

   public void piilotaVyökoe()
   {
      vyökoe = null;
   }

   public void vyökoeValittu(SelectEvent e)
   {
      vyökoe = (Vyökoe) e.getObject();
   }

   public void kuvatallennus(FileEntryEvent event) throws IOException
   {
      FileEntry fileEntry = (FileEntry) event.getSource();
      FileEntryResults results = fileEntry.getResults();
      for (FileEntryResults.FileInfo fileInfo : results.getFiles())
      {
         if (fileInfo.isSaved())
         {
            byte[] tieto = Files.readAllBytes(fileInfo.getFile().toPath());
            if (ylläpidettävä.isKuvallinen())
            {
               ylläpidettävä.getKuva().setTieto(tieto);
            } else
            {
               ylläpidettävä.setKuva(new BlobData(String.format("kuva-%d", ylläpidettävä.getId()), tieto,
                  Tiedostotyyppi.JPG));
            }
            info("Kuva päivitetty");
            entityManager.persist(itse);
            fileInfo.getFile().delete();
         }
      }
   }

   @Produces
   @Named
   public Henkilö getItse()
   {
      return itse;
   }

   public String tallennaKohde()
   {
      entityManager.persist(ylläpidettävä);
      info("Tiedot tallennettu");
      return "käyttäjä.xhtml?faces-redirect=true";
   }

   public List<GaugeSeries> getAikaaVyökokeeseen()
   {
      Harrastaja harrastaja = (Harrastaja) ylläpidettävä;
      List<GaugeSeries> data = new ArrayList<GaugeSeries>();
      GaugeSeries sarja = new GaugeSeries();
      JäljelläVyökokeeseen jäljelläVyökokeeseen = harrastaja.getJäljelläVyökokeeseen(vyöarvot);
      int max = jäljelläVyökokeeseen.getSeuraavaVyöarvo().getMinimikuukaudet() * 30;
      sarja.setMax(max);
      sarja.setMin(0);
      int arvo = max - jäljelläVyökokeeseen.getAika().getDays();
      sarja.setValue(arvo);
      sarja.setLabel("Aikarajoitus");
      data.add(sarja);
      return Arrays.asList(new GaugeSeries[]
      { sarja });
   }

   public List<GaugeSeries> getTreenejäVyökokeeseen()
   {
      Harrastaja harrastaja = (Harrastaja) ylläpidettävä;
      List<GaugeSeries> data = new ArrayList<GaugeSeries>();
      GaugeSeries sarja = new GaugeSeries();
      JäljelläVyökokeeseen jäljelläVyökokeeseen = harrastaja.getJäljelläVyökokeeseen(vyöarvot);
      int max = jäljelläVyökokeeseen.getSeuraavaVyöarvo().getMinimitreenit();
      sarja.setMax(max);
      sarja.setMin(0);
      int arvo = (int) harrastaja.getTreenejäViimeVyökokeesta();
      sarja.setValue(arvo);
      sarja.setLabel("Treenirajoitus");
      data.add(sarja);
      return Arrays.asList(new GaugeSeries[]
      { sarja });
   }

   public RowStateMap getVyökoeRSM()
   {
      return vyökoeRSM;
   }

   public void setVyökoeRSM(RowStateMap vyökoeRSM)
   {
      this.vyökoeRSM = vyökoeRSM;
   }

   public TabSet getTabi()
   {
      return tabi;
   }

   public void setTabi(TabSet tabi)
   {
      this.tabi = tabi;
   }

   public Henkilö getYlläpidettävä()
   {
      return ylläpidettävä;
   }

   public void setYlläpidettävä(Henkilö ylläpidettävä)
   {
      this.ylläpidettävä = ylläpidettävä;
   }

}
