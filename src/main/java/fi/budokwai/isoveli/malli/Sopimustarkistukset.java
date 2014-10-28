package fi.budokwai.isoveli.malli;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Sopimustarkistukset
{
   private List<Sopimustarkistus> sopimustarkistukset = new ArrayList<Sopimustarkistus>();

   public void lis‰‰(List<Sopimustarkistus> sopimustarkistus)
   {
      sopimustarkistukset.addAll(sopimustarkistus);
   }

   public void lis‰‰(Sopimustarkistus sopimustarkistus)
   {
      sopimustarkistukset.add(sopimustarkistus);
   }

   public boolean isOK()
   {
      for (Sopimustarkistus sopimustarkistus : sopimustarkistukset)
      {
         if (!sopimustarkistus.isOK())
         {
            return false;
         }
      }
      return true;
   }

   public List<String> getViestit()
   {
      return sopimustarkistukset.stream().filter(st -> st.getViesti() != null).map(st -> st.getViesti())
         .collect(Collectors.toList());
   }

}
