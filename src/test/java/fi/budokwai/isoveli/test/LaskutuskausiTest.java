package fi.budokwai.isoveli.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Assert;
import org.junit.Test;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Jakso;
import fi.budokwai.isoveli.malli.Laskutuskausi;
import fi.budokwai.isoveli.malli.Sopimus;
import fi.budokwai.isoveli.malli.Sopimuslasku;
import fi.budokwai.isoveli.util.DateUtil;

public class LaskutuskausiTest extends Perustesti
{
   @Test
   public void testTuoreSopimus() {
      Sopimus sopimus = teeHarjoittelusopimus(new Harrastaja(), "23.02.2015", 3);
      Laskutuskausi kausi = sopimus.getLaskutuskausi();
      Assert.assertEquals("23.02.2015", DateUtil.formatoi(kausi.getKausi().getAlkaa()));
      Assert.assertEquals("23.05.2015", DateUtil.formatoi(kausi.getKausi().getP‰‰ttyy()));
      Assert.assertEquals(3, kausi.getKausikuukausia());
   }
   
   @Test
   public void testYhdenVuodenJ‰senmaksu()
   {
      Sopimus sopimus = teeJ‰senmaksusopimus(new Harrastaja(), "02.02.2015");
      Laskutuskausi kausi = sopimus.getLaskutuskausi();
      Assert.assertEquals("02.02.2015", DateUtil.formatoi(kausi.getKausi().getAlkaa()));
      Assert.assertEquals("31.12.2015", DateUtil.formatoi(kausi.getKausi().getP‰‰ttyy()));
      Assert.assertEquals(1, kausi.getKausikuukausia());
   }

   @Test
   public void testMonenVuodenJ‰senmasku()
   {
      Sopimus sopimus = teeJ‰senmaksusopimus(new Harrastaja(), "03.03.2014");
      Laskutuskausi kausi = sopimus.getLaskutuskausi();
      Assert.assertEquals("03.03.2014", DateUtil.formatoi(kausi.getKausi().getAlkaa()));
      Assert.assertEquals("31.12.2015", DateUtil.formatoi(kausi.getKausi().getP‰‰ttyy()));
      Assert.assertEquals(2, kausi.getKausikuukausia());
   }

   @Test
   public void test1kkHarjoitusmaksu()
   {
      Sopimus sopimus = teeHarjoittelusopimus(new Harrastaja(), "02.02.2015", 1);
      Laskutuskausi kausi = sopimus.getLaskutuskausi();
      Assert.assertEquals("02.02.2015", DateUtil.formatoi(kausi.getKausi().getAlkaa()));
      Assert.assertEquals("02.03.2015", DateUtil.formatoi(kausi.getKausi().getP‰‰ttyy()));
      Assert.assertEquals(1, kausi.getKausikuukausia());
   }

   @Test
   public void test6kkHarjoitusmaksu()
   {
      Sopimus sopimus = teeHarjoittelusopimus(new Harrastaja(), "02.01.2015", 6);
      Laskutuskausi kausi = sopimus.getLaskutuskausi();
      Assert.assertEquals("02.01.2015", DateUtil.formatoi(kausi.getKausi().getAlkaa()));
      Assert.assertEquals("02.07.2015", DateUtil.formatoi(kausi.getKausi().getP‰‰ttyy()));
      Assert.assertEquals(6, kausi.getKausikuukausia());
   }

   @Test
   public void test6kkVanhaHarjoitusmaksu()
   {
      Sopimus sopimus = teeHarjoittelusopimus(new Harrastaja(), "02.01.2014", 6);
      Laskutuskausi kausi = sopimus.getLaskutuskausi();
      Assert.assertEquals("02.01.2014", DateUtil.formatoi(kausi.getKausi().getAlkaa()));
      Assert.assertEquals("02.07.2015", DateUtil.formatoi(kausi.getKausi().getP‰‰ttyy()));
      Assert.assertEquals(18, kausi.getKausikuukausia());
   }

   @Test
   public void testAlkeiskurssi()
   {
      Sopimus sopimus = teeAlkeiskurssisopimus(new Harrastaja(), "02.01.2015", "02.04.2015");
      Laskutuskausi kausi = sopimus.getLaskutuskausi();
      Assert.assertEquals("02.01.2015", DateUtil.formatoi(kausi.getKausi().getAlkaa()));
      Assert.assertEquals("02.04.2015", DateUtil.formatoi(kausi.getKausi().getP‰‰ttyy()));
      Assert.assertEquals(1, kausi.getKausikuukausia());
   }

   @Test
   public void testVanhaAlkeiskurssi()
   {
      Sopimus sopimus = teeAlkeiskurssisopimus(new Harrastaja(), "02.01.2014", "02.04.2014");
      Laskutuskausi kausi = sopimus.getLaskutuskausi();
      Assert.assertEquals("02.01.2014", DateUtil.formatoi(kausi.getKausi().getAlkaa()));
      Assert.assertEquals("02.04.2014", DateUtil.formatoi(kausi.getKausi().getP‰‰ttyy()));
      Assert.assertEquals(1, kausi.getKausikuukausia());
   }

   @Test
   public void testP‰‰ttyv‰Sopimus() throws ParseException
   {
      Sopimus sopimus = teeHarjoittelusopimus(new Harrastaja(), "02.01.2015", 6);
      sopimus.setUmpeutuu(DateUtil.silloinD("31.01.2015"));
      Laskutuskausi kausi = sopimus.getLaskutuskausi();
      Assert.assertEquals("02.01.2015", DateUtil.formatoi(kausi.getKausi().getAlkaa()));
      Assert.assertEquals("31.01.2015", DateUtil.formatoi(kausi.getKausi().getP‰‰ttyy()));
      Assert.assertEquals(1, kausi.getKausikuukausia());
   }

   @Test
   public void testJatkosopimus() throws ParseException
   {
      Sopimus sopimus = teeHarjoittelusopimus(new Harrastaja(), "01.01.2014", 6);
      Sopimuslasku sopimuslasku = new Sopimuslasku(sopimus, new Laskutuskausi(new Jakso(), 0, new Jakso(), 0));
      sopimuslasku.setP‰‰ttyy(new SimpleDateFormat("dd.MM.yyyy").parse("02.02.2014"));
      sopimus.getSopimuslaskut().add(sopimuslasku);
      Laskutuskausi kausi = sopimus.getLaskutuskausi();
      Assert.assertEquals("02.02.2014", DateUtil.formatoi(kausi.getKausi().getAlkaa()));
      Assert.assertEquals("02.08.2015", DateUtil.formatoi(kausi.getKausi().getP‰‰ttyy()));
      Assert.assertEquals(18, kausi.getKausikuukausia());
   }

   @Test
   public void testLaskutaKertakortti() throws ParseException
   {
      Sopimus sopimus = teeKertamaksusopimus(new Harrastaja(), "01.01.2014");
      Laskutuskausi kausi = sopimus.getLaskutuskausi();
      String t‰n‰‰n = DateUtil.formatoi(DateUtil.t‰n‰‰nDate());
      Assert.assertEquals("01.01.2014", DateUtil.formatoi(kausi.getKausi().getAlkaa()));
      Assert.assertEquals(t‰n‰‰n, DateUtil.formatoi(kausi.getKausi().getP‰‰ttyy()));
      Assert.assertEquals(10, kausi.getKausikuukausia());
   }

   @Test
   public void testTaukoEiOsu()
   {
      Harrastaja harrastaja = teeHarrastaja("Nicklas Karlsson", "01.01.2015");
      teeHarjoittelusopimus(harrastaja, "01.01.2015", 6);
      harrastaja.getTauko().setAlkaa(DateUtil.LocalDate2Date(DateUtil.silloin("01.04.2016")));
      harrastaja.getTauko().setP‰‰ttyy(DateUtil.LocalDate2Date(DateUtil.silloin("01.05.2016")));
      Laskutuskausi kausi = harrastaja.getSopimukset().iterator().next().getLaskutuskausi();
      Assert.assertEquals("01.01.2015-01.07.2015", kausi.getKausi().getKuvaus());
      Assert.assertEquals("", kausi.getTauko().getKuvaus());
      Assert.assertEquals(0, kausi.getTaukop‰ivi‰());
   }

   @Test
   public void testTaukoOsuu()
   {
      Harrastaja harrastaja = teeHarrastaja("Nicklas Karlsson", "01.01.2015");
      teeHarjoittelusopimus(harrastaja, "01.01.2015", 6);
      harrastaja.getTauko().setAlkaa(DateUtil.LocalDate2Date(DateUtil.silloin("01.04.2015")));
      harrastaja.getTauko().setP‰‰ttyy(DateUtil.LocalDate2Date(DateUtil.silloin("01.05.2015")));
      Laskutuskausi kausi = harrastaja.getSopimukset().iterator().next().getLaskutuskausi();
      Assert.assertEquals(30, kausi.getTaukop‰ivi‰());
      Assert.assertEquals("01.01.2015-01.07.2015", kausi.getKausi().getKuvaus());
      Assert.assertEquals("01.04.2015-01.05.2015", kausi.getTauko().getKuvaus());
   }

   @Test
   public void testTaukoAlkuOsuu()
   {
      Harrastaja harrastaja = teeHarrastaja("Nicklas Karlsson", "01.01.2015");
      teeHarjoittelusopimus(harrastaja, "01.01.2015", 6);
      harrastaja.getTauko().setAlkaa(DateUtil.LocalDate2Date(DateUtil.silloin("30.06.2015")));
      harrastaja.getTauko().setP‰‰ttyy(DateUtil.LocalDate2Date(DateUtil.silloin("30.08.2015")));
      Laskutuskausi kausi = harrastaja.getSopimukset().iterator().next().getLaskutuskausi();
      Assert.assertEquals("01.01.2015-01.07.2015", kausi.getKausi().getKuvaus());
      Assert.assertEquals("30.06.2015-01.07.2015", kausi.getTauko().getKuvaus());
      Assert.assertEquals(1, kausi.getTaukop‰ivi‰());
   }

   @Test
   public void testTaukoLoppuOsuu()
   {
      Harrastaja harrastaja = teeHarrastaja("Nicklas Karlsson", "01.01.2015");
      teeHarjoittelusopimus(harrastaja, "01.01.2015", 6);
      harrastaja.getTauko().setAlkaa(DateUtil.LocalDate2Date(DateUtil.silloin("01.12.2014")));
      harrastaja.getTauko().setP‰‰ttyy(DateUtil.LocalDate2Date(DateUtil.silloin("02.01.2015")));
      Laskutuskausi kausi = harrastaja.getSopimukset().iterator().next().getLaskutuskausi();
      Assert.assertEquals(1, kausi.getTaukop‰ivi‰());
      Assert.assertEquals("01.01.2015-01.07.2015", kausi.getKausi().getKuvaus());
      Assert.assertEquals("01.01.2015-02.01.2015", kausi.getTauko().getKuvaus());
   }

}
