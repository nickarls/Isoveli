package fi.budokwai.isoveli.malli.jäsenkortti;

public class Teksti
{
   private String teksti;
   private Fontti fontti;
   private Alue alue;

   public Teksti(String teksti, Fontti fontti, Alue alue)
   {
      this.teksti = teksti;
      this.fontti = fontti;
      this.alue = alue;
   }

   public Teksti()
   {
   }

   public String getTeksti()
   {
      return teksti;
   }

   public void setTeksti(String teksti)
   {
      this.teksti = teksti;
   }

   public Fontti getFontti()
   {
      return fontti;
   }

   public void setFontti(Fontti fontti)
   {
      this.fontti = fontti;
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
