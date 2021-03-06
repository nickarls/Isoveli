package fi.budokwai.isoveli.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import fi.budokwai.isoveli.malli.Jakso;
import fi.budokwai.isoveli.util.DateUtil;

public class DateUtilTest
{
   @Test
   public void testTänään()
   {
      Calendar tarkistus = Calendar.getInstance();

      LocalDate tänään = DateUtil.tänään();
      Assert.assertEquals(tarkistus.get(Calendar.YEAR), tänään.getYear());
      Assert.assertEquals(tarkistus.get(Calendar.MONTH) + 1, tänään.getMonthValue());
      Assert.assertEquals(tarkistus.get(Calendar.DAY_OF_MONTH), tänään.getDayOfMonth());
   }

   @Test
   public void testAikaväli2String()
   {
      Date d1 = DateUtil.string2Date("01.01.2015");
      Date d2 = DateUtil.string2Date("31.12.2015");
      Assert.assertEquals("01.01.2015-31.12.2015", DateUtil.aikaväli2String(d1, d2));
   }

   @Test
   public void testString2Date()
   {
      Date d = DateUtil.string2Date("01.01.2015");
      Calendar cal = Calendar.getInstance();
      cal.setTime(d);
      Assert.assertEquals(1, cal.get(Calendar.MONTH) + 1);
      Assert.assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
      Assert.assertEquals(2015, cal.get(Calendar.YEAR));
   }

   @Test
   public void testPäiviäVälissä() throws ParseException
   {
      SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
      Date d1 = sdf.parse("01.01.2015");
      Date d2 = sdf.parse("11.01.2015");
      Assert.assertEquals(10, DateUtil.päiviäVälissä(d1, d2));
   }

   @Test
   public void testSilloin()
   {
      LocalDate silloin = DateUtil.silloin("01.01.2015");
      Assert.assertEquals(2015, silloin.getYear());
      Assert.assertEquals(1, silloin.getMonthValue());
      Assert.assertEquals(1, silloin.getDayOfMonth());
   }

   @Test
   public void testOnkoVälissä() throws ParseException
   {
      Date d1 = new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2015");
      Date d2 = new SimpleDateFormat("dd.MM.yyyy").parse("31.12.2015");
      Assert.assertTrue(DateUtil.onkoVälissä(d1, d2));
   }

   @Test
   public void testOnkoVälissäOn() throws ParseException
   {
      Date d1 = new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2015");
      Date d2 = new SimpleDateFormat("dd.MM.yyyy").parse("31.12.2015");
      Date d3 = new SimpleDateFormat("dd.MM.yyyy").parse("01.06.2015");
      Assert.assertTrue(DateUtil.onkoVälissä(new Jakso(d1, d2), d3));
   }

   @Test
   public void testOnkoVälissäOnEnnen() throws ParseException
   {
      Date d1 = new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2015");
      Date d2 = new SimpleDateFormat("dd.MM.yyyy").parse("31.12.2015");
      Date d3 = new SimpleDateFormat("dd.MM.yyyy").parse("01.06.2014");
      Assert.assertFalse(DateUtil.onkoVälissä(new Jakso(d1, d2), d3));
   }

   @Test
   public void testOnkoVälissäOnJälkeen() throws ParseException
   {
      Date d1 = new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2015");
      Date d2 = new SimpleDateFormat("dd.MM.yyyy").parse("31.12.2015");
      Date d3 = new SimpleDateFormat("dd.MM.yyyy").parse("01.06.2016");
      Assert.assertFalse(DateUtil.onkoVälissä(new Jakso(d1, d2), d3));
   }

   @Test
   public void testOnkoVälissäOnRajalla() throws ParseException
   {
      Date d1 = new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2015");
      Date d2 = new SimpleDateFormat("dd.MM.yyyy").parse("31.12.2015");
      Date d3 = new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2015");
      Assert.assertFalse(DateUtil.onkoVälissä(new Jakso(d1, d2), d3));
   }

