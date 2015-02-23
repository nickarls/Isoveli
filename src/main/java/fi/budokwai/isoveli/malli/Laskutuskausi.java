package fi.budokwai.isoveli.malli;

public class Laskutuskausi
{
   private Jakso kausi;
   private int kausikuukausia;
   private Jakso tauko;
   private long taukop�ivi�;

   public Laskutuskausi(Jakso kausi, int kausikuukausia, Jakso tauko, long taukop�ivi�)
   {
      this.kausi = kausi;
      this.kausikuukausia = kausikuukausia;
      this.tauko = tauko;
      this.taukop�ivi� = taukop�ivi�;
   }

   public Jakso getKausi()
   {
      return kausi;
   }

   public int getKausikuukausia()
   {
      return kausikuukausia;
   }

   public Jakso getTauko()
   {
      return tauko;
   }

   public long getTaukop�ivi�()
   {
      return taukop�ivi�;
   }

   public boolean isTaukop�ivi�()
   {
      return taukop�ivi� > 0;
   }

}
