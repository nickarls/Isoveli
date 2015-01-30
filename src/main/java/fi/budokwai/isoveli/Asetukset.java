package fi.budokwai.isoveli;

import java.io.Serializable;

import javax.enterprise.inject.Typed;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "asetukset")
@Typed({})
public class Asetukset implements Serializable
{
   private static final long serialVersionUID = 1L;

   @Id
   private String installaatio;

   private String saaja;

   private String IBAN;

   private String BIC;

   private String viitenumero;

   private String osoite;

   private String postinumero;

   private String kaupunki;

   private String YTunnus;

   private String ALVtunnus;

   private String puhelin;

   @Column(name = "sahkoposti")
   private String s�hk�posti;

   private String kotisivu;

   @Column(name = "viivastysprosentti")
   private int viiv�stysprosentti;

   private int maksuaikaa;
   
   private String yl�otsikko;
   
   private String huomio;

   public String getInstallaatio()
   {
      return installaatio;
   }

   public void setInstallaatio(String installaatio)
   {
      this.installaatio = installaatio;
   }

   public String getSaaja()
   {
      return saaja;
   }

   public void setSaaja(String saaja)
   {
      this.saaja = saaja;
   }

   public String getIBAN()
   {
      return IBAN;
   }

   public void setIBAN(String iBAN)
   {
      IBAN = iBAN;
   }

   public String getBIC()
   {
      return BIC;
   }

   public void setBIC(String bIC)
   {
      BIC = bIC;
   }

   public String getViitenumero()
   {
      return viitenumero;
   }

   public void setViitenumero(String viitenumero)
   {
      this.viitenumero = viitenumero;
   }

   public String getOsoite()
   {
      return osoite;
   }

   public void setOsoite(String osoite)
   {
      this.osoite = osoite;
   }

   public String getPostinumero()
   {
      return postinumero;
   }

   public void setPostinumero(String postinumero)
   {
      this.postinumero = postinumero;
   }

   public String getKaupunki()
   {
      return kaupunki;
   }

   public void setKaupunki(String kaupunki)
   {
      this.kaupunki = kaupunki;
   }

   public String getYTunnus()
   {
      return YTunnus;
   }

   public void setYTunnus(String yTunnus)
   {
      YTunnus = yTunnus;
   }

   public String getALVtunnus()
   {
      return ALVtunnus;
   }

   public void setALVtunnus(String aLVtunnus)
   {
      ALVtunnus = aLVtunnus;
   }

   public String getPuhelin()
   {
      return puhelin;
   }

   public void setPuhelin(String puhelin)
   {
      this.puhelin = puhelin;
   }

   public String getS�hk�posti()
   {
      return s�hk�posti;
   }

   public void setS�hk�posti(String s�hk�posti)
   {
      this.s�hk�posti = s�hk�posti;
   }

   public String getKotisivu()
   {
      return kotisivu;
   }

   public void setKotisivu(String kotisivu)
   {
      this.kotisivu = kotisivu;
   }

   public int getViiv�stysprosentti()
   {
      return viiv�stysprosentti;
   }

   public void setViiv�stysprosentti(int viiv�stysprosentti)
   {
      this.viiv�stysprosentti = viiv�stysprosentti;
   }

   public int getMaksuaikaa()
   {
      return maksuaikaa;
   }

   public void setMaksuaikaa(int maksuaikaa)
   {
      this.maksuaikaa = maksuaikaa;
   }

   public String getYl�otsikko()
   {
      return yl�otsikko;
   }

   public void setYl�otsikko(String yl�otsikko)
   {
      this.yl�otsikko = yl�otsikko;
   }

   public String getHuomio()
   {
      return huomio;
   }

   public void setHuomio(String huomio)
   {
      this.huomio = huomio;
   }

   public static long getSerialversionuid()
   {
      return serialVersionUID;
   }
}
