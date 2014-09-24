package fi.budokwai.isoveli.malli;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@NamedQuery(name = "treenik�ynnit", query = "select t from Treenik�ynti t order by t.id desc")
@Table(name="treenikaynti")
public class Treenik�ynti
{
   @Id
   @GeneratedValue
   private int id;

   @OneToOne(optional = false)
   @JoinColumn(name = "harrastaja")
   private Harrastaja harrastaja;

   @OneToOne(optional = false)
   @JoinColumn(name = "treeni")
   private Treeni treeni;

   @Temporal(TemporalType.TIMESTAMP)
   private Date aikaleima = new Date();

   @Temporal(TemporalType.DATE)
   @Column(name="paiva")
   private Date p�iv�;

   public Treenik�ynti()
   {
   }

   public Treenik�ynti(Harrastaja harrastaja, Treeni treeni, Date p�iv�)
   {
      this.harrastaja = harrastaja;
      this.treeni = treeni;
      this.p�iv� = p�iv�;
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

   public Treeni getTreeni()
   {
      return treeni;
   }

   public void setTreeni(Treeni treeni)
   {
      this.treeni = treeni;
   }

   public Date getAikaleima()
   {
      return aikaleima;
   }

   public void setAikaleima(Date aikaleima)
   {
      this.aikaleima = aikaleima;
   }

   public Date getP�iv�()
   {
      return p�iv�;
   }

   public void setP�iv�(Date p�iv�)
   {
      this.p�iv� = p�iv�;
   }

}
