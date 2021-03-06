package fi.budokwai.isoveli.malli;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;

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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import fi.budokwai.isoveli.util.DateUtil;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "lasku")
@NamedQueries(
{ @NamedQuery(name = "l�hett�m�tt�m�t_laskut", query = "select l from Lasku l where l.l�hetetty=null and l.tila='M' order by l.muodostettu asc"),
      @NamedQuery(name = "maksamattomat_laskut", query = "select l from Lasku l where l.maksettu=null and l.tila='L' order by l.l�hetetty asc"),
      @NamedQuery(name = "maksetut_laskut", query = "select l from Lasku l where l.maksettu is not null and l.tila='K' order by l.maksettu asc"),
      @NamedQuery(name = "lasku_viitenumero", query = "select l from Lasku l where l.viitenumero=:viitenumero") })
public class Lasku
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @OneToMany(mappedBy = "lasku", cascade =
   { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
   @OrderBy(value = "rivinumero")
   private List<Laskurivi> laskurivit = new ArrayList<Laskurivi>();

   @Enumerated(EnumType.STRING)
   private LaskuTila tila = LaskuTila.M;

   @Temporal(TemporalType.DATE)
   @Column(name = "erapaiva")
   private Date er�p�iv�;

   @Temporal(TemporalType.DATE)
   private Date maksettu;

   @OneToOne
   @JoinColumn(name = "henkilo")
   private Henkil� henkil�;

   @Temporal(TemporalType.TIMESTAMP)
   private Date muodostettu = new Date();

   @Column(name = "lahetetty")
   @Temporal(TemporalType.TIMESTAMP)
   private Date l�hetetty;

   @OneToOne(cascade =
   { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, orphanRemoval = true)
   @JoinColumn(name = "pdf")
   private BlobData pdf;

   @Size(max = 50)
   private String viitenumero;

   public Lasku()
   {
   }

   public Lasku(Henkil� henkil�)
   {
      this.henkil� = henkil�;
      er�p�iv� = DateUtil.p�ivienP��st�(14);
   }

   public void lis��Rivi(Laskurivi laskurivi)
   {
      laskurivi.setLasku(this);
      OptionalInt rivinumero = laskurivit.stream().mapToInt(l -> l.getRivinumero()).max();
      if (rivinumero.isPresent())
      {
         laskurivi.setRivinumero(rivinumero.getAsInt() + 1);
      } else
      {
         laskurivi.setRivinumero(1);
      }
      laskurivit.add(laskurivi);
   }

   public int getLaskurivej�()
   {
      return laskurivit.size();
   }

   public double getYhteishinta()
   {
      return laskurivit.stream().mapToDouble((lr) -> lr.getRivihinta()).sum();
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

   public Date getEr�p�iv�()
   {
      return er�p�iv�;
   }

   public void setEr�p�iv�(Date er�p�iv�)
   {
      this.er�p�iv� = er�p�iv�;
   }

   public LaskuTila getTila()
   {
      return tila;
   }

   public void setTila(LaskuTila tila)
   {
      this.tila = tila;
   }

   public void merkkaaMit�t�idyksi()
   {
      tila = LaskuTila.X;
   }

   public void merkkaaMaksetuksi()
   {
      maksettu = new Date();
      tila = LaskuTila.K;
   }

   public void merkkaaMuodostetuksi()
   {
      maksettu = null;
      tila = LaskuTila.M;
   }

   public void merkkaaL�hetetyksi()
   {
      maksettu = null;
      tila = LaskuTila.L;
   }

   public long getMaksuaikaa()
   {
      if (maksettu != null)
      {
         return 0;
      }
      return DateUtil.getP�ivi�V�liss�(er�p�iv�);
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

   public String getViitenumero()
   {
      return viitenumero;
   }

   public void setViitenumero(String viitenumero)
   {
      this.viitenumero = viitenumero;
   }

   public void laskeViitenumero()
   {
      DateFormat sdf = new SimpleDateFormat("yyyy");
      String viitenumero = String.format("%s%d", sdf.format(new Date()), id);
      this.viitenumero = String.format("%s%s", viitenumero, viitenumeronTarkistussumma(viitenumero));
   }

   public static String viitenumeronTarkistussumma(String runko)
   {
      String numerot = runko.replace(" ", "");
      if (numerot.length() <= 0)
         return "";
      int[] kertoimet =
      { 7, 3, 1 };
      int tarkistussumma = 0;
      for (int i = numerot.length() - 1, j = 0; i >= 0; i--, j++)
      {
         tarkistussumma += kertoimet[j % 3] * Character.digit(numerot.charAt(i), 10);
      }
      return "" + (10 - tarkistussumma % 10) % 10;
   }

   public void laskePerhealennukset()
   {
      List<Laskurivi> harjoitusmaksut = laskurivit.stream()
         .filter(
            l -> l.getSopimuslasku() != null && l.getSopimuslasku().getSopimus().getTyyppi().isHarjoittelumaksutyyppi())
         .sorted((lr1, lr2) -> Double.valueOf(lr1.getRivihinta()).compareTo(Double.valueOf(lr2.getRivihinta())))
         .collect(Collectors.toList());
      if (harjoitusmaksut.size() < 2)
      {
         return;
      }
      float kerroin = 0;
      float alennus = 0;
      String infotieto = "";
      Set<Harrastaja> k�ytetytAlennukset = new HashSet<>();
      for (Laskurivi laskurivi : harjoitusmaksut)
      {
         Harrastaja harrastaja = laskurivi.getSopimuslasku().getSopimus().getHarrastaja();
         if (k�ytetytAlennukset.contains(harrastaja))
         {
            continue;
         }
         alennus += (kerroin * laskurivi.getRivihinta());
         if (kerroin > 0)
         {
            if (infotieto.length() > 0)
            {
               infotieto += ", ";
            }
            infotieto += String.format("%s (%d%%)", harrastaja.getEtunimi(), (int) (kerroin * 100));
         }
         kerroin += 0.1;
         k�ytetytAlennukset.add(harrastaja);
      }
      if (alennus > 0)
      {
         Laskurivi perhealennus = Laskurivi.perhealennus(alennus, infotieto);
         lis��Rivi(perhealennus);
      }
   }

   public void lis��Rivit(List<Laskurivi> laskurivit)
   {
      laskurivit.forEach(lr -> lis��Rivi(lr));
   }

   @Override
   public String toString()
   {
      return String.format("%s: %d rivi�, EUR%f", henkil�.getNimi(), laskurivit.size(), getYhteishinta());
   }

   public Date getL�hetetty()
   {
      return l�hetetty;
   }

   public void setL�hetetty(Date l�hetetty)
   {
      this.l�hetetty = l�hetetty;
   }

   public Date getMuodostettu()
   {
      return muodostettu;
   }

   public void setMuodostettu(Date muodostettu)
   {
      this.muodostettu = muodostettu;
   }

}
