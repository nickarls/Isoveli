package fi.budokwai.isoveli.malli;

public enum LaskuTila
{
   M("Muodostettu"), L("Lähetetty"), K("Maksettu"), X("Mitätöity");

   private final String nimi;

   private LaskuTila(String nimi)
   {
      this.nimi = nimi;
   }

   public String getNimi()
   {
      return nimi;
   }
}
