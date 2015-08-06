package fi.budokwai.isoveli.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

import fi.budokwai.isoveli.malli.Jakso;

public class DateUtil
{
   public static LocalDateTime Date2LocalDateTime(Date p‰iv‰m‰‰r‰)
   {
      Instant instant = Instant.ofEpochMilli(p‰iv‰m‰‰r‰.getTime());
      LocalDateTime res = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
      return res;
   }

   public static LocalDate Date2LocalDate(Date p‰iv‰m‰‰r‰)
   {
      return Date2LocalDateTime(p‰iv‰m‰‰r‰).toLocalDate();
   }

   @SuppressWarnings("unused")
   private LocalTime Date2LocalTime(Date p‰iv‰m‰‰r‰)
   {
      return Date2LocalDateTime(p‰iv‰m‰‰r‰).toLocalTime();
   }

   public static Date LocalDateTime2Date(LocalDateTime p‰iv‰m‰‰r‰)
   {
      Instant instant = p‰iv‰m‰‰r‰.atZone(ZoneId.systemDefault()).toInstant();
      Date res = Date.from(instant);
      return res;
   }

   public static Date LocalDate2Date(LocalDate p‰iv‰m‰‰r‰)
   {
      Instant instant = p‰iv‰m‰‰r‰.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
      Date res = Date.from(instant);
      return res;
   }

   private static LocalDate kuukausienP‰‰st‰LD(int kuukausia)
   {
      return t‰n‰‰n().plus(kuukausia, ChronoUnit.MONTHS);
   }

   public static Date kuukausienP‰‰st‰(int kuukausia)
   {
      return LocalDate2Date(kuukausienP‰‰st‰LD(kuukausia));
   }

   public static LocalDate t‰n‰‰n()
   {
      return LocalDate.now().atStartOfDay().toLocalDate();
   }

   public static String aikav‰li2String(Period aikav‰li)
   {
      if (aikav‰li == Period.ZERO)
      {
         return "";
      }
      StringBuilder tulos = new StringBuilder();
      if (aikav‰li.getYears() != 0)
      {
         String monikko = Math.abs(aikav‰li.getYears()) == 1 ? "vuosi" : "vuotta";
         tulos.append(String.format("%d %s", aikav‰li.getYears(), monikko));
      }
      if (aikav‰li.getMonths() != 0)
      {
         if (tulos.toString().length() > 0)
         {
            tulos.append(", ");
         }
         String monikko = Math.abs(aikav‰li.getMonths()) == 1 ? "kuukausi" : "kuukautta";
         tulos.append(String.format("%d %s", aikav‰li.getMonths(), monikko));
      }
      if (aikav‰li.getDays() != 0)
      {
         if (tulos.toString().length() > 0)
         {
            tulos.append(", ");
         }
         String monikko = Math.abs(aikav‰li.getDays()) == 1 ? "p‰iv‰" : "p‰iv‰‰";
         tulos.append(String.format("%d %s", aikav‰li.getDays(), monikko));
      }
      return tulos.toString();
   }

   private static boolean onkoTulevaisuudessa(LocalDate p‰iv‰m‰‰r‰)
   {
      return t‰n‰‰n().isBefore(p‰iv‰m‰‰r‰);
   }

   public static boolean onkoTulevaisuudessa(Date p‰iv‰m‰‰r‰)
   {
      return onkoTulevaisuudessa(Date2LocalDate(p‰iv‰m‰‰r‰));
   }

   public static Date t‰n‰‰nDate()
   {
      return LocalDate2Date(t‰n‰‰n());
   }

   public static Date p‰ivienP‰‰st‰(int p‰iv‰t)
   {
      return LocalDate2Date(t‰n‰‰n().plus(p‰iv‰t, ChronoUnit.DAYS));
   }

   public static boolean onkoMenneysyydess‰(Date p‰iv‰m‰‰r‰)
   {
      return t‰n‰‰n().isAfter(Date2LocalDate(p‰iv‰m‰‰r‰));
   }

   public static long getP‰ivi‰V‰liss‰(Date p‰iv‰m‰‰r‰)
   {
      return ChronoUnit.DAYS.between(t‰n‰‰n(), Date2LocalDate(p‰iv‰m‰‰r‰));
   }

   public static LocalDate vuodenViimeinenP‰iv‰()
   {
      return t‰n‰‰n().with(TemporalAdjusters.lastDayOfYear());
   }

   public static LocalDate kuukausienP‰‰st‰(Date p‰iv‰m‰‰r‰, int kuukaudet)
   {
      return DateUtil.Date2LocalDate(p‰iv‰m‰‰r‰).plus(kuukaudet, ChronoUnit.MONTHS);
   }

   private static LocalDate vuosienP‰‰st‰(LocalDate p‰iv‰m‰‰r‰, int vuodet)
   {
      return p‰iv‰m‰‰r‰.plus(vuodet, ChronoUnit.YEARS);
   }

   public static long ik‰(Date p‰iv‰m‰‰r‰)
   {
      return DateUtil.Date2LocalDate(p‰iv‰m‰‰r‰).until(t‰n‰‰n(), ChronoUnit.YEARS);
   }

   public static long vuosiaV‰liss‰(Date syntynyt, Date tarkistus)
   {
      return ChronoUnit.YEARS.between(Date2LocalDate(syntynyt), Date2LocalDate(tarkistus));
   }

