package fi.budokwai.isoveli.malli;

public class Vastaanottaja
{
   private Henkil� henkil�;
   private Osoite osoite;

   public Vastaanottaja(Henkil� henkil�)
   {
      this.henkil� = henkil�;
      if (henkil� instanceof Henkil�)
      {
         osoite = henkil�.getPerhe().getOsoite();
      } else
      {
         Harrastaja harrastaja = (Harrastaja) henkil�;
         if (harrastaja.getOsoite() != null)
         {
            osoite = harrastaja.getOsoite();
         } else
         {
            osoite = harrastaja.getPerhe().getOsoite();
         }
      }
   }

   public Henkil� getHenkil�()
   {
      return henkil�;
   }

   public Osoite getOsoite()
   {
      return osoite;
   }
}