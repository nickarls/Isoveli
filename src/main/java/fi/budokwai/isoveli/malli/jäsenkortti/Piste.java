package fi.budokwai.isoveli.malli.jäsenkortti;

public class Piste
{
   private int x;
   private int y;

   public Piste(int x, int y)
   {
      this.x = x;
      this.y = y;
   }

   public int getX()
   {
      return x;
   }

   public void setX(int x)
   {
      this.x = x;
   }

   public int getY()
   {
      return y;
   }

   public void setY(int y)
   {
      this.y = y;
   }
}