   @Test
   public void testTänäänDate()
   {
      Calendar tarkistus = Calendar.getInstance();

      Date tänäänDate = DateUtil.tänäänDate();
      Calendar tänään = Calendar.getInstance();
      tänään.setTime(tänäänDate);

      Assert.assertEquals(tarkistus.get(Calendar.YEAR), tänään.get(Calendar.YEAR));
      Assert.assertEquals(tarkistus.get(Calendar.MONTH), tänään.get(Calendar.MONTH));
      Assert.assertEquals(tarkistus.get(Calendar.DAY_OF_MONTH), tänään.get(Calendar.DAY_OF_MONTH));
      Assert.assertEquals(0, tänään.get(Calendar.HOUR_OF_DAY));
      Assert.assertEquals(0, tänään.get(Calendar.MINUTE));
      Assert.assertEquals(0, tänään.get(Calendar.SECOND));
      Assert.assertEquals(0, tänään.get(Calendar.MILLISECOND));
   }

   @Test
   public void testAikaväli()
   {
      Calendar kuukaudenPäästä = Calendar.getInstance();
      kuukaudenPäästä.add(Calendar.MONTH, 1);
      Period aikaväli = DateUtil.aikaväli(new Date(kuukaudenPäästä.getTimeInMillis()));
      Assert.assertEquals(0, aikaväli.getDays());
      Assert.assertEquals(1, aikaväli.getMonths());
      Assert.assertEquals(0, aikaväli.getYears());
   }

   @Test
   public void testAikaväliEteenpäinYliVuoden()
   {
      Calendar kuukaudenPäästä = Calendar.getInstance();
      kuukaudenPäästä.add(Calendar.MONTH, 13);
      Period aikaväli = DateUtil.aikaväli(new Date(kuukaudenPäästä.getTimeInMillis()));
      Assert.assertEquals(0, aikaväli.getDays());
      Assert.assertEquals(1, aikaväli.getMonths());
      Assert.assertEquals(1, aikaväli.getYears());
   }

   @Test
   public void testAikaväliTaaksepäin()
   {
      Calendar kuukaudenPäästä = Calendar.getInstance();
      kuukaudenPäästä.add(Calendar.MONTH, -1);
      Period aikaväli = DateUtil.aikaväli(new Date(kuukaudenPäästä.getTimeInMillis()));
      Assert.assertEquals(0, aikaväli.getDays());
      Assert.assertEquals(-1, aikaväli.getMonths());
      Assert.assertEquals(0, aikaväli.getYears());
   }

   @Test
   public void testAikaväliString()
   {
      Assert.assertEquals("1 vuosi", DateUtil.aikaväli2String(Period.ofYears(1)));
      Assert.assertEquals("1 kuukausi", DateUtil.aikaväli2String(Period.ofMonths(1)));
      Assert.assertEquals("1 päivä", DateUtil.aikaväli2String(Period.ofDays(1)));
      Assert.assertEquals("2 vuotta", DateUtil.aikaväli2String(Period.ofYears(2)));
      Assert.assertEquals("2 kuukautta", DateUtil.aikaväli2String(Period.ofMonths(2)));
      Assert.assertEquals("2 päivää", DateUtil.aikaväli2String(Period.ofDays(2)));
      Assert.assertEquals("1 vuosi, 2 kuukautta, 3 päivää", DateUtil.aikaväli2String(Period.of(1, 2, 3)));
      Assert.assertEquals("1 vuosi, 2 kuukautta", DateUtil.aikaväli2String(Period.of(1, 2, 0)));
      Assert.assertEquals("1 vuosi, 2 päivää", DateUtil.aikaväli2String(Period.of(1, 0, 2)));
      Assert.assertEquals("1 kuukausi, 2 päivää", DateUtil.aikaväli2String(Period.of(0, 1, 2)));
      Assert.assertEquals("-1 vuosi", DateUtil.aikaväli2String(Period.ofYears(-1)));
      Assert.assertEquals("-2 vuotta", DateUtil.aikaväli2String(Period.ofYears(-2)));
   }

