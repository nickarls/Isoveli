package fi.budokwai.isoveli.util;

import java.util.ArrayList;
import java.util.List;

import fi.budokwai.isoveli.malli.Harrastaja;

public class Tuontitulos
{
   private List<Harrastaja> harrastajat = new ArrayList<>();
   private List<String> virheet = new ArrayList<>();

   public void lisääHarrastaja(Harrastaja harrastaja)
   {
      harrastajat.add(harrastaja);
   }

   public void lisääVirhe(String virhe)
   {
      virheet.add(virhe);
   }

   public boolean isOK()
   {
      return virheet.isEmpty();
   }

   public List<Harrastaja> getHarrastajat()
   {
      return harrastajat;
   }

   public List<String> getVirheet()
   {
      return virheet;
   }

}