   public static Period aikav‰li(Date p‰iv‰m‰‰r‰)
   {
      return Period.between(t‰n‰‰n(), DateUtil.Date2LocalDate(p‰iv‰m‰‰r‰));
   }

   public static int kuukausiaV‰liss‰(Date alkaa, Date p‰‰ttyy)
   {
      return (int) ChronoUnit.MONTHS.between(Date2LocalDate(alkaa), Date2LocalDate(p‰‰ttyy));
   }

   public static int laskutuskuukausiaV‰liss‰(Jakso jakso)
   {
      Period aikav‰li = Period.between(Date2LocalDate(jakso.getAlkaa()), Date2LocalDate(jakso.getP‰‰ttyy()));
      int vuosienkuukaudet = aikav‰li.getYears() * 12;
      int kuukaudet = aikav‰li.getMonths();
      if (aikav‰li.getDays() > 0)
      {
         kuukaudet++;
      }
      return vuosienkuukaudet + kuukaudet;
   }

   public static int laskutusvuosiaV‰liss‰(Jakso jakso)
   {
      Period aikav‰li = Period.between(Date2LocalDate(jakso.getAlkaa()), Date2LocalDate(jakso.getP‰‰ttyy()));
      return aikav‰li.getYears() + 1;
   }

   public static String formatoi(Date p‰iv‰m‰‰r‰)
   {
      return Date2LocalDate(p‰iv‰m‰‰r‰).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
   }

   public static Date vuosienP‰‰st‰(Date p‰iv‰m‰‰r‰, int vuosia)
   {
      return LocalDate2Date(vuosienP‰‰st‰(Date2LocalDate(p‰iv‰m‰‰r‰), vuosia));
   }

   public static boolean onkoV‰liss‰(Date alkaa, Date p‰‰ttyy)
   {
      return t‰n‰‰n().isAfter(Date2LocalDate(alkaa)) && t‰n‰‰n().isBefore(Date2LocalDate(p‰‰ttyy));
   }

   public static boolean onkoV‰liss‰(Jakso jakso, Date p‰iv‰m‰‰r‰)
   {
      LocalDate testi = Date2LocalDate(p‰iv‰m‰‰r‰);
      return testi.isAfter(Date2LocalDate(jakso.getAlkaa())) && testi.isBefore(Date2LocalDate(jakso.getP‰‰ttyy()));
   }

   public static LocalDate silloin(String p‰iv‰m‰‰r‰)
   {
      return Date2LocalDate(silloinD(p‰iv‰m‰‰r‰));
   }

   public static Date silloinD(String p‰iv‰m‰‰r‰)
   {
      try
      {
         return new SimpleDateFormat("dd.MM.yyyy").parse(p‰iv‰m‰‰r‰);
      } catch (ParseException e)
      {
         e.printStackTrace();
      }
      return null;
   }

   public static long p‰ivi‰V‰liss‰(Date alkaa, Date p‰‰ttyy)
   {
      return ChronoUnit.DAYS.between(Date2LocalDate(alkaa), Date2LocalDate(p‰‰ttyy));
   }

   public static String aikav‰li2String(Date alkaa, Date p‰‰ttyy)
   {
      if (alkaa == null || p‰‰ttyy == null)
      {
         return "";
      }
      return String.format("%s-%s", formatoi(alkaa), formatoi(p‰‰ttyy));
   }

   public static Date string2Date(String p‰iv‰m‰‰r‰)
   {
      if (p‰iv‰m‰‰r‰ == null)
      {
         return null;
      }
      try
      {
         return new SimpleDateFormat("dd.MM.yyyy").parse(p‰iv‰m‰‰r‰);
      } catch (ParseException e)
      {
         throw new RuntimeException(e);
      }
   }

   public static boolean samat(Date ensimm‰inen, LocalDate toinen)
   {
      LocalDate alkaaPvm = Date2LocalDate(ensimm‰inen);
      return alkaaPvm.getYear() == toinen.getYear() && alkaaPvm.getMonthValue() == toinen.getMonthValue()
         && alkaaPvm.getDayOfMonth() == toinen.getDayOfMonth();
   }

   private static boolean onkoAiemmin(LocalDate alkaa, LocalDate loppuu)
   {
      return ChronoUnit.DAYS.between(alkaa, loppuu) > 0;
   }

   public static boolean onkoAiemmin(Date alkaa, LocalDate loppuu)
   {
      return onkoAiemmin(Date2LocalDate(alkaa), loppuu);
   }

   public static boolean onkoAiemmin(Date alkaa, Date loppuu)
   {
      return onkoAiemmin(Date2LocalDate(alkaa), Date2LocalDate(loppuu));
   }

   public static boolean onkoAikaAiemmin(Date alkaa, Date loppuu)
   {
      return onkoAikaAikaisemmin(Date2LocalDateTime(alkaa), Date2LocalDateTime(loppuu));
   }

   private static boolean onkoAikaAikaisemmin(LocalDateTime alkaa, LocalDateTime loppuu)
   {
      return ChronoUnit.MINUTES.between(alkaa, loppuu) > 0;
   }

   public static String p‰iv‰Tekstiksi(Date p‰iv‰)
   {
      return new SimpleDateFormat("dd.MM.yyyy").format(p‰iv‰);
   }

}
