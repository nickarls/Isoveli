package fi.budokwai.isoveli.malli;

public enum TilausTila
{
   M("Muodostettu"), L("Lähetetty"), K("Maksettu"), X("Mitätöity");

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
