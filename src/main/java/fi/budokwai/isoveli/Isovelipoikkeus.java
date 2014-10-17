package fi.budokwai.isoveli;

public class Isovelipoikkeus extends RuntimeException
{
   public Isovelipoikkeus(String message, Exception e)
   {
      super(message, e);
   }

   private static final long serialVersionUID = 1L;
}
