package fi.budokwai.isoveli.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

public class Util
{

   public static String MD5(String teksti)
   {
      try
      {
         MessageDigest md = MessageDigest.getInstance("MD5");
         return (new HexBinaryAdapter()).marshal(md.digest(teksti.getBytes()));
      } catch (NoSuchAlgorithmException e)
      {
         e.printStackTrace();
      }
      return null;
   }

   public static String getIk�rajat(Integer alaraja, Integer yl�raja)
   {
      if (alaraja == null && yl�raja == null)
      {
         return null;
      } else if (alaraja != null && yl�raja == null)
      {
         return String.format("%d+", alaraja);
      } else if (alaraja == null && yl�raja != null)
      {
         return String.format("-%d", yl�raja);
      } else
      {
         return String.format("%d-%d", alaraja, yl�raja);
      }
   }

}
