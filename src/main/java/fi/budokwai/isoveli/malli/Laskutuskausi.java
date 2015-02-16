package fi.budokwai.isoveli.malli;

import java.util.Date;

public class Laskutuskausi
{
   private Date alkaa;
   private Date p‰‰ttyy;
   private int m‰‰r‰;
   
   public Laskutuskausi(Date alkaa, Date p‰‰ttyy, int m‰‰r‰) {
      this.alkaa  =alkaa;
      this.p‰‰ttyy = p‰‰ttyy;
      this.m‰‰r‰ = m‰‰r‰;
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

}
