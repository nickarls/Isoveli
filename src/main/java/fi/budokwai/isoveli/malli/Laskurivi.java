package fi.budokwai.isoveli.malli;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Laskurivi
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @ManyToOne
   @JoinColumn(name = "lasku")
   private Lasku lasku;

   private int rivinumero;

   @OneToOne(optional = false)
   @JoinColumn(name = "sopimus")
   private Sopimus sopimus;

   @Temporal(TemporalType.TIMESTAMP)
   private Date luotu = new Date();

   public Laskurivi()
   {
   }

   public Laskurivi(Sopimus sopimus)
   {
      this.sopimus = sopimus;
      sopimus.setLaskurivi(this);
   }

   public double getRivihinta()
   {
      return sopimus.getTyyppi().getVerollinenHinta();
   }

   public double getAlv()
   {
      return sopimus.getTyyppi().getVerotonHinta();
   }

   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public Lasku getLasku()
   {
      return lasku;
   }

   public void setLasku(Lasku lasku)
   {
      this.lasku = lasku;
   }

   public int getRivinumero()
   {
      return rivinumero;
   }

   public void setRivinumero(int rivinumero)
   {
      this.rivinumero = rivinumero;
   }

   public Date getLuotu()
   {
      return luotu;
   }

   public void setLuotu(Date luotu)
   {
      this.luotu = luotu;
   }
}
