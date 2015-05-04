package fi.budokwai.isoveli.malli;

public enum Viestilaatikkotyypi
{
   I("Saapuneet"), O("Lähetetyt"), A("Arkistoidut");

   public String nimi;

   private Viestilaatikkotyypi(String nimi)
   {
      this.nimi = nimi;
   }
}
