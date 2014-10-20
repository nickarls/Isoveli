package fi.budokwai.isoveli.malli;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.Size;

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

   @Size(max = 10)
   private String tuotekoodi;

   @Size(max = 500)
   private String tuotenimi;

   @Column(name = "maara")
   private int määrä;

   @Column(name = "yksikko")
   @Size(max = 20)
   private String yksikkö;

   private int verokanta;

   @Column(name = "verotonyksikkohinta")
   private int verotonYksikköhinta;

   @Size(max = 2000)
   private String huomautus;

   public Laskurivi()
   {
   }

   public Laskurivi(Sopimus sopimus)
   {
      tuotekoodi = sopimus.getTyyppi().getTuotekoodi();
      tuotenimi = sopimus.getTuotenimi();
      määrä = sopimus.getTyyppi().getMäärä();
      yksikkö = sopimus.getTyyppi().getYksikkö();
      verokanta = sopimus.getTyyppi().getVerokanta();
      verotonYksikköhinta = sopimus.getTyyppi().getHinta();
      huomautus = "!";
      this.sopimus = sopimus;
      sopimus.setLaskurivi(this);
   }

   public double getRivihinta()
   {
      return määrä * verotonYksikköhinta * (1 + verokanta / 100);
   }

   public double getAlv()
   {
      return määrä * verotonYksikköhinta * (verokanta / 100);
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

   public String getTuotekoodi()
   {
      return tuotekoodi;
   }

   public void setTuotekoodi(String tuotekoodi)
   {
      this.tuotekoodi = tuotekoodi;
   }

   public String getTuotenimi()
   {
      return tuotenimi;
   }

   public void setTuotenimi(String tuotenimi)
   {
      this.tuotenimi = tuotenimi;
   }

   public int getMäärä()
   {
      return määrä;
   }

   public void setMäärä(int määrä)
   {
      this.määrä = määrä;
   }

   public int getVerokanta()
   {
      return verokanta;
   }

   public void setVerokanta(int verokanta)
   {
      this.verokanta = verokanta;
   }

   public int getVerotonYksikköhinta()
   {
      return verotonYksikköhinta;
   }

   public void setVerotonYksikköhinta(int verotonYksikköhinta)
   {
      this.verotonYksikköhinta = verotonYksikköhinta;
   }

   public String getHuomautus()
   {
      return huomautus;
   }

   public void setHuomautus(String huomautus)
   {
      this.huomautus = huomautus;
   }

   public String getYksikkö()
   {
      return yksikkö;
   }

   public void setYksikkö(String yksikkö)
   {
      this.yksikkö = yksikkö;
   }

   public int getRivinumero()
   {
      return rivinumero;
   }

   public void setRivinumero(int rivinumero)
   {
      this.rivinumero = rivinumero;
   }
}
