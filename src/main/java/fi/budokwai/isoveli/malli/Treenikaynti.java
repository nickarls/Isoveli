package fi.budokwai.isoveli.malli;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@NamedQuery(name = "treenikaynnit", query = "select t from Treenikaynti t order by t.id desc")
public class Treenikaynti
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

   public Treenikaynti()
   {

   }

   public Treenikaynti(Harrastaja harrastaja, Treeni treeni)
   {
      this.harrastaja = harrastaja;
      this.treeni = treeni;
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

}
