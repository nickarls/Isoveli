package fi.budokwai.isoveli.malli;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
   private int m��r�;

   @Size(max = 20)
   @Column(name = "yksikko")
   private String yksikk�;

   @Column(name = "yksikkohinta")
   private double yksikk�hinta;

   public Laskurivi()
   {
   }

   public Laskurivi(Sopimuslasku sopimuslasku)
   {
      this.sopimuslasku = sopimuslasku;
      sopimuslasku.setLaskurivi(this);
      virkist�Laskurivi();
   }

   public void virkist�Laskurivi()
   {
      if (sopimuslasku == null)
      {
         return;
      }
      Sopimustyyppi sopimustyyppi = sopimuslasku.getSopimus().getTyyppi();
      tuotenimi = String.format("%s (%s)", sopimustyyppi.getNimi(), sopimuslasku.getSopimus().getHarrastaja()
         .getEtunimi());
      infotieto = sopimuslasku.getJakso();
      yksikk�hinta = sopimustyyppi.getHinta();
      m��r� = sopimustyyppi.getM��r�();
      yksikk� = sopimustyyppi.getYksikk�();
   }

   public double getRivihinta()
   {
      return m��r� * yksikk�hinta;
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

   public int getM��r�()
   {
      return m��r�;
   }

   public void setM��r�(int m��r�)
   {
      this.m��r� = m��r�;
   }

   public double getYksikk�hinta()
   {
      return yksikk�hinta;
   }

   public void setYksikk�hinta(double yksikk�hinta)
   {
      this.yksikk�hinta = yksikk�hinta;
   }

   public String getYksikk�()
   {
      return yksikk�;
   }

   public void setYksikk�(String yksikk�)
   {
      this.yksikk� = yksikk�;
   }

   public static Laskurivi perhealennus(float alennus, String infotieto)
   {
      Laskurivi laskurivi = new Laskurivi();
      laskurivi.setM��r�(1);
      laskurivi.setTuotenimi("Perhealennus");
      laskurivi.setInfotieto(infotieto);
      laskurivi.setYksikk�hinta(new BigDecimal(-1 * alennus).setScale(2, RoundingMode.HALF_UP).doubleValue());
      laskurivi.setYksikk�("kpl");
      return laskurivi;
   }

}
