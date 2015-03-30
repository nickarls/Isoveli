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

   public static String getIkärajat(Integer alaraja, Integer yläraja)
   {
      if (alaraja == null && yläraja == null)
      {
         return null;
      } else if (alaraja != null && yläraja == null)
      {
         return String.format("%d+", alaraja);
      } else if (alaraja == null && yläraja != null)
      {
         return String.format("-%d", yläraja);
      } else
      {
         return String.format("%d-%d", alaraja, yläraja);
      }
   }

}
