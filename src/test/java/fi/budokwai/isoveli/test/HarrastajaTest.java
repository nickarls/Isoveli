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
      Assert.assertEquals("J‰senmaksu puuttuu", tarkistukset.getViestit().get(0));
      Assert.assertEquals("Harjoittelumaksu puuttuu", tarkistukset.getViestit().get(1));
   }
   
   @Test
   public void testPelk‰st‰‰nJ‰senmaksu()
   {
      Harrastaja harrastaja = new Harrastaja();
      teeJ‰senmaksusopimus(harrastaja);
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
   
   private Sopimus teeJ‰senmaksusopimus(Harrastaja harrastaja)
   {
      Sopimustyyppi tyyppi = new Sopimustyyppi();
      tyyppi.setJ‰senmaksu(true);
      tyyppi.setNimi("J‰senmaksu");
      Sopimus sopimus = new Sopimus();
      sopimus.setTyyppi(tyyppi);
      sopimus.setHarrastaja(harrastaja);
      harrastaja.getSopimukset().add(sopimus);
      return sopimus;
   }

   @Test
   public void testJ‰senmaksuEiVoimassa()
   {
      Harrastaja harrastaja = new Harrastaja();
      Sopimus sopimus = teeJ‰senmaksusopimus(harrastaja);
      sopimus.setUmpeutuu(new Date());
      Assert.assertFalse(harrastaja.isSopimuksetOK());
      Sopimustarkistukset tarkistukset = harrastaja.getSopimusTarkistukset();
      Assert.assertEquals(2, tarkistukset.getViestit().size());
      Assert.assertTrue(tarkistukset.getViestit().get(1).startsWith("J‰senmaksu: sopimus umpeutui"));
   }

   @Test
   public void testLaskuMaksamatta() throws ParseException
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja);
      Sopimus sopimus = teeJ‰senmaksusopimus(harrastaja);
      Sopimuslasku sopimuslasku = teeSopimuslasku(sopimus);
      sopimuslasku.getLaskurivi().getLasku().setEr‰p‰iv‰(new SimpleDateFormat("dd.MM.yyyy").parse("02.02.2015"));
      Assert.assertFalse(harrastaja.isSopimuksetOK());
      Sopimustarkistukset tarkistukset = harrastaja.getSopimusTarkistukset();
      Assert.assertEquals(1, tarkistukset.getViestit().size());
      Assert.assertTrue(tarkistukset.getViestit().get(0).startsWith("J‰senmaksu: lasku myˆh‰ss‰"));
   }

   @Test
   public void testLaskuMaksamattaT‰n‰‰n() throws ParseException
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja);
      Sopimus sopimus = teeJ‰senmaksusopimus(harrastaja);
      Sopimuslasku sopimuslasku = teeSopimuslasku(sopimus);
      sopimuslasku.getLaskurivi().getLasku().setEr‰p‰iv‰(DateUtil.LocalDate2Date(LocalDate.now()));
      Assert.assertTrue(harrastaja.isSopimuksetOK());
   }

   @Test
   public void testLaskuAvoin() throws ParseException
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja);
      Sopimus sopimus = teeJ‰senmaksusopimus(harrastaja);
      Sopimuslasku sopimuslasku = teeSopimuslasku(sopimus);
      sopimuslasku.getLaskurivi().getLasku().setEr‰p‰iv‰(new SimpleDateFormat("dd.MM.yyyy").parse("02.02.2016"));
      Assert.assertTrue(harrastaja.isSopimuksetOK());
   }

   @Test
   public void testLaskuMaksettu() throws ParseException
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja);
      Sopimus sopimus = teeJ‰senmaksusopimus(harrastaja);
      Sopimuslasku sopimuslasku = teeSopimuslasku(sopimus);
      sopimuslasku.getLaskurivi().getLasku().setMaksettu(new SimpleDateFormat("dd.MM.yyyy").parse("02.02.2015"));
      Assert.assertTrue(harrastaja.isSopimuksetOK());
   }
   
   @Test
   public void testLaskuMaksettuT‰n‰‰n() throws ParseException
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja);
      Sopimus sopimus = teeJ‰senmaksusopimus(harrastaja);
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
   public void testPelk‰st‰‰nHarjoittelumaksu()
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja);
      Assert.assertFalse(harrastaja.isSopimuksetOK());
      Sopimustarkistukset tarkistukset = harrastaja.getSopimusTarkistukset();
      Assert.assertEquals(1, tarkistukset.getViestit().size());
      Assert.assertEquals("J‰senmaksu puuttuu", tarkistukset.getViestit().get(0));
   }

   @Test
   public void testJ‰senmaksuJaHarjoittelumaksu()
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja);
      teeJ‰senmaksusopimus(harrastaja);
      Assert.assertTrue(harrastaja.isSopimuksetOK());
   }
   
   @Test
   public void testJ‰senmaksuJaTreenikertoja()
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja);
      teeJ‰senmaksusopimus(harrastaja);
      Sopimus sopimus = teeKertamaksusopimus(harrastaja);
      sopimus.setTreenikertoja(10);
      Assert.assertTrue(harrastaja.isSopimuksetOK());
   }
   
   @Test
   public void testPerheell‰Treenikertoja()
   {
      Harrastaja vanhempi = new Harrastaja();
      Sopimus sopimus = teeKertamaksusopimus(vanhempi);
      sopimus.setTreenikertoja(10);
      Harrastaja harrastaja = new Harrastaja();
      teeJ‰senmaksusopimus(harrastaja);
      Perhe perhe = new Perhe();
      perhe.getPerheenj‰senet().add(vanhempi);
      perhe.getPerheenj‰senet().add(harrastaja);
      vanhempi.setPerhe(perhe);
      harrastaja.setPerhe(perhe);
      Assert.assertTrue(harrastaja.isSopimuksetOK());
   }
   
   
   @Test
   public void testTreenikerratLoppu()
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja);
      teeJ‰senmaksusopimus(harrastaja);
      teeKertamaksusopimus(harrastaja);
      Assert.assertFalse(harrastaja.isSopimuksetOK());
      Sopimustarkistukset tarkistukset = harrastaja.getSopimusTarkistukset();
      Assert.assertEquals(1, tarkistukset.getViestit().size());
      Assert.assertEquals("Treenikertoja j‰ljell‰ 0", tarkistukset.getViestit().get(0));
   }   
   

}
