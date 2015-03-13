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
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicInsert
@DynamicUpdate
@NamedQuery(name = "treenik‰ynnit", query = "select t from Treenik‰ynti t order by t.id desc")
@Table(name = "treenikaynti")
public class Treenik‰ynti
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

   @Transient
   @Size(min = 1)
   private String harrastajaHaku;

   @Temporal(TemporalType.TIMESTAMP)
   private Date aikaleima = new Date();

   @PostLoad
   public void alusta()
   {
      harrastajaHaku = harrastaja.getNimi();
   }

   public Treenik‰ynti()
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
      if (!(toinen instanceof Treenik‰ynti))
      {
         return false;
      }
      Treenik‰ynti toinenTreenik‰ynti = (Treenik‰ynti) toinen;
      return id == toinenTreenik‰ynti.getId();
   }

   public void p‰ivit‰Aikaleima()
   {
      aikaleima = treenisessio.getAikaleima();
   }

   @Override
   public String toString()
   {
      return String.format("%s@%s", harrastaja, treenisessio);
   }

   public boolean isTallentamaton()
   {
      return id == 0;
   }

   public String getHarrastajaHaku()
   {
      return harrastajaHaku;
   }

   public void setHarrastajaHaku(String harrastajaHaku)
   {
      this.harrastajaHaku = harrastajaHaku;
   }

}
