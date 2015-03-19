package fi.budokwai.isoveli.test.yllapito.harrastaja;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Perhe;
import fi.budokwai.isoveli.malli.Sopimus;
import fi.budokwai.isoveli.malli.Sopimuslasku;
import fi.budokwai.isoveli.malli.Sopimustarkistukset;
import fi.budokwai.isoveli.malli.Treenikäynti;
import fi.budokwai.isoveli.test.Perustesti;
import fi.budokwai.isoveli.util.DateUtil;

public class HarrastajaOlioTest extends Perustesti
{
   private static Validator validator;

   @BeforeClass
   public static void init()
   {
      ValidatorFactory f = Validation.buildDefaultValidatorFactory();
      validator = f.getValidator();
   }

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
      Harrastaja harrastaja = teeHarrastaja("Nicklas Karlsson", "01.01.1970");
      teeVyökoe(harrastaja, "1.1.2015", "8kup", 1);
      teeVyökoe(harrastaja, "1.2.2015", "7kup", 2);
      teeVyökoe(harrastaja, "1.3.2015", "6kup", 3);
      Set<ConstraintViolation<Harrastaja>> virheet = validator.validate(harrastaja);
      Assert.assertEquals(0, virheet.size());
   }

   @Test
   public void lisääVyökokeitaJärjestys()
   {
      Harrastaja harrastaja = teeHarrastaja("Nicklas Karlsson", "01.01.1970");
      teeVyökoe(harrastaja, "1.2.2015", "7kup", 2);
      teeVyökoe(harrastaja, "1.1.2015", "8kup", 1);
      teeVyökoe(harrastaja, "1.3.2015", "6kup", 3);
      Assert.assertEquals(1, harrastaja.getVyökokeet().get(0).getVyöarvo().getJärjestys());
      Assert.assertEquals(2, harrastaja.getVyökokeet().get(1).getVyöarvo().getJärjestys());
      Assert.assertEquals(3, harrastaja.getVyökokeet().get(2).getVyöarvo().getJärjestys());
      Set<ConstraintViolation<Harrastaja>> virheet = validator.validate(harrastaja);
      Assert.assertEquals(0, virheet.size());
   }

   @Test
   public void lisääVyökokeitaEnnenSyntymää()
   {
      Harrastaja harrastaja = teeHarrastaja("Nicklas Karlsson", "1.1.2015");
      teeVyökoe(harrastaja, "1.2.2014", "7kup", 2);
      Set<ConstraintViolation<Harrastaja>> virheet = validator.validate(harrastaja);
      Assert.assertEquals(1, virheet.size());
      Assert.assertEquals("Harrastaja on syntynyt 01.01.2015, hän ei ole voinut suorittaa vyöarvon 7kup 01.02.2014",
         virheet.iterator().next().getMessage());
   }

   @Test
   public void lisääTuplaVyökoe()
   {
      Harrastaja harrastaja = teeHarrastaja("Nicklas Karlsson", "01.01.1970");
      teeVyökoe(harrastaja, "1.1.2015", "8kup", 1);
      teeVyökoe(harrastaja, "1.1.2015", "8kup", 1);
      Set<ConstraintViolation<Harrastaja>> virheet = validator.validate(harrastaja);
      Assert.assertEquals(2, virheet.size());
      Iterator<ConstraintViolation<Harrastaja>> i = virheet.iterator();
      Assert.assertEquals("Harrastajalla on jo vyöarvo 8kup", i.next().getMessage());
      Assert.assertEquals("8kup (01.01.2015) ei voi olla suoritettuna aikaisemmin kun 8kup (01.01.2015)", i.next()
         .getMessage());
   }

   @Test
   public void lisääVyökokeitaVääräJärjestys()
   {
      Harrastaja harrastaja = teeHarrastaja("Nicklas Karlsson", "28.06.1975");
      teeVyökoe(harrastaja, "1.1.2015", "8kup", 1);
      teeVyökoe(harrastaja, "1.1.2014", "7kup", 2);
      Set<ConstraintViolation<Harrastaja>> virheet = validator.validate(harrastaja);
      Assert.assertEquals(1, virheet.size());
      Assert.assertEquals("7kup (01.01.2014) ei voi olla suoritettuna aikaisemmin kun 8kup (01.01.2015)", virheet
         .iterator().next().getMessage());
   }

   @Test
   public void lisääVyökokeitaDanAlaikaiselle()
   {
      Harrastaja harrastaja = teeHarrastaja("Nicklas Karlsson", "28.06.2005");
      teeVyökoe(harrastaja, "1.1.2015", "8kup", 1, false, true);
      Set<ConstraintViolation<Harrastaja>> virheet = validator.validate(harrastaja);
      Assert.assertEquals(1, virheet.size());
      Assert.assertEquals("Harrastaja oli 01.01.2015 9 vuotta, tarkoitit varmaan poom-arvoa etkä 8kup?", virheet
         .iterator().next().getMessage());
   }

   @Test
   public void lisääVyökokeitaPoomYliikaiselle()
   {
      Harrastaja harrastaja = teeHarrastaja("Nicklas Karlsson", "28.06.1970");
      teeVyökoe(harrastaja, "1.1.2015", "8kup", 1, true, false);
      Set<ConstraintViolation<Harrastaja>> virheet = validator.validate(harrastaja);
      Assert.assertEquals(1, virheet.size());
      Assert.assertEquals("Harrastaja oli 01.01.2015 44 vuotta, tarkoitit varmaan dan-arvoa etkä 8kup?", virheet
         .iterator().next().getMessage());
   }

   @Test
   public void testTaukoAlkaaEnnenSyntymaa()
   {
      Harrastaja harrastaja = teeHarrastaja("Nicklas Karlsson", "28.06.1970");
      harrastaja.getTauko().setAlkaa(DateUtil.silloinD("01.01.1960"));
      harrastaja.getTauko().setPäättyy(DateUtil.silloinD("01.01.1961"));
      Set<ConstraintViolation<Harrastaja>> virheet = validator.validate(harrastaja);
      Assert.assertEquals(1, virheet.size());
      Assert
         .assertEquals("Tauko ei voi alkaa ennen kun harrastaja on syntynyt", virheet.iterator().next().getMessage());
   }

   @Test
   public void testTuplaAktiiviJäsenmaksuEiOK()
   {
      Harrastaja harrastaja = teeHarrastaja("Nicklas Karlsson", "28.06.1970");
      teeJäsenmaksusopimus(harrastaja, "01.01.2000");
      teeJäsenmaksusopimus(harrastaja, "01.01.2000");
      Set<ConstraintViolation<Harrastaja>> virheet = validator.validate(harrastaja);
      Assert.assertEquals(1, virheet.size());
      Assert
         .assertEquals("Harrastajalla on jo aktiivinen sopimustyyppi Jäsenmaksu", virheet.iterator().next().getMessage());
   }
   
   @Test
   public void testTuplaHarrastusmaksuEiOK()
   {
      Harrastaja harrastaja = teeHarrastaja("Nicklas Karlsson", "28.06.1970");
      teeHarjoittelusopimus(harrastaja, "01.01.2000", 6);
      teeHarjoittelusopimus(harrastaja, "01.01.2000", 6);
      Set<ConstraintViolation<Harrastaja>> virheet = validator.validate(harrastaja);
      Assert.assertEquals(1, virheet.size());
      Assert
         .assertEquals("Harrastajalla on jo aktiivinen sopimustyyppi Harjoittelumaksu", virheet.iterator().next().getMessage());
   }
   

   @Test
   public void testTuplaAktiiviJäsenmaksuJosPassiivinenToinenOK()
   {
      Harrastaja harrastaja = teeHarrastaja("Nicklas Karlsson", "28.06.1970");
      Sopimus sopimus = teeJäsenmaksusopimus(harrastaja, "01.01.2000");
      sopimus.setArkistoitu(true);
      teeJäsenmaksusopimus(harrastaja, "01.01.2000");
      Set<ConstraintViolation<Harrastaja>> virheet = validator.validate(harrastaja);
      Assert.assertEquals(0, virheet.size());
   }

}