   @Test
   public void testDate2LocalDate()
   {
      Calendar nyt = Calendar.getInstance();
      Date nytDate = new Date(nyt.getTimeInMillis());
      LocalDate tarkistus = DateUtil.Date2LocalDate(nytDate);
      Assert.assertEquals(nyt.get(Calendar.YEAR), tarkistus.getYear());
      Assert.assertEquals(nyt.get(Calendar.MONTH) + 1, tarkistus.getMonthValue());
      Assert.assertEquals(nyt.get(Calendar.DAY_OF_MONTH), tarkistus.getDayOfMonth());
   }

   @Test
   public void testDate2LocalDateTime()
   {
      Calendar nyt = Calendar.getInstance();
      Date nytDate = new Date(nyt.getTimeInMillis());
      LocalDateTime tarkistus = DateUtil.Date2LocalDateTime(nytDate);
      Assert.assertEquals(nyt.get(Calendar.YEAR), tarkistus.getYear());
      Assert.assertEquals(nyt.get(Calendar.MONTH) + 1, tarkistus.getMonthValue());
      Assert.assertEquals(nyt.get(Calendar.DAY_OF_MONTH), tarkistus.getDayOfMonth());
      Assert.assertEquals(nyt.get(Calendar.HOUR_OF_DAY), tarkistus.getHour());
      Assert.assertEquals(nyt.get(Calendar.MINUTE), tarkistus.getMinute());
      Assert.assertEquals(nyt.get(Calendar.SECOND), tarkistus.getSecond());
   }

   @Test
   public void testFormatoiPäivämäärä() throws ParseException
   {
      Date tarkistus = new SimpleDateFormat("dd.MM.yyyy").parse("02.02.2015");
      Assert.assertEquals("02.02.2015", DateUtil.formatoi(tarkistus));
   }

   @Test
   public void testPäiviävälissä()
   {
      Calendar sitten = Calendar.getInstance();
      sitten.add(Calendar.DAY_OF_MONTH, 10);
      Date sittenDate = new Date(sitten.getTimeInMillis());
      long tarkistus = DateUtil.getPäiviäVälissä(sittenDate);
      Assert.assertEquals(10, tarkistus);

      sitten = Calendar.getInstance();
      sitten.add(Calendar.DAY_OF_MONTH, -10);
      sittenDate = new Date(sitten.getTimeInMillis());
      tarkistus = DateUtil.getPäiviäVälissä(sittenDate);
      Assert.assertEquals(-10, tarkistus);
   }

   @Test
   public void testIkä() throws ParseException
   {
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.YEAR, -20);
      Date date = new Date(cal.getTimeInMillis());
      Assert.assertEquals(20, DateUtil.ikä(date));

      cal = Calendar.getInstance();
      cal.add(Calendar.YEAR, -20);
      cal.add(Calendar.MONTH, -1);
      date = new Date(cal.getTimeInMillis());
      Assert.assertEquals(20, DateUtil.ikä(date));

      cal = Calendar.getInstance();
      cal.add(Calendar.YEAR, -20);
      cal.add(Calendar.MONTH, 1);
      date = new Date(cal.getTimeInMillis());
      Assert.assertEquals(19, DateUtil.ikä(date));

      cal = Calendar.getInstance();
      cal.add(Calendar.YEAR, -20);
      cal.add(Calendar.DAY_OF_MONTH, -1);
      date = new Date(cal.getTimeInMillis());
      Assert.assertEquals(20, DateUtil.ikä(date));

      cal = Calendar.getInstance();
      cal.add(Calendar.YEAR, -20);
      cal.add(Calendar.DAY_OF_MONTH, 1);
      date = new Date(cal.getTimeInMillis());
      Assert.assertEquals(19, DateUtil.ikä(date));

