package fi.budokwai.isoveli.malli.j�senkortti;

public class Alue
{
   private Piste yl�vasen;
   private Piste alaoikea;

   public Alue(int x1, int y1, int x2, int y2)
   {
      yl�vasen = new Piste(x1, y1);
      alaoikea = new Piste(x2, y2);
   }

   public Alue()
   {
   }

   public Piste getYl�vasen()
   {
      return yl�vasen;
   }

   public void setYl�vasen(Piste yl�vasen)
   {
      this.yl�vasen = yl�vasen;
   }

   public Piste getAlaoikea()
   {
      return alaoikea;
   }

   public void setAlaoikea(Piste alaoikea)
   {
      this.alaoikea = alaoikea;
   }

   public int getLeveys()
   {
      return alaoikea.getX() - yl�vasen.getX();
   }

   public int getKorkeus()
   {
      return alaoikea.getY() - yl�vasen.getY();
   }
}
