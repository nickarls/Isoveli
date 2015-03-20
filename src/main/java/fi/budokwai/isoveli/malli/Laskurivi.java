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

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicInsert
@DynamicUpdate
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

   @OneToOne(cascade =
   { CascadeType.PERSIST, CascadeType.REMOVE })
   @JoinColumn(name = "sopimuslasku")
   private Sopimuslasku sopimuslasku;

   @Temporal(TemporalType.TIMESTAMP)
   private Date luotu = new Date();

   @Size(max = 50)
   @NotNull
   private String tuotenimi = "";

   @Size(max = 200)
   private String infotieto;

   @Column(name = "maara")
   private int määrä = 1;

   @Size(max = 20)
   @Column(name = "yksikko")
   private String yksikkö = "kpl";

   @Column(name = "yksikkohinta")
   private double yksikköhinta = 0;

   public Laskurivi(Sopimuslasku sopimuslasku, Laskutuskausi laskutuskausi)
   {
      this.sopimuslasku = sopimuslasku;
      sopimuslasku.setLaskurivi(this);
      virkistäLaskurivi(laskutuskausi);
   }

   public Laskurivi()
   {
   }

   private void virkistäLaskurivi(Laskutuskausi laskutuskausi)
   {
      if (sopimuslasku == null)
      {
         return;
      }
      Sopimustyyppi sopimustyyppi = sopimuslasku.getSopimus().getTyyppi();
      tuotenimi = String.format("%s (%s)", sopimustyyppi.getNimi(), sopimuslasku.getSopimus().getHarrastaja()
         .getEtunimi());
      infotieto = sopimuslasku.getJakso();
      yksikköhinta = sopimustyyppi.getHinta();
      määrä = laskutuskausi.getKausikuukausia();
   }

   public double getRivihinta()
   {
      return määrä * yksikköhinta;
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

   public String getYksikkö()
   {
      return yksikkö;
   }

   public void setYksikkö(String yksikkö)
   {
      this.yksikkö = yksikkö;
   }

   public static Laskurivi perhealennus(float alennus, String infotieto)
   {
      Laskurivi laskurivi = new Laskurivi();
      laskurivi.setMäärä(1);
      laskurivi.setTuotenimi("Perhealennus");
      laskurivi.setInfotieto(infotieto);
      laskurivi.setYksikköhinta(new BigDecimal(-1 * alennus).setScale(2, RoundingMode.HALF_UP).doubleValue());
      laskurivi.setYksikkö("kpl");
      return laskurivi;
   }

   public static Laskurivi taukohyvitys(Laskutuskausi laskutuskausi, Sopimus sopimus)
   {
      Laskurivi laskurivi = new Laskurivi();
      laskurivi.setMäärä((int) laskutuskausi.getTaukopäiviä());
      laskurivi.setTuotenimi(String.format("Taukohyvitys (%s)", sopimus.getHarrastaja().getEtunimi()));
      laskurivi.setInfotieto(laskutuskausi.getTauko().getKuvaus());
      double hinta = -1
         * new BigDecimal(sopimus.getTyyppi().getHinta() / 30).setScale(2, RoundingMode.HALF_UP).doubleValue();
      laskurivi.setYksikköhinta(hinta);
      laskurivi.setYksikkö("kpl");
      return laskurivi;
   }

   @Override
   public String toString()
   {
      return String.format("%s %s, %d %s @EUR%f = EUR%f", tuotenimi, infotieto, määrä, yksikkö, yksikköhinta,
         getRivihinta());
   }
   
   public boolean isMuokattavissa() {
      return lasku.getTila().equals(LaskuTila.M);
   }
}
