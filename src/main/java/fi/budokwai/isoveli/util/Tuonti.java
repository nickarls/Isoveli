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
import fi.budokwai.isoveli.malli.Henkil�;
import fi.budokwai.isoveli.malli.Osoite;
import fi.budokwai.isoveli.malli.Perhe;
import fi.budokwai.isoveli.malli.Sukupuoli;
import fi.budokwai.isoveli.malli.Vy�arvo;
import fi.budokwai.isoveli.malli.Vy�koe;
import fi.budokwai.isoveli.malli.Yhteystieto;

public class Tuonti
{

   private Set<String> j�sennumerot = new HashSet<>();

   @Inject
   private List<Vy�arvo> vy�arvot;

   private boolean tyhj�(String s)
   {
      return s == null || "".equals(s);
   }

   public Tuontitulos tuoJ�senrekisteri(byte[] excel)
   {
      try
      {
         Tuontitulos tulos = new Tuontitulos();
         Workbook ty�kirja = Workbook.getWorkbook(new ByteArrayInputStream(excel));
         Map<Integer, Perhe> perheet = haePerheet(ty�kirja.getSheet(1), tulos);
         haeHarrastajat(ty�kirja.getSheet(0), perheet, tulos);
         return tulos;
      } catch (IOException | BiffException e)
      {
         throw new IsoveliPoikkeus("Excelin avaus ep�onnistui", e);
      }
   }

   private void haeHarrastajat(Sheet v�lilehti, Map<Integer, Perhe> perheet, Tuontitulos tulos)
   {
      for (int rivi = 2; rivi < v�lilehti.getRows(); rivi++)
      {
         Harrastaja harrastaja = new Harrastaja();
         asetaNimi(v�lilehti, rivi, harrastaja);
         asetaLisenssinumero(v�lilehti, rivi, harrastaja);
         asetaSukupuoli(v�lilehti, rivi, harrastaja);
         asetaSyntym�aika(v�lilehti, rivi, harrastaja);
         asetaJ�sennumero(harrastaja);
         String perheId = v�lilehti.getCell(14, rivi).getContents().trim();
         valitsePerhe(perheId, perheet, harrastaja, tulos);
         asetaOmaOsoite(v�lilehti, rivi, harrastaja);
         asetaOmaYhteystieto(v�lilehti, rivi, harrastaja);
         s��d�Perhetiedot(harrastaja);
         asetaVy�arvo(v�lilehti, rivi, harrastaja, tulos);
         tulos.lis��Harrastaja(harrastaja);
      }
   }

   private void asetaVy�arvo(Sheet v�lilehti, int rivi, Harrastaja harrastaja, Tuontitulos tulos)
   {
      if (vy�arvot == null)
      {
         return;
      }
      String vy�arvoString = v�lilehti.getCell(17, rivi).getContents().trim();
      if (tyhj�(vy�arvoString))
      {
         return;
      }
      Optional<Vy�arvo> vy�arvo = vy�arvot.stream().filter(v -> v.getNimi().equals(vy�arvoString)).findFirst();
      if (vy�arvo.isPresent())
      {
         Vy�koe vy�koe = new Vy�koe();
         vy�koe.setHarrastaja(harrastaja);
         vy�koe.setP�iv�(new Date());
         vy�koe.setVy�arvo(vy�arvo.get());
         harrastaja.getVy�kokeet().add(vy�koe);
      } else
      {
         tulos.lis��Virhe(String.format("Harrastajalla %s tuntematon vy�arvo %s", harrastaja.getNimi(), vy�arvoString));
      }
   }

   private void asetaLisenssinumero(Sheet v�lilehti, int rivi, Harrastaja harrastaja)
   {
      harrastaja.setLisenssinumero(v�lilehti.getCell(1, rivi).getContents().trim());
      if (tyhj�(harrastaja.getLisenssinumero()))
      {
         harrastaja.setLisenssinumero(null);
      }
   }

   private void asetaJ�sennumero(Harrastaja harrastaja)
   {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
      String j�sennumero = null;
      int i = 1;
      do
      {
         j�sennumero = String.format("%s-%d", sdf.format(harrastaja.getSyntynyt()), i++);
      } while (j�sennumerot.contains(j�sennumero));
      j�sennumerot.add(j�sennumero);
      harrastaja.setJ�sennumero(j�sennumero);
   }

   private void asetaSukupuoli(Sheet v�lilehti, int rivi, Harrastaja harrastaja)
   {
      String sukupuoli = v�lilehti.getCell(23, rivi).getContents().trim();
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
      harrastaja.setEtunimi(v�lilehti.getCell(3, rivi).getContents().trim());
   }

