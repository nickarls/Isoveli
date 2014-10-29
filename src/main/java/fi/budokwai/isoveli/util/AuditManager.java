package fi.budokwai.isoveli.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import fi.budokwai.isoveli.malli.Henkilö;

@Singleton
@Startup
public class AuditManager
{
   private Map<String, Integer> resetointipyynnöt = new HashMap<String, Integer>();

   public String teeResetointiavain(Henkilö henkilö)
   {
      String avain = Util.MD5(UUID.randomUUID().toString());
      resetointipyynnöt.put(avain, henkilö.getId());
      return avain;
   }

   public Integer haeResetoitava(String avain)
   {
      return resetointipyynnöt.get(avain);
   }
}
