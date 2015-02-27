package fi.budokwai.isoveli.malli;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import fi.budokwai.isoveli.util.DateUtil;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "lasku")
@NamedQueries(
{ @NamedQuery(name = "laskut", query = "select l from Lasku l order by l.luotu asc"),
      @NamedQuery(name = "laskuttamattomat_laskut", query = "select l from Lasku l where l.laskutettu='E'") })
public class Lasku
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @OneToMany(mappedBy = "lasku", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<Laskurivi> laskurivit = new ArrayList<Laskurivi>();

   @Enumerated(EnumType.STRING)
   private TilausTila tila = TilausTila.M;

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

   @Type(type = "KylläEi")
   private boolean laskutettu;

   @Size(max = 50)
   private String viitenumero;

   public Lasku()
   {
   }

   public Lasku(Henkilö henkilö)
   {
      this.henkilö = henkilö;
      eräpäivä = DateUtil.päivienPäästä(14);
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
      tila = TilausTila.K;
   }

   public void merkkaaMuodostetuksi()
   {
      maksettu = null;
      tila = TilausTila.M;
   }

   public void merkkaaLähetetyksi()
   {
      maksettu = null;
      tila = TilausTila.L;
   }

   public long getMaksuaikaa()
   {
      if (maksettu != null)
      {
         return 0;
      }
      return DateUtil.getPäiviäVälissä(eräpäivä);
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

   public boolean isLaskutettu()
   {
      return laskutettu;
   }

   public void setLaskutettu(boolean laskutettu)
   {
      this.laskutettu = laskutettu;
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
      List<Laskurivi> harjoitusmaksut = laskurivit
         .stream()
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
      Set<Harrastaja> käytetytAlennukset = new HashSet<>();
      for (Laskurivi laskurivi : harjoitusmaksut)
      {
         Harrastaja harrastaja = laskurivi.getSopimuslasku().getSopimus().getHarrastaja();
         if (käytetytAlennukset.contains(harrastaja))
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
         käytetytAlennukset.add(harrastaja);
      }
      if (alennus > 0)
      {
         Laskurivi perhealennus = Laskurivi.perhealennus(alennus, infotieto);
         lisääRivi(perhealennus);
      }
   }

   public void lisääRivit(List<Laskurivi> laskurivit)
   {
      laskurivit.forEach(lr -> lisääRivi(lr));
   }

}
