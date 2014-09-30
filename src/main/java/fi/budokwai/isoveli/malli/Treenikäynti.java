package fi.budokwai.isoveli.malli;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
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
   @JoinColumn(name = "harrastaja")
   @NotNull
   private Harrastaja harrastaja;

   @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
   @JoinColumn(name = "treenisessio")
   @NotNull
   private Treenisessio treenisessio;

   @Temporal(TemporalType.TIMESTAMP)
   private Date aikaleima = new Date();

   public Treenikäynti()
   {

   }

   public Treenikäynti(Harrastaja harrastaja, Treenisessio treenisessio)
   {
      this.harrastaja = harrastaja;
      this.treenisessio = treenisessio;
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

   public Treenisessio getTreenisessio()
   {
      return treenisessio;
   }

   public void setTreenisessio(Treenisessio treenisessio)
   {
      this.treenisessio = treenisessio;
   }

   @Override
   public int hashCode()
   {
      return Integer.valueOf(id).hashCode();
   }

   @Override
   public boolean equals(Object other)
   {
      Treenikäynti toinen = (Treenikäynti) other;
      return id == toinen.getId();
   }
}
