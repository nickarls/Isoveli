package fi.budokwai.isoveli.malli;

public class Vastaanottaja
{
   private Henkil� henkil�;
   private Osoite osoite;

   public Vastaanottaja(Henkil� henkil�)
   {
      this.henkil� = henkil�;
      if (henkil�.getOsoite() != null)
      {
         osoite = henkil�.getOsoite();
      } else if (henkil�.getPerhe() != null)
      {
         osoite = henkil�.getPerhe().getOsoite();
      } else
      {
         osoite = new Osoite();
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

   public String getPostinumeroJaKaupunki()
   {
      return String.format("%s %s", osoite.getPostinumero(), osoite.getKaupunki());
   }
}