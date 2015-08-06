package fi.budokwai.isoveli.test.yllapito.laskutus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Jakso;
import fi.budokwai.isoveli.malli.Laskutuskausi;
import fi.budokwai.isoveli.malli.Sopimus;
import fi.budokwai.isoveli.malli.Sopimuslasku;
import fi.budokwai.isoveli.test.Perustesti;
import fi.budokwai.isoveli.util.DateUtil;

public class LaskutuskausiTest extends Perustesti
{
   public static class Nyt
   {
      public static LocalDate localDate = DateUtil.t‰n‰‰n();
      public static Date date = DateUtil.t‰n‰‰nDate();
      public static String string = DateUtil.p‰iv‰Tekstiksi(date);

      public static String kuukausienP‰‰st‰(long kk)
      {
         return DateUtil.p‰iv‰Tekstiksi(DateUtil.LocalDate2Date(localDate.plusMonths(kk)));
      }
   }

   @Test
   public void testTuoreSopimus()
   {
      Sopimus sopimus = teeHarjoittelusopimus(new Harrastaja(), Nyt.string, 3);
      Laskutuskausi kausi = sopimus.getLaskutuskausi();
      Assert.assertEquals(Nyt.string, DateUtil.formatoi(kausi.getKausi().getAlkaa()));
      Assert.assertEquals(Nyt.kuukausienP‰‰st‰(3), DateUtil.formatoi(kausi.getKausi().getP‰‰ttyy()));
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
      Sopimus sopimus = teeHarjoittelusopimus(new Harrastaja(), Nyt.string, 1);
      Laskutuskausi kausi = sopimus.getLaskutuskausi();
      Assert.assertEquals(Nyt.string, DateUtil.formatoi(kausi.getKausi().getAlkaa()));
      Assert.assertEquals(Nyt.kuukausienP‰‰st‰(1), DateUtil.formatoi(kausi.getKausi().getP‰‰ttyy()));
      Assert.assertEquals(1, kausi.getKausikuukausia());
   }

   @Test
   public void test6kkHarjoitusmaksu()
   {
      Sopimus sopimus = teeHarjoittelusopimus(new Harrastaja(), Nyt.string, 6);
      Laskutuskausi kausi = sopimus.getLaskutuskausi();
      Assert.assertEquals(Nyt.string, DateUtil.formatoi(kausi.getKausi().getAlkaa()));
      Assert.assertEquals(Nyt.kuukausienP‰‰st‰(6), DateUtil.formatoi(kausi.getKausi().getP‰‰ttyy()));
      Assert.assertEquals(6, kausi.getKausikuukausia());
   }

   @Test
   public void test6kkVanhaHarjoitusmaksu()
   {
      Sopimus sopimus = teeHarjoittelusopimus(new Harrastaja(), Nyt.kuukausienP‰‰st‰(-6), 6);
      Laskutuskausi kausi = sopimus.getLaskutuskausi();
      Assert.assertEquals(DateUtil.p‰iv‰Tekstiksi(DateUtil.LocalDate2Date(Nyt.localDate.minusMonths(6))), DateUtil.formatoi(kausi.getKausi().getAlkaa()));
      Assert.assertEquals(DateUtil.p‰iv‰Tekstiksi(DateUtil.LocalDate2Date(Nyt.localDate.plusMonths(6))), DateUtil.formatoi(kausi.getKausi().getP‰‰ttyy()));
      Assert.assertEquals(12, kausi.getKausikuukausia());
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
      LocalDate nyt = DateUtil.t‰n‰‰n();
      Harrastaja harrastaja = teeHarrastaja("Nicklas Karlsson", "01.01.2015");
      teeHarjoittelusopimus(harrastaja, Nyt.string, 6);
      harrastaja.getTauko().setAlkaa(DateUtil.LocalDate2Date(nyt.plusMonths(8)));
      harrastaja.getTauko().setP‰‰ttyy(DateUtil.LocalDate2Date(nyt.plusMonths(9)));
      Laskutuskausi kausi = harrastaja.getSopimukset().iterator().next().getLaskutuskausi();
      Assert.assertEquals(String.format("%s-%s", Nyt.string, Nyt.kuukausienP‰‰st‰(6)), kausi.getKausi().getKuvaus());
      Assert.assertEquals("", kausi.getTauko().getKuvaus());
      Assert.assertEquals(0, kausi.getTaukop‰ivi‰());
   }

   @Test
   public void testEiTaukoa()
   {
      Harrastaja harrastaja = teeHarrastaja("Nicklas Karlsson", "01.01.2015");
      teeHarjoittelusopimus(harrastaja, Nyt.string, 6);
      Laskutuskausi kausi = harrastaja.getSopimukset().iterator().next().getLaskutuskausi();
      Assert.assertEquals(String.format("%s-%s", Nyt.string, Nyt.kuukausienP‰‰st‰(6)), kausi.getKausi().getKuvaus());
      Assert.assertEquals("", kausi.getTauko().getKuvaus());
      Assert.assertEquals(0, kausi.getTaukop‰ivi‰());
   }

