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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "vyokoe")
public class Vyökoe
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @ManyToOne(optional = false)
   @JoinColumn(name = "harrastaja", insertable = false, updatable = false)
   @NotNull
   private Harrastaja harrastaja;

   @OneToOne(optional = false)
   @JoinColumn(name = "vyoarvo")
   @NotNull
   private Vyöarvo vyöarvo;

   @Temporal(TemporalType.DATE)
   @Column(name = "paiva")
   @NotNull
   private Date päivä;

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

   public Vyöarvo getVyöarvo()
   {
      return vyöarvo;
   }

   public boolean isTuoreinVyökoe()
   {
      return harrastaja.getVyökokeet().get(harrastaja.getVyökokeet().size() - 1) == this;
   }

   public long getAikaaValissa()
   {
      int koeIndeksi = harrastaja.getVyökokeet().indexOf(this);
      if (koeIndeksi == 0)
      {
         return 0;
      }
      Vyökoe edellinenKoe = harrastaja.getVyökokeet().get(koeIndeksi - 1);
      long diff = päivä.getTime() - edellinenKoe.getPäivä().getTime();
      return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
   }

   public void setVyöarvo(Vyöarvo vyöarvo)
   {
      this.vyöarvo = vyöarvo;
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
