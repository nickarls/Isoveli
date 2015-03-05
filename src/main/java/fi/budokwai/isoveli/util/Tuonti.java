package fi.budokwai.isoveli.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import fi.budokwai.isoveli.IsoveliPoikkeus;
import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Henkilö;
import fi.budokwai.isoveli.malli.Osoite;
import fi.budokwai.isoveli.malli.Perhe;
import fi.budokwai.isoveli.malli.Yhteystieto;

public class Tuonti
{

   private boolean tyhjä(String s)
   {
      return s == null || "".equals(s);
   }

   public Tuontitulos tuoJäsenrekisteri(byte[] excel) throws BiffException, IOException
   {
      Tuontitulos tulos = new Tuontitulos();
      Workbook työkirja = Workbook.getWorkbook(new ByteArrayInputStream(excel));
      Map<Integer, Perhe> perheet = haePerheet(työkirja.getSheet(1), tulos);
      haeHarrastajat(työkirja.getSheet(0), perheet, tulos);
      return tulos;
   }

   private void haeHarrastajat(Sheet välilehti, Map<Integer, Perhe> perheet, Tuontitulos tulos)
   {
      for (int rivi = 2; rivi < välilehti.getRows(); rivi++)
      {
         Harrastaja harrastaja = new Harrastaja();
         harrastaja.setSukunimi(välilehti.getCell(2, rivi).getContents().trim());
         harrastaja.setEtunimi(välilehti.getCell(3, rivi).getContents().trim());
         asetaSyntymäaika(välilehti, rivi, harrastaja);
         String perheId = välilehti.getCell(14, rivi).getContents().trim();
         valitsePerhe(perheId, perheet, harrastaja, tulos);
         asetaOmaOsoite(välilehti, rivi, harrastaja);
         asetaOmaYhteystieto(välilehti, rivi, harrastaja);
         säädäPerhetiedot(harrastaja);
         tulos.lisääHarrastaja(harrastaja);
      }
   }

   private void asetaSyntymäaika(Sheet välilehti, int rivi, Harrastaja harrastaja)
   {
      String vuosi = välilehti.getCell(7, rivi).getContents().trim();
      String kuukausi = välilehti.getCell(8, rivi).getContents().trim();
      String päivä = välilehti.getCell(9, rivi).getContents().trim();

      if (tyhjä(vuosi) || tyhjä(kuukausi) || tyhjä(päivä))
      {
         String maksuperuste = välilehti.getCell(15, rivi).getContents().trim();
         switch (maksuperuste)
         {
         case "junior":
            harrastaja.setSyntynyt(DateUtil.silloinD("01.01.2000"));
            break;
         case "aikuinen":
            harrastaja.setSyntynyt(DateUtil.silloinD("01.01.1970"));
            break;
         default:
            throw new IsoveliPoikkeus(String.format("Harrastajan %s syntymäaika puuttuu", harrastaja.getNimi()));
         }
      } else
      {
         harrastaja.setSyntynyt(DateUtil.string2Date(String.format("%s.%s.%s", päivä, kuukausi, vuosi)));
      }
   }

   private void asetaOmaYhteystieto(Sheet välilehti, int rivi, Harrastaja harrastaja)
   {
      Yhteystieto yhteystieto = new Yhteystieto();
      yhteystieto.setPuhelinnumero(välilehti.getCell(10, rivi).getContents().trim());
      yhteystieto.setSähköposti(välilehti.getCell(11, rivi).getContents().trim());
      yhteystieto.setSähköpostilistalla(true);
      harrastaja.setYhteystiedot(yhteystieto);
      if (!harrastaja.isAlaikäinen())
      {
         if (tyhjä(harrastaja.getYhteystiedot().getPuhelinnumero()))
         {
            harrastaja.lisääHuomautus("Tarkista oma puhelinnumero");
         }
         if (tyhjä(harrastaja.getYhteystiedot().getSähköposti()))
         {
            harrastaja.lisääHuomautus("Tarkista oma sähköpostiosoite");
         }
      }
   }

   private void asetaOmaOsoite(Sheet välilehti, int rivi, Harrastaja harrastaja)
   {
      Osoite osoite = new Osoite();
      osoite.setOsoite(välilehti.getCell(4, rivi).getContents().trim());
      osoite.setPostinumero(välilehti.getCell(5, rivi).getContents().trim());
      osoite.setKaupunki(välilehti.getCell(6, rivi).getContents().trim());
      harrastaja.setOsoite(osoite);
   }

   private void valitsePerhe(String perheId, Map<Integer, Perhe> perheet, Harrastaja harrastaja, Tuontitulos tulos)
   {
      if ("Edelman".equals(harrastaja.getSukunimi()))
      {
         System.out.println("!");
      }
      if (!tyhjä(perheId))
      {
         int pid = Integer.parseInt(perheId);
         if (!perheet.containsKey(pid))
         {
            tulos.lisääVirhe(String.format("Harrastajalla %s tuntematon perhe-id %d", harrastaja.getNimi(), pid));
         } else
         {
            Perhe perhe = perheet.get(pid);
            perhe.lisääPerheenjäsen(harrastaja);
         }
      }
      if (harrastaja.isAlaikäinen())
      {
         if (harrastaja.getPerhe() == null)
         {
            harrastaja.lisääHuomautus("Tarkista perhetiedot");
            Perhe perhe = luoPerhe(harrastaja.getSukunimi());
            perhe.lisääPerheenjäsen(harrastaja);
            harrastaja.setPerhe(perhe);
         }
         if (harrastaja.getPerhe().getHuoltajat().isEmpty())
         {
            Henkilö huoltaja = luoHuoltaja(harrastaja.getSukunimi());
            harrastaja.getPerhe().lisääPerheenjäsen(huoltaja);
            harrastaja.setHuoltaja(huoltaja);
            harrastaja.lisääHuomautus("Tarkista perhetiedot");
         } else
         {
            harrastaja.setHuoltaja(harrastaja.getPerhe().getHuoltajat().iterator().next());
         }
      }

   }

