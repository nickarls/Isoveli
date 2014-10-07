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
public class Vy�koe
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @ManyToOne
   @JoinColumn(name = "vyoarvo")
   private Vy�arvo vy�arvo;

   @ManyToOne
   @JoinColumn(name = "harrastaja")
   private Harrastaja harrastaja;

   @Temporal(TemporalType.DATE)
   @Column(name = "paiva")
   private Date p�iv�;

   public Date getP�iv�()
   {
      return p�iv�;
   }

   public void setP�iv�(Date p�iv�)
   {
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

   public Vy�arvo getVy�arvo()
   {
      return vy�arvo;
   }

   public void setVy�arvo(Vy�arvo vy�arvo)
   {
      this.vy�arvo = vy�arvo;
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
   public boolean isTuoreinVy�koe()
   {
      return getHarrastaja().getVy�kokeet().get(getHarrastaja().getVy�kokeet().size() - 1) == this;
   }

   @Transient
   public boolean isPoistettavissa()
   {
      return id > 0;
   }

   @Transient
   public long getAikaaV�liss�()
   {
      int koeIndeksi = getHarrastaja().getVy�kokeet().indexOf(this);
      if (koeIndeksi == 0)
      {
         return 0;
      }
      Vy�koe edellinenKoe = getHarrastaja().getVy�kokeet().get(koeIndeksi - 1);
      long diff = p�iv�.getTime() - edellinenKoe.getP�iv�().getTime();
      return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
   }

   public boolean equals(Object toinen)
   {
      Vy�koe toinenVy�koe = (Vy�koe) toinen;
      return id == toinenVy�koe.getId();
   }

   public int hashCode()
   {
      return Integer.valueOf(id).hashCode();
   }

}