package fi.budokwai.isoveli.malli.jäsenkortti;

public class Viivakoodi
{
   private String data;
   private int dpi;
   private Alue alue;
   private Viivakoodityyppi tyyppi;

   public Viivakoodi(String data, int dpi, Alue alue, Viivakoodityyppi tyyppi)
   {
      this.data = data;
      this.dpi = dpi;
      this.alue = alue;
      this.tyyppi = tyyppi;
   }

   public Viivakoodi()
   {
   }

   public String getData()
   {
      return data;
   }

   public void setData(String data)
   {
      this.data = data;
   }

   public int getDpi()
   {
      return dpi;
   }

   public void setDpi(int dpi)
   {
      this.dpi = dpi;
   }

   public Alue getAlue()
   {
      return alue;
   }

   public void setAlue(Alue alue)
   {
      this.alue = alue;
   }

   public Viivakoodityyppi getTyyppi()
   {
      return tyyppi;
   }

   public void setTyyppi(Viivakoodityyppi tyyppi)
   {
      this.tyyppi = tyyppi;
   }
}
