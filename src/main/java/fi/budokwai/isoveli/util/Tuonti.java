package fi.budokwai.isoveli.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import fi.budokwai.isoveli.IsoveliPoikkeus;
import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Henkilö;
import fi.budokwai.isoveli.malli.Osoite;
import fi.budokwai.isoveli.malli.Perhe;
import fi.budokwai.isoveli.malli.Sukupuoli;
import fi.budokwai.isoveli.malli.Vyöarvo;
import fi.budokwai.isoveli.malli.Vyökoe;
import fi.budokwai.isoveli.malli.Yhteystieto;

public class Tuonti
{

   private Set<String> jäsennumerot = new HashSet<>();

   @Inject
   private List<Vyöarvo> vyöarvot;

   private boolean tyhjä(String s)
   {
      return s == null || "".equals(s);
   }

   public Tuontitulos tuoJäsenrekisteri(byte[] excel)
   {
      try
      {
         Tuontitulos tulos = new Tuontitulos();
         Workbook työkirja = Workbook.getWorkbook(new ByteArrayInputStream(excel));
         Map<Integer, Perhe> perheet = haePerheet(työkirja.getSheet(1), tulos);
         haeHarrastajat(työkirja.getSheet(0), perheet, tulos);
         return tulos;
      } catch (IOException | BiffException e)
      {
         throw new IsoveliPoikkeus("Excelin avaus epäonnistui", e);
      }
   }

   private void haeHarrastajat(Sheet välilehti, Map<Integer, Perhe> perheet, Tuontitulos tulos)
   {
      for (int rivi = 2; rivi < välilehti.getRows(); rivi++)
      {
         Harrastaja harrastaja = new Harrastaja();
         asetaNimi(välilehti, rivi, harrastaja);
         asetaLisenssinumero(välilehti, rivi, harrastaja);
         asetaSukupuoli(välilehti, rivi, harrastaja);
         asetaSyntymäaika(välilehti, rivi, harrastaja);
         asetaJäsennumero(harrastaja);
         String perheId = välilehti.getCell(14, rivi).getContents().trim();
         valitsePerhe(perheId, perheet, harrastaja, tulos);
         asetaOmaOsoite(välilehti, rivi, harrastaja);
         asetaOmaYhteystieto(välilehti, rivi, harrastaja);
         säädäPerhetiedot(harrastaja);
         asetaVyöarvo(välilehti, rivi, harrastaja, tulos);
         tulos.lisääHarrastaja(harrastaja);
      }
   }

   private void asetaVyöarvo(Sheet välilehti, int rivi, Harrastaja harrastaja, Tuontitulos tulos)
   {
      if (vyöarvot == null)
      {
         return;
      }
      String vyöarvoString = välilehti.getCell(17, rivi).getContents().trim();
      if (tyhjä(vyöarvoString))
      {
         return;
      }
      Optional<Vyöarvo> vyöarvo = vyöarvot.stream().filter(v -> v.getNimi().equals(vyöarvoString)).findFirst();
      if (vyöarvo.isPresent())
      {
         Vyökoe vyökoe = new Vyökoe();
         vyökoe.setHarrastaja(harrastaja);
         vyökoe.setPäivä(new Date());
         vyökoe.setVyöarvo(vyöarvo.get());
         harrastaja.getVyökokeet().add(vyökoe);
      } else
      {
         tulos.lisääVirhe(String.format("Harrastajalla %s tuntematon vyöarvo %s", harrastaja.getNimi(), vyöarvoString));
      }
   }

   private void asetaLisenssinumero(Sheet välilehti, int rivi, Harrastaja harrastaja)
   {
      harrastaja.setLisenssinumero(välilehti.getCell(1, rivi).getContents().trim());
      if (tyhjä(harrastaja.getLisenssinumero()))
      {
         harrastaja.setLisenssinumero(null);
      }
   }

   private void asetaJäsennumero(Harrastaja harrastaja)
   {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
      String jäsennumero = null;
      int i = 1;
      do
      {
         jäsennumero = String.format("%s-%d", sdf.format(harrastaja.getSyntynyt()), i++);
      } while (jäsennumerot.contains(jäsennumero));
      jäsennumerot.add(jäsennumero);
      harrastaja.setJäsennumero(jäsennumero);
   }

   private void asetaSukupuoli(Sheet välilehti, int rivi, Harrastaja harrastaja)
   {
      String sukupuoli = välilehti.getCell(23, rivi).getContents().trim();
      switch (sukupuoli)
      {
      case "t":
         harrastaja.setSukupuoli(Sukupuoli.N);
         break;
      case "p":
         harrastaja.setSukupuoli(Sukupuoli.M);
         break;
      default:
         throw new IsoveliPoikkeus(String.format("Tuntematon sukupuoli %s harrastajalla %s", sukupuoli,
            harrastaja.getNimi()));
      }
      harrastaja.setEtunimi(välilehti.getCell(3, rivi).getContents().trim());
   }

   private void asetaNimi(Sheet välilehti, int rivi, Harrastaja harrastaja)
   {
      harrastaja.setSukunimi(välilehti.getCell(2, rivi).getContents().trim());
      harrastaja.setEtunimi(välilehti.getCell(3, rivi).getContents().trim());
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
      if (!tyhjä(perheId))
      {
         int pid = Integer.parseInt(perheId);
         if (!perheet.containsKey(pid))
         {
            tulos.lisääVirhe(String.format("Harrastajalla %s tuntematon perhe-id %d", harrastaja.getNimi(), pid));
         } else
         {
            Perhe perhe = perheet.get(pid);
            Iterator<Henkilö> i = perhe.getPerheenjäsenet().iterator();
            while (i.hasNext())
            {
               Henkilö h = i.next();
               if (harrastaja.getNimi().equals(h.getNimi()))
               {
                  i.remove();
               }
            }
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
         harrastaja.setIce(harrastaja.getHuoltaja().getYhteystiedot().getPuhelinnumero());
      } else
      {
         if (harrastaja.getPerhe() != null)
         {
            Optional<Henkilö> toinenTäysiIkäinen = harrastaja.getPerhe().getHuoltajat().stream()
               .filter(h -> !h.getNimi().equals(harrastaja.getNimi())).filter(h -> h.getYhteystiedot() != null)
               .findFirst();
            if (toinenTäysiIkäinen.isPresent())
            {
               harrastaja.setIce(toinenTäysiIkäinen.get().getYhteystiedot().getPuhelinnumero());
            }
         }
      }
      if (harrastaja.getIce() == null)
      {
         harrastaja.lisääHuomautus("Tarkista ICE-numero");
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
      perhe.getOsoite().setOsoite(harrastaja.getOsoite().getOsoite());
      perhe.getOsoite().setPostinumero(harrastaja.getOsoite().getPostinumero());
      perhe.getOsoite().setKaupunki(harrastaja.getOsoite().getKaupunki());
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
      perhe.setId(0);
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
         if (perheet.containsKey(perhe.getId()))
         {
            perhe = perheet.get(perhe.getId());
         }

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
