package fi.budokwai.isoveli.malli;

public class Sopimustarkistus
{
   private boolean OK;
   private String viesti;


   public Sopimustarkistus()
   {
      OK = true;
   }

   public Sopimustarkistus(String viesti, boolean OK)
   {
      this();
      this.viesti = viesti;
      this.OK = OK;
   }

   public boolean isOK()
   {
      return OK;
   }

   public String getViesti()
   {
      return viesti;
   }
}