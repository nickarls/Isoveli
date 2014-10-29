package fi.budokwai.isoveli.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import fi.budokwai.isoveli.malli.Henkil�;

@Singleton
@Startup
public class AuditManager
{
   private Map<String, Integer> resetointipyynn�t = new HashMap<String, Integer>();

   public String teeResetointiavain(Henkil� henkil�)
   {
      String avain = Util.MD5(UUID.randomUUID().toString());
      resetointipyynn�t.put(avain, henkil�.getId());
      return avain;
   }

   public Integer haeResetoitava(String avain)
   {
      return resetointipyynn�t.get(avain);
   }
}
