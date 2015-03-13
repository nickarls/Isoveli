package fi.budokwai.isoveli.malli;

import java.text.SimpleDateFormat;
import java.util.Date;

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
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicInsert
@DynamicUpdate
@NamedQuery(name = "treenikäynnit", query = "select t from Treenikäynti t order by t.id desc")
@Table(name = "treenikaynti")
public class Treenikäynti
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @ManyToOne
   @JoinColumn(name = "harrastaja")
   @NotNull
   @Valid
   private Harrastaja harrastaja;

   @ManyToOne
   @JoinColumn(name = "treenisessio")
   @NotNull
   @Valid
   private Treenisessio treenisessio = new Treenisessio();

   @Temporal(TemporalType.TIMESTAMP)
   private Date aikaleima = new Date();

   public Treenikäynti()
   {
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

   public String getAikaleimaString()
   {
      SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
      return sdf.format(aikaleima);
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
   public boolean equals(Object toinen)
   {
      if (!(toinen instanceof Treenikäynti))
      {
         return false;
      }
      Treenikäynti toinenTreenikäynti = (Treenikäynti) toinen;
      return id == toinenTreenikäynti.getId();
   }

   public void päivitäAikaleima()
   {
      aikaleima = treenisessio.getAikaleima();
   }

   public boolean isTallennettu()
   {
      return id > 0;
   }

   @Override
   public String toString()
   {
      return String.format("%s@%s", harrastaja, treenisessio);
   }
}
