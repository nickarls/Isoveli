package fi.budokwai.isoveli.malli;

import java.time.Period;

import fi.budokwai.isoveli.util.Util;

public class J�ljell�Vy�kokeeseen
{
   public static J�ljell�Vy�kokeeseen EI_OOTA = new J�ljell�Vy�kokeeseen();
   private Period aika = Period.ZERO;
   private long treenikertoja;
   private long p�ivi�;
   private Vy�arvo seuraavaVy�arvo;

   public J�ljell�Vy�kokeeseen(Period aika, long treenikertoja, long p�ivi�, Vy�arvo seuraavaVy�arvo)
   {
      this.aika = aika;
      this.treenikertoja = treenikertoja;
      this.p�ivi� = p�ivi�;
      this.seuraavaVy�arvo = seuraavaVy�arvo;
   }

   public J�ljell�Vy�kokeeseen()
   {
   }

   public long getTreenikertoja()
   {
      return treenikertoja;
   }

   public long getP�ivi�()
   {
      return p�ivi�;
   }

   public Period getAika()
   {
      return aika;
   }

   public String getAikaString()
   {
      return Util.period2String(aika);
   }

   public Vy�arvo getSeuraavaVy�arvo()
   {
      return seuraavaVy�arvo;
   }

   public void setTreenikertoja(long treenikertoja)
   {
      this.treenikertoja = treenikertoja;
   }

}
