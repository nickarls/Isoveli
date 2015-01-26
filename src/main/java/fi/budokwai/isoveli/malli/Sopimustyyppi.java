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

import org.hibernate.annotations.Type;

@Entity
@Table(name = "sopimustyyppi")
@NamedQueries(
{
      @NamedQuery(name = "koeaikasopimus", query = "select s from Sopimustyyppi s where s.koeaika='K'"),
      @NamedQuery(name = "sopimustyypit", query = "select s from Sopimustyyppi s order by s.nimi"),
      @NamedQuery(name = "sopimustyyppik�ytt�", query = "select s from Harrastaja h join h.sopimukset s join s.tyyppi st where st = :sopimustyyppi") })
public class Sopimustyyppi
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @Size(max = 50)
   @NotNull
   private String nimi;

   @Column(name = "jasenmaksu")
   @Type(type = "Kyll�Ei")
   private boolean j�senmaksu;

   @Type(type = "Kyll�Ei")
   private boolean harjoittelumaksu;

   @Type(type = "Kyll�Ei")
   private boolean treenikertoja;

   @Type(type = "Kyll�Ei")
   private boolean alkeiskurssi;

   @Type(type = "Kyll�Ei")
   private boolean koeaika;

   @Type(type = "Kyll�Ei")
   private boolean vapautus;

   @Type(type = "Kyll�Ei")
   private boolean power;

   @Type(type = "Kyll�Ei")
   private boolean laskutettava;

   private int oletusKuukaudetVoimassa;

   @Column(name = "oletusmaksuvali")
   private int oletusMaksuv�li;

   private int oletusTreenikerrat;

   private double hinta;

   @Size(max = 20)
   private String tuotekoodi;

   @Column(name = "maara")
   private int m��r�;

   @Column(name = "yksikko")
   @Size(max = 20)
   private String yksikk�;

   private int verokanta;

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

   public boolean isJ�senmaksu()
   {
      return j�senmaksu;
   }

   public void setJ�senmaksu(boolean j�senmaksu)
   {
      this.j�senmaksu = j�senmaksu;
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

   public int getOletusMaksuv�li()
   {
      return oletusMaksuv�li;
   }

   public void setOletusMaksuv�li(int oletusMaksuv�li)
   {
      this.oletusMaksuv�li = oletusMaksuv�li;
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

   public double getVerollinenHinta()
   {
      double result = m��r� * hinta * (1 + verokanta / 100f);
      return result;
   }

   public double getVerotonHinta()
   {
      double result = m��r� * hinta;
      return result;
   }

   public double getALVnOsuus()
   {
      double result = getVerollinenHinta() - getVerotonHinta();
      return result;
   }

   public double getHinta()
   {
      return hinta;
   }

   public void setHinta(double hinta)
   {
      this.hinta = hinta;
   }

   public String getTuotekoodi()
   {
      return tuotekoodi;
   }

   public void setTuotekoodi(String tuotekoodi)
   {
      this.tuotekoodi = tuotekoodi;
   }

   public int getM��r�()
   {
      return m��r�;
   }

   public void setM��r�(int m��r�)
   {
      this.m��r� = m��r�;
   }

   public String getYksikk�()
   {
      return yksikk�;
   }

   public void setYksikk�(String yksikk�)
   {
      this.yksikk� = yksikk�;
   }

   public int getVerokanta()
   {
      return verokanta;
   }

   public void setVerokanta(int verokanta)
   {
      this.verokanta = verokanta;
   }

}
