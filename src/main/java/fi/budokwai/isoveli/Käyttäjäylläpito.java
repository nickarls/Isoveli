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
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.icefaces.ace.event.SelectEvent;
import org.icefaces.ace.model.chart.GaugeSeries;
import org.icefaces.ace.model.table.RowStateMap;

import fi.budokwai.isoveli.admin.Perustoiminnallisuus;
import fi.budokwai.isoveli.malli.BlobData;
import fi.budokwai.isoveli.malli.Harrastaja;
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
   private Harrastaja itse;

   private List<Vyöarvo> vyöarvot;

   private RowStateMap vyökoeRSM = new RowStateMap();
   private Vyökoe vyökoe;

   @PostConstruct
   public void init()
   {
      vyöarvot = entityManager.createNamedQuery("vyöarvot", Vyöarvo.class).getResultList();
      itse = entityManager.merge(itse);
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
      if (!itse.getVyökokeet().contains(vyökoe))
      {
         vyökoe.setHarrastaja(itse);
         itse.getVyökokeet().add(vyökoe);
         itse.getVyökokeet().sort(
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
      itse.getVyökokeet().remove(vyökoe);
      itse = entityManager.merge(itse);
      entityManager.flush();
      info("Vyökoe poistettu");
   }

   public void lisääVyökoe()
   {
      vyökoe = new Vyökoe();
      Vyöarvo seuraavaVyöarvo = vyöarvot.iterator().next();
      if (!itse.getVyökokeet().isEmpty())
      {
         seuraavaVyöarvo = vyöarvot.get(vyöarvot.indexOf(itse.getTuoreinVyöarvo()) + 1);
      }
      vyökoe.setVyöarvo(seuraavaVyöarvo);
      vyökoe.setPäivä(new Date());
      vyökoe.setHarrastaja(itse);
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
            if (itse.isKuvallinen())
            {
               itse.getKuva().setTieto(tieto);
            } else
            {
               itse.setKuva(new BlobData(String.format("kuva-%d", itse.getId()), tieto, Tiedostotyyppi.JPG));
            }
            info("Kuva päivitetty");
            entityManager.persist(itse);
            fileInfo.getFile().delete();
         }
      }
   }

   @Produces
   @Named
   public Harrastaja getItse()
   {
      return itse;
   }

   public String tallennaItse()
   {
      entityManager.persist(itse);
      info("Tiedot tallennettu");
      return "käyttäjä.xhtml?faces-redirect=true";
   }

   public List<GaugeSeries> getAikaaVyökokeeseen()
   {
      List<GaugeSeries> data = new ArrayList<GaugeSeries>();
      GaugeSeries sarja = new GaugeSeries();
      JäljelläVyökokeeseen jäljelläVyökokeeseen = itse.getJäljelläVyökokeeseen(vyöarvot);
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
      List<GaugeSeries> data = new ArrayList<GaugeSeries>();
      GaugeSeries sarja = new GaugeSeries();
      JäljelläVyökokeeseen jäljelläVyökokeeseen = itse.getJäljelläVyökokeeseen(vyöarvot);
      int max = jäljelläVyökokeeseen.getSeuraavaVyöarvo().getMinimitreenit();
      sarja.setMax(max);
      sarja.setMin(0);
      int arvo = (int) itse.getTreenejäViimeVyökokeesta();
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

}
