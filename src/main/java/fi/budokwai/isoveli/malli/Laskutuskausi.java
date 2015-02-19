package fi.budokwai.isoveli.malli;

import java.util.Date;

public class Laskutuskausi
{
   private Date alkaa;
   private Date p‰‰ttyy;
   private int m‰‰r‰;
   private long taukop‰ivi‰;

   public Laskutuskausi(Date alkaa, Date p‰‰ttyy, int m‰‰r‰, long taukop‰ivi‰)
   {
      this.alkaa = alkaa;
      this.p‰‰ttyy = p‰‰ttyy;
      this.m‰‰r‰ = m‰‰r‰;
      this.taukop‰ivi‰ = taukop‰ivi‰;
   }

   public Date getAlkaa()
   {
      return alkaa;
   }

   public Date getP‰‰ttyy()
   {
      return p‰‰ttyy;
   }

   public int getM‰‰r‰()
   {
      return m‰‰r‰;
   }

   public long getTaukop‰ivi‰()
   {
      return taukop‰ivi‰;
   }

}