   private Perhe luoPerhe(String sukunimi)
   {
      Perhe perhe = new Perhe();
      Henkilö huoltaja = luoHuoltaja(sukunimi);
      perhe.lisääPerheenjäsen(huoltaja);
      return perhe;
   }

   private Henkilö luoHuoltaja(String sukunimi)
   {
      Henkilö huoltaja = new Henkilö();
      huoltaja.setEtunimi("Huoltaja");
      huoltaja.setSukunimi(sukunimi);
      huoltaja.setYhteystiedot(new Yhteystieto());
      return huoltaja;
   }

   private void säädäPerhetiedot(Harrastaja harrastaja)
   {
      Perhe perhe = harrastaja.getPerhe();
      if (perhe == null)
      {
         return;
      }
      perhe.setNimi(harrastaja.getSukunimi());
      if (tyhjä(perhe.getOsoite().getOsoite()))
      {
         perhe.getOsoite().setOsoite(harrastaja.getOsoite().getOsoite());
      }
      if (tyhjä(perhe.getOsoite().getPostinumero()))
      {
         perhe.getOsoite().setPostinumero(harrastaja.getOsoite().getPostinumero());
      }
      if (tyhjä(perhe.getOsoite().getKaupunki()))
      {
         perhe.getOsoite().setKaupunki(harrastaja.getOsoite().getKaupunki());
      }
      if (tyhjä(perhe.getOsoite().getOsoite()))
      {
         perhe.getOsoite().setOsoite("Kirstinkatu 1");
         harrastaja.lisääHuomautus("Tarkista perheen osoitetiedot (katu)");
      }
      if (tyhjä(perhe.getOsoite().getPostinumero()))
      {
         perhe.getOsoite().setPostinumero("20520");
         harrastaja.lisääHuomautus("Tarkista perheen osoitetiedot (postinumero)");
      }
      if (tyhjä(perhe.getOsoite().getKaupunki()))
      {
         perhe.getOsoite().setKaupunki("Turku");
         harrastaja.lisääHuomautus("Tarkista perheen osoitetiedot (kaupunki)");
      }
      perhe.getHuoltajat().forEach(h -> {
         int i = 1;
         if (tyhjä(h.getEtunimi()))
         {
            String nimi = String.format("Huoltaja%d", i++);
            h.setEtunimi(nimi);
            harrastaja.lisääHuomautus(String.format("Tarkista huoltajan etunimi %s", h.getEtunimi()));
         }
         if (tyhjä(h.getSukunimi()))
         {
            h.setSukunimi(harrastaja.getSukunimi());
            harrastaja.lisääHuomautus(String.format("Tarkista huoltajan sukunimi %s", h.getSukunimi()));
         }
         if (tyhjä(h.getYhteystiedot().getPuhelinnumero()))
         {
            harrastaja.lisääHuomautus(String.format("Huoltajan %s puhelinnumero puuttuu", h.getNimi()));
         }
         if (tyhjä(h.getYhteystiedot().getSähköposti()))
         {
            harrastaja.lisääHuomautus(String.format("Huoltajan %s sähköposti puuttuu", h.getNimi()));
         }
      });
   }

   private Map<Integer, Perhe> haePerheet(Sheet välilehti, Tuontitulos tulos)
   {
      Map<Integer, Perhe> perheet = new HashMap<>();
      for (int rivi = 1; rivi < välilehti.getRows(); rivi++)
      {
         Perhe perhe = new Perhe();
         String pid = välilehti.getCell(0, rivi).getContents();
         if ("".equals(pid))
         {
            continue;
         }
         perhe.setId(Integer.parseInt(pid));

         Henkilö huoltaja = new Henkilö();
         String nimi = välilehti.getCell(1, rivi).getContents().trim();
         String[] nimiosat = nimi.split(" ");
         if (nimiosat.length == 2)
         {
            huoltaja.setEtunimi(nimiosat[0]);
            huoltaja.setSukunimi(nimiosat[1]);
         }

         Yhteystieto yhteystieto = new Yhteystieto();
         yhteystieto.setPuhelinnumero(välilehti.getCell(2, rivi).getContents().trim());
         yhteystieto.setSähköposti(välilehti.getCell(3, rivi).getContents().trim());
         yhteystieto.setSähköpostilistalla(true);
         huoltaja.setYhteystiedot(yhteystieto);

         Osoite osoite = new Osoite();
         perhe.setOsoite(osoite);

         perhe.lisääPerheenjäsen(huoltaja);
         perheet.put(perhe.getId(), perhe);
      }
      return perheet;
   }
}
