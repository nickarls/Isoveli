package fi.budokwai.isoveli.util;

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

   private LocalTime Date2LocalTime(Date p‰iv‰m‰‰r‰) 
   {
      return Date2LocalDateTime(p‰iv‰m‰‰r‰).toLocalTime();
   }

   private static Date LocalDateTime2Date(LocalDateTime p‰iv‰m‰‰r‰)
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

   public static Period aikav‰li(Date p‰iv‰m‰‰r‰)
   {
      return Period.between(t‰n‰‰n(), DateUtil.Date2LocalDate(p‰iv‰m‰‰r‰));
   }

   public static int kuukausiaV‰liss‰(Date alkaa, Date p‰‰ttyy)
   {
      return (int) ChronoUnit.MONTHS.between(Date2LocalDate(alkaa), Date2LocalDate(p‰‰ttyy));
   }

   public static String formatoi(Date p‰iv‰m‰‰r‰)
   {
      return Date2LocalDate(p‰iv‰m‰‰r‰).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
   }

   public static Date vuosienP‰‰st‰(Date p‰iv‰m‰‰r‰, int vuosia)
   {
      return LocalDate2Date(vuosienP‰‰st‰(Date2LocalDate(p‰iv‰m‰‰r‰), vuosia));
   }

}
