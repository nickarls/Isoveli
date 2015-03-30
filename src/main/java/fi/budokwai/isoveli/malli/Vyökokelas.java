package fi.budokwai.isoveli.malli;

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

}
