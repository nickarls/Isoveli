package fi.budokwai.isoveli.malli;

import java.util.Date;

import javax.persistence.CascadeType;
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

@Entity
@Table(name = "laskurivi")
public class Laskurivi
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @ManyToOne
   @JoinColumn(name = "lasku")
   private Lasku lasku;

   private int rivinumero;

   @OneToOne(cascade=CascadeType.PERSIST, optional = false)
   @JoinColumn(name="sopimuslasku")
   private Sopimuslasku sopimuslasku;

   @Temporal(TemporalType.TIMESTAMP)
   private Date luotu = new Date();

   public Laskurivi()
   {
   }

   public Laskurivi(Sopimuslasku sopimuslasku)
   {
      this.sopimuslasku = sopimuslasku;
      sopimuslasku.setLaskurivi(this);
   }

   private Sopimustyyppi getSopimustyyppi()
   {
      return sopimuslasku.getSopimus().getTyyppi();
   }

   public double getVerotonHinta()
   {
      return getSopimustyyppi().getVerotonHinta();
   }

   public double getALVnOsuus()
   {
      return getSopimustyyppi().getALVnOsuus();
   }

   public double getVerollinenHinta()
   {
      return getSopimustyyppi().getVerollinenHinta();
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

   public Sopimuslasku getSopimuslasku()
   {
      return sopimuslasku;
   }

   public void setSopimuslasku(Sopimuslasku sopimuslasku)
   {
      this.sopimuslasku = sopimuslasku;
   }

}
