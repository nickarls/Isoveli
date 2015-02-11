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

}
