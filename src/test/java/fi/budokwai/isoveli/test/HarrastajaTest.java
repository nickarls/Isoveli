package fi.budokwai.isoveli.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Lasku;
import fi.budokwai.isoveli.malli.Laskurivi;
import fi.budokwai.isoveli.malli.Perhe;
import fi.budokwai.isoveli.malli.Sopimus;
import fi.budokwai.isoveli.malli.Sopimuslasku;
import fi.budokwai.isoveli.malli.Sopimustarkistukset;
import fi.budokwai.isoveli.malli.Sopimustyyppi;
import fi.budokwai.isoveli.util.DateUtil;

public class HarrastajaTest
{
   @Test
   public void testEiSopimuksia()
   {
      Harrastaja harrastaja = new Harrastaja();
      Assert.assertFalse(harrastaja.isSopimuksetOK());
      Sopimustarkistukset tarkistukset = harrastaja.getSopimusTarkistukset();
      Assert.assertEquals(2, tarkistukset.getViestit().size());
      Assert.assertEquals("Jäsenmaksu puuttuu", tarkistukset.getViestit().get(0));
      Assert.assertEquals("Harjoittelumaksu puuttuu", tarkistukset.getViestit().get(1));
   }

   @Test
   public void testPelkästäänJäsenmaksu()
   {
      Harrastaja harrastaja = new Harrastaja();
      teeJäsenmaksusopimus(harrastaja);
      Assert.assertFalse(harrastaja.isSopimuksetOK());
      Sopimustarkistukset tarkistukset = harrastaja.getSopimusTarkistukset();
      Assert.assertEquals(1, tarkistukset.getViestit().size());
      Assert.assertEquals("Harjoittelumaksu puuttuu", tarkistukset.getViestit().get(0));
   }

   
   private Sopimus teeKertamaksusopimus(Harrastaja harrastaja)
   {
      Sopimustyyppi tyyppi = new Sopimustyyppi();
      tyyppi.setTreenikertoja(true);
      tyyppi.setNimi("Treenikertoja");
      Sopimus sopimus = new Sopimus();
      sopimus.setTyyppi(tyyppi);
      sopimus.setHarrastaja(harrastaja);
      harrastaja.getSopimukset().add(sopimus);
      return sopimus;
   }
   
   private Sopimus teeJäsenmaksusopimus(Harrastaja harrastaja)
   {
      Sopimustyyppi tyyppi = new Sopimustyyppi();
      tyyppi.setJäsenmaksu(true);
      tyyppi.setNimi("Jäsenmaksu");
      Sopimus sopimus = new Sopimus();
      sopimus.setTyyppi(tyyppi);
      sopimus.setHarrastaja(harrastaja);
      harrastaja.getSopimukset().add(sopimus);
      return sopimus;
   }

   @Test
   public void testJäsenmaksuEiVoimassa()
   {
      Harrastaja harrastaja = new Harrastaja();
      Sopimus sopimus = teeJäsenmaksusopimus(harrastaja);
      sopimus.setUmpeutuu(new Date());
      Assert.assertFalse(harrastaja.isSopimuksetOK());
      Sopimustarkistukset tarkistukset = harrastaja.getSopimusTarkistukset();
      Assert.assertEquals(2, tarkistukset.getViestit().size());
      Assert.assertTrue(tarkistukset.getViestit().get(1).startsWith("Jäsenmaksu: sopimus umpeutui"));
   }

