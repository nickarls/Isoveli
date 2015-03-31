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
public class Vy�kokelas
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
   private Vy�koetilaisuus vy�koetilaisuus;

   @Column(name = "vetajahyvaksynta")
   @Type(type = "Kyll�Ei")
   private boolean vet�j�nHyv�ksynt�;

   @Type(type = "Kyll�Ei")
   private boolean maksu;

   @Type(type = "Kyll�Ei")
   private boolean passi;

   @Type(type = "Kyll�Ei")
   private boolean onnistui;

   public Vy�kokelas()
   {
   }

   public Vy�kokelas(Harrastaja harrastaja)
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

   public Vy�koetilaisuus getVy�koetilaisuus()
   {
      return vy�koetilaisuus;
   }

   public void setVy�koetilaisuus(Vy�koetilaisuus vy�koetilaisuus)
   {
      this.vy�koetilaisuus = vy�koetilaisuus;
   }

   public boolean isVet�j�nHyv�ksynt�()
   {
      return vet�j�nHyv�ksynt�;
   }

   public void setVet�j�nHyv�ksynt�(boolean vet�j�nHyv�ksynt�)
   {
      this.vet�j�nHyv�ksynt� = vet�j�nHyv�ksynt�;
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
      Vy�kokelas toinenKokelas = (Vy�kokelas) toinen;
      return id == toinenKokelas.getId();
   }
}
