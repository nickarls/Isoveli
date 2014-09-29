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
import fi.budokwai.isoveli.malli.Vyökoe;

@Entity
@Table(name = "vyokoe")
@AssociationOverrides(
{ @AssociationOverride(name = "id.vyöarvo", joinColumns = @JoinColumn(name = "vyoarvo")),
      @AssociationOverride(name = "id.harrastaja", joinColumns = @JoinColumn(name = "harrastaja")) })
public class Vyökoe
{
   private Vyökoeavain id = new Vyökoeavain();

   private Date päivä;

   @EmbeddedId
   private Vyökoeavain getId()
   {
      return id;
   }

   private void setId(Vyökoeavain id)
   {
      this.id = id;
   }

   @Transient
   public Vyöarvo getVyöarvo()
   {
      return getId().getVyöarvo();
   }

   public void setVyöarvo(Vyöarvo vyöarvo)
   {
      getId().setVyöarvo(vyöarvo);
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
   public Date getPäivä()
   {
      return päivä;
   }

   public void setPäivä(Date päivä)
   {
      this.päivä = päivä;
   }

   @Transient
   public boolean isTuoreinVyökoe()
   {
      return getHarrastaja().getVyökokeet().get(getHarrastaja().getVyökokeet().size() - 1) == this;
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
      Vyökoe that = (Vyökoe) o;
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