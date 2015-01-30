package fi.budokwai.isoveli.malli;

public class Vastaanottaja
{
   private Henkilö henkilö;
   private Osoite osoite;

   public Vastaanottaja(Henkilö henkilö)
   {
      this.henkilö = henkilö;
      if (henkilö.getOsoite() != null)
      {
         osoite = henkilö.getOsoite();
      } else if (henkilö.getPerhe() != null)
      {
         osoite = henkilö.getPerhe().getOsoite();
      } else
      {
         osoite = new Osoite();
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

   public String getPostinumeroJaKaupunki()
   {
      return String.format("%s %s", osoite.getPostinumero(), osoite.getKaupunki());
   }
}