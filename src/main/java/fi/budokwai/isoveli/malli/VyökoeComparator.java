package fi.budokwai.isoveli.malli;

import java.util.Comparator;

public class Vy�koeComparator implements Comparator<Vy�koe>
{

   @Override
   public int compare(Vy�koe v1, Vy�koe v2)
   {
      return Integer.valueOf(v1.getVy�arvo().getJ�rjestys()).compareTo(Integer.valueOf(v2.getVy�arvo().getJ�rjestys()));
   }

}
