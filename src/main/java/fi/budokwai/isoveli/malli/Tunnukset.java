package fi.budokwai.isoveli.malli;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.enterprise.inject.Model;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

@Model
public class Tunnukset
{
   @NotNull
   private String nimi;

   @NotNull
   private String salasana;

   public String getNimi()
   {
      return nimi;
   }

   public void setNimi(String nimi)
   {
      this.nimi = nimi;
   }

   public String getSalasana()
   {
      return salasana;
   }

   public void setSalasana(String salasana)
   {
      this.salasana = salasana;
   }

   public String getMD5Salasana()
   {
      if (salasana == null)
      {
         return null;
      }
      try
      {
         MessageDigest md = MessageDigest.getInstance("MD5");
         return (new HexBinaryAdapter()).marshal(md.digest(salasana.getBytes()));
      } catch (NoSuchAlgorithmException e)
      {
         e.printStackTrace();
      }
      return null;
   }

}
