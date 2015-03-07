package fi.budokwai.isoveli.malli;

import java.util.Comparator;

public class VyökoeComparator implements Comparator<Vyökoe>
{

   @Override
   public int compare(Vyökoe v1, Vyökoe v2)
   {
      return Integer.valueOf(v1.getVyöarvo().getJärjestys()).compareTo(Integer.valueOf(v2.getVyöarvo().getJärjestys()));
   }

}
