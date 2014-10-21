package fi.budokwai.isoveli.malli;

public enum Tiedostotyyppi
{
   PDF("PDF", "application/pdf"), PNG("PNG", "image/png");

   private String tyyppi;
   private String mimetyyppi;

   private Tiedostotyyppi(String tyyppi, String mimetyyppi)
   {
      this.tyyppi = tyyppi;
      this.mimetyyppi = mimetyyppi;
   }

   public String getTyyppi()
   {
      return tyyppi;
   }

   public String getMimetyyppi()
   {
      return mimetyyppi;
   }
}
