package fi.budokwai.isoveli.malli;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "vyokokelas")
public class Vyökokelas
{

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @ManyToOne
   @JoinColumn(name = "harrastaja")
   @NotNull
   private Harrastaja harrastaja;

   @ManyToOne
   @JoinColumn(name = "vyokoetilaisuus")
   @NotNull
   private Vyökoetilaisuus vyökoetilaisuus;

   @Column(name = "vetajahyvaksynta")
   @Type(type = "KylläEi")
   private boolean vetäjänHyväksyntä;

   @Type(type = "KylläEi")
   private boolean maksu;

   @Type(type = "KylläEi")
   private boolean passi;

   @Type(type = "KylläEi")
   private boolean onnistui;

   public Vyökokelas()
   {
   }

   public Vyökokelas(Harrastaja harrastaja)
   {
      this.harrastaja = harrastaja;
   }

   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public Harrastaja getHarrastaja()
   {
      return harrastaja;
   }

   public void setHarrastaja(Harrastaja harrastaja)
   {
      this.harrastaja = harrastaja;
   }

   public Vyökoetilaisuus getVyökoetilaisuus()
   {
      return vyökoetilaisuus;
   }

   public void setVyökoetilaisuus(Vyökoetilaisuus vyökoetilaisuus)
   {
      this.vyökoetilaisuus = vyökoetilaisuus;
   }

   public boolean isVetäjänHyväksyntä()
   {
      return vetäjänHyväksyntä;
   }

   public void setVetäjänHyväksyntä(boolean vetäjänHyväksyntä)
   {
      this.vetäjänHyväksyntä = vetäjänHyväksyntä;
   }

   public boolean isMaksu()
   {
      return maksu;
   }

   public void setMaksu(boolean maksu)
   {
      this.maksu = maksu;
   }

   public boolean isPassi()
   {
      return passi;
   }

   public void setPassi(boolean passi)
   {
      this.passi = passi;
   }

   public boolean isOnnistui()
   {
      return onnistui;
   }

   public void setOnnistui(boolean onnistui)
   {
      this.onnistui = onnistui;
   }

   @Override
   public String toString()
   {
      return harrastaja.getNimi();
   }

   @Override
   public int hashCode()
   {
      return Integer.valueOf(id).hashCode();
   }

   @Override
   public boolean equals(Object toinen)
   {
      Vyökokelas toinenKokelas = (Vyökokelas) toinen;
      return id == toinenKokelas.getId();
   }
}
