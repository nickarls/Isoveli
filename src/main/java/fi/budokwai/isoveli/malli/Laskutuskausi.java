package fi.budokwai.isoveli.malli;

public class Laskutuskausi
{
   private Jakso kausi;
   private int kausikuukausia;
   private Jakso tauko;
   private long taukopäiviä;

   public Laskutuskausi(Jakso kausi, int kausikuukausia, Jakso tauko, long taukopäiviä)
   {
      this.kausi = kausi;
      this.kausikuukausia = kausikuukausia;
      this.tauko = tauko;
      this.taukopäiviä = taukopäiviä;
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

   public long getTaukopäiviä()
   {
      return taukopäiviä;
   }

   public boolean isTaukopäiviä()
   {
      return taukopäiviä > 0;
   }

}
