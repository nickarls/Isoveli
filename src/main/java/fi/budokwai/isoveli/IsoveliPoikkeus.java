package fi.budokwai.isoveli;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = false)
public class IsoveliPoikkeus extends RuntimeException
{
   private static final long serialVersionUID = 1L;

   public IsoveliPoikkeus(String viesti, Exception e)
   {
      super(viesti, e);
   }

   public IsoveliPoikkeus(String viesti)
   {
      super(viesti);
   }

}
