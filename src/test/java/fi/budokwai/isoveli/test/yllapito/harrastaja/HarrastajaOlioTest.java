package fi.budokwai.isoveli.test.yllapito.harrastaja;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import fi.budokwai.isoveli.IsoveliPoikkeus;
import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Perhe;
import fi.budokwai.isoveli.malli.Sopimus;
import fi.budokwai.isoveli.malli.Sopimuslasku;
import fi.budokwai.isoveli.malli.Sopimustarkistukset;
import fi.budokwai.isoveli.malli.Treenikäynti;
import fi.budokwai.isoveli.malli.Vyökoe;
import fi.budokwai.isoveli.test.Perustesti;
import fi.budokwai.isoveli.util.DateUtil;

public class HarrastajaOlioTest extends Perustesti
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
      teeJäsenmaksusopimus(harrastaja, "01.01.2015");
      Assert.assertFalse(harrastaja.isSopimuksetOK());
      Sopimustarkistukset tarkistukset = harrastaja.getSopimusTarkistukset();
      Assert.assertEquals(1, tarkistukset.getViestit().size());
      Assert.assertEquals("Harjoittelumaksu puuttuu", tarkistukset.getViestit().get(0));
   }

   @Test
   public void testJäsenmaksuEiVoimassa()
   {
      Harrastaja harrastaja = new Harrastaja();
      teeJäsenmaksusopimus(harrastaja, "01.01.2015");
      harrastaja.getSopimukset().iterator().next().setUmpeutuu(new Date());
      Assert.assertFalse(harrastaja.isSopimuksetOK());
      Sopimustarkistukset tarkistukset = harrastaja.getSopimusTarkistukset();
      Assert.assertEquals(2, tarkistukset.getViestit().size());
      Assert.assertTrue(tarkistukset.getViestit().get(1).startsWith("Jäsenmaksu: sopimus umpeutui"));
   }

   @Test
   public void testTreenikerratSiirtotreeneillä()
   {
      Harrastaja harrastaja = new Harrastaja();
      harrastaja.setSiirtotreenejä(10);
      harrastaja.lisääTreenikäynti(new Treenikäynti());
      Assert.assertEquals(11, harrastaja.getTreenejäYhteensä());
   }

   @Test
   public void testLaskuMaksamatta() throws ParseException
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja, "01.01.2015", 6);
      teeJäsenmaksusopimus(harrastaja, "01.01.2015");
      Sopimuslasku sopimuslasku = teeSopimuslasku(harrastaja.getSopimukset().iterator().next());
      sopimuslasku.getLaskurivi().getLasku().setEräpäivä(new SimpleDateFormat("dd.MM.yyyy").parse("02.02.2015"));
      Assert.assertFalse(harrastaja.isSopimuksetOK());
      Sopimustarkistukset tarkistukset = harrastaja.getSopimusTarkistukset();
      Assert.assertEquals(1, tarkistukset.getViestit().size());
      Assert.assertTrue(tarkistukset.getViestit().get(0).startsWith("Harjoittelumaksu: lasku myöhässä"));
   }

   @Test
   public void testLaskuMaksamattaTänään() throws ParseException
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja, "01.01.2005", 6);
      teeJäsenmaksusopimus(harrastaja, "01.01.2015");
      Sopimuslasku sopimuslasku = teeSopimuslasku(harrastaja.getSopimukset().iterator().next());
      sopimuslasku.getLaskurivi().getLasku().setEräpäivä(DateUtil.LocalDate2Date(LocalDate.now()));
      Assert.assertTrue(harrastaja.isSopimuksetOK());
   }

   @Test
   public void testLaskuAvoin() throws ParseException
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja, "01.01.2015", 6);
      teeJäsenmaksusopimus(harrastaja, "01.01.2015");
      Sopimuslasku sopimuslasku = teeSopimuslasku(harrastaja.getSopimukset().iterator().next());
      sopimuslasku.getLaskurivi().getLasku().setEräpäivä(new SimpleDateFormat("dd.MM.yyyy").parse("02.02.2016"));
      Assert.assertTrue(harrastaja.isSopimuksetOK());
   }

   @Test
   public void testLaskuMaksettu() throws ParseException
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja, "01.01.2015", 6);
      teeJäsenmaksusopimus(harrastaja, "01.01.2015");
      Sopimuslasku sopimuslasku = teeSopimuslasku(harrastaja.getSopimukset().iterator().next());
      sopimuslasku.getLaskurivi().getLasku().setMaksettu(new SimpleDateFormat("dd.MM.yyyy").parse("02.02.2015"));
      Assert.assertTrue(harrastaja.isSopimuksetOK());
   }

   @Test
   public void testLaskuMaksettuTänään() throws ParseException
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja, "01.01.2015", 6);
      teeJäsenmaksusopimus(harrastaja, "01.01.2015");
      Sopimuslasku sopimuslasku = teeSopimuslasku(harrastaja.getSopimukset().iterator().next());
      sopimuslasku.getLaskurivi().getLasku().setMaksettu(DateUtil.LocalDate2Date(LocalDate.now()));
      Assert.assertTrue(harrastaja.isSopimuksetOK());
   }

   @Test
   public void testPelkästäänHarjoittelumaksu()
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja, "01.01.2015", 6);
      Assert.assertFalse(harrastaja.isSopimuksetOK());
      Sopimustarkistukset tarkistukset = harrastaja.getSopimusTarkistukset();
      Assert.assertEquals(1, tarkistukset.getViestit().size());
      Assert.assertEquals("Jäsenmaksu puuttuu", tarkistukset.getViestit().get(0));
   }

   @Test
   public void testJäsenmaksuJaHarjoittelumaksu()
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja, "01.01.2015", 6);
      teeJäsenmaksusopimus(harrastaja, "01.01.2015");
      Assert.assertTrue(harrastaja.isSopimuksetOK());
   }

   @Test
   public void testJäsenmaksuJaTreenikertoja()
   {
      Harrastaja harrastaja = new Harrastaja();
      teeJäsenmaksusopimus(harrastaja, "01.01.2015");
      teeKertamaksusopimus(harrastaja, "01.01.2015");
      Assert.assertTrue(harrastaja.isSopimuksetOK());
   }

   @Test
   public void testPerheelläTreenikertoja()
   {
      Harrastaja vanhempi = new Harrastaja();
      teeKertamaksusopimus(vanhempi, "01.01.2015");
      Harrastaja harrastaja = new Harrastaja();
      teeJäsenmaksusopimus(harrastaja, "01.01.2015");
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
      teeJäsenmaksusopimus(harrastaja, "01.01.2015");
      teeKertamaksusopimus(harrastaja, "01.01.2015");
      harrastaja.getSopimukset().get(1).setTreenikertojaJäljellä(0);
      Assert.assertFalse(harrastaja.isSopimuksetOK());
      Sopimustarkistukset tarkistukset = harrastaja.getSopimusTarkistukset();
      Assert.assertEquals(1, tarkistukset.getViestit().size());
      Assert.assertEquals("Treenikertoja jäljellä 0", tarkistukset.getViestit().get(0));
   }

   @Test
   public void testaaHarjoitteluoikeusSopimusEiSopimuksia()
   {
      Harrastaja harrastaja = new Harrastaja();
      Sopimus sopimus = harrastaja.getHarjoitteluoikeusSopimus();
      Assert.assertEquals(Sopimus.EI_OOTA, sopimus);
   }

   @Test
   public void testaaHarjoitteluoikeusSopimusSopimusUmpeutunut()
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja, "01.01.2015", 6);
      harrastaja.getSopimukset().iterator().next().setUmpeutuu(DateUtil.tänäänDate());
      Sopimus sopimus = harrastaja.getHarjoitteluoikeusSopimus();
      Assert.assertEquals(Sopimus.EI_OOTA, sopimus);
   }

   @Test
   public void testaaHarjoitteluoikeusSopimusHarjoitusEnnenKertoja()
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja, "01.01.2015", 6);
      teeKertamaksusopimus(harrastaja, "01.01.2015");
      Sopimus sopimus = harrastaja.getHarjoitteluoikeusSopimus();
      Assert.assertEquals(0, sopimus.getTreenikertojaJäljellä());
   }

   @Test
   public void testaaHarjoitteluoikeusSopimusHarjoitusEnnenPerheenKertoja()
   {
      Harrastaja vanhempi = new Harrastaja();
      teeKertamaksusopimus(vanhempi, "01.01.2015");
      Harrastaja harrastaja = new Harrastaja();
      teeJäsenmaksusopimus(harrastaja, "01.01.2015");
      teeHarjoittelusopimus(harrastaja, "01.01.2015", 6);
      Perhe perhe = new Perhe();
      perhe.getPerheenjäsenet().add(vanhempi);
      perhe.getPerheenjäsenet().add(harrastaja);
      vanhempi.setPerhe(perhe);
      harrastaja.setPerhe(perhe);
      Sopimus sopimus = harrastaja.getHarjoitteluoikeusSopimus();
      Assert.assertEquals(0, sopimus.getTreenikertojaJäljellä());
   }

   @Test
   public void testaaHarjoitteluoikeusSopimusOmatKerttatEnnenPerheenKertoja()
   {
      Harrastaja vanhempi = new Harrastaja();
      teeKertamaksusopimus(vanhempi, "01.01.2015");
      Harrastaja harrastaja = new Harrastaja();
      teeJäsenmaksusopimus(harrastaja, "01.01.2015");
      teeKertamaksusopimus(harrastaja, "01.01.2015");
      harrastaja.getSopimukset().get(1).setTreenikertojaJäljellä(6);
      Perhe perhe = new Perhe();
      perhe.getPerheenjäsenet().add(vanhempi);
      perhe.getPerheenjäsenet().add(harrastaja);
      vanhempi.setPerhe(perhe);
      harrastaja.setPerhe(perhe);
      Sopimus sopimus = harrastaja.getHarjoitteluoikeusSopimus();
      Assert.assertEquals(6, sopimus.getTreenikertojaJäljellä());
   }

   @Test
   public void testEiTauolla()
   {
      Harrastaja harrastaja = new Harrastaja();
      Assert.assertEquals(false, harrastaja.isTauolla());
   }

   @Test
   public void testTauollaAvoinPäättyy() throws ParseException
   {
      Harrastaja harrastaja = new Harrastaja();
      harrastaja.getTauko().setAlkaa(DateUtil.silloinD("01.01.2015"));
      Assert.assertEquals(true, harrastaja.isTauolla());
   }

   @Test
   public void testTauollaAvoinAlkaa() throws ParseException
   {
      Harrastaja harrastaja = new Harrastaja();
      harrastaja.getTauko().setPäättyy(DateUtil.silloinD("01.01.2016"));
      Assert.assertEquals(true, harrastaja.isTauolla());
   }

   @Test
   public void testTauollaVäli() throws ParseException
   {
      Harrastaja harrastaja = new Harrastaja();
      harrastaja.getTauko().setAlkaa(DateUtil.silloinD("01.01.2015"));
      harrastaja.getTauko().setPäättyy(DateUtil.silloinD("01.01.2016"));
      Assert.assertEquals(true, harrastaja.isTauolla());
   }

   @Test
   public void testTauollaEiVäli() throws ParseException
   {
      Harrastaja harrastaja = new Harrastaja();
      harrastaja.getTauko().setAlkaa(DateUtil.silloinD("01.01.2015"));
      harrastaja.getTauko().setPäättyy(DateUtil.silloinD("01.02.2015"));
      Assert.assertEquals(false, harrastaja.isTauolla());
   }

   @Test
   public void testAikaaViimeVyökokeestaEiKokeita()
   {
      Harrastaja harrastaja = new Harrastaja();
      Period aikaväli = harrastaja.getAikaaViimeVyökokeesta();
      Assert.assertEquals(aikaväli, Period.ZERO);
   }

   @Test
   public void testAikaaViimeVyökokeesta()
   {
      LocalDate nyt = DateUtil.tänään();
      LocalDate silloin = DateUtil.silloin("01.01.2015");
      Period väli = Period.between(nyt, silloin);
      Harrastaja harrastaja = new Harrastaja();
      teeVyökoe(harrastaja, "01.01.2014", "9. kup", 1);
      teeVyökoe(harrastaja, "01.01.2015", "8. kup", 2);
      Period aikaväli = harrastaja.getAikaaViimeVyökokeesta();
      Assert.assertEquals(väli.getYears(), aikaväli.getYears());
      Assert.assertEquals(väli.getMonths(), aikaväli.getMonths());
      Assert.assertEquals(väli.getDays(), aikaväli.getDays());
   }

   @Test
   public void lisääVyökokeitaOK()
   {
      Harrastaja harrastaja = new Harrastaja();
      teeVyökoe(harrastaja, "1.1.2015", "8kup", 1);
      teeVyökoe(harrastaja, "1.2.2015", "7kup", 2);
      teeVyökoe(harrastaja, "1.3.2015", "6kup", 3);
      harrastaja.tarkistaVyökokeet();
   }

   @Test
   public void lisääVyökokeitaJärjestys()
   {
      Harrastaja harrastaja = new Harrastaja();
      teeVyökoe(harrastaja, "1.2.2015", "7kup", 2);
      teeVyökoe(harrastaja, "1.1.2015", "8kup", 1);
      teeVyökoe(harrastaja, "1.3.2015", "6kup", 3);
      Assert.assertEquals(1, harrastaja.getVyökokeet().get(0).getVyöarvo().getJärjestys());
      Assert.assertEquals(2, harrastaja.getVyökokeet().get(1).getVyöarvo().getJärjestys());
      Assert.assertEquals(3, harrastaja.getVyökokeet().get(2).getVyöarvo().getJärjestys());
   }

   @Test
   public void lisääVyökokeitaEnnenSyntymää()
   {
      Harrastaja harrastaja = new Harrastaja();
      harrastaja.setSyntynyt(DateUtil.silloinD("1.1.2015"));
      Vyökoe vyökoe = teeVyökoe(harrastaja, "1.2.2014", "7kup", 2);
      exception.expect(IsoveliPoikkeus.class);
      exception.expectMessage("Harrastaja on syntynyt 01.01.2015, hän ei ole voinut suorittaa vyöarvon 7kup 01.02.2014");
      vyökoe.validoi(harrastaja);
      harrastaja.tarkistaVyökokeet();
   }

   @Test
   public void lisääTuplaVyökoe()
   {
      Harrastaja harrastaja = new Harrastaja();
      teeVyökoe(harrastaja, "1.1.2015", "8kup", 1);
      teeVyökoe(harrastaja, "1.1.2015", "8kup", 1);
      exception.expect(IsoveliPoikkeus.class);
      exception.expectMessage("Harrastajalla on jo vyöarvo 8kup");
      harrastaja.tarkistaVyökokeet();
   }

   @Test
   public void lisääVyökokeitaVääräJärjestys()
   {
      Harrastaja harrastaja = new Harrastaja();
      teeVyökoe(harrastaja, "1.1.2015", "8kup", 1);
      teeVyökoe(harrastaja, "1.1.2014", "7kup", 2);
      exception.expect(IsoveliPoikkeus.class);
      exception.expectMessage("7kup (01.01.2014) ei voi olla suoritettuna aikaisemmin kun 8kup (01.01.2015)");
      harrastaja.tarkistaVyökokeet();
   }

   @Test
   public void lisääVyökokeitaDanAlaikaiselle()
   {
      Harrastaja harrastaja = new Harrastaja();
      harrastaja.setSyntynyt(DateUtil.silloinD("1.1.2010"));
      Vyökoe vyökoe = teeVyökoe(harrastaja, "1.1.2015", "8kup", 1, false, true);
      exception.expect(IsoveliPoikkeus.class);
      exception.expectMessage("Harrastaja oli 01.01.2015 5 vuotta, tarkoitit varmaan poom-arvoa?");
      vyökoe.validoi(harrastaja);
   }

   @Test
   public void lisääVyökokeitaPoomYliikaiselle()
   {
      Harrastaja harrastaja = new Harrastaja();
      harrastaja.setSyntynyt(DateUtil.silloinD("1.1.1970"));
      Vyökoe vyökoe = teeVyökoe(harrastaja, "1.1.2015", "8kup", 1, true, false);
      exception.expect(IsoveliPoikkeus.class);
      exception.expectMessage("Harrastaja oli 01.01.2015 45 vuotta, tarkoitit varmaan dan-arvoa?");
      vyökoe.validoi(harrastaja);
   }

}
