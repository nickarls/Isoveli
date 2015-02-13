package fi.budokwai.isoveli.malli;

public class Järjestelmätilasto
{
   private long harrastajia;
   private long treenejä;
   private long treenikäyntejä;
   private long vyöarvoja;

   public Järjestelmätilasto(long harrastajia, long treenejä, long treenikäyntejä, long vyöarvoja)
   {
      this.harrastajia = harrastajia;
      this.treenejä = treenejä;
      this.treenikäyntejä = treenikäyntejä;
      this.vyöarvoja = vyöarvoja;
   }

   public long getHarrastajia()
   {
      return harrastajia;
   }

   public long getTreenejä()
   {
      return treenejä;
   }

   public long getTreenikäyntejä()
   {
      return treenikäyntejä;
   }

   public long getVyöarvoja()
   {
      return vyöarvoja;
   }

}