      cal = Calendar.getInstance();
      cal.add(Calendar.DAY_OF_MONTH, 1);
      date = new Date(cal.getTimeInMillis());
      Assert.assertEquals(0, DateUtil.ikä(date));

   }

   @Test
   public void testLaskutusKuukausiaVälissä() throws ParseException
   {
      Date d1 = new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2015");
      Date d2 = new SimpleDateFormat("dd.MM.yyyy").parse("02.02.2015");
      Assert.assertEquals(2, DateUtil.laskutuskuukausiaVälissä(new Jakso(d1, d2)));

      d1 = new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2015");
      d2 = new SimpleDateFormat("dd.MM.yyyy").parse("02.01.2015");
      Assert.assertEquals(1, DateUtil.laskutuskuukausiaVälissä(new Jakso(d1, d2)));

      d1 = new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2014");
      d2 = new SimpleDateFormat("dd.MM.yyyy").parse("02.01.2015");
      Assert.assertEquals(13, DateUtil.laskutuskuukausiaVälissä(new Jakso(d1, d2)));
   }

   @Test
   public void testKuukausiaVälissä() throws ParseException
   {
      Calendar sitten = Calendar.getInstance();
      sitten.add(Calendar.MONTH, 1);
      Date check = new Date(sitten.getTimeInMillis());
      Assert.assertEquals(1, DateUtil.kuukausiaVälissä(new Date(), check));

      sitten = Calendar.getInstance();
      sitten.add(Calendar.MONTH, -1);
      check = new Date(sitten.getTimeInMillis());
      Assert.assertEquals(-1, DateUtil.kuukausiaVälissä(new Date(), check));

      sitten = Calendar.getInstance();
      sitten.add(Calendar.YEAR, 1);
      check = new Date(sitten.getTimeInMillis());
      Assert.assertEquals(12, DateUtil.kuukausiaVälissä(new Date(), check));

      sitten = Calendar.getInstance();
      sitten.add(Calendar.MONTH, 13);
      check = new Date(sitten.getTimeInMillis());
      Assert.assertEquals(13, DateUtil.kuukausiaVälissä(new Date(), check));

      Date d1 = new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2015");
      Date d2 = new SimpleDateFormat("dd.MM.yyyy").parse("01.02.2015");
      Assert.assertEquals(1, DateUtil.laskutuskuukausiaVälissä(new Jakso(d1, d2)));
   }

   @Test
   public void testVuodenViimeinenPäivä()
   {
      Calendar nyt = Calendar.getInstance();
      LocalDate viimeinen = DateUtil.vuodenViimeinenPäivä();
      Assert.assertEquals(nyt.get(Calendar.YEAR), viimeinen.getYear());
      Assert.assertEquals(12, viimeinen.getMonthValue());
      Assert.assertEquals(31, viimeinen.getDayOfMonth());
   }

   @Test
   public void testLaskutusVuosiaVälissäSamaVuosi() throws ParseException
   {
      Date d1 = new SimpleDateFormat("dd.MM.yyyy").parse("12.12.2000");
      Date d2 = new SimpleDateFormat("dd.MM.yyyy").parse("14.12.2000");
      int tulos = DateUtil.laskutusvuosiaVälissä(new Jakso(d1, d2));
      Assert.assertEquals(1, tulos);
   }

   @Test
   public void testLaskutusVuosiaVälissäEriVuosi() throws ParseException
   {
      Date d1 = new SimpleDateFormat("dd.MM.yyyy").parse("12.12.2000");
      Date d2 = new SimpleDateFormat("dd.MM.yyyy").parse("14.12.2001");
      int tulos = DateUtil.laskutusvuosiaVälissä(new Jakso(d1, d2));
      Assert.assertEquals(2, tulos);
   }

   @Test
   public void testaaVuosienPäästä() throws ParseException
   {
      Date koska = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse("02.02.2015 10:11:12");
      Date date = DateUtil.vuosienPäästä(koska, 20);
      Calendar check = Calendar.getInstance();
      check.setTime(date);
      Assert.assertEquals(2, check.get(Calendar.MONTH) + 1);
      Assert.assertEquals(2, check.get(Calendar.DAY_OF_MONTH));
      Assert.assertEquals(2035, check.get(Calendar.YEAR));
      Assert.assertEquals(0, check.get(Calendar.HOUR_OF_DAY));
      Assert.assertEquals(0, check.get(Calendar.MINUTE));
      Assert.assertEquals(0, check.get(Calendar.SECOND));
   }

   @Test
   public void testKuukausienPäästä() throws ParseException
   {
      Date koska = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse("02.02.2015 10:11:12");
      LocalDate date = DateUtil.kuukausienPäästä(koska, 2);
      Assert.assertEquals(2, date.getDayOfMonth());
      Assert.assertEquals(4, date.getMonthValue());
      Assert.assertEquals(2015, date.getYear());

      koska = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse("02.02.2015 10:11:12");
      date = DateUtil.kuukausienPäästä(koska, 13);
      Assert.assertEquals(2, date.getDayOfMonth());
      Assert.assertEquals(3, date.getMonthValue());
      Assert.assertEquals(2016, date.getYear());

      koska = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse("02.02.2015 10:11:12");
      date = DateUtil.kuukausienPäästä(koska, -1);
      Assert.assertEquals(2, date.getDayOfMonth());
      Assert.assertEquals(1, date.getMonthValue());
      Assert.assertEquals(2015, date.getYear());
   }

   @Test
   public void testKuukausienPäästä2() throws ParseException
   {
      Calendar sitten = Calendar.getInstance();
      sitten.add(Calendar.MONTH, 2);
      Date date = DateUtil.kuukausienPäästä(2);
      Calendar check = Calendar.getInstance();
      check.setTime(date);
      Assert.assertEquals(sitten.get(Calendar.YEAR), check.get(Calendar.YEAR));
      Assert.assertEquals(sitten.get(Calendar.MONTH), check.get(Calendar.MONTH));
      Assert.assertEquals(sitten.get(Calendar.DAY_OF_MONTH), check.get(Calendar.DAY_OF_MONTH));

      sitten = Calendar.getInstance();
      sitten.add(Calendar.MONTH, -2);
      date = DateUtil.kuukausienPäästä(-2);
      check = Calendar.getInstance();
      check.setTime(date);
      Assert.assertEquals(sitten.get(Calendar.YEAR), check.get(Calendar.YEAR));
      Assert.assertEquals(sitten.get(Calendar.MONTH), check.get(Calendar.MONTH));
      Assert.assertEquals(sitten.get(Calendar.DAY_OF_MONTH), check.get(Calendar.DAY_OF_MONTH));

      sitten = Calendar.getInstance();
      sitten.add(Calendar.MONTH, -13);
      date = DateUtil.kuukausienPäästä(-13);
      check = Calendar.getInstance();
      check.setTime(date);
      Assert.assertEquals(sitten.get(Calendar.YEAR), check.get(Calendar.YEAR));
      Assert.assertEquals(sitten.get(Calendar.MONTH), check.get(Calendar.MONTH));
      Assert.assertEquals(sitten.get(Calendar.DAY_OF_MONTH), check.get(Calendar.DAY_OF_MONTH));

   }

   @Test
   public void testOnkoMenneisyydessä() throws ParseException
   {
      Date test = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse("02.02.2015 10:11:12");
      Assert.assertTrue(DateUtil.onkoMenneysyydessä(test));
      test = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse("02.02.2016 10:11:12");
      Assert.assertFalse(DateUtil.onkoMenneysyydessä(test));
   }

   @Test
   public void testOnkoTulevaisuudessa() throws ParseException
   {
      Date test = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse("02.02.2015 10:11:12");
      Assert.assertFalse(DateUtil.onkoTulevaisuudessa(test));
      test = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse("02.02.2016 10:11:12");
      Assert.assertTrue(DateUtil.onkoTulevaisuudessa(test));
   }

   @Test
   public void testPäivienPäästä() throws ParseException
   {
      LocalDate tänään = DateUtil.tänään();
      LocalDate sitten = tänään.plus(10, ChronoUnit.DAYS);
      LocalDate testi = DateUtil.Date2LocalDate(DateUtil.päivienPäästä(10));

      Assert.assertEquals(sitten.getYear(), testi.getYear());
      Assert.assertEquals(sitten.getMonthValue(), testi.getMonthValue());
      Assert.assertEquals(sitten.getDayOfMonth(), testi.getDayOfMonth());

      sitten = tänään.minus(10, ChronoUnit.DAYS);
      testi = DateUtil.Date2LocalDate(DateUtil.päivienPäästä(-10));

      Assert.assertEquals(sitten.getYear(), testi.getYear());
      Assert.assertEquals(sitten.getMonthValue(), testi.getMonthValue());
      Assert.assertEquals(sitten.getDayOfMonth(), testi.getDayOfMonth());
   }

   @Test
   public void testLocalDate2Date()
   {
      LocalDate testDate = LocalDate.of(2015, 2, 2);
      Date result = DateUtil.LocalDate2Date(testDate);
      Calendar cal = Calendar.getInstance();
      cal.setTime(result);
      Assert.assertEquals(cal.get(Calendar.YEAR), testDate.getYear());
      Assert.assertEquals(cal.get(Calendar.MONTH) + 1, testDate.getMonthValue());
      Assert.assertEquals(cal.get(Calendar.DAY_OF_MONTH), testDate.getDayOfMonth());
   }

   @Test
   public void testSamat()
   {
      Date a = DateUtil.silloinD("01.02.2015");
      LocalDate b = DateUtil.silloin("01.02.2015");
      Assert.assertTrue(DateUtil.samat(a, b));
   }

   @Test
   public void testEiSamat()
   {
      Date a = DateUtil.silloinD("01.02.2015");
      LocalDate b = DateUtil.silloin("02.02.2015");
      Assert.assertFalse(DateUtil.samat(a, b));
   }

   @Test
   public void testOnkoAiemmin()
   {
      Date d1 = DateUtil.silloinD("01.01.2015");
      LocalDate d2 = DateUtil.tänään();
      Assert.assertTrue(DateUtil.onkoAiemmin(d1, d2));
   }

   @Test
   public void testOnkoAiemminSamaPäivä()
   {
      Date d1 = DateUtil.silloinD("01.01.2015");
      LocalDate d2 = DateUtil.silloin("01.01.2015");
      Assert.assertFalse(DateUtil.onkoAiemmin(d1, d2));
   }

   @Test
   public void testOnkoEiAiemmin()
   {
      Date d1 = DateUtil.silloinD("01.01.2016");
      LocalDate d2 = DateUtil.tänään();
      Assert.assertFalse(DateUtil.onkoAiemmin(d1, d2));
   }

   @Test
   public void testOnkoAikaAiemmin() throws ParseException
   {
      Date d1 = new SimpleDateFormat("dd.MM.yyyy HH:mm").parse("01.01.2015 16:00");
      Date d2 = new SimpleDateFormat("dd.MM.yyyy HH:mm").parse("01.01.2015 17:00");
      Assert.assertTrue(DateUtil.onkoAikaAiemmin(d1, d2));
   }

   @Test
   public void testOnkoAikaEiAiemmin() throws ParseException
   {
      Date d1 = new SimpleDateFormat("dd.MM.yyyy HH:mm").parse("01.01.2015 16:00");
      Date d2 = new SimpleDateFormat("dd.MM.yyyy HH:mm").parse("01.01.2015 17:00");
      Assert.assertFalse(DateUtil.onkoAikaAiemmin(d2, d1));
   }

   @Test
   public void testOnkoAikaEiAiemminSamat() throws ParseException
   {
      Date d1 = new SimpleDateFormat("dd.MM.yyyy HH:mm").parse("01.01.2015 16:00");
      Date d2 = new SimpleDateFormat("dd.MM.yyyy HH:mm").parse("01.01.2015 16:00");
      Assert.assertFalse(DateUtil.onkoAikaAiemmin(d1, d2));
   }

   @Test
   public void testPäiväTekstiksi()
   {
      Date päivä = DateUtil.silloinD("12.12.2012");
      Assert.assertEquals("12.12.2012", DateUtil.päiväTekstiksi(päivä));
   }

}
