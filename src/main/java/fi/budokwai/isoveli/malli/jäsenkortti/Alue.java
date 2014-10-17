package fi.budokwai.isoveli.malli.jäsenkortti;

public class Alue
{
   private Piste ylävasen;
   private Piste alaoikea;

   public Alue(int x1, int y1, int x2, int y2)
   {
      ylävasen = new Piste(x1, y1);
      alaoikea = new Piste(x2, y2);
   }

   public Alue()
   {
   }

   public Piste getYlävasen()
   {
      return ylävasen;
   }

   public void setYlävasen(Piste ylävasen)
   {
      this.ylävasen = ylävasen;
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
      return alaoikea.getX() - ylävasen.getX();
   }

   public int getKorkeus()
   {
      return alaoikea.getY() - ylävasen.getY();
   }
}
