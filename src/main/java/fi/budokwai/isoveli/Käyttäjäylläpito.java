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
import fi.budokwai.isoveli.malli.Henkil�;
import fi.budokwai.isoveli.malli.Henkil�viesti;
import fi.budokwai.isoveli.malli.J�ljell�Vy�kokeeseen;
import fi.budokwai.isoveli.malli.Tiedostotyyppi;
import fi.budokwai.isoveli.malli.Viestilaatikko;
import fi.budokwai.isoveli.malli.Viestilaatikkotyypi;
import fi.budokwai.isoveli.malli.Vy�arvo;
import fi.budokwai.isoveli.malli.Vy�koe;
import fi.budokwai.isoveli.util.Kirjautunut;
import fi.budokwai.isoveli.util.Vy�koehelper;

@Stateful
@SessionScoped
@Named
public class K�ytt�j�yll�pito extends Perustoiminnallisuus
{
   @Inject
   private EntityManager entityManager;

   @Inject
   @Kirjautunut
   private Henkil� itse;

   @Inject
   private List<Vy�arvo> vy�arvot;

   @Inject
   private Vy�koehelper vy�koehelper;

   private Henkil� yll�pidett�v�;

   private RowStateMap vy�koeRSM = new RowStateMap();
   private Vy�koe vy�koe;

   private RowStateMap viestiRSM = new RowStateMap();
   private Henkil�viesti viesti;

   private TabSet tabi;

   @PostConstruct
   public void init()
   {
      itse = entityManager.merge(itse);
      yll�pidett�v� = itse;
   }

   @Produces
   @Named
   public Henkil�viesti getViesti()
   {
      return viesti;
   }

   public void arkistoiViesti()
   {
      Viestilaatikko saapuvat = yll�pidett�v�.getViestilaatikko(Viestilaatikkotyypi.I);
      Viestilaatikko arkisto = yll�pidett�v�.getViestilaatikko(Viestilaatikkotyypi.A);
      saapuvat.poistaViesti(viesti);
      arkisto.lis��Viesti(viesti);
      saapuvat = entityManager.merge(saapuvat);
      arkisto = entityManager.merge(arkisto);
   }

   public void esifokus()
   {
      if (FacesContext.getCurrentInstance().isPostback())
      {
         return;
      }
      if (itse.isHarrastaja() && !itse.isAlaik�inen() && !((Harrastaja) itse).isSopimuksetOK())
      {
         tabi.setSelectedIndex(3);
      }
   }

   @Produces
   @Named
   public Henkil� getKohde()
   {
      return yll�pidett�v�;
   }

   @Produces
   @Named
   public List<Henkil�> getYll�pidett�v�t()
   {
      List<Henkil�> huollettavat = itse.getHuollettavat();
      huollettavat.add(0, itse);
      return huollettavat;
   }

   @Produces
   @Named
   public Vy�koe getOmaVy�koe()
   {
      return vy�koe;
   }

   public void tallennaVy�koe()
   {
      Harrastaja harrastaja = (Harrastaja) itse;
      if (!harrastaja.getVy�kokeet().contains(vy�koe))
      {
         vy�koe.setHarrastaja(harrastaja);
         harrastaja.getVy�kokeet().add(vy�koe);
         harrastaja.getVy�kokeet().sort(
            (v1, v2) -> Integer.valueOf(v1.getVy�arvo().getJ�rjestys()).compareTo(
               Integer.valueOf(v2.getVy�arvo().getJ�rjestys())));
      }
      itse = entityManager.merge(itse);
      vy�koeRSM.get(vy�koe).setSelected(true);
      vy�koe = null;
      info("Vy�koe tallennettu");
   }

   public void poistaVy�koe()
   {
      Harrastaja harrastaja = (Harrastaja) itse;
      vy�koe = entityManager.merge(vy�koe);
      harrastaja.getVy�kokeet().remove(vy�koe);
      harrastaja = entityManager.merge(harrastaja);
      entityManager.flush();
      info("Vy�koe poistettu");
   }

   public void lis��Vy�koe()
   {
      Harrastaja harrastaja = (Harrastaja) itse;
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

   public void piilotaVy�koe()
   {
      vy�koe = null;
   }

   public void vy�koeValittu(SelectEvent e)
   {
      vy�koe = (Vy�koe) e.getObject();
   }

   public void viestiValittu(SelectEvent e)
   {
      viesti = (Henkil�viesti) e.getObject();
      viesti.setLuettu(true);
      viesti = entityManager.merge(viesti);
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
            if (yll�pidett�v�.isKuvallinen())
            {
               yll�pidett�v�.getKuva().setTieto(tieto);
            } else
            {
               yll�pidett�v�.setKuva(new BlobData(String.format("kuva-%d", yll�pidett�v�.getId()), tieto,
                  Tiedostotyyppi.JPG));
            }
            info("Kuva p�ivitetty");
            itse = entityManager.merge(itse);
            fileInfo.getFile().delete();
         }
      }
   }

   @Produces
   @Named
   public Henkil� getItse()
   {
      return itse;
   }

   public String tallennaKohde()
   {
      yll�pidett�v� = entityManager.merge(yll�pidett�v�);
      info("Tiedot tallennettu");
      return "k�ytt�j�.xhtml?faces-redirect=true";
   }

   public List<GaugeSeries> getAikaaVy�kokeeseen()
   {
      Harrastaja harrastaja = (Harrastaja) yll�pidett�v�;
      List<GaugeSeries> data = new ArrayList<GaugeSeries>();
      GaugeSeries sarja = new GaugeSeries();
      J�ljell�Vy�kokeeseen j�ljell�Vy�kokeeseen = vy�koehelper.getJ�ljell�Vy�kokeeseen(harrastaja);
      int max = j�ljell�Vy�kokeeseen.getSeuraavaVy�arvo().getMinimikuukaudet() * 30;
      sarja.setMax(max);
      sarja.setMin(0);
      long arvo = max - j�ljell�Vy�kokeeseen.getP�ivi�();
      sarja.setValue(arvo);
      sarja.setLabel("Aikarajoitus");
      data.add(sarja);
      return Arrays.asList(new GaugeSeries[]
      { sarja });
   }

   public List<GaugeSeries> getTreenej�Vy�kokeeseen()
   {
      Harrastaja harrastaja = (Harrastaja) yll�pidett�v�;
      List<GaugeSeries> data = new ArrayList<GaugeSeries>();
      GaugeSeries sarja = new GaugeSeries();
      J�ljell�Vy�kokeeseen j�ljell�Vy�kokeeseen = vy�koehelper.getJ�ljell�Vy�kokeeseen(harrastaja);
      int max = j�ljell�Vy�kokeeseen.getSeuraavaVy�arvo().getMinimitreenit();
      sarja.setMax(max);
      sarja.setMin(0);
      int arvo = (int) harrastaja.getTreenej�ViimeVy�kokeesta();
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

   public TabSet getTabi()
   {
      return tabi;
   }

   public void setTabi(TabSet tabi)
   {
      this.tabi = tabi;
   }

   public Henkil� getYll�pidett�v�()
   {
      return yll�pidett�v�;
   }

   public void setYll�pidett�v�(Henkil� yll�pidett�v�)
   {
      this.yll�pidett�v� = yll�pidett�v�;
   }

   public RowStateMap getViestiRSM()
   {
      return viestiRSM;
   }

   public void setViestiRSM(RowStateMap viestiRSM)
   {
      this.viestiRSM = viestiRSM;
   }

}
