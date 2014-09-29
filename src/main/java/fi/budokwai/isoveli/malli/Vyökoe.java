package fi.budokwai.isoveli.malli;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Vy�koe;

@Entity
@Table(name = "vyokoe")
@AssociationOverrides(
{ @AssociationOverride(name = "id.vy�arvo", joinColumns = @JoinColumn(name = "vyoarvo")),
      @AssociationOverride(name = "id.harrastaja", joinColumns = @JoinColumn(name = "harrastaja")) })
public class Vy�koe
{
   private Vy�koeavain id = new Vy�koeavain();

   private Date p�iv�;

   @EmbeddedId
   private Vy�koeavain getId()
   {
      return id;
   }

   private void setId(Vy�koeavain id)
   {
      this.id = id;
   }

   @Transient
   public Vy�arvo getVy�arvo()
   {
      return getId().getVy�arvo();
   }

   public void setVy�arvo(Vy�arvo vy�arvo)
   {
      getId().setVy�arvo(vy�arvo);
   }

   @Transient
   public Harrastaja getHarrastaja()
   {
      return getId().getHarrastaja();
   }

   public void setHarrastaja(Harrastaja harrastaja)
   {
      getId().setHarrastaja(harrastaja);
   }

   @Temporal(TemporalType.DATE)
   @Column(name = "paiva")
   public Date getP�iv�()
   {
      return p�iv�;
   }

   public void setP�iv�(Date p�iv�)
   {
      this.p�iv� = p�iv�;
   }

   @Transient
   public boolean isTuoreinVy�koe()
   {
      return getHarrastaja().getVy�kokeet().get(getHarrastaja().getVy�kokeet().size() - 1) == this;
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

   public boolean equals(Object o)
   {
      if (this == o)
      {
         return true;
      }
      if (o == null || getClass() != o.getClass())
      {
         return false;
      }
      Vy�koe that = (Vy�koe) o;
      if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null)
      {
         return false;
      }
      return true;
   }

   public int hashCode()
   {
      return (getId() != null ? getId().hashCode() : 0);
   }

}