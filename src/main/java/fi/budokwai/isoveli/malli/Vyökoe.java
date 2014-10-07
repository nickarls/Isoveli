package fi.budokwai.isoveli.malli;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name = "vyokoe")
public class Vyökoe
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @ManyToOne
   @JoinColumn(name = "vyoarvo")
   private Vyöarvo vyöarvo;

   @ManyToOne
   @JoinColumn(name = "harrastaja")
   private Harrastaja harrastaja;

   @Temporal(TemporalType.DATE)
   @Column(name = "paiva")
   private Date päivä;

   public Date getPäivä()
   {
      return päivä;
   }

   public void setPäivä(Date päivä)
   {
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

   public Vyöarvo getVyöarvo()
   {
      return vyöarvo;
   }

   public void setVyöarvo(Vyöarvo vyöarvo)
   {
      this.vyöarvo = vyöarvo;
   }

   public Harrastaja getHarrastaja()
   {
      return harrastaja;
   }

   public void setHarrastaja(Harrastaja harrastaja)
   {
      this.harrastaja = harrastaja;
   }

   @Transient
   public boolean isTuoreinVyökoe()
   {
      return getHarrastaja().getVyökokeet().get(getHarrastaja().getVyökokeet().size() - 1) == this;
   }

   @Transient
   public boolean isPoistettavissa()
   {
      return id > 0;
   }

   @Transient
   public long getAikaaVälissä()
   {
      int koeIndeksi = getHarrastaja().getVyökokeet().indexOf(this);
      if (koeIndeksi == 0)
      {
         return 0;
      }
      Vyökoe edellinenKoe = getHarrastaja().getVyökokeet().get(koeIndeksi - 1);
      long diff = päivä.getTime() - edellinenKoe.getPäivä().getTime();
      return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
   }

   public boolean equals(Object toinen)
   {
      Vyökoe toinenVyökoe = (Vyökoe) toinen;
      return id == toinenVyökoe.getId();
   }

   public int hashCode()
   {
      return Integer.valueOf(id).hashCode();
   }

}