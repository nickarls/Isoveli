package fi.budokwai.isoveli.malli;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
@NamedQuery(name = "treenikäynnit", query = "select t from Treenikäynti t order by t.id desc")
@Table(name = "treenikaynti")
public class Treenikäynti
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @ManyToOne(optional = false)
   @JoinColumn(name = "harrastaja", insertable = false, updatable = false)
   @NotNull
   private Harrastaja harrastaja;

   @OneToOne(optional = false)
   @JoinColumn(name = "treenisessio")
   private Treenisessio treenisessio;

   @Temporal(TemporalType.TIMESTAMP)
   private Date aikaleima = new Date();

   @Temporal(TemporalType.DATE)
   @Column(name = "paiva")
   private Date päivä;

   public Treenikäynti()
   {
   }

   public Treenikäynti(Harrastaja harrastaja, Treenisessio treenisessio, Date päivä)
   {
      this.harrastaja = harrastaja;
      this.treenisessio = treenisessio;
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

   public Treenisessio getTreenisessio()
   {
      return treenisessio;
   }

   public void setTreenisessio(Treenisessio treenisessio)
   {
      this.treenisessio = treenisessio;
   }

}
