package fi.budokwai.isoveli.malli;

public enum Sukupuoli
{
   M("Mies"), N("Nainen");

   private String nimi;

   private Sukupuoli(String nimi)
   {
      this.nimi = nimi;
   }

   public String getNimi()
   {
      return nimi;
   }
}