   @Test
   public void testTaukoOsuu()
   {
      LocalDate nyt = DateUtil.t‰n‰‰n();
      Harrastaja harrastaja = teeHarrastaja("Nicklas Karlsson", "01.01.2015");
      teeHarjoittelusopimus(harrastaja, Nyt.string, 6);
      harrastaja.getTauko().setAlkaa(DateUtil.LocalDate2Date(nyt.plusMonths(1)));
      harrastaja.getTauko().setP‰‰ttyy(DateUtil.LocalDate2Date(nyt.plusMonths(2)));
      long taukoa = DateUtil.p‰ivi‰V‰liss‰(harrastaja.getTauko().getAlkaa(), harrastaja.getTauko().getP‰‰ttyy());
      Laskutuskausi kausi = harrastaja.getSopimukset().iterator().next().getLaskutuskausi();
      Assert.assertEquals(30, kausi.getTaukop‰ivi‰());
      Assert.assertEquals(String.format("%s-%s", Nyt.string, Nyt.kuukausienP‰‰st‰(6)), kausi.getKausi().getKuvaus());
      Assert.assertEquals(String.format("%s-%s", Nyt.kuukausienP‰‰st‰(1), Nyt.kuukausienP‰‰st‰(2)),
         kausi.getTauko().getKuvaus());
      Assert.assertEquals(taukoa, kausi.getTaukop‰ivi‰());
   }

   @Test
   public void testUmpeutuvaSopimusEiUmpeudu()
   {
      Sopimus sopimus = teeHarjoittelusopimus(new Harrastaja(), Nyt.string, 6);
      sopimus.setUmpeutuu(DateUtil.silloinD("01.01.2020"));
      Laskutuskausi kausi = sopimus.getLaskutuskausi();
      Assert.assertEquals(Nyt.string, DateUtil.formatoi(kausi.getKausi().getAlkaa()));
      Assert.assertEquals(Nyt.kuukausienP‰‰st‰(6), DateUtil.formatoi(kausi.getKausi().getP‰‰ttyy()));
      Assert.assertEquals(6, kausi.getKausikuukausia());
   }

   @Test
   public void testUmpeutuvaSopimusUmpeutuu()
   {
      Sopimus sopimus = teeHarjoittelusopimus(new Harrastaja(), Nyt.string, 6);
      sopimus.setUmpeutuu(DateUtil.kuukausienP‰‰st‰(2));
      Laskutuskausi kausi = sopimus.getLaskutuskausi();
      Assert.assertEquals(Nyt.string, DateUtil.formatoi(kausi.getKausi().getAlkaa()));
      Assert.assertEquals(Nyt.kuukausienP‰‰st‰(2), DateUtil.formatoi(kausi.getKausi().getP‰‰ttyy()));
      Assert.assertEquals(2, kausi.getKausikuukausia());
   }

   @Test
   public void testTaukoAlkuOsuu()
   {
      Harrastaja harrastaja = teeHarrastaja("Nicklas Karlsson", "01.01.2015");
      teeHarjoittelusopimus(harrastaja, Nyt.string, 6);

      LocalDate taukoAlkaa = Nyt.localDate.minusMonths(1);
      LocalDate taukoP‰‰ttyy = Nyt.localDate.plusMonths(1);
      long taukoa = DateUtil.p‰ivi‰V‰liss‰(DateUtil.LocalDate2Date(taukoAlkaa), DateUtil.LocalDate2Date(taukoP‰‰ttyy));

      harrastaja.getTauko().setAlkaa(DateUtil.LocalDate2Date(taukoAlkaa));
      harrastaja.getTauko().setP‰‰ttyy(DateUtil.LocalDate2Date(taukoP‰‰ttyy));
      Laskutuskausi kausi = harrastaja.getSopimukset().iterator().next().getLaskutuskausi();
      Assert.assertEquals(String.format("%s-%s", Nyt.string, Nyt.kuukausienP‰‰st‰(6)), kausi.getKausi().getKuvaus());
      Assert.assertEquals(
         String.format("%s-%s", Nyt.string, DateUtil.p‰iv‰Tekstiksi(DateUtil.LocalDate2Date(taukoP‰‰ttyy))),
         kausi.getTauko().getKuvaus());
      Assert.assertEquals(taukoa - DateUtil.p‰ivi‰V‰liss‰(DateUtil.LocalDate2Date(taukoAlkaa), Nyt.date),
         kausi.getTaukop‰ivi‰());
   }

   @Test
   public void testTaukoLoppuOsuu()
   {
      Harrastaja harrastaja = teeHarrastaja("Nicklas Karlsson", "01.01.2015");
      teeHarjoittelusopimus(harrastaja, Nyt.string, 6);

      LocalDate taukoAlkaa = Nyt.localDate.plusMonths(5);
      LocalDate taukoP‰‰ttyy = Nyt.localDate.plusMonths(7);
      long taukoa = DateUtil.p‰ivi‰V‰liss‰(DateUtil.LocalDate2Date(taukoAlkaa), DateUtil.LocalDate2Date(taukoP‰‰ttyy));

      harrastaja.getTauko().setAlkaa(DateUtil.LocalDate2Date(taukoAlkaa));
      harrastaja.getTauko().setP‰‰ttyy(DateUtil.LocalDate2Date(taukoP‰‰ttyy));
      Laskutuskausi kausi = harrastaja.getSopimukset().iterator().next().getLaskutuskausi();
      Assert.assertEquals(String.format("%s-%s", Nyt.string, Nyt.kuukausienP‰‰st‰(6)), kausi.getKausi().getKuvaus());
      Assert.assertEquals(
         String.format("%s-%s", DateUtil.p‰iv‰Tekstiksi(DateUtil.LocalDate2Date(Nyt.localDate.plusMonths(5))),
            DateUtil.p‰iv‰Tekstiksi(DateUtil.LocalDate2Date(Nyt.localDate.plusMonths(6)))),
         kausi.getTauko().getKuvaus());
      Assert.assertEquals(taukoa - DateUtil.p‰ivi‰V‰liss‰(DateUtil.LocalDate2Date(Nyt.localDate.plusMonths(6)),
         DateUtil.LocalDate2Date(taukoP‰‰ttyy)), kausi.getTaukop‰ivi‰());

   }

}
