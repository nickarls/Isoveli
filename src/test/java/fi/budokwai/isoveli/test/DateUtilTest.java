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
   public void testT‰n‰‰n()
   {
      Calendar tarkistus = Calendar.getInstance();

      LocalDate t‰n‰‰n = DateUtil.t‰n‰‰n();
      Assert.assertEquals(tarkistus.get(Calendar.YEAR), t‰n‰‰n.getYear());
      Assert.assertEquals(tarkistus.get(Calendar.MONTH) + 1, t‰n‰‰n.getMonthValue());
      Assert.assertEquals(tarkistus.get(Calendar.DAY_OF_MONTH), t‰n‰‰n.getDayOfMonth());
   }

   @Test
   public void testAikav‰li2String()
   {
      Date d1 = DateUtil.string2Date("01.01.2015");
      Date d2 = DateUtil.string2Date("31.12.2015");
      Assert.assertEquals("01.01.2015-31.12.2015", DateUtil.aikav‰li2String(d1, d2));
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
   public void testP‰ivi‰V‰liss‰() throws ParseException
   {
      SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
      Date d1 = sdf.parse("01.01.2015");
      Date d2 = sdf.parse("11.01.2015");
      Assert.assertEquals(10, DateUtil.p‰ivi‰V‰liss‰(d1, d2));
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
   public void testOnkoV‰liss‰() throws ParseException
   {
      Date d1 = new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2015");
      Date d2 = new SimpleDateFormat("dd.MM.yyyy").parse("31.12.2015");
      Assert.assertTrue(DateUtil.onkoV‰liss‰(d1, d2));
   }

   @Test
   public void testOnkoV‰liss‰On() throws ParseException
   {
      Date d1 = new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2015");
      Date d2 = new SimpleDateFormat("dd.MM.yyyy").parse("31.12.2015");
      Date d3 = new SimpleDateFormat("dd.MM.yyyy").parse("01.06.2015");
      Assert.assertTrue(DateUtil.onkoV‰liss‰(new Jakso(d1, d2), d3));
   }

   @Test
   public void testOnkoV‰liss‰OnEnnen() throws ParseException
   {
      Date d1 = new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2015");
      Date d2 = new SimpleDateFormat("dd.MM.yyyy").parse("31.12.2015");
      Date d3 = new SimpleDateFormat("dd.MM.yyyy").parse("01.06.2014");
      Assert.assertFalse(DateUtil.onkoV‰liss‰(new Jakso(d1, d2), d3));
   }

   @Test
   public void testOnkoV‰liss‰OnJ‰lkeen() throws ParseException
   {
      Date d1 = new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2015");
      Date d2 = new SimpleDateFormat("dd.MM.yyyy").parse("31.12.2015");
      Date d3 = new SimpleDateFormat("dd.MM.yyyy").parse("01.06.2016");
      Assert.assertFalse(DateUtil.onkoV‰liss‰(new Jakso(d1, d2), d3));
   }

   @Test
   public void testOnkoV‰liss‰OnRajalla() throws ParseException
   {
      Date d1 = new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2015");
      Date d2 = new SimpleDateFormat("dd.MM.yyyy").parse("31.12.2015");
      Date d3 = new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2015");
      Assert.assertFalse(DateUtil.onkoV‰liss‰(new Jakso(d1, d2), d3));
   }

   @Test
   public void testT‰n‰‰nDate()
   {
      Calendar tarkistus = Calendar.getInstance();

      Date t‰n‰‰nDate = DateUtil.t‰n‰‰nDate();
      Calendar t‰n‰‰n = Calendar.getInstance();
      t‰n‰‰n.setTime(t‰n‰‰nDate);

      Assert.assertEquals(tarkistus.get(Calendar.YEAR), t‰n‰‰n.get(Calendar.YEAR));
      Assert.assertEquals(tarkistus.get(Calendar.MONTH), t‰n‰‰n.get(Calendar.MONTH));
      Assert.assertEquals(tarkistus.get(Calendar.DAY_OF_MONTH), t‰n‰‰n.get(Calendar.DAY_OF_MONTH));
      Assert.assertEquals(0, t‰n‰‰n.get(Calendar.HOUR_OF_DAY));
      Assert.assertEquals(0, t‰n‰‰n.get(Calendar.MINUTE));
      Assert.assertEquals(0, t‰n‰‰n.get(Calendar.SECOND));
      Assert.assertEquals(0, t‰n‰‰n.get(Calendar.MILLISECOND));
   }

   @Test
   public void testAikav‰li()
   {
      Calendar kuukaudenP‰‰st‰ = Calendar.getInstance();
      kuukaudenP‰‰st‰.add(Calendar.MONTH, 1);
      Period aikav‰li = DateUtil.aikav‰li(new Date(kuukaudenP‰‰st‰.getTimeInMillis()));
      Assert.assertEquals(0, aikav‰li.getDays());
      Assert.assertEquals(1, aikav‰li.getMonths());
      Assert.assertEquals(0, aikav‰li.getYears());
   }

   @Test
   public void testAikav‰liEteenp‰inYliVuoden()
   {
      Calendar kuukaudenP‰‰st‰ = Calendar.getInstance();
      kuukaudenP‰‰st‰.add(Calendar.MONTH, 13);
      Period aikav‰li = DateUtil.aikav‰li(new Date(kuukaudenP‰‰st‰.getTimeInMillis()));
      Assert.assertEquals(0, aikav‰li.getDays());
      Assert.assertEquals(1, aikav‰li.getMonths());
      Assert.assertEquals(1, aikav‰li.getYears());
   }

   @Test
   public void testAikav‰liTaaksep‰in()
   {
      Calendar kuukaudenP‰‰st‰ = Calendar.getInstance();
      kuukaudenP‰‰st‰.add(Calendar.MONTH, -1);
      Period aikav‰li = DateUtil.aikav‰li(new Date(kuukaudenP‰‰st‰.getTimeInMillis()));
      Assert.assertEquals(0, aikav‰li.getDays());
      Assert.assertEquals(-1, aikav‰li.getMonths());
      Assert.assertEquals(0, aikav‰li.getYears());
   }

   @Test
   public void testAikav‰liString()
   {
      Assert.assertEquals("1 vuosi", DateUtil.aikav‰li2String(Period.ofYears(1)));
      Assert.assertEquals("1 kuukausi", DateUtil.aikav‰li2String(Period.ofMonths(1)));
      Assert.assertEquals("1 p‰iv‰", DateUtil.aikav‰li2String(Period.ofDays(1)));
      Assert.assertEquals("2 vuotta", DateUtil.aikav‰li2String(Period.ofYears(2)));
      Assert.assertEquals("2 kuukautta", DateUtil.aikav‰li2String(Period.ofMonths(2)));
      Assert.assertEquals("2 p‰iv‰‰", DateUtil.aikav‰li2String(Period.ofDays(2)));
      Assert.assertEquals("1 vuosi, 2 kuukautta, 3 p‰iv‰‰", DateUtil.aikav‰li2String(Period.of(1, 2, 3)));
      Assert.assertEquals("1 vuosi, 2 kuukautta", DateUtil.aikav‰li2String(Period.of(1, 2, 0)));
      Assert.assertEquals("1 vuosi, 2 p‰iv‰‰", DateUtil.aikav‰li2String(Period.of(1, 0, 2)));
      Assert.assertEquals("1 kuukausi, 2 p‰iv‰‰", DateUtil.aikav‰li2String(Period.of(0, 1, 2)));
      Assert.assertEquals("-1 vuosi", DateUtil.aikav‰li2String(Period.ofYears(-1)));
      Assert.assertEquals("-2 vuotta", DateUtil.aikav‰li2String(Period.ofYears(-2)));
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
   public void testFormatoiP‰iv‰m‰‰r‰() throws ParseException
   {
      Date tarkistus = new SimpleDateFormat("dd.MM.yyyy").parse("02.02.2015");
      Assert.assertEquals("02.02.2015", DateUtil.formatoi(tarkistus));
   }

   @Test
   public void testP‰ivi‰v‰liss‰()
   {
      Calendar sitten = Calendar.getInstance();
      sitten.add(Calendar.DAY_OF_MONTH, 10);
      Date sittenDate = new Date(sitten.getTimeInMillis());
      long tarkistus = DateUtil.getP‰ivi‰V‰liss‰(sittenDate);
      Assert.assertEquals(10, tarkistus);

      sitten = Calendar.getInstance();
      sitten.add(Calendar.DAY_OF_MONTH, -10);
      sittenDate = new Date(sitten.getTimeInMillis());
      tarkistus = DateUtil.getP‰ivi‰V‰liss‰(sittenDate);
      Assert.assertEquals(-10, tarkistus);
   }

   @Test
   public void testIk‰() throws ParseException
   {
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.YEAR, -20);
      Date date = new Date(cal.getTimeInMillis());
      Assert.assertEquals(20, DateUtil.ik‰(date));

      cal = Calendar.getInstance();
      cal.add(Calendar.YEAR, -20);
      cal.add(Calendar.MONTH, -1);
      date = new Date(cal.getTimeInMillis());
      Assert.assertEquals(20, DateUtil.ik‰(date));

      cal = Calendar.getInstance();
      cal.add(Calendar.YEAR, -20);
      cal.add(Calendar.MONTH, 1);
      date = new Date(cal.getTimeInMillis());
      Assert.assertEquals(19, DateUtil.ik‰(date));

      cal = Calendar.getInstance();
      cal.add(Calendar.YEAR, -20);
      cal.add(Calendar.DAY_OF_MONTH, -1);
      date = new Date(cal.getTimeInMillis());
      Assert.assertEquals(20, DateUtil.ik‰(date));

      cal = Calendar.getInstance();
      cal.add(Calendar.YEAR, -20);
      cal.add(Calendar.DAY_OF_MONTH, 1);
      date = new Date(cal.getTimeInMillis());
      Assert.assertEquals(19, DateUtil.ik‰(date));

      cal = Calendar.getInstance();
      cal.add(Calendar.DAY_OF_MONTH, 1);
      date = new Date(cal.getTimeInMillis());
      Assert.assertEquals(0, DateUtil.ik‰(date));

   }

   @Test
   public void testLaskutusKuukausiaV‰liss‰() throws ParseException
   {
      Date d1 = new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2015");
      Date d2 = new SimpleDateFormat("dd.MM.yyyy").parse("02.02.2015");
      Assert.assertEquals(2, DateUtil.laskutuskuukausiaV‰liss‰(new Jakso(d1, d2)));

      d1 = new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2015");
      d2 = new SimpleDateFormat("dd.MM.yyyy").parse("02.01.2015");
      Assert.assertEquals(1, DateUtil.laskutuskuukausiaV‰liss‰(new Jakso(d1, d2)));

      d1 = new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2014");
      d2 = new SimpleDateFormat("dd.MM.yyyy").parse("02.01.2015");
      Assert.assertEquals(13, DateUtil.laskutuskuukausiaV‰liss‰(new Jakso(d1, d2)));
   }

   @Test
   public void testKuukausiaV‰liss‰() throws ParseException
   {
      Calendar sitten = Calendar.getInstance();
      sitten.add(Calendar.MONTH, 1);
      Date check = new Date(sitten.getTimeInMillis());
      Assert.assertEquals(1, DateUtil.kuukausiaV‰liss‰(new Date(), check));

      sitten = Calendar.getInstance();
      sitten.add(Calendar.MONTH, -1);
      check = new Date(sitten.getTimeInMillis());
      Assert.assertEquals(-1, DateUtil.kuukausiaV‰liss‰(new Date(), check));

      sitten = Calendar.getInstance();
      sitten.add(Calendar.YEAR, 1);
      check = new Date(sitten.getTimeInMillis());
      Assert.assertEquals(12, DateUtil.kuukausiaV‰liss‰(new Date(), check));

      sitten = Calendar.getInstance();
      sitten.add(Calendar.MONTH, 13);
      check = new Date(sitten.getTimeInMillis());
      Assert.assertEquals(13, DateUtil.kuukausiaV‰liss‰(new Date(), check));

      Date d1 = new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2015");
      Date d2 = new SimpleDateFormat("dd.MM.yyyy").parse("01.02.2015");
      Assert.assertEquals(1, DateUtil.laskutuskuukausiaV‰liss‰(new Jakso(d1, d2)));
   }

   @Test
   public void testVuodenViimeinenP‰iv‰()
   {
      Calendar nyt = Calendar.getInstance();
      LocalDate viimeinen = DateUtil.vuodenViimeinenP‰iv‰();
      Assert.assertEquals(nyt.get(Calendar.YEAR), viimeinen.getYear());
      Assert.assertEquals(12, viimeinen.getMonthValue());
      Assert.assertEquals(31, viimeinen.getDayOfMonth());
   }

   @Test
   public void testLaskutusVuosiaV‰liss‰SamaVuosi() throws ParseException
   {
      Date d1 = new SimpleDateFormat("dd.MM.yyyy").parse("12.12.2000");
      Date d2 = new SimpleDateFormat("dd.MM.yyyy").parse("14.12.2000");
      int tulos = DateUtil.laskutusvuosiaV‰liss‰(new Jakso(d1, d2));
      Assert.assertEquals(1, tulos);
   }

   @Test
   public void testLaskutusVuosiaV‰liss‰EriVuosi() throws ParseException
   {
      Date d1 = new SimpleDateFormat("dd.MM.yyyy").parse("12.12.2000");
      Date d2 = new SimpleDateFormat("dd.MM.yyyy").parse("14.12.2001");
      int tulos = DateUtil.laskutusvuosiaV‰liss‰(new Jakso(d1, d2));
      Assert.assertEquals(2, tulos);
   }

   @Test
   public void testaaVuosienP‰‰st‰() throws ParseException
   {
      Date koska = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse("02.02.2015 10:11:12");
      Date date = DateUtil.vuosienP‰‰st‰(koska, 20);
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
   public void testKuukausienP‰‰st‰() throws ParseException
   {
      Date koska = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse("02.02.2015 10:11:12");
      LocalDate date = DateUtil.kuukausienP‰‰st‰(koska, 2);
      Assert.assertEquals(2, date.getDayOfMonth());
      Assert.assertEquals(4, date.getMonthValue());
      Assert.assertEquals(2015, date.getYear());

      koska = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse("02.02.2015 10:11:12");
      date = DateUtil.kuukausienP‰‰st‰(koska, 13);
      Assert.assertEquals(2, date.getDayOfMonth());
      Assert.assertEquals(3, date.getMonthValue());
      Assert.assertEquals(2016, date.getYear());

      koska = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse("02.02.2015 10:11:12");
      date = DateUtil.kuukausienP‰‰st‰(koska, -1);
      Assert.assertEquals(2, date.getDayOfMonth());
      Assert.assertEquals(1, date.getMonthValue());
      Assert.assertEquals(2015, date.getYear());
   }

   @Test
   public void testKuukausienP‰‰st‰2() throws ParseException
   {
      Calendar sitten = Calendar.getInstance();
      sitten.add(Calendar.MONTH, 2);
      Date date = DateUtil.kuukausienP‰‰st‰(2);
      Calendar check = Calendar.getInstance();
      check.setTime(date);
      Assert.assertEquals(sitten.get(Calendar.YEAR), check.get(Calendar.YEAR));
      Assert.assertEquals(sitten.get(Calendar.MONTH), check.get(Calendar.MONTH));
      Assert.assertEquals(sitten.get(Calendar.DAY_OF_MONTH), check.get(Calendar.DAY_OF_MONTH));

      sitten = Calendar.getInstance();
      sitten.add(Calendar.MONTH, -2);
      date = DateUtil.kuukausienP‰‰st‰(-2);
      check = Calendar.getInstance();
      check.setTime(date);
      Assert.assertEquals(sitten.get(Calendar.YEAR), check.get(Calendar.YEAR));
      Assert.assertEquals(sitten.get(Calendar.MONTH), check.get(Calendar.MONTH));
      Assert.assertEquals(sitten.get(Calendar.DAY_OF_MONTH), check.get(Calendar.DAY_OF_MONTH));

      sitten = Calendar.getInstance();
      sitten.add(Calendar.MONTH, -13);
      date = DateUtil.kuukausienP‰‰st‰(-13);
      check = Calendar.getInstance();
      check.setTime(date);
      Assert.assertEquals(sitten.get(Calendar.YEAR), check.get(Calendar.YEAR));
      Assert.assertEquals(sitten.get(Calendar.MONTH), check.get(Calendar.MONTH));
      Assert.assertEquals(sitten.get(Calendar.DAY_OF_MONTH), check.get(Calendar.DAY_OF_MONTH));

   }

   @Test
   public void testOnkoMenneisyydess‰() throws ParseException
   {
      Date test = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse("02.02.2015 10:11:12");
      Assert.assertTrue(DateUtil.onkoMenneysyydess‰(test));
      test = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse("02.02.2016 10:11:12");
      Assert.assertFalse(DateUtil.onkoMenneysyydess‰(test));
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
   public void testP‰ivienP‰‰st‰() throws ParseException
   {
      LocalDate t‰n‰‰n = DateUtil.t‰n‰‰n();
      LocalDate sitten = t‰n‰‰n.plus(10, ChronoUnit.DAYS);
      LocalDate testi = DateUtil.Date2LocalDate(DateUtil.p‰ivienP‰‰st‰(10));
      
      Assert.assertEquals(sitten.getYear(), testi.getYear());
      Assert.assertEquals(sitten.getMonthValue(), testi.getMonthValue());
      Assert.assertEquals(sitten.getDayOfMonth(), testi.getDayOfMonth());

      sitten = t‰n‰‰n.minus(10, ChronoUnit.DAYS);
      testi = DateUtil.Date2LocalDate(DateUtil.p‰ivienP‰‰st‰(-10));

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
   public void testOnkoAiemmin() {
      Date d1 = DateUtil.silloinD("01.01.2015");
      LocalDate d2 = DateUtil.t‰n‰‰n();
      Assert.assertTrue(DateUtil.onkoAiemmin(d1, d2));
   }
   
   @Test
   public void testOnkoEiAiemmin() {
      Date d1 = DateUtil.silloinD("01.01.2016");
      LocalDate d2 = DateUtil.t‰n‰‰n();
      Assert.assertFalse(DateUtil.onkoAiemmin(d1, d2));
   }

}
