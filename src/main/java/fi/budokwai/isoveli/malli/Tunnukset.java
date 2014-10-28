package fi.budokwai.isoveli.malli;

import javax.enterprise.inject.Model;
import javax.validation.constraints.NotNull;

import fi.budokwai.isoveli.util.Util;

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
      return Util.MD5(salasana);
   }

   public String[] getNimiosat()
   {
      if (nimi == null)
      {
         return new String[]
         {};
      }
      return nimi.split(" ");
   }

}
