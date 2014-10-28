package fi.budokwai.isoveli.malli;

public class Vastaanottaja
{
   private Henkilö henkilö;
   private Osoite osoite;

   public Vastaanottaja(Henkilö henkilö)
   {
      this.henkilö = henkilö;
      if (henkilö instanceof Henkilö)
      {
         osoite = henkilö.getPerhe().getOsoite();
      } else
      {
         Harrastaja harrastaja = (Harrastaja) henkilö;
         if (harrastaja.getOsoite() != null)
         {
            osoite = harrastaja.getOsoite();
         } else
         {
            osoite = harrastaja.getPerhe().getOsoite();
         }
      }
   }

   public Henkilö getHenkilö()
   {
      return henkilö;
   }

   public Osoite getOsoite()
   {
      return osoite;
   }
}