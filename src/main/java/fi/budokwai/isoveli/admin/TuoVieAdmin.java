package fi.budokwai.isoveli.admin;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import jxl.Cell;
import jxl.DateCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.icefaces.ace.model.table.RowStateMap;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Sukupuoli;

@Named
@SessionScoped
@Stateful
public class TuoVieAdmin extends Perustoiminnallisuus
{
   private List<Harrastaja> tuodutHarrastajat = new ArrayList<Harrastaja>();
   private RowStateMap harrastajaRSM = new RowStateMap();

   @PersistenceContext
   private EntityManager entityManager;

   @Produces
   @Named
   public List<Harrastaja> getTuodutHarrastajat()
   {
      return tuodutHarrastajat;
   }

   public void käsittele()
   {
      @SuppressWarnings("unchecked")
      List<Harrastaja> valitut = harrastajaRSM.getSelected();
      valitut.forEach(h -> entityManager.persist(h));
      harrastajaRSM.setAllSelected(false);
      info(String.format("%d harrastajaa tuotu", valitut.size()));
   }
   
   public void tuoExcel(FileEntryEvent event) throws IOException, BiffException
   {
      FileEntry fileEntry = (FileEntry) event.getSource();
      FileEntryResults results = fileEntry.getResults();
      for (FileEntryResults.FileInfo fileInfo : results.getFiles())
      {
         if (fileInfo.isSaved())
         {
            byte[] tieto = Files.readAllBytes(fileInfo.getFile().toPath());
            tuodutHarrastajat = tuoExcel(tieto);
            valitseUudetHarrastajat();
            info("Excel tuotu");
            fileInfo.getFile().delete();
         }
      }
   }

   private void valitseUudetHarrastajat()
   {
      List<Harrastaja> vanhatHarrastajat = entityManager.createNamedQuery("harrastajat", Harrastaja.class)
         .getResultList();
      List<String> jäsennumerot = vanhatHarrastajat.stream().map(h -> h.getJäsennumero()).collect(Collectors.toList());
      List<Harrastaja> uudet = tuodutHarrastajat.stream().filter(h -> !jäsennumerot.contains(h.getJäsennumero()))
         .collect(Collectors.toList());
      uudet.forEach(h -> harrastajaRSM.get(h).setSelected(true));
   }

   private class ExcelTuoja
   {
      private Workbook työkirja;
      private Sheet välilehti;
      private Map<String, Integer> sarakemappaus;
      private int rivi;

      public ExcelTuoja(byte[] tieto) throws BiffException, IOException
      {
         työkirja = Workbook.getWorkbook(new ByteArrayInputStream(tieto));
         välilehti = työkirja.getSheet(0);
         sarakemappaus = Arrays.asList(välilehti.getRow(0)).stream()
            .collect(Collectors.toMap(Cell::getContents, Cell::getColumn));
      }

      public List<Harrastaja> tuoHarrastajat()
      {
         List<Harrastaja> harrastajat = new ArrayList<Harrastaja>();
         for (rivi = 1; rivi < välilehti.getRows(); rivi++)
         {
            Harrastaja harrastaja = new Harrastaja();
            harrastaja.setEtunimi(haeString("Etunimi"));
            harrastaja.setSukunimi(haeString("Sukunimi"));
            harrastaja.setSukupuoli(haeEnum(Sukupuoli.class, "Sukupuoli"));
            harrastaja.setSyntynyt(haeDate("Syntynyt"));
            harrastaja.setIce(haeString("ICE"));
            harrastaja.setHuomautus(haeString("Huomautus"));
            harrastaja.setArkistoitu(haeBoolean("Arkistoitu"));
            harrastaja.getOsoite().setOsoite(haeString("Osoite"));
            harrastaja.getOsoite().setKaupunki(haeString("Kaupunki"));
            harrastaja.getOsoite().setPostinumero(haeString("Postinumero"));
            harrastaja.getYhteystiedot().setSähköposti(haeString("Sposti"));
            harrastaja.getYhteystiedot().setSähköpostilistalla(haeBoolean("Spostilistalla"));
            harrastaja.setJäsennumero(haeString("Jäsennumero"));
            harrastaja.setKorttinumero(haeString("Korttinumero"));
            harrastaja.setLisenssinumero(haeString("Lisenssinumero"));
            harrastaja.siivoa();
            harrastajat.add(harrastaja);
         }
         return harrastajat;
      }

      private boolean haeBoolean(String sarake)
      {
         return "K".equals(haeString(sarake)) ? true : false;
      }

      private Date haeDate(String sarake)
      {
         return ((DateCell) haeSolu(sarake)).getDate();
      }

      public <T extends Enum<T>> T haeEnum(Class<T> enumTyyppi, String sarake)
      {
         return Enum.valueOf(enumTyyppi, haeString(sarake));
      }

      private String haeString(String sarake)
      {
         return haeSolu(sarake).getContents();
      }

      private Cell haeSolu(String sarake)
      {
         return välilehti.getCell(sarakemappaus.get(sarake), rivi);
      }

      public void valmis()
      {
         työkirja.close();
      }
   }

   private List<Harrastaja> tuoExcel(byte[] tieto) throws BiffException, IOException
   {
      ExcelTuoja excelTuoja = new ExcelTuoja(tieto);
      try
      {
         return excelTuoja.tuoHarrastajat();
      } finally
      {
         excelTuoja.valmis();
      }
   }

   public RowStateMap getHarrastajaRSM()
   {
      return harrastajaRSM;
   }

   public void setHarrastajaRSM(RowStateMap harrastajaRSM)
   {
      this.harrastajaRSM = harrastajaRSM;
   }

}
