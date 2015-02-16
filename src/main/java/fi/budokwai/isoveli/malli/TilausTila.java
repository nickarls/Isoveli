package fi.budokwai.isoveli.malli;

public enum TilausTila
{
   M("Muodostettu"), L("L�hetetty"), K("Maksettu"), X("Mit�t�ity");

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
