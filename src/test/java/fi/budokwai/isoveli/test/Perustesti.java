package fi.budokwai.isoveli.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Henkilˆ;
import fi.budokwai.isoveli.malli.Perhe;
import fi.budokwai.isoveli.malli.Sopimus;
import fi.budokwai.isoveli.malli.Sopimustyyppi;

public class Perustesti
{
   protected Henkilˆ teeHenkilˆ(String nimi)
   {
      Henkilˆ henkilˆ = new Henkilˆ();
      asetaNimi(henkilˆ, nimi);
      return henkilˆ;
   }

   protected void asetaNimi(Henkilˆ henkilˆ, String nimi)
   {
      String[] nimiosat = nimi.split(" ");
      henkilˆ.setEtunimi(nimiosat[0]);
      henkilˆ.setSukunimi(nimiosat[1]);
   }

   protected Perhe teePerhe(String nimi, Henkilˆ... perheenj‰senet)
   {
      Perhe perhe = new Perhe();
      perhe.setNimi(nimi);
      Arrays.asList(perheenj‰senet).stream().forEach(p -> perhe.lis‰‰Perheenj‰sen(p));
      return perhe;
   }

   protected Harrastaja teeAlaik‰inenHarrastaja(String nimi)
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

   protected Harrastaja teeT‰ysiik‰inenHarrastaja(String nimi)
   {
      return teeHarrastaja(nimi, "01.01.1970");
   }

   protected Sopimus teeHarjoittelusopimus(Harrastaja harrastaja, String luotu, int maksuv‰li)
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
      sopimus.setMaksuv‰li(maksuv‰li);
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

   protected Sopimus teeJ‰senmaksusopimus(Harrastaja harrastaja, String luotu)
   {
      Sopimus sopimus = new Sopimus();
      Sopimustyyppi sopimustyyppi = new Sopimustyyppi();
      sopimustyyppi.setLaskutettava(true);;
      sopimustyyppi.setHinta(10);
      sopimustyyppi.setOletusMaksuv‰li(12);
      sopimustyyppi.setNimi("J‰senmaksu");
      sopimustyyppi.setJ‰senmaksu(true);
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