   private void asetaNimi(Sheet v�lilehti, int rivi, Harrastaja harrastaja)
   {
      harrastaja.setSukunimi(v�lilehti.getCell(2, rivi).getContents().trim());
      harrastaja.setEtunimi(v�lilehti.getCell(3, rivi).getContents().trim());
   }

   private void asetaSyntym�aika(Sheet v�lilehti, int rivi, Harrastaja harrastaja)
   {
      String vuosi = v�lilehti.getCell(7, rivi).getContents().trim();
      String kuukausi = v�lilehti.getCell(8, rivi).getContents().trim();
      String p�iv� = v�lilehti.getCell(9, rivi).getContents().trim();

      if (tyhj�(vuosi) || tyhj�(kuukausi) || tyhj�(p�iv�))
      {
         String maksuperuste = v�lilehti.getCell(15, rivi).getContents().trim();
         switch (maksuperuste)
         {
         case "junior":
            harrastaja.setSyntynyt(DateUtil.silloinD("01.01.2000"));
            break;
         case "aikuinen":
            harrastaja.setSyntynyt(DateUtil.silloinD("01.01.1970"));
            break;
         default:
            throw new IsoveliPoikkeus(String.format("Harrastajan %s syntym�aika puuttuu", harrastaja.getNimi()));
         }
      } else
      {
         harrastaja.setSyntynyt(DateUtil.string2Date(String.format("%s.%s.%s", p�iv�, kuukausi, vuosi)));
      }
   }

   private void asetaOmaYhteystieto(Sheet v�lilehti, int rivi, Harrastaja harrastaja)
   {
      Yhteystieto yhteystieto = new Yhteystieto();
      yhteystieto.setPuhelinnumero(v�lilehti.getCell(10, rivi).getContents().trim());
      yhteystieto.setS�hk�posti(v�lilehti.getCell(11, rivi).getContents().trim());
      yhteystieto.setS�hk�postilistalla(true);
      harrastaja.setYhteystiedot(yhteystieto);
      if (!harrastaja.isAlaik�inen())
      {
         if (tyhj�(harrastaja.getYhteystiedot().getPuhelinnumero()))
         {
            harrastaja.lis��Huomautus("Tarkista oma puhelinnumero");
         }
         if (tyhj�(harrastaja.getYhteystiedot().getS�hk�posti()))
         {
            harrastaja.lis��Huomautus("Tarkista oma s�hk�postiosoite");
         }
      }
   }

   private void asetaOmaOsoite(Sheet v�lilehti, int rivi, Harrastaja harrastaja)
   {
      Osoite osoite = new Osoite();
      osoite.setOsoite(v�lilehti.getCell(4, rivi).getContents().trim());
      osoite.setPostinumero(v�lilehti.getCell(5, rivi).getContents().trim());
      osoite.setKaupunki(v�lilehti.getCell(6, rivi).getContents().trim());
      harrastaja.setOsoite(osoite);
   }

   private void valitsePerhe(String perheId, Map<Integer, Perhe> perheet, Harrastaja harrastaja, Tuontitulos tulos)
   {
      if (!tyhj�(perheId))
      {
         int pid = Integer.parseInt(perheId);
         if (!perheet.containsKey(pid))
         {
            tulos.lis��Virhe(String.format("Harrastajalla %s tuntematon perhe-id %d", harrastaja.getNimi(), pid));
         } else
         {
            Perhe perhe = perheet.get(pid);
            Iterator<Henkil�> i = perhe.getPerheenj�senet().iterator();
            while (i.hasNext())
            {
               Henkil� h = i.next();
               if (harrastaja.getNimi().equals(h.getNimi()))
               {
                  i.remove();
               }
            }
            perhe.lis��Perheenj�sen(harrastaja);
         }
      }
      if (harrastaja.isAlaik�inen())
      {
         if (harrastaja.getPerhe() == null)
         {
            harrastaja.lis��Huomautus("Tarkista perhetiedot");
            Perhe perhe = luoPerhe(harrastaja.getSukunimi());
            perhe.lis��Perheenj�sen(harrastaja);
            harrastaja.setPerhe(perhe);
         }
         if (harrastaja.getPerhe().getHuoltajat().isEmpty())
         {
            Henkil� huoltaja = luoHuoltaja(harrastaja.getSukunimi());
            harrastaja.getPerhe().lis��Perheenj�sen(huoltaja);
            harrastaja.setHuoltaja(huoltaja);
            harrastaja.lis��Huomautus("Tarkista perhetiedot");
         } else
         {
            harrastaja.setHuoltaja(harrastaja.getPerhe().getHuoltajat().iterator().next());
         }
         harrastaja.setIce(harrastaja.getHuoltaja().getYhteystiedot().getPuhelinnumero());
      } else
      {
         if (harrastaja.getPerhe() != null)
         {
            Optional<Henkil�> toinenT�ysiIk�inen = harrastaja.getPerhe().getHuoltajat().stream()
               .filter(h -> !h.getNimi().equals(harrastaja.getNimi())).filter(h -> h.getYhteystiedot() != null)
               .findFirst();
            if (toinenT�ysiIk�inen.isPresent())
            {
               harrastaja.setIce(toinenT�ysiIk�inen.get().getYhteystiedot().getPuhelinnumero());
            }
         }
      }
      if (harrastaja.getIce() == null)
      {
         harrastaja.lis��Huomautus("Tarkista ICE-numero");
      }

   }

