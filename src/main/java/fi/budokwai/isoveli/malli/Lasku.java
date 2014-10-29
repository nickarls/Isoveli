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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import fi.budokwai.isoveli.util.Util;

@Entity
@NamedQueries(
{ @NamedQuery(name = "laskut", query = "select l from Lasku l order by l.luotu asc"),
      @NamedQuery(name = "laskuttamattomat_laskut", query = "select l from Lasku l where l.laskutettu='E'") })
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
   private Date er�p�iv�;

   @Temporal(TemporalType.DATE)
   private Date maksettu;

   @OneToOne(optional = true)
   @JoinColumn(name = "henkilo")
   private Henkil� henkil�;

   @Temporal(TemporalType.TIMESTAMP)
   private Date luotu = new Date();

   @OneToOne(cascade = CascadeType.ALL, optional = true, orphanRemoval = true)
   @JoinColumn(name = "pdf")
   private BlobData pdf;

   @Type(type = "Kyll�Ei")
   private boolean laskutettu;

   public Lasku()
   {
   }

   public Lasku(Henkil� henkil�)
   {
      this.henkil� = henkil�;
      er�p�iv� = Util.p�ivienP��st�(14);
   }

   public void lis��Rivi(Laskurivi laskurivi)
   {
      laskurivi.setLasku(this);
      laskurivit.add(laskurivi);
      laskurivi.setRivinumero(laskurivit.size());
   }

   public int getLaskurivej�()
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

   public long getMaksuaikaa()
   {
      if (maksettu != null)
      {
         return 0;
      }
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

   public Henkil� getHenkil�()
   {
      return henkil�;
   }

   public void setHenkil�(Henkil� henkil�)
   {
      this.henkil� = henkil�;
   }

   public boolean isLaskuMy�h�ss�()
   {
      return getMaksuaikaa() < 0;
   }

   public boolean isLaskutettu()
   {
      return laskutettu;
   }

   public void setLaskutettu(boolean laskutettu)
   {
      this.laskutettu = laskutettu;
   }
}
