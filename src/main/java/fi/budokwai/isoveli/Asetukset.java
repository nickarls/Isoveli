package fi.budokwai.isoveli;

import java.io.Serializable;

import javax.enterprise.inject.Typed;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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

   @NotNull
   @Size(max = 50)
   private String saaja;

   @NotNull
   @Size(max = 50)
   private String IBAN;

   @NotNull
   @Size(max = 50)
   private String BIC;

   @NotNull
   private Integer viitenumero;

   @NotNull
   @Size(max = 50)
   private String osoite;

   @NotNull
   @Size(max = 10)
   private String postinumero;

   @NotNull
   @Size(max = 50)
   private String kaupunki;

   @NotNull
   @Size(max = 10)
   private String YTunnus;

   @NotNull
   @Size(max = 10)
   private String ALVtunnus;

   @NotNull
   @Size(max = 20)
   private String puhelin;

   @NotNull
   @Size(max = 100)
   private String tulostin;

   @NotNull
   @Size(max = 100)
   private String ghostScript;

   @Column(name = "sahkoposti")
   @NotNull
   @Size(max = 50)
   private String sähköposti;

   @NotNull
   @Size(max = 50)
   private String kotisivut;

   @Column(name = "viivastysprosentti")
   @NotNull
   private Integer viivästysprosentti;

   @NotNull
   private Integer maksuaikaa;

   @Column(name = "ylaotsikko")
   @NotNull
   @Size(max = 20)
   private String yläotsikko;

   @NotNull
   @Size(max = 300)
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

   public Integer getViitenumero()
   {
      return viitenumero;
   }

   public void setViitenumero(Integer viitenumero)
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

   public Integer getViivästysprosentti()
   {
      return viivästysprosentti;
   }

   public void setViivästysprosentti(Integer viivästysprosentti)
   {
      this.viivästysprosentti = viivästysprosentti;
   }

   public Integer getMaksuaikaa()
   {
      return maksuaikaa;
   }

   public void setMaksuaikaa(Integer maksuaikaa)
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
