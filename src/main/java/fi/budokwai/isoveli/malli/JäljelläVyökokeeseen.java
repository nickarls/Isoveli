package fi.budokwai.isoveli.malli;

import java.time.Period;

import fi.budokwai.isoveli.util.Util;

public class JäljelläVyökokeeseen
{
   public static JäljelläVyökokeeseen EI_OOTA = new JäljelläVyökokeeseen();
   private Period aika = Period.ZERO;
   private long treenikertoja;
   private long päiviä;
   private Vyöarvo seuraavaVyöarvo;

   public JäljelläVyökokeeseen(Period aika, long treenikertoja, long päiviä, Vyöarvo seuraavaVyöarvo)
   {
      this.aika = aika;
      this.treenikertoja = treenikertoja;
      this.päiviä = päiviä;
      this.seuraavaVyöarvo = seuraavaVyöarvo;
   }

   public JäljelläVyökokeeseen()
   {
   }

   public long getTreenikertoja()
   {
      return treenikertoja;
   }

   public long getPäiviä()
   {
      return päiviä;
   }

   public Period getAika()
   {
      return aika;
   }

   public String getAikaString()
   {
      return Util.period2String(aika);
   }

   public Vyöarvo getSeuraavaVyöarvo()
   {
      return seuraavaVyöarvo;
   }

   public void setTreenikertoja(long treenikertoja)
   {
      this.treenikertoja = treenikertoja;
   }

}