   private Perhe luoPerhe(String sukunimi)
   {
      Perhe perhe = new Perhe();
      Henkil� huoltaja = luoHuoltaja(sukunimi);
      perhe.lis��Perheenj�sen(huoltaja);
      return perhe;
   }

   private Henkil� luoHuoltaja(String sukunimi)
   {
      Henkil� huoltaja = new Henkil�();
      huoltaja.setEtunimi("Huoltaja");
      huoltaja.setSukunimi(sukunimi);
      huoltaja.setYhteystiedot(new Yhteystieto());
      return huoltaja;
   }

   private void s��d�Perhetiedot(Harrastaja harrastaja)
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
      if (tyhj�(perhe.getOsoite().getOsoite()))
      {
         perhe.getOsoite().setOsoite("Kirstinkatu 1");
         harrastaja.lis��Huomautus("Tarkista perheen osoitetiedot (katu)");
      }
      if (tyhj�(perhe.getOsoite().getPostinumero()))
      {
         perhe.getOsoite().setPostinumero("20520");
         harrastaja.lis��Huomautus("Tarkista perheen osoitetiedot (postinumero)");
      }
      if (tyhj�(perhe.getOsoite().getKaupunki()))
      {
         perhe.getOsoite().setKaupunki("Turku");
         harrastaja.lis��Huomautus("Tarkista perheen osoitetiedot (kaupunki)");
      }

      perhe.getHuoltajat().forEach(h -> {
         int i = 1;
         if (tyhj�(h.getEtunimi()))
         {
            String nimi = String.format("Huoltaja%d", i++);
            h.setEtunimi(nimi);
            harrastaja.lis��Huomautus(String.format("Tarkista huoltajan etunimi %s", h.getEtunimi()));
         }
         if (tyhj�(h.getSukunimi()))
         {
            h.setSukunimi(harrastaja.getSukunimi());
            harrastaja.lis��Huomautus(String.format("Tarkista huoltajan sukunimi %s", h.getSukunimi()));
         }
         if (tyhj�(h.getYhteystiedot().getPuhelinnumero()))
         {
            harrastaja.lis��Huomautus(String.format("Huoltajan %s puhelinnumero puuttuu", h.getNimi()));
         }
         if (tyhj�(h.getYhteystiedot().getS�hk�posti()))
         {
            harrastaja.lis��Huomautus(String.format("Huoltajan %s s�hk�posti puuttuu", h.getNimi()));
         }
      });
      perhe.setId(0);
   }

   private Map<Integer, Perhe> haePerheet(Sheet v�lilehti, Tuontitulos tulos)
   {
      Map<Integer, Perhe> perheet = new HashMap<>();
      for (int rivi = 1; rivi < v�lilehti.getRows(); rivi++)
      {
         Perhe perhe = new Perhe();
         String pid = v�lilehti.getCell(0, rivi).getContents();
         if ("".equals(pid))
         {
            continue;
         }
         perhe.setId(Integer.parseInt(pid));
         if (perheet.containsKey(perhe.getId()))
         {
            perhe = perheet.get(perhe.getId());
         }

         Henkil� huoltaja = new Henkil�();
         String nimi = v�lilehti.getCell(1, rivi).getContents().trim();
         String[] nimiosat = nimi.split(" ");
         if (nimiosat.length == 2)
         {
            huoltaja.setEtunimi(nimiosat[0]);
            huoltaja.setSukunimi(nimiosat[1]);
         }

         Yhteystieto yhteystieto = new Yhteystieto();
         yhteystieto.setPuhelinnumero(v�lilehti.getCell(2, rivi).getContents().trim());
         yhteystieto.setS�hk�posti(v�lilehti.getCell(3, rivi).getContents().trim());
         yhteystieto.setS�hk�postilistalla(true);
         huoltaja.setYhteystiedot(yhteystieto);

         Osoite osoite = new Osoite();
         perhe.setOsoite(osoite);

         perhe.lis��Perheenj�sen(huoltaja);
         perheet.put(perhe.getId(), perhe);
      }
      return perheet;
   }
}
