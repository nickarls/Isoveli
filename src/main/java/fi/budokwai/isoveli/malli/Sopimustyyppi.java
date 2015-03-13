package fi.budokwai.isoveli.malli;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "sopimustyyppi")
@NamedQueries(
{
      @NamedQuery(name = "koeaikasopimus", query = "select s from Sopimustyyppi s where s.koeaika='K' and s.arkistoitu='E'"),
      @NamedQuery(name = "sopimustyypit", query = "select s from Sopimustyyppi s where s.arkistoitu='E' order by s.nimi"),
      @NamedQuery(name = "sopimustyyppiArq", query = "select s from Sopimustyyppi s order by s.nimi") })
public class Sopimustyyppi
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @Size(max = 50)
   @NotNull
   private String nimi;

   @Column(name = "jasenmaksu")
   @Type(type = "KylläEi")
   private boolean jäsenmaksu;

   @Type(type = "KylläEi")
   private boolean harjoittelumaksu;

   @Type(type = "KylläEi")
   private boolean treenikertoja;

   @Type(type = "KylläEi")
   private boolean alkeiskurssi;

   @Type(type = "KylläEi")
   private boolean koeaika;

   @Type(type = "KylläEi")
   private boolean vapautus;

   @Type(type = "KylläEi")
   private boolean power;

   @Type(type = "KylläEi")
   private boolean opiskelija;

   @Type(type = "KylläEi")
   private boolean valmennuskeskus;

   @Type(type = "KylläEi")
   private boolean laskutettava;

   private int oletusKuukaudetVoimassa;

   @Column(name = "alaikaraja")
   private int alaikäraja;

   @Column(name = "ylaikaraja")
   private int yläikäraja;

   @Column(name = "oletusmaksuvali")
   private int oletusMaksuväli;

   private int oletusTreenikerrat;

   private double hinta;

   @Type(type = "KylläEi")
   private boolean arkistoitu;

   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public String getNimi()
   {
      return nimi;
   }

   public void setNimi(String nimi)
   {
      this.nimi = nimi;
   }

   public boolean isJäsenmaksu()
   {
      return jäsenmaksu;
   }

   public boolean isJäsenmaksutyyppi()
   {
      return jäsenmaksu && !alkeiskurssi && !harjoittelumaksu && !koeaika && !power && !treenikertoja && !vapautus;
   }

   public void setJäsenmaksu(boolean jäsenmaksu)
   {
      this.jäsenmaksu = jäsenmaksu;
   }

   public boolean isHarjoittelumaksu()
   {
      return harjoittelumaksu;
   }

   public void setHarjoittelumaksu(boolean harjoittelumaksu)
   {
      this.harjoittelumaksu = harjoittelumaksu;
   }

   public boolean isTreenikertoja()
   {
      return treenikertoja;
   }

   public void setTreenikertoja(boolean treenikertoja)
   {
      this.treenikertoja = treenikertoja;
   }

   public boolean isAlkeiskurssi()
   {
      return alkeiskurssi;
   }

   public void setAlkeiskurssi(boolean alkeiskurssi)
   {
      this.alkeiskurssi = alkeiskurssi;
   }

   public boolean isKoeaika()
   {
      return koeaika;
   }

   public void setKoeaika(boolean koeaika)
   {
      this.koeaika = koeaika;
   }

   public boolean isVapautus()
   {
      return vapautus;
   }

   public void setVapautus(boolean vapautus)
   {
      this.vapautus = vapautus;
   }

   public boolean isPower()
   {
      return power;
   }

   public void setPower(boolean power)
   {
      this.power = power;
   }

   @Override
   public int hashCode()
   {
      return Integer.valueOf(id).hashCode();
   }

   @Override
   public boolean equals(Object toinen)
   {
      if (!(toinen instanceof Sopimustyyppi))
      {
         return false;
      }
      Sopimustyyppi toinenSopimustyyppi = (Sopimustyyppi) toinen;
      return id == toinenSopimustyyppi.getId();
   }

   public int getOletusKuukaudetVoimassa()
   {
      return oletusKuukaudetVoimassa;
   }

   public void setOletusKuukaudetVoimassa(int oletusKuukaudetVoimassa)
   {
      this.oletusKuukaudetVoimassa = oletusKuukaudetVoimassa;
   }

   public int getOletusMaksuväli()
   {
      return oletusMaksuväli;
   }

   public void setOletusMaksuväli(int oletusMaksuväli)
   {
      this.oletusMaksuväli = oletusMaksuväli;
   }

   public int getOletusTreenikerrat()
   {
      return oletusTreenikerrat;
   }

   public void setOletusTreenikerrat(int oletusTreenikerrat)
   {
      this.oletusTreenikerrat = oletusTreenikerrat;
   }

   public boolean isLaskutettava()
   {
      return laskutettava;
   }

   public void setLaskutettava(boolean laskutettava)
   {
      this.laskutettava = laskutettava;
   }

   public double getHinta()
   {
      return hinta;
   }

   public void setHinta(double hinta)
   {
      this.hinta = hinta;
   }

   public boolean isOpiskelija()
   {
      return opiskelija;
   }

   public void setOpiskelija(boolean opiskelija)
   {
      this.opiskelija = opiskelija;
   }

   public int getAlaikäraja()
   {
      return alaikäraja;
   }

   public void setAlaikäraja(int alaikäraja)
   {
      this.alaikäraja = alaikäraja;
   }

   public int getYläikäraja()
   {
      return yläikäraja;
   }

   public void setYläikäraja(int yläikäraja)
   {
      this.yläikäraja = yläikäraja;
   }

   public boolean isValmennuskeskus()
   {
      return valmennuskeskus;
   }

   public void setValmennuskeskus(boolean valmennuskeskus)
   {
      this.valmennuskeskus = valmennuskeskus;
   }

   public boolean isKuukausilaskutus()
   {
      return isHarjoittelumaksutyyppi();
   }

   public boolean isHarjoittelumaksutyyppi()
   {
      return !jäsenmaksu && !alkeiskurssi && harjoittelumaksu && !koeaika && !power && !treenikertoja && !vapautus;
   }

   public boolean sopiiHarrastajalle(Harrastaja harrastaja)
   {
      if (alaikäraja == 0 && yläikäraja == 0)
      {
         return true;
      } else if (alaikäraja > 0 && yläikäraja == 0)
      {
         return harrastaja.getIkä() > alaikäraja;
      } else if (alaikäraja == 0 && yläikäraja > 0)
      {
         return harrastaja.getIkä() < yläikäraja;
      } else if (alaikäraja > 0 && yläikäraja > 0)
      {
         return harrastaja.getIkä() > alaikäraja && harrastaja.getIkä() < yläikäraja;
      }
      return false;
   }

   public boolean isArkistoitu()
   {
      return arkistoitu;
   }

   public void setArkistoitu(boolean arkistoitu)
   {
      this.arkistoitu = arkistoitu;
   }

   @Override
   public String toString()
   {
      return nimi;
   }

   public boolean isTallentamaton()
   {
      return id == 0;
   }

}
