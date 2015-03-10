package fi.budokwai.isoveli.malli;

import java.util.ArrayList;
import java.util.List;

public class Ilmoittautumistulos
{
   public enum Tila
   {
      OK, KÄYTTÄJÄ_EI_LÖYTYNYT, SOPIMUSVIRHE, TAUOLLA
   };

   private Tila tila;
   private String viesti;
   private Harrastaja harrastaja;
   private List<String> sopimusvirheet = new ArrayList<>();

   public Ilmoittautumistulos(Harrastaja harrastaja, Tila tila, String viesti)
   {
      this.tila = tila;
      this.harrastaja = harrastaja;
      this.viesti = viesti;
      if (harrastaja != null)
      {
         this.sopimusvirheet = harrastaja.getSopimusTarkistukset().getViestit();
      }
   }

   public Tila getTila()
   {
      return tila;
   }

   public String getViesti()
   {
      return viesti;
   }

   public Harrastaja getHarrastaja()
   {
      return harrastaja;
   }

   public boolean infotiskille()
   {
      return harrastaja != null && harrastaja.isInfotiskille();
   }

   public boolean treenioikeus()
   {
      return sopimusvirheet.isEmpty();
   }

   public boolean OK()
   {
      return Tila.OK.equals(tila);
   }

   public List<String> getSopimusvirheet()
   {
      return sopimusvirheet;
   }

}
