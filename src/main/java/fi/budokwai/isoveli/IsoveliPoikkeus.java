package fi.budokwai.isoveli;

public class IsoveliPoikkeus extends RuntimeException
{
   public IsoveliPoikkeus(String message, Exception e)
   {
      super(message, e);
   }

   private static final long serialVersionUID = 1L;
}
