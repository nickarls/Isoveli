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
import fi.budokwai.isoveli.malli.Treenik�ynti;
import fi.budokwai.isoveli.malli.Vy�koe;
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
      Assert.assertEquals("J�senmaksu puuttuu", tarkistukset.getViestit().get(0));
      Assert.assertEquals("Harjoittelumaksu puuttuu", tarkistukset.getViestit().get(1));
   }

   @Test
   public void testPelk�st��nJ�senmaksu()
   {
      Harrastaja harrastaja = new Harrastaja();
      teeJ�senmaksusopimus(harrastaja, "01.01.2015");
      Assert.assertFalse(harrastaja.isSopimuksetOK());
      Sopimustarkistukset tarkistukset = harrastaja.getSopimusTarkistukset();
      Assert.assertEquals(1, tarkistukset.getViestit().size());
      Assert.assertEquals("Harjoittelumaksu puuttuu", tarkistukset.getViestit().get(0));
   }

   @Test
   public void testJ�senmaksuEiVoimassa()
   {
      Harrastaja harrastaja = new Harrastaja();
      teeJ�senmaksusopimus(harrastaja, "01.01.2015");
      harrastaja.getSopimukset().iterator().next().setUmpeutuu(new Date());
      Assert.assertFalse(harrastaja.isSopimuksetOK());
      Sopimustarkistukset tarkistukset = harrastaja.getSopimusTarkistukset();
      Assert.assertEquals(2, tarkistukset.getViestit().size());
      Assert.assertTrue(tarkistukset.getViestit().get(1).startsWith("J�senmaksu: sopimus umpeutui"));
   }

   @Test
   public void testTreenikerratSiirtotreeneill�()
   {
      Harrastaja harrastaja = new Harrastaja();
      harrastaja.setSiirtotreenej�(10);
      harrastaja.lis��Treenik�ynti(new Treenik�ynti());
      Assert.assertEquals(11, harrastaja.getTreenej�Yhteens�());
   }

   @Test
   public void testLaskuMaksamatta() throws ParseException
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja, "01.01.2015", 6);
      teeJ�senmaksusopimus(harrastaja, "01.01.2015");
      Sopimuslasku sopimuslasku = teeSopimuslasku(harrastaja.getSopimukset().iterator().next());
      sopimuslasku.getLaskurivi().getLasku().setEr�p�iv�(new SimpleDateFormat("dd.MM.yyyy").parse("02.02.2015"));
      Assert.assertFalse(harrastaja.isSopimuksetOK());
      Sopimustarkistukset tarkistukset = harrastaja.getSopimusTarkistukset();
      Assert.assertEquals(1, tarkistukset.getViestit().size());
      Assert.assertTrue(tarkistukset.getViestit().get(0).startsWith("Harjoittelumaksu: lasku my�h�ss�"));
   }

   @Test
   public void testLaskuMaksamattaT�n��n() throws ParseException
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja, "01.01.2005", 6);
      teeJ�senmaksusopimus(harrastaja, "01.01.2015");
      Sopimuslasku sopimuslasku = teeSopimuslasku(harrastaja.getSopimukset().iterator().next());
      sopimuslasku.getLaskurivi().getLasku().setEr�p�iv�(DateUtil.LocalDate2Date(LocalDate.now()));
      Assert.assertTrue(harrastaja.isSopimuksetOK());
   }

   @Test
   public void testLaskuAvoin() throws ParseException
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja, "01.01.2015", 6);
      teeJ�senmaksusopimus(harrastaja, "01.01.2015");
      Sopimuslasku sopimuslasku = teeSopimuslasku(harrastaja.getSopimukset().iterator().next());
      sopimuslasku.getLaskurivi().getLasku().setEr�p�iv�(new SimpleDateFormat("dd.MM.yyyy").parse("02.02.2016"));
      Assert.assertTrue(harrastaja.isSopimuksetOK());
   }

   @Test
   public void testLaskuMaksettu() throws ParseException
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja, "01.01.2015", 6);
      teeJ�senmaksusopimus(harrastaja, "01.01.2015");
      Sopimuslasku sopimuslasku = teeSopimuslasku(harrastaja.getSopimukset().iterator().next());
      sopimuslasku.getLaskurivi().getLasku().setMaksettu(new SimpleDateFormat("dd.MM.yyyy").parse("02.02.2015"));
      Assert.assertTrue(harrastaja.isSopimuksetOK());
   }

   @Test
   public void testLaskuMaksettuT�n��n() throws ParseException
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja, "01.01.2015", 6);
      teeJ�senmaksusopimus(harrastaja, "01.01.2015");
      Sopimuslasku sopimuslasku = teeSopimuslasku(harrastaja.getSopimukset().iterator().next());
      sopimuslasku.getLaskurivi().getLasku().setMaksettu(DateUtil.LocalDate2Date(LocalDate.now()));
      Assert.assertTrue(harrastaja.isSopimuksetOK());
   }

   @Test
   public void testPelk�st��nHarjoittelumaksu()
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja, "01.01.2015", 6);
      Assert.assertFalse(harrastaja.isSopimuksetOK());
      Sopimustarkistukset tarkistukset = harrastaja.getSopimusTarkistukset();
      Assert.assertEquals(1, tarkistukset.getViestit().size());
      Assert.assertEquals("J�senmaksu puuttuu", tarkistukset.getViestit().get(0));
   }

   @Test
   public void testJ�senmaksuJaHarjoittelumaksu()
   {
      Harrastaja harrastaja = new Harrastaja();
      teeHarjoittelusopimus(harrastaja, "01.01.2015", 6);
      teeJ�senmaksusopimus(harrastaja, "01.01.2015");
      Assert.assertTrue(harrastaja.isSopimuksetOK());
   }

   @Test
   public void testJ�senmaksuJaTreenikertoja()
   {
      Harrastaja harrastaja = new Harrastaja();
      teeJ�senmaksusopimus(harrastaja, "01.01.2015");
      teeKertamaksusopimus(harrastaja, "01.01.2015");
      Assert.assertTrue(harrastaja.isSopimuksetOK());
   }

   @Test
   public void testPerheell�Treenikertoja()
   {
      Harrastaja vanhempi = new Harrastaja();
      teeKertamaksusopimus(vanhempi, "01.01.2015");
      Harrastaja harrastaja = new Harrastaja();
      teeJ�senmaksusopimus(harrastaja, "01.01.2015");
      Perhe perhe = new Perhe();
      perhe.getPerheenj�senet().add(vanhempi);
      perhe.getPerheenj�senet().add(harrastaja);
      vanhempi.setPerhe(perhe);
      harrastaja.setPerhe(perhe);
      Assert.assertTrue(harrastaja.isSopimuksetOK());
   }

   @Test
   public void testTreenikerratLoppu()
   {
      Harrastaja harrastaja = new Harrastaja();
      teeJ�senmaksusopimus(harrastaja, "01.01.2015");
      teeKertamaksusopimus(harrastaja, "01.01.2015");
      harrastaja.getSopimukset().get(1).setTreenikertojaJ�ljell�(0);
      Assert.assertFalse(harrastaja.isSopimuksetOK());
      Sopimustarkistukset tarkistukset = harrastaja.getSopimusTarkistukset();
      Assert.assertEquals(1, tarkistukset.getViestit().size());
      Assert.assertEquals("Treenikertoja j�ljell� 0", tarkistukset.getViestit().get(0));
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
      harrastaja.getSopimukset().iterator().next().setUmpeutuu(DateUtil.t�n��nDate());
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
      Assert.assertEquals(0, sopimus.getTreenikertojaJ�ljell�());
   }

   @Test
   public void testaaHarjoitteluoikeusSopimusHarjoitusEnnenPerheenKertoja()
   {
      Harrastaja vanhempi = new Harrastaja();
      teeKertamaksusopimus(vanhempi, "01.01.2015");
      Harrastaja harrastaja = new Harrastaja();
      teeJ�senmaksusopimus(harrastaja, "01.01.2015");
      teeHarjoittelusopimus(harrastaja, "01.01.2015", 6);
      Perhe perhe = new Perhe();
      perhe.getPerheenj�senet().add(vanhempi);
      perhe.getPerheenj�senet().add(harrastaja);
      vanhempi.setPerhe(perhe);
      harrastaja.setPerhe(perhe);
      Sopimus sopimus = harrastaja.getHarjoitteluoikeusSopimus();
      Assert.assertEquals(0, sopimus.getTreenikertojaJ�ljell�());
   }

   @Test
   public void testaaHarjoitteluoikeusSopimusOmatKerttatEnnenPerheenKertoja()
   {
      Harrastaja vanhempi = new Harrastaja();
      teeKertamaksusopimus(vanhempi, "01.01.2015");
      Harrastaja harrastaja = new Harrastaja();
      teeJ�senmaksusopimus(harrastaja, "01.01.2015");
      teeKertamaksusopimus(harrastaja, "01.01.2015");
      harrastaja.getSopimukset().get(1).setTreenikertojaJ�ljell�(6);
      Perhe perhe = new Perhe();
      perhe.getPerheenj�senet().add(vanhempi);
      perhe.getPerheenj�senet().add(harrastaja);
      vanhempi.setPerhe(perhe);
      harrastaja.setPerhe(perhe);
      Sopimus sopimus = harrastaja.getHarjoitteluoikeusSopimus();
      Assert.assertEquals(6, sopimus.getTreenikertojaJ�ljell�());
   }

   @Test
   public void testEiTauolla()
   {
      Harrastaja harrastaja = new Harrastaja();
      Assert.assertEquals(false, harrastaja.isTauolla());
   }

   @Test
   public void testTauollaAvoinP��ttyy() throws ParseException
   {
      Harrastaja harrastaja = new Harrastaja();
      harrastaja.getTauko().setAlkaa(DateUtil.silloinD("01.01.2015"));
      Assert.assertEquals(true, harrastaja.isTauolla());
   }

   @Test
   public void testTauollaAvoinAlkaa() throws ParseException
   {
      Harrastaja harrastaja = new Harrastaja();
      harrastaja.getTauko().setP��ttyy(DateUtil.silloinD("01.01.2016"));
      Assert.assertEquals(true, harrastaja.isTauolla());
   }

   @Test
   public void testTauollaV�li() throws ParseException
   {
      Harrastaja harrastaja = new Harrastaja();
      harrastaja.getTauko().setAlkaa(DateUtil.silloinD("01.01.2015"));
      harrastaja.getTauko().setP��ttyy(DateUtil.silloinD("01.01.2016"));
      Assert.assertEquals(true, harrastaja.isTauolla());
   }

   @Test
   public void testTauollaEiV�li() throws ParseException
   {
      Harrastaja harrastaja = new Harrastaja();
      harrastaja.getTauko().setAlkaa(DateUtil.silloinD("01.01.2015"));
      harrastaja.getTauko().setP��ttyy(DateUtil.silloinD("01.02.2015"));
      Assert.assertEquals(false, harrastaja.isTauolla());
   }

   @Test
   public void testAikaaViimeVy�kokeestaEiKokeita()
   {
      Harrastaja harrastaja = new Harrastaja();
      Period aikav�li = harrastaja.getAikaaViimeVy�kokeesta();
      Assert.assertEquals(aikav�li, Period.ZERO);
   }

   @Test
   public void testAikaaViimeVy�kokeesta()
   {
      LocalDate nyt = DateUtil.t�n��n();
      LocalDate silloin = DateUtil.silloin("01.01.2015");
      Period v�li = Period.between(nyt, silloin);
      Harrastaja harrastaja = new Harrastaja();
      teeVy�koe(harrastaja, "01.01.2014", "9. kup", 1);
      teeVy�koe(harrastaja, "01.01.2015", "8. kup", 2);
      Period aikav�li = harrastaja.getAikaaViimeVy�kokeesta();
      Assert.assertEquals(v�li.getYears(), aikav�li.getYears());
      Assert.assertEquals(v�li.getMonths(), aikav�li.getMonths());
      Assert.assertEquals(v�li.getDays(), aikav�li.getDays());
   }

   @Test
   public void lis��Vy�kokeitaOK()
   {
      Harrastaja harrastaja = new Harrastaja();
      teeVy�koe(harrastaja, "1.1.2015", "8kup", 1);
      teeVy�koe(harrastaja, "1.2.2015", "7kup", 2);
      teeVy�koe(harrastaja, "1.3.2015", "6kup", 3);
      harrastaja.tarkistaVy�kokeet();
   }

   @Test
   public void lis��Vy�kokeitaJ�rjestys()
   {
      Harrastaja harrastaja = new Harrastaja();
      teeVy�koe(harrastaja, "1.2.2015", "7kup", 2);
      teeVy�koe(harrastaja, "1.1.2015", "8kup", 1);
      teeVy�koe(harrastaja, "1.3.2015", "6kup", 3);
      Assert.assertEquals(1, harrastaja.getVy�kokeet().get(0).getVy�arvo().getJ�rjestys());
      Assert.assertEquals(2, harrastaja.getVy�kokeet().get(1).getVy�arvo().getJ�rjestys());
      Assert.assertEquals(3, harrastaja.getVy�kokeet().get(2).getVy�arvo().getJ�rjestys());
   }

   @Test
   public void lis��Vy�kokeitaEnnenSyntym��()
   {
      Harrastaja harrastaja = new Harrastaja();
      harrastaja.setSyntynyt(DateUtil.silloinD("1.1.2015"));
      Vy�koe vy�koe = teeVy�koe(harrastaja, "1.2.2014", "7kup", 2);
      exception.expect(IsoveliPoikkeus.class);
      exception.expectMessage("Harrastaja on syntynyt 01.01.2015, h�n ei ole voinut suorittaa vy�arvon 7kup 01.02.2014");
      vy�koe.validoi(harrastaja);
      harrastaja.tarkistaVy�kokeet();
   }

   @Test
   public void lis��TuplaVy�koe()
   {
      Harrastaja harrastaja = new Harrastaja();
      teeVy�koe(harrastaja, "1.1.2015", "8kup", 1);
      teeVy�koe(harrastaja, "1.1.2015", "8kup", 1);
      exception.expect(IsoveliPoikkeus.class);
      exception.expectMessage("Harrastajalla on jo vy�arvo 8kup");
      harrastaja.tarkistaVy�kokeet();
   }

   @Test
   public void lis��Vy�kokeitaV��r�J�rjestys()
   {
      Harrastaja harrastaja = new Harrastaja();
      teeVy�koe(harrastaja, "1.1.2015", "8kup", 1);
      teeVy�koe(harrastaja, "1.1.2014", "7kup", 2);
      exception.expect(IsoveliPoikkeus.class);
      exception.expectMessage("7kup (01.01.2014) ei voi olla suoritettuna aikaisemmin kun 8kup (01.01.2015)");
      harrastaja.tarkistaVy�kokeet();
   }

   @Test
   public void lis��Vy�kokeitaDanAlaikaiselle()
   {
      Harrastaja harrastaja = new Harrastaja();
      harrastaja.setSyntynyt(DateUtil.silloinD("1.1.2010"));
      Vy�koe vy�koe = teeVy�koe(harrastaja, "1.1.2015", "8kup", 1, false, true);
      exception.expect(IsoveliPoikkeus.class);
      exception.expectMessage("Harrastaja oli 01.01.2015 5 vuotta, tarkoitit varmaan poom-arvoa?");
      vy�koe.validoi(harrastaja);
   }

   @Test
   public void lis��Vy�kokeitaPoomYliikaiselle()
   {
      Harrastaja harrastaja = new Harrastaja();
      harrastaja.setSyntynyt(DateUtil.silloinD("1.1.1970"));
      Vy�koe vy�koe = teeVy�koe(harrastaja, "1.1.2015", "8kup", 1, true, false);
      exception.expect(IsoveliPoikkeus.class);
      exception.expectMessage("Harrastaja oli 01.01.2015 45 vuotta, tarkoitit varmaan dan-arvoa?");
      vy�koe.validoi(harrastaja);
   }

}
