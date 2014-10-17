package fi.budokwai.isoveli.malli.jäsenkortti;

import java.util.ArrayList;
import java.util.List;

public class Jäsenkortti
{
   private String etusivunTiedostonimi;
   private List<Kuva> kuvat = new ArrayList<Kuva>();
   private List<Teksti> tekstit = new ArrayList<Teksti>();
   private List<Viivakoodi> viivakoodit = new ArrayList<Viivakoodi>();

   public Jäsenkortti(String etusivunTiedostonimi)
   {
      this.etusivunTiedostonimi = etusivunTiedostonimi;
   }

   public Jäsenkortti()
   {
   }

   public List<Kuva> getKuvat()
   {
      return kuvat;
   }

   public void setKuvat(List<Kuva> kuvat)
   {
      this.kuvat = kuvat;
   }

   public List<Teksti> getTekstit()
   {
      return tekstit;
   }

   public void setTekstit(List<Teksti> tekstit)
   {
      this.tekstit = tekstit;
   }

   public List<Viivakoodi> getViivakoodit()
   {
      return viivakoodit;
   }

   public void setViivakoodit(List<Viivakoodi> viivakoodit)
   {
      this.viivakoodit = viivakoodit;
   }

   public String getEtusivunTiedostonimi()
   {
      return etusivunTiedostonimi;
   }
}
