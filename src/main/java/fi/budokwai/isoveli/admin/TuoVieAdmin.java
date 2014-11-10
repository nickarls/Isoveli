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
import fi.budokwai.isoveli.malli.Henkil�;
import fi.budokwai.isoveli.malli.Perhe;
import fi.budokwai.isoveli.malli.Sukupuoli;

@Named
@SessionScoped
@Stateful
public class TuoVieAdmin extends Perustoiminnallisuus
{
   private List<Henkil�> tuodutHenkil�t = new ArrayList<Henkil�>();
   private RowStateMap harrastajaRSM = new RowStateMap();

   @PersistenceContext
   private EntityManager entityManager;

   @Produces
   @Named
   public List<Henkil�> getTuodutHenkil�t()
   {
      return tuodutHenkil�t;
   }

   public void k�sittele()
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
            tuodutHenkil�t = tuoExcel(tieto);
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
      List<String> j�sennumerot = vanhatHarrastajat.stream().map(h -> h.getJ�sennumero()).collect(Collectors.toList());
      List<Harrastaja> uudet = tuodutHenkil�t.stream().filter(h -> !j�sennumerot.contains(h.getJ�sennumero()))
         .collect(Collectors.toList());
      uudet.forEach(h -> harrastajaRSM.get(h).setSelected(true));
   }

   private class ExcelTuoja
   {
      private Workbook ty�kirja;
      private Sheet v�lilehti;
      private Map<String, Integer> sarakemappaus;
      private int rivi;

      public ExcelTuoja(byte[] tieto) throws BiffException, IOException
      {
         ty�kirja = Workbook.getWorkbook(new ByteArrayInputStream(tieto));
         v�lilehti = ty�kirja.getSheet(0);
         sarakemappaus = Arrays.asList(v�lilehti.getRow(0)).stream()
            .collect(Collectors.toMap(Cell::getContents, Cell::getColumn));
      }

      public List<Henkil�> tuoHenkil�t()
      {
         List<Henkil�> henkil�t = new ArrayList<Henkil�>();
         Perhe perhe = null;
         int perheluku = 0;
         for (rivi = 1; rivi < v�lilehti.getRows(); rivi++)
         {
            perheluku = haeInt("Perhe");
            if (perhe == null && perheluku > 0)
            {
               perhe = new Perhe();
            }
            boolean huoltaja = haeBoolean("Huoltaja");
            if (huoltaja)
            {
               Henkil� henkil� = new Henkil�();
               henkil�.setEtunimi(haeString("Etunimi"));
               henkil�.setSukunimi(haeString("Sukunimi"));
               henkil�.setArkistoitu(haeBoolean("Arkistoitu"));
               henkil�.getOsoite().setOsoite(haeString("Osoite"));
               henkil�.getOsoite().setKaupunki(haeString("Kaupunki"));
               henkil�.getOsoite().setPostinumero(haeString("Postinumero"));
               henkil�.getYhteystiedot().setS�hk�posti(haeString("Sposti"));
               henkil�.getYhteystiedot().setS�hk�postilistalla(haeBoolean("Spostilistalla"));
               henkil�.siivoa();
               henkil�t.add(henkil�);
            } else
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
               harrastaja.getYhteystiedot().setS�hk�posti(haeString("Sposti"));
               harrastaja.getYhteystiedot().setS�hk�postilistalla(haeBoolean("Spostilistalla"));
               harrastaja.setJ�sennumero(haeString("J�sennumero"));
               harrastaja.setKorttinumero(haeString("Korttinumero"));
               harrastaja.setLisenssinumero(haeString("Lisenssinumero"));
               harrastaja.siivoa();
               henkil�t.add(harrastaja);
            }
            perheluku--;
         }
         return henkil�t;
      }

      private int haeInt(String sarake)
      {
         try
         {
            return Integer.parseInt(haeString(sarake));
         } catch (NumberFormatException e)
         {
            return 0;
         }
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
         return v�lilehti.getCell(sarakemappaus.get(sarake), rivi);
      }

      public void valmis()
      {
         ty�kirja.close();
      }
   }

   private List<Henkil�> tuoExcel(byte[] tieto) throws BiffException, IOException
   {
      ExcelTuoja excelTuoja = new ExcelTuoja(tieto);
      try
      {
         return excelTuoja.tuoHenkil�t();
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
