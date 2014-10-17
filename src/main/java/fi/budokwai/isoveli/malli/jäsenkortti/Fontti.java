package fi.budokwai.isoveli.malli.jäsenkortti;

public class Fontti
{
   private String nimi;
   private int koko;
   private String väri;

   public Fontti(String nimi, int koko, String väri)
   {
      this.nimi = nimi;
      this.koko = koko;
      this.väri = väri;
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

   public String getVäri()
   {
      return väri;
   }

   public void setVäri(String väri)
   {
      this.väri = väri;
   }
}
