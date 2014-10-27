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
import fi.budokwai.isoveli.malli.J�ljell�Vy�kokeeseen;
import fi.budokwai.isoveli.malli.Tiedostotyyppi;
import fi.budokwai.isoveli.malli.Vy�arvo;
import fi.budokwai.isoveli.malli.Vy�koe;
import fi.budokwai.isoveli.util.Kirjautunut;

@Stateful
@SessionScoped
@Named
public class K�ytt�j�yll�pito extends Perustoiminnallisuus
{
   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   @Inject
   @Kirjautunut
   private Harrastaja itse;

   private List<Vy�arvo> vy�arvot;

   private RowStateMap vy�koeRSM = new RowStateMap();
   private Vy�koe vy�koe;

   @PostConstruct
   public void init()
   {
      vy�arvot = entityManager.createNamedQuery("vy�arvot", Vy�arvo.class).getResultList();
      itse = entityManager.merge(itse);
   }

   @Produces
   @Named
   public Vy�koe getOmaVy�koe()
   {
      return vy�koe;
   }

   @Produces
   @Named
   public List<Vy�arvo> getVy�arvot()
   {
      return vy�arvot;
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

   public void tallennaVy�koe()
   {
      if (!itse.getVy�kokeet().contains(vy�koe))
      {
         vy�koe.setHarrastaja(itse);
         itse.getVy�kokeet().add(vy�koe);
         itse.getVy�kokeet().sort(
            (v1, v2) -> Integer.valueOf(v1.getVy�arvo().getJ�rjestys()).compareTo(
               Integer.valueOf(v2.getVy�arvo().getJ�rjestys())));
      }
      entityManager.persist(itse);
      vy�koeRSM.get(vy�koe).setSelected(true);
      vy�koe = null;
      info("Vy�koe tallennettu");
   }

   public void poistaVy�koe()
   {
      itse.getVy�kokeet().remove(vy�koe);
      itse = entityManager.merge(itse);
      entityManager.flush();
      info("Vy�koe poistettu");
   }

   public void lis��Vy�koe()
   {
      vy�koe = new Vy�koe();
      Vy�arvo seuraavaVy�arvo = vy�arvot.iterator().next();
      if (!itse.getVy�kokeet().isEmpty())
      {
         seuraavaVy�arvo = vy�arvot.get(vy�arvot.indexOf(itse.getTuoreinVy�arvo()) + 1);
      }
      vy�koe.setVy�arvo(seuraavaVy�arvo);
      vy�koe.setP�iv�(new Date());
      vy�koe.setHarrastaja(itse);
      vy�koeRSM.setAllSelected(false);
      info("Uusi vy�koe alustettu");
   }

   public void piilotaVy�koe()
   {
      vy�koe = null;
   }

   public void vy�koeValittu(SelectEvent e)
   {
      vy�koe = (Vy�koe) e.getObject();
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
            info("Kuva p�ivitetty");
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
      return "k�ytt�j�.xhtml?faces-redirect=true";
   }

   public List<GaugeSeries> getAikaaVy�kokeeseen()
   {
      List<GaugeSeries> data = new ArrayList<GaugeSeries>();
      GaugeSeries sarja = new GaugeSeries();
      J�ljell�Vy�kokeeseen j�ljell�Vy�kokeeseen = itse.getJ�ljell�Vy�kokeeseen(vy�arvot);
      int max = j�ljell�Vy�kokeeseen.getSeuraavaVy�arvo().getMinimikuukaudet() * 30;
      sarja.setMax(max);
      sarja.setMin(0);
      int arvo = max - j�ljell�Vy�kokeeseen.getAika().getDays();
      sarja.setValue(arvo);
      sarja.setLabel("Aikarajoitus");
      data.add(sarja);
      return Arrays.asList(new GaugeSeries[]
      { sarja });
   }

   public List<GaugeSeries> getTreenej�Vy�kokeeseen()
   {
      List<GaugeSeries> data = new ArrayList<GaugeSeries>();
      GaugeSeries sarja = new GaugeSeries();
      J�ljell�Vy�kokeeseen j�ljell�Vy�kokeeseen = itse.getJ�ljell�Vy�kokeeseen(vy�arvot);
      int max = j�ljell�Vy�kokeeseen.getSeuraavaVy�arvo().getMinimitreenit();
      sarja.setMax(max);
      sarja.setMin(0);
      int arvo = (int) itse.getTreenej�ViimeVy�kokeesta();
      sarja.setValue(arvo);
      sarja.setLabel("Treenirajoitus");
      data.add(sarja);
      return Arrays.asList(new GaugeSeries[]
      { sarja });
   }

   public RowStateMap getVy�koeRSM()
   {
      return vy�koeRSM;
   }

   public void setVy�koeRSM(RowStateMap vy�koeRSM)
   {
      this.vy�koeRSM = vy�koeRSM;
   }

}
