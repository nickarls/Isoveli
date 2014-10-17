package fi.budokwai.isoveli.malli.jäsenkortti;

public class Kuva
{
   private String tiedostonimi;
   private Alue alue;

   public Kuva(String tiedostonimi, Alue alue)
   {
      this.tiedostonimi = tiedostonimi;
      this.alue = alue;
   }

   public Kuva()
   {
   }

   public String getTiedostonimi()
   {
      return tiedostonimi;
   }

   public void setTiedostonimi(String tiedostonimi)
   {
      this.tiedostonimi = tiedostonimi;
   }

   public Alue getAlue()
   {
      return alue;
   }

   public void setAlue(Alue alue)
   {
      this.alue = alue;
   }
}
