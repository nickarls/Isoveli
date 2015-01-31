package fi.budokwai.isoveli.malli;

import java.util.Date;

import javax.persistence.CascadeType;
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
import javax.validation.constraints.Size;

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

   @OneToOne(cascade = CascadeType.PERSIST)
   @JoinColumn(name = "sopimuslasku")
   private Sopimuslasku sopimuslasku;

   @Temporal(TemporalType.TIMESTAMP)
   private Date luotu = new Date();

   @Size(max = 50)
   @NotNull
   private String tuotenimi;

   @Size(max = 200)
   private String infotieto;

   @Column(name = "maara")
   private int määrä;

   @Size(max = 20)
   @Column(name="yksikko")
   private String yksikkö;
   
   @Column(name = "yksikkohinta")
   private double yksikköhinta;

   private int verokanta;

   public Laskurivi()
   {
   }

   public Laskurivi(Sopimuslasku sopimuslasku)
   {
      this.sopimuslasku = sopimuslasku;
      sopimuslasku.setLaskurivi(this);
      virkistäLasku();
   }

   public void virkistäLasku()
   {
      if (sopimuslasku == null)
      {
         return;
      }
      Sopimustyyppi sopimustyyppi = sopimuslasku.getSopimus().getTyyppi();
      tuotenimi = sopimustyyppi.getNimi();
      infotieto = sopimuslasku.getJakso();
      yksikköhinta = sopimustyyppi.getHinta();
      määrä = sopimustyyppi.getMäärä();
      yksikkö = sopimustyyppi.getYksikkö();
      verokanta = sopimustyyppi.getVerokanta();
   }

   public double getVerotonHinta()
   {
      return määrä * yksikköhinta;
   }

   public double getALVnOsuus()
   {
      return getVerollinenHinta() - getVerotonHinta();
   }

   public double getVerollinenHinta()
   {
      return määrä * yksikköhinta * (1 + verokanta / 100f);
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

   public String getTuotenimi()
   {
      return tuotenimi;
   }

   public void setTuotenimi(String tuotenimi)
   {
      this.tuotenimi = tuotenimi;
   }

   public String getInfotieto()
   {
      return infotieto;
   }

   public void setInfotieto(String infotieto)
   {
      this.infotieto = infotieto;
   }

   public int getMäärä()
   {
      return määrä;
   }

   public void setMäärä(int määrä)
   {
      this.määrä = määrä;
   }

   public double getYksikköhinta()
   {
      return yksikköhinta;
   }

   public void setYksikköhinta(double yksikköhinta)
   {
      this.yksikköhinta = yksikköhinta;
   }

   public int getVerokanta()
   {
      return verokanta;
   }

   public void setVerokanta(int verokanta)
   {
      this.verokanta = verokanta;
   }

   public String getYksikkö()
   {
      return yksikkö;
   }

   public void setYksikkö(String yksikkö)
   {
      this.yksikkö = yksikkö;
   }

}
