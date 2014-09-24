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
@NamedQuery(name = "treenikäynnit", query = "select t from Treenikäynti t order by t.id desc")
@Table(name="treenikaynti")
public class Treenikäynti
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
   private Date päivä;

   public Treenikäynti()
   {
   }

   public Treenikäynti(Harrastaja harrastaja, Treeni treeni, Date päivä)
   {
      this.harrastaja = harrastaja;
      this.treeni = treeni;
      this.päivä = päivä;
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

   public Date getPäivä()
   {
      return päivä;
   }

   public void setPäivä(Date päivä)
   {
      this.päivä = päivä;
   }

}
