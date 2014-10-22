package fi.budokwai.isoveli.malli;

public enum Tiedostotyyppi
{
   PDF("PDF", "application/pdf"), JPG("JPG", "image/jpg"), PNG("PNG", "image/png"), ZIP("ZIP", "application/zip");

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
