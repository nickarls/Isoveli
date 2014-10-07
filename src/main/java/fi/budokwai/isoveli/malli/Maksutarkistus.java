package fi.budokwai.isoveli.malli;

public class Maksutarkistus
{
   private String viesti;

   public Maksutarkistus(String viesti)
   {
      this.viesti = viesti;
   }

   public Maksutarkistus()
   {
   }

   public String getViesti()
   {
      return viesti;
   }

   public boolean isOK()
   {
      return viesti == null;
   }
}
