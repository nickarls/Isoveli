package fi.budokwai.isoveli.malli;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

import fi.budokwai.isoveli.util.Util;

@Entity
@Table(name = "sopimus")
@NamedQueries(
{
      @NamedQuery(name = "uudet_sopimukset", query = "select s from Sopimus s, Harrastaja h "
         + "where s.harrastaja=h and s.sopimuslaskut is empty " + "and s.tyyppi.laskutettava='K' and h.arkistoitu='E' "
         + "and not exists (select 1 from Sopimus s2 where s2.tyyppi.vapautus='K' and s2.harrastaja=h)"),
      @NamedQuery(name = "laskuttamattomat_sopimukset", query = "select s from Sopimus s, Harrastaja h, Sopimuslasku sl "
         + "where s.harrastaja=h and sl.sopimus=s "
         + "and s.tyyppi.laskutettava='K' and h.arkistoitu='E' "
         + "and not exists (select 1 from Sopimus s2 where s2.tyyppi.vapautus='K' and s2.harrastaja=h) "
         + "and sl.päättyy < :nyt "
         + "and sl.päättyy = (select max(sl2.päättyy) from Sopimuslasku sl2 where sl2.sopimus=s)"),
      @NamedQuery(name = "laskuttamattomat_kymppikerrat", query = "select s from Sopimus s, Harrastaja h where s.harrastaja = h and s.tyyppi.treenikertoja='K' and s.treenikertoja = 0 and h.arkistoitu = 'E' and s.sopimuslaskut is not empty") })
public class Sopimus
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @ManyToOne(optional = false)
   @JoinColumn(name = "harrastaja")
   private Harrastaja harrastaja;

   @OneToOne(optional = false)
   @JoinColumn(name = "tyyppi")
   @NotNull
   private Sopimustyyppi tyyppi;

   @OneToMany(mappedBy = "sopimus")
   private List<Sopimuslasku> sopimuslaskut = new ArrayList<Sopimuslasku>();

   @Temporal(TemporalType.DATE)
   private Date umpeutuu;

   @Temporal(TemporalType.DATE)
   private Date luotu = new Date();

   private int treenikertoja;

   @Column(name = "maksuvali")
   private int maksuväli;

   @Type(type = "KylläEi")
   private boolean arkistoitu;

   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public Harrastaja getHarrastaja()
   {
      return harrastaja;
   }

   public void setHarrastaja(Harrastaja harrastaja)
   {
      this.harrastaja = harrastaja;
   }

   public Date getUmpeutuu()
   {
      return umpeutuu;
   }

   public void setUmpeutuu(Date umpeutuu)
   {
      this.umpeutuu = umpeutuu;
   }

   public int getTreenikertoja()
   {
      return treenikertoja;
   }

   public void setTreenikertoja(int treenikertoja)
   {
      this.treenikertoja = treenikertoja;
   }

   public int getMaksuväli()
   {
      return maksuväli;
   }

   public void setMaksuväli(int maksuväli)
   {
      this.maksuväli = maksuväli;
   }

   public Sopimustyyppi getTyyppi()
   {
      return tyyppi;
   }

   public void setTyyppi(Sopimustyyppi tyyppi)
   {
      this.tyyppi = tyyppi;
   }

   public boolean isPoistettavissa()
   {
      return id > 0 && sopimuslaskut.isEmpty();
   }

   @Override
   public int hashCode()
   {
      return Integer.valueOf(id).hashCode();
   }

   @Override
   public boolean equals(Object toinen)
   {
      if (!(toinen instanceof Sopimus))
      {
         return false;
      }
      Sopimus toinenSopimus = (Sopimus) toinen;
      return id == toinenSopimus.getId();
   }

   public boolean isVoimassa()
   {
      if (umpeutuu == null)
      {
         return true;
      }
      return umpeutuu.after(new Date());
   }

   public String getTuotenimi()
   {
      return String.format("%s (%s)", tyyppi.getNimi(), harrastaja.getEtunimi());
   }

   public List<Sopimuslasku> getSopimuslaskut()
   {
      return sopimuslaskut;
   }

   public void setSopimuslaskut(List<Sopimuslasku> sopimuslaskut)
   {
      this.sopimuslaskut = sopimuslaskut;
   }

   public Osoite getLaskutusosoite()
   {
      if (harrastaja.isAlaikäinen())
      {
         return harrastaja.getPerhe().getOsoite();
      } else
      {
         return harrastaja.getOsoite();
      }
   }

   public Henkilö getLaskutushenkilö()
   {
      if (harrastaja.isAlaikäinen())
      {
         return harrastaja.getHuoltaja();
      } else
      {
         return harrastaja;
      }
   }

   public boolean isSopimusOK()
   {
      Sopimustarkistukset tarkistukset = new Sopimustarkistukset();
      tarkistukset.lisää(tarkista());
      return tarkistukset.isOK();
   }

   public List<Sopimustarkistus> tarkista()
   {
      List<Sopimustarkistus> tulos = new ArrayList<Sopimustarkistus>();
      if (!isVoimassa())
      {
         LocalDate pvm = Util.date2LocalDate(umpeutuu);
         String viesti = String.format("%s: sopimus umpeutui %s", tyyppi.getNimi(),
            pvm.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
         tulos.add(new Sopimustarkistus(viesti, false));
      }
      if ((tyyppi.isKoeaika() || tyyppi.isTreenikertoja()) && treenikertoja <= 0)
      {
         String viesti = String.format("Treenikertoja jäljellä %d", treenikertoja);
         tulos.add(new Sopimustarkistus(viesti, false));
      }
      if (tyyppi.getAlaikäraja() > 0 && harrastaja.getIkä() < tyyppi.getAlaikäraja())
      {
         String viesti = String.format("%s: alaikäraja on %d mutta ikää on %d", tyyppi.getNimi(),
            tyyppi.getAlaikäraja(), harrastaja.getIkä());
         tulos.add(new Sopimustarkistus(viesti, false));
      }
      if (tyyppi.getYläikäraja() > 0 && harrastaja.getIkä() > tyyppi.getYläikäraja())
      {
         String viesti = String.format("%s: yläikäraja on %d mutta ikää on %d", tyyppi.getNimi(),
            tyyppi.getYläikäraja(), harrastaja.getIkä());
         tulos.add(new Sopimustarkistus(viesti, false));
      }

      for (Sopimuslasku sopimuslasku : sopimuslaskut)
      {
         if (sopimuslasku.getLaskurivi().getLasku().isLaskuMyöhässä())
         {
            String viesti = String.format("%s: lasku myöhässä %d päivää", tyyppi.getNimi(), sopimuslasku.getLaskurivi()
               .getLasku().getMaksuaikaa());
            tulos.add(new Sopimustarkistus(viesti, false));
         }
      }
      return tulos;
   }

   public Date getLuotu()
   {
      return luotu;
   }

   public void setLuotu(Date luotu)
   {
      this.luotu = luotu;
   }

   public Date getViimeksiLaskutettu()
   {
      if (sopimuslaskut.isEmpty())
      {
         return null;
      }
      return sopimuslaskut.stream()
         .sorted((Sopimuslasku s1, Sopimuslasku s2) -> -1 * s1.getPäättyy().compareTo(s2.getPäättyy())).findFirst()
         .get().getPäättyy();
   }

   public boolean valmiiksiLaskutettu()
   {
      LocalDate check = Util.date2LocalDate(getViimeksiLaskutettu());
      return check.isAfter(Util.tänäänLD());
   }

   public void lisääTreenikertoja()
   {
      treenikertoja += tyyppi.getOletusTreenikerrat();
   }

   public boolean isArkistoitu()
   {
      return arkistoitu;
   }

   public void setArkistoitu(boolean arkistoitu)
   {
      this.arkistoitu = arkistoitu;
   }

   public void asetaPäättymispäivä()
   {
      if (tyyppi.getOletusKuukaudetVoimassa() > 0)
      {
         umpeutuu = Date.from(LocalDate.now().plus(tyyppi.getOletusKuukaudetVoimassa(), ChronoUnit.MONTHS)
            .atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
      } else
      {
         umpeutuu = null;
      }
      if (tyyppi.getYläikäraja() > 0)
      {
         umpeutuu = Date.from(harrastaja.getSyntynyt().toInstant().atZone(ZoneId.systemDefault()).plus(tyyppi.getYläikäraja(), ChronoUnit.YEARS).toInstant());
      }
   }
}
