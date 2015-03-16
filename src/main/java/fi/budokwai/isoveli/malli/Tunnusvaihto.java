package fi.budokwai.isoveli.malli;

import javax.enterprise.inject.Typed;
import javax.validation.constraints.NotNull;

import fi.budokwai.isoveli.util.Util;

@Typed(
{})
public class Tunnusvaihto extends Tunnukset
{
   @NotNull
   private String uusiSalasana;

   @NotNull
   private String toistettuSalasana;

   public String getUusiSalasana()
   {
      return uusiSalasana;
   }

   public void setUusiSalasana(String uusiSalasana)
   {
      this.uusiSalasana = uusiSalasana;
   }

   public String getToistettuSalasana()
   {
      return toistettuSalasana;
   }

   public void setToistettuSalasana(String toistettuSalasana)
   {
      this.toistettuSalasana = toistettuSalasana;
   }

   public boolean oikeinToistettuSalasana()
   {
      return uusiSalasana.equals(toistettuSalasana);
   }

   public String getUusiMD5Salasana()
   {
      return Util.MD5(uusiSalasana);
   }
}
