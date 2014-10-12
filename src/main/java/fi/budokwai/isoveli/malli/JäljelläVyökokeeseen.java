package fi.budokwai.isoveli.malli;

import java.time.Period;

import fi.budokwai.isoveli.util.Util;

public class JäljelläVyökokeeseen
{
   public static JäljelläVyökokeeseen EI_OOTA = new JäljelläVyökokeeseen();
   private Period aika = Period.ZERO;
   private long treenikertoja;

   public JäljelläVyökokeeseen(Period aika, long treenikertoja)
   {
      this.aika = aika;
      this.treenikertoja = treenikertoja;
   }

   public JäljelläVyökokeeseen()
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