   @Test
   public void testLaskuMaksamatta() throws ParseException
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja);
      Sopimus sopimus = teeJäsenmaksusopimus(harrastaja);
      Sopimuslasku sopimuslasku = teeSopimuslasku(sopimus);
      sopimuslasku.getLaskurivi().getLasku().setEräpäivä(new SimpleDateFormat("dd.MM.yyyy").parse("02.02.2015"));
      Assert.assertFalse(harrastaja.isSopimuksetOK());
      Sopimustarkistukset tarkistukset = harrastaja.getSopimusTarkistukset();
      Assert.assertEquals(1, tarkistukset.getViestit().size());
      Assert.assertTrue(tarkistukset.getViestit().get(0).startsWith("Jäsenmaksu: lasku myöhässä"));
   }

   @Test
   public void testLaskuMaksamattaTänään() throws ParseException
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja);
      Sopimus sopimus = teeJäsenmaksusopimus(harrastaja);
      Sopimuslasku sopimuslasku = teeSopimuslasku(sopimus);
      sopimuslasku.getLaskurivi().getLasku().setEräpäivä(DateUtil.LocalDate2Date(LocalDate.now()));
      Assert.assertTrue(harrastaja.isSopimuksetOK());
   }

   @Test
   public void testLaskuAvoin() throws ParseException
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja);
      Sopimus sopimus = teeJäsenmaksusopimus(harrastaja);
      Sopimuslasku sopimuslasku = teeSopimuslasku(sopimus);
      sopimuslasku.getLaskurivi().getLasku().setEräpäivä(new SimpleDateFormat("dd.MM.yyyy").parse("02.02.2016"));
      Assert.assertTrue(harrastaja.isSopimuksetOK());
   }

   @Test
   public void testLaskuMaksettu() throws ParseException
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja);
      Sopimus sopimus = teeJäsenmaksusopimus(harrastaja);
      Sopimuslasku sopimuslasku = teeSopimuslasku(sopimus);
      sopimuslasku.getLaskurivi().getLasku().setMaksettu(new SimpleDateFormat("dd.MM.yyyy").parse("02.02.2015"));
      Assert.assertTrue(harrastaja.isSopimuksetOK());
   }
   
   @Test
   public void testLaskuMaksettuTänään() throws ParseException
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja);
      Sopimus sopimus = teeJäsenmaksusopimus(harrastaja);
      Sopimuslasku sopimuslasku = teeSopimuslasku(sopimus);
      sopimuslasku.getLaskurivi().getLasku().setMaksettu(DateUtil.LocalDate2Date(LocalDate.now()));
      Assert.assertTrue(harrastaja.isSopimuksetOK());
   }   

   private Sopimuslasku teeSopimuslasku(Sopimus sopimus) throws ParseException
   {
      Sopimuslasku sopimuslasku = new Sopimuslasku();
      sopimuslasku.setSopimus(sopimus);
      sopimus.getSopimuslaskut().add(sopimuslasku);
      Lasku lasku = new Lasku();
      Laskurivi laskurivi = new Laskurivi();
      lasku.getLaskurivit().add(laskurivi);
      laskurivi.setLasku(lasku);
      sopimuslasku.setLaskurivi(laskurivi);
      return sopimuslasku;
   }

   private Sopimus teeHarjoittelusopimus(Harrastaja harrastaja)
   {
      Sopimustyyppi tyyppi = new Sopimustyyppi();
      tyyppi.setHarjoittelumaksu(true);
      tyyppi.setNimi("Harjoittelumaksu");
      Sopimus sopimus = new Sopimus();
      sopimus.setTyyppi(tyyppi);
      sopimus.setHarrastaja(harrastaja);
      harrastaja.getSopimukset().add(sopimus);
      return sopimus;
   }

   @Test
   public void testPelkästäänHarjoittelumaksu()
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja);
      Assert.assertFalse(harrastaja.isSopimuksetOK());
      Sopimustarkistukset tarkistukset = harrastaja.getSopimusTarkistukset();
      Assert.assertEquals(1, tarkistukset.getViestit().size());
      Assert.assertEquals("Jäsenmaksu puuttuu", tarkistukset.getViestit().get(0));
   }

   @Test
   public void testJäsenmaksuJaHarjoittelumaksu()
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja);
      teeJäsenmaksusopimus(harrastaja);
      Assert.assertTrue(harrastaja.isSopimuksetOK());
   }
   
   @Test
   public void testJäsenmaksuJaTreenikertoja()
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja);
      teeJäsenmaksusopimus(harrastaja);
      Sopimus sopimus = teeKertamaksusopimus(harrastaja);
      sopimus.setTreenikertoja(10);
      Assert.assertTrue(harrastaja.isSopimuksetOK());
   }
   
   @Test
   public void testPerheelläTreenikertoja()
   {
      Harrastaja vanhempi = new Harrastaja();
      Sopimus sopimus = teeKertamaksusopimus(vanhempi);
      sopimus.setTreenikertoja(10);
      Harrastaja harrastaja = new Harrastaja();
      teeJäsenmaksusopimus(harrastaja);
      Perhe perhe = new Perhe();
      perhe.getPerheenjäsenet().add(vanhempi);
      perhe.getPerheenjäsenet().add(harrastaja);
      vanhempi.setPerhe(perhe);
      harrastaja.setPerhe(perhe);
      Assert.assertTrue(harrastaja.isSopimuksetOK());
   }
   
   
   @Test
   public void testTreenikerratLoppu()
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja);
      teeJäsenmaksusopimus(harrastaja);
      teeKertamaksusopimus(harrastaja);
      Assert.assertFalse(harrastaja.isSopimuksetOK());
      Sopimustarkistukset tarkistukset = harrastaja.getSopimusTarkistukset();
      Assert.assertEquals(1, tarkistukset.getViestit().size());
      Assert.assertEquals("Treenikertoja jäljellä 0", tarkistukset.getViestit().get(0));
   }   
   

}
