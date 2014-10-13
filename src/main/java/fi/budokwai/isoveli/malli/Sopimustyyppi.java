package fi.budokwai.isoveli.malli;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import fi.budokwai.isoveli.KylläEiTyyppi;

@Entity
@TypeDef(name = "KylläEi", typeClass = KylläEiTyyppi.class)
@NamedQueries(
{
      @NamedQuery(name = "sopimustyypit", query = "select s from Sopimustyyppi s order by s.nimi"),
      @NamedQuery(name = "sopimustyyppikäyttö", query = "select s from Harrastaja h join h.sopimukset s join s.tyyppi st where st = :sopimustyyppi") })
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
   private boolean jatkuva;

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
   private boolean perhealennus;

   private int oletusKuukaudetVoimassa;

   @Column(name = "oletusmaksuvali")
   private int oletusMaksuväli;

   private int oletusTreenikerrat;

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

   public boolean isPoistettavissa()
   {
      return id > 0;
   }

   public boolean isJäsenmaksu()
   {
      return jäsenmaksu;
   }

   public void setJäsenmaksu(boolean jäsenmaksu)
   {
      this.jäsenmaksu = jäsenmaksu;
   }

   public boolean isJatkuva()
   {
      return jatkuva;
   }

   public void setJatkuva(boolean jatkuva)
   {
      this.jatkuva = jatkuva;
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

   public boolean isPerhealennus()
   {
      return perhealennus;
   }

   public void setPerhealennus(boolean perhealennus)
   {
      this.perhealennus = perhealennus;
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

}
