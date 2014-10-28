package fi.budokwai.isoveli.malli;

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
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import fi.budokwai.isoveli.util.Util;

@Entity
@NamedQuery(name = "laskut", query = "select l from Lasku l order by l.luotu asc")
public class Lasku
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @OneToMany(mappedBy = "lasku", cascade = CascadeType.PERSIST, orphanRemoval = true)
   private List<Laskurivi> laskurivit = new ArrayList<Laskurivi>();

   @Enumerated(EnumType.STRING)
   private TilausTila tila = TilausTila.A;

   @Temporal(TemporalType.DATE)
   @Column(name = "erapaiva")
   private Date eräpäivä;

   @Temporal(TemporalType.DATE)
   private Date maksettu;

   @OneToOne(optional = true)
   @JoinColumn(name = "henkilo")
   private Henkilö henkilö;

   @Temporal(TemporalType.TIMESTAMP)
   private Date luotu = new Date();

   @OneToOne(cascade = CascadeType.ALL, optional = true, orphanRemoval = true)
   @JoinColumn(name = "pdf")
   private BlobData pdf;

   public Lasku()
   {
   }

   public Lasku(Henkilö henkilö)
   {
      this.henkilö = henkilö;
      eräpäivä = Util.päivienPäästä(14);
   }

   public void lisääRivi(Laskurivi laskurivi)
   {
      laskurivi.setLasku(this);
      laskurivit.add(laskurivi);
      laskurivi.setRivinumero(laskurivit.size());
   }

   public int getLaskurivejä()
   {
      return laskurivit.size();
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

   public Date getLuotu()
   {
      return luotu;
   }

   public void setLuotu(Date luotu)
   {
      this.luotu = luotu;
   }

   public Date getEräpäivä()
   {
      return eräpäivä;
   }

   public void setEräpäivä(Date eräpäivä)
   {
      this.eräpäivä = eräpäivä;
   }

   public TilausTila getTila()
   {
      return tila;
   }

   public void setTila(TilausTila tila)
   {
      this.tila = tila;
   }

   public void merkkaaMitätöidyksi()
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

   public long getMaksuaikaa()
   {
      if (maksettu != null)
      {
         return 0;
      }
      return Util.getPäiviäVälissä(eräpäivä);
   }

   public BlobData getPdf()
   {
      return pdf;
   }

   public void setPdf(BlobData pdf)
   {
      this.pdf = pdf;
   }

   public Henkilö getHenkilö()
   {
      return henkilö;
   }

   public void setHenkilö(Henkilö henkilö)
   {
      this.henkilö = henkilö;
   }

   public boolean isLaskuMyöhässä()
   {
      return getMaksuaikaa() < 0;
   }
}
