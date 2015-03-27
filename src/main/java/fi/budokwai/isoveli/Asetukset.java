package fi.budokwai.isoveli;

import java.io.Serializable;

import javax.enterprise.inject.Typed;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "asetukset")
@Typed(
{})
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

   @Transient
   private String tulostin = "\\\\KUUTTI\\TKU2-RATA-4250";

   @Transient
   private String ghostScript = "c:/Program Files/gs/gs9.15/bin/gswin64c.exe";

   @Column(name = "sahkoposti")
   private String sähköposti;

   private String kotisivut;

   @Column(name = "viivastysprosentti")
   private int viivästysprosentti;

   private int maksuaikaa;

   @Column(name = "ylaotsikko")
   private String yläotsikko;

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

   public String getSähköposti()
   {
      return sähköposti;
   }

   public void setSähköposti(String sähköposti)
   {
      this.sähköposti = sähköposti;
   }

   public String getKotisivut()
   {
      return kotisivut;
   }

   public void setKotisivut(String kotisivut)
   {
      this.kotisivut = kotisivut;
   }

   public int getViivästysprosentti()
   {
      return viivästysprosentti;
   }

   public void setViivästysprosentti(int viivästysprosentti)
   {
      this.viivästysprosentti = viivästysprosentti;
   }

   public int getMaksuaikaa()
   {
      return maksuaikaa;
   }

   public void setMaksuaikaa(int maksuaikaa)
   {
      this.maksuaikaa = maksuaikaa;
   }

   public String getYläotsikko()
   {
      return yläotsikko;
   }

   public void setYläotsikko(String yläotsikko)
   {
      this.yläotsikko = yläotsikko;
   }

   public String getHuomio()
   {
      return huomio;
   }

   public void setHuomio(String huomio)
   {
      this.huomio = huomio;
   }

   public String getTulostin()
   {
      return tulostin;
   }

   public void setTulostin(String tulostin)
   {
      this.tulostin = tulostin;
   }

   public String getGhostScript()
   {
      return ghostScript;
   }

   public void setGhostScript(String ghostScript)
   {
      this.ghostScript = ghostScript;
   }
}
