package fi.budokwai.isoveli.malli;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import fi.budokwai.isoveli.util.Util;

@Entity
@NamedQueries(
{
      @NamedQuery(name = "maksamattomat_laskut", query = "select l from Lasku l where l.maksettu is null and l.er�p�iv� >= :t�n��n order by l.er�p�iv� desc"),
      @NamedQuery(name = "maksetut_laskut", query = "select l from Lasku l where l.maksettu is not null order by l.maksettu desc"),
      @NamedQuery(name = "my�h�styneet_laskut", query = "select l from Lasku l where l.maksettu is null and l.er�p�iv� < :t�n��n order by l.er�p�iv� desc") })
public class Lasku
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @OneToMany(mappedBy = "lasku", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<Laskurivi> laskurivit = new ArrayList<Laskurivi>();

   @Enumerated(EnumType.STRING)
   private TilausTila tila = TilausTila.A;

   @Temporal(TemporalType.DATE)
   @Column(name = "erapaiva")
   private Date er�p�iv�;

   @Temporal(TemporalType.DATE)
   private Date maksettu;

   @OneToOne(optional = true)
   @JoinColumn(name = "harrastaja")
   private Harrastaja harrastaja;

   @Temporal(TemporalType.TIMESTAMP)
   private Date luotu = new Date();

   @OneToOne(cascade = CascadeType.ALL, optional = true, orphanRemoval = true)
   @JoinColumn(name = "pdf")
   private BlobData pdf;

   public Lasku()
   {
   }

   public Lasku(List<Sopimus> sopimukset)
   {
      harrastaja = sopimukset.stream().findFirst().get().getHarrastaja();
      er�p�iv� = haeEr�p�iv�(14);
      sopimukset.forEach(sopimus -> {
         Laskurivi laskurivi = new Laskurivi(sopimus);
         laskurivi.setLasku(this);
         laskurivit.add(laskurivi);
         laskurivi.setRivinumero(laskurivit.size());
      });
   }

   public int getLaskurivej�()
   {
      return laskurivit.size();
   }

   private Date haeEr�p�iv�(int p�ivi�)
   {
      LocalDate nyt = LocalDateTime.now().toLocalDate();
      nyt = nyt.plus(p�ivi�, ChronoUnit.DAYS);
      return Date.from(nyt.atStartOfDay().atZone(ZoneOffset.systemDefault()).toInstant());
   }

   public double getVerotonHinta()
   {
      return laskurivit.stream().mapToDouble((lr) -> lr.getVerotonHinta()).sum();
   }

   public double getALVnOsuus()
   {
      return laskurivit.stream().mapToDouble((lr) -> lr.getALVnOsuus()).sum();
   }

   public double getVerollinenHinta()
   {
      return laskurivit.stream().mapToDouble((lr) -> lr.getVerollinenHinta()).sum();
   }

   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public List<Laskurivi> getLaskurivit()
   {
      return laskurivit;
   }

   public void setLaskurivit(List<Laskurivi> laskurivit)
   {
      this.laskurivit = laskurivit;
   }

   public Date getMaksettu()
   {
      return maksettu;
   }

   public void setMaksettu(Date maksettu)
   {
      this.maksettu = maksettu;
   }

   public boolean isLaskuMaksettu()
   {
      return maksettu != null;
   }

   public Harrastaja getHarrastaja()
   {
      return harrastaja;
   }

   public void setHarrastaja(Harrastaja harrastaja)
   {
      this.harrastaja = harrastaja;
   }

   public Date getLuotu()
   {
      return luotu;
   }

   public void setLuotu(Date luotu)
   {
      this.luotu = luotu;
   }

   public Date getEr�p�iv�()
   {
      return er�p�iv�;
   }

   public void setEr�p�iv�(Date er�p�iv�)
   {
      this.er�p�iv� = er�p�iv�;
   }

   public TilausTila getTila()
   {
      return tila;
   }

   public void setTila(TilausTila tila)
   {
      this.tila = tila;
   }

   public void merkkaaMit�t�idyksi()
   {
      tila = TilausTila.X;
   }

   public void merkkaaMaksetuksi()
   {
      maksettu = new Date();
      tila = TilausTila.M;
   }

   public void merkkaaAvoimeksi()
   {
      maksettu = null;
      tila = TilausTila.A;
   }

   public long getP�ivi�My�h�ss�()
   {
      return Util.getP�ivi�V�liss�(er�p�iv�);
   }

   public long getP�ivi�Er�p�iv��n()
   {
      return Util.getP�ivi�V�liss�(er�p�iv�);
   }

   public BlobData getPdf()
   {
      return pdf;
   }

   public void setPdf(BlobData pdf)
   {
      this.pdf = pdf;
   }
}
