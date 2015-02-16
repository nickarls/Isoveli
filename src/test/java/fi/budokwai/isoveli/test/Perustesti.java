package fi.budokwai.isoveli.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Henkilö;
import fi.budokwai.isoveli.malli.Perhe;
import fi.budokwai.isoveli.malli.Sopimus;
import fi.budokwai.isoveli.malli.Sopimustyyppi;

public class Perustesti
{
   protected Henkilö teeHenkilö(String nimi)
   {
      Henkilö henkilö = new Henkilö();
      asetaNimi(henkilö, nimi);
      return henkilö;
   }

   protected void asetaNimi(Henkilö henkilö, String nimi)
   {
      String[] nimiosat = nimi.split(" ");
      henkilö.setEtunimi(nimiosat[0]);
      henkilö.setSukunimi(nimiosat[1]);
   }

   protected Perhe teePerhe(String nimi, Henkilö... perheenjäsenet)
   {
      Perhe perhe = new Perhe();
      perhe.setNimi(nimi);
      Arrays.asList(perheenjäsenet).stream().forEach(p -> perhe.lisääPerheenjäsen(p));
      return perhe;
   }

   protected Harrastaja teeAlaikäinenHarrastaja(String nimi)
   {
      return teeHarrastaja(nimi, "01.01.2010");
   }

   protected Harrastaja teeHarrastaja(String nimi, String pvm)
   {
      Harrastaja harrastaja = new Harrastaja();
      asetaNimi(harrastaja, nimi);
      try
      {
         harrastaja.setSyntynyt(new SimpleDateFormat("dd.MM.yyyy").parse(pvm));
      } catch (ParseException e)
      {
         e.printStackTrace();
      }
      return harrastaja;
   }

   protected Harrastaja teeTäysiikäinenHarrastaja(String nimi)
   {
      return teeHarrastaja(nimi, "01.01.1970");
   }

   protected Sopimus teeHarjoittelusopimus(Harrastaja harrastaja, String luotu, int maksuväli)
   {
      Sopimus sopimus = new Sopimus();
      Sopimustyyppi sopimustyyppi = new Sopimustyyppi();
      sopimustyyppi.setLaskutettava(true);
      sopimustyyppi.setHinta(100);
      sopimustyyppi.setNimi("Harjoittelumaksu");
      sopimustyyppi.setHarjoittelumaksu(true);
      sopimus.setTyyppi(sopimustyyppi);
      try
      {
         sopimus.setLuotu(new SimpleDateFormat("dd.MM.yyyy").parse(luotu));
      } catch (ParseException e)
      {
         e.printStackTrace();
      }
      sopimus.setHarrastaja(harrastaja);
      sopimus.setMaksuväli(maksuväli);
      harrastaja.getSopimukset().add(sopimus);
      return sopimus;
   }

   protected Sopimus teeKertamaksusopimus(Harrastaja harrastaja, String luotu)
   {
      Sopimus sopimus = new Sopimus();
      Sopimustyyppi sopimustyyppi = new Sopimustyyppi();
      sopimustyyppi.setLaskutettava(true);
      sopimustyyppi.setHinta(6);
      sopimustyyppi.setOletusTreenikerrat(10);
      sopimustyyppi.setNimi("Kertamaksu");
      sopimustyyppi.setTreenikertoja(true);
      sopimus.setTyyppi(sopimustyyppi);
      try
      {
         sopimus.setLuotu(new SimpleDateFormat("dd.MM.yyyy").parse(luotu));
      } catch (ParseException e)
      {
         e.printStackTrace();
      }
      sopimus.setHarrastaja(harrastaja);
      harrastaja.getSopimukset().add(sopimus);
      return sopimus;
   }

   protected Sopimus teeAlkeiskurssisopimus(Harrastaja harrastaja, String luotu, String umpeutuu)
   {
      Sopimus sopimus = new Sopimus();
      Sopimustyyppi sopimustyyppi = new Sopimustyyppi();
      sopimustyyppi.setLaskutettava(true);
      sopimustyyppi.setHinta(200);
      sopimustyyppi.setNimi("Alkeiskurssi");
      sopimustyyppi.setAlkeiskurssi(true);
      sopimus.setTyyppi(sopimustyyppi);
      try
      {
         sopimus.setLuotu(new SimpleDateFormat("dd.MM.yyyy").parse(luotu));
         sopimus.setUmpeutuu(new SimpleDateFormat("dd.MM.yyyy").parse(umpeutuu));
      } catch (ParseException e)
      {
         e.printStackTrace();
      }
      sopimus.setHarrastaja(harrastaja);
      harrastaja.getSopimukset().add(sopimus);
      return sopimus;
   }

   protected Sopimus teeJäsenmaksusopimus(Harrastaja harrastaja, String luotu)
   {
      Sopimus sopimus = new Sopimus();
      Sopimustyyppi sopimustyyppi = new Sopimustyyppi();
      sopimustyyppi.setLaskutettava(true);;
      sopimustyyppi.setHinta(10);
      sopimustyyppi.setOletusMaksuväli(12);
      sopimustyyppi.setNimi("Jäsenmaksu");
      sopimustyyppi.setJäsenmaksu(true);
      sopimus.setTyyppi(sopimustyyppi);
      try
      {
         sopimus.setLuotu(new SimpleDateFormat("dd.MM.yyyy").parse(luotu));
      } catch (ParseException e)
      {
         e.printStackTrace();
      }
      sopimus.setHarrastaja(harrastaja);
      harrastaja.getSopimukset().add(sopimus);
      return sopimus;
   }

}
