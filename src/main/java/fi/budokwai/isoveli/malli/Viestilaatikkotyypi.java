package fi.budokwai.isoveli.malli;

public enum Viestilaatikkotyypi
{
   I("Saapuneet"), O("L�hetetyt"), A("Arkistoidut");

   public String nimi;

   private Viestilaatikkotyypi(String nimi)
   {
      this.nimi = nimi;
   }
}
