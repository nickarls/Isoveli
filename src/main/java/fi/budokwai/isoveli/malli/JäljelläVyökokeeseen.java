package fi.budokwai.isoveli.malli;

import java.time.Period;

import fi.budokwai.isoveli.util.Util;

public class J�ljell�Vy�kokeeseen
{
   public static J�ljell�Vy�kokeeseen EI_OOTA = new J�ljell�Vy�kokeeseen();
   private Period aika = Period.ZERO;
   private long treenikertoja;

   public J�ljell�Vy�kokeeseen(Period aika, long treenikertoja)
   {
      this.aika = aika;
      this.treenikertoja = treenikertoja;
   }

   public J�ljell�Vy�kokeeseen()
   {
   }

   public long getTreenikertoja()
   {
      return treenikertoja;
   }

   public Period getAika()
   {
      return aika;
   }

   public String getAikaString()
   {
      return Util.period2String(aika);
   }

}
