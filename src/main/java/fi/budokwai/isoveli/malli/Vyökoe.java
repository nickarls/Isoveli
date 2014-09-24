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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@SequenceGenerator(name = "vyokoe_seq", sequenceName = "vyokoe_seq", allocationSize = 1, initialValue = 2)
@Table(name = "vyokoe")
public class Vy�koe
{
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vyokoe_seq")
   private int id;

   @ManyToOne(optional = false)
   @JoinColumn(name = "harrastaja", insertable = false, updatable = false)
   private Harrastaja harrastaja;

   @OneToOne(optional = false)
   @JoinColumn(name = "vyoarvo")
   private Vy�arvo vy�arvo;

   @Temporal(TemporalType.DATE)
   @Column(name = "paiva")
   private Date p�iv�;

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

   public Vy�arvo getVy�arvo()
   {
      return vy�arvo;
   }

   public boolean isTuoreinVy�koe()
   {
      return harrastaja.getVy�kokeet().get(harrastaja.getVy�kokeet().size() - 1) == this;
   }

   public long getAikaaValissa()
   {
      int koeIndeksi = harrastaja.getVy�kokeet().indexOf(this);
      if (koeIndeksi == 0)
      {
         return 0;
      }
      Vy�koe edellinenKoe = harrastaja.getVy�kokeet().get(koeIndeksi - 1);
      long diff = p�iv�.getTime() - edellinenKoe.getP�iv�().getTime();
      return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
   }

   public void setVy�arvo(Vy�arvo vy�arvo)
   {
      this.vy�arvo = vy�arvo;
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
