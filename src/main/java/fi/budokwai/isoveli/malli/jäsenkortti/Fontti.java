package fi.budokwai.isoveli.malli.j�senkortti;

public class Fontti
{
   private String nimi;
   private int koko;
   private String v�ri;

   public Fontti(String nimi, int koko, String v�ri)
   {
      this.nimi = nimi;
      this.koko = koko;
      this.v�ri = v�ri;
   }

   public Fontti()
   {
   }

   public String getNimi()
   {
      return nimi;
   }

   public void setNimi(String nimi)
   {
      this.nimi = nimi;
   }

   public int getKoko()
   {
      return koko;
   }

   public void setKoko(int koko)
   {
      this.koko = koko;
   }

   public String getV�ri()
   {
      return v�ri;
   }

   public void setV�ri(String v�ri)
   {
      this.v�ri = v�ri;
   }
}
