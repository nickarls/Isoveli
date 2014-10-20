package fi.budokwai.isoveli.util;

import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

public class Util
{
   public static Date t‰n‰‰n()
   {
      return Date.from(LocalDateTime.now().toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
   }

   public static String period2String(Period aikav‰li)
   {
      if (aikav‰li == Period.ZERO)
      {
         return "";
      }
      StringBuilder tulos = new StringBuilder();
      if (aikav‰li.getYears() > 0)
      {
         String monikko = aikav‰li.getYears() == 1 ? "vuosi" : "vuotta";
         tulos.append(String.format("%d %s", aikav‰li.getYears(), monikko));
      }
      if (aikav‰li.getMonths() > 0)
      {
         if (tulos.toString().length() > 0)
         {
            tulos.append(", ");
         }
         String monikko = aikav‰li.getMonths() == 1 ? "kuukausi" : "kuukautta";
         tulos.append(String.format("%d %s", aikav‰li.getMonths(), monikko));
      }
      if (aikav‰li.getDays() > 0)
      {
         if (tulos.toString().length() > 0)
         {
            tulos.append(", ");
         }
         String monikko = aikav‰li.getDays() == 1 ? "p‰iv‰" : "p‰iv‰‰";
         tulos.append(String.format("%d %s", aikav‰li.getDays(), monikko));
      }
      return tulos.toString();
   }
}
