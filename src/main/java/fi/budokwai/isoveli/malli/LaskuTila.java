package fi.budokwai.isoveli.malli;

public enum LaskuTila
{
   M("Muodostettu"), L("L�hetetty"), K("Maksettu"), X("Mit�t�ity");

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
