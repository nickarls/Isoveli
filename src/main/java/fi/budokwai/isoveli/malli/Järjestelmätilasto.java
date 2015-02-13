package fi.budokwai.isoveli.malli;

public class J�rjestelm�tilasto
{
   private long harrastajia;
   private long treenej�;
   private long treenik�yntej�;
   private long vy�arvoja;

   public J�rjestelm�tilasto(long harrastajia, long treenej�, long treenik�yntej�, long vy�arvoja)
   {
      this.harrastajia = harrastajia;
      this.treenej� = treenej�;
      this.treenik�yntej� = treenik�yntej�;
      this.vy�arvoja = vy�arvoja;
   }

   public long getHarrastajia()
   {
      return harrastajia;
   }

   public long getTreenej�()
   {
      return treenej�;
   }

   public long getTreenik�yntej�()
   {
      return treenik�yntej�;
   }

   public long getVy�arvoja()
   {
      return vy�arvoja;
   }

}
