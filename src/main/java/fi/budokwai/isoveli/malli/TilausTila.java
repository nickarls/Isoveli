package fi.budokwai.isoveli.malli;

public enum TilausTila
{
   A("Avoin"), M("Maksettu"), X("Mit�t�ity");

   private final String nimi;

   private TilausTila(String nimi)
   {
      this.nimi = nimi;
   }

   public String getNimi()
   {
      return nimi;
   }
}
