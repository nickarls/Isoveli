package fi.budokwai.isoveli.malli;

import java.time.LocalDate;
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

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import fi.budokwai.isoveli.util.DateUtil;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "sopimus")
@NamedQueries(
{
      @NamedQuery(name = "uudet_sopimukset", query = "select s from Sopimus s, Harrastaja h "
         + "where s.harrastaja=h and s.sopimuslaskut is empty " + "and s.tyyppi.laskutettava='K' and h.arkistoitu='E' "
         + "and (siirtomaksuvoimassa is null or siirtomaksuvoimassa < :nyt) " + " and s.tyyppi.treenikertoja = 'E' "
         + "and not exists (select 1 from Sopimus s2 where s2.tyyppi.vapautus='K' and s2.harrastaja=h)"),
      @NamedQuery(name = "laskuttamattomat_sopimukset", query = "select s from Sopimus s, Harrastaja h, Sopimuslasku sl "
         + "where s.harrastaja=h and sl.sopimus=s "
         + "and s.tyyppi.laskutettava='K' and h.arkistoitu='E' "
         + "and not exists (select 1 from Sopimus s2 where s2.tyyppi.vapautus='K' and s2.harrastaja=h) "
         + "and sl.päättyy < :nyt "
         + "and sl.päättyy = (select max(sl2.päättyy) from Sopimuslasku sl2 where sl2.sopimus=s)"),
      @NamedQuery(name = "laskuttamattomat_kymppikerrat", query = ""
         + "select s "
         + "from Sopimus s, Harrastaja h "
         + "where s.harrastaja = h and s.tyyppi.treenikertoja='K' and h.arkistoitu = 'E' and " 
         + "s.treenikertojaJäljellä = 0"
         ),
      @NamedQuery(name="sopimukset_tyypillä", query="select s from Sopimus s where s.tyyppi=:tyyppi")
      })
public class Sopimus
{
   public static final Sopimus EI_OOTA = new Sopimus();

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @ManyToOne
   @JoinColumn(name = "harrastaja")
   private Harrastaja harrastaja;

   @OneToOne
   @JoinColumn(name = "tyyppi")
   @NotNull
   private Sopimustyyppi tyyppi;

   @OneToMany(mappedBy = "sopimus")
   private List<Sopimuslasku> sopimuslaskut = new ArrayList<Sopimuslasku>();

   @Temporal(TemporalType.DATE)
   private Date umpeutuu;

   @Temporal(TemporalType.DATE)
   private Date luotu = new Date();

   private int treenikertojaTilattu;

   @Column(name = "treenikertojajaljella")
   private int treenikertojaJäljellä;

   @Column(name = "maksuvali")
   private int maksuväli;

   @Type(type = "KylläEi")
   private boolean arkistoitu;

   private Date siirtomaksuVoimassa;

   public Sopimus(Harrastaja harrastaja, Sopimustyyppi sopimustyyppi)
   {
      this.harrastaja = harrastaja;
      tyyppi = sopimustyyppi;
      maksuväli = tyyppi.getOletusMaksuväli();
      treenikertojaTilattu = tyyppi.getOletusTreenikerrat();
      treenikertojaJäljellä = treenikertojaTilattu;
   }

   public Sopimus()
   {
   }

   public Sopimus(Sopimustyyppi sopimustyyppi)
   {
      tyyppi = sopimustyyppi;
   }

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
         String pvm = DateUtil.formatoi(umpeutuu);
         String viesti = String.format("%s: sopimus umpeutui %s", tyyppi.getNimi(), pvm);
         tulos.add(new Sopimustarkistus(viesti, false));
      }
      if (tyyppi.isKoeaika() && treenikertojaJäljellä <= 0)
      {
         String viesti = String.format("Koeajan treenikertoja jäljellä %d", treenikertojaJäljellä);
         tulos.add(new Sopimustarkistus(viesti, false));
      }
      if (tyyppi.isTreenikertoja())
      {
         int perheKertoja = harrastaja.getPerhe() == null ? 0 : harrastaja.getPerhe().getPerheenTreenikerrat();
         int yhteensä = perheKertoja + treenikertojaJäljellä;
         if (yhteensä <= 0)
         {
            String viesti = String.format("Treenikertoja jäljellä %d", treenikertojaJäljellä);
            tulos.add(new Sopimustarkistus(viesti, false));
         }
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
               .getLasku().getMaksuaikaa()
               * -1);
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
      return getViimeksiLaskutettu() == null || DateUtil.onkoTulevaisuudessa(getViimeksiLaskutettu());
   }

   public void lisääTreenikertoja()
   {
      treenikertojaJäljellä += treenikertojaTilattu;
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
         umpeutuu = DateUtil.kuukausienPäästä(tyyppi.getOletusKuukaudetVoimassa());
      } else
      {
         umpeutuu = null;
      }
      if (tyyppi.getYläikäraja() > 0 && !tyyppi.isAlkeiskurssi())
      {
         umpeutuu = DateUtil.vuosienPäästä(harrastaja.getSyntynyt(), tyyppi.getYläikäraja());
      }
   }

   public void käytäTreenikerta()
   {
      treenikertojaJäljellä--;
   }

   public Laskutuskausi getLaskutuskausi()
   {
      Jakso kausi = getKausirajat();
      int kausikuukausia = getKausikuukaudet(kausi);
      Jakso tauko = kausi.getPäällekkäisyys(harrastaja.getTauko());
      long taukopäiviä = tauko.getPäiviäVälissä();
      return new Laskutuskausi(kausi, kausikuukausia, tauko, taukopäiviä);
   }

   private Jakso getKausirajat()
   {
      Date alkaa = getKausiAlkaa();
      Date päättyy = getKausiPäättyy(alkaa);
      return new Jakso(alkaa, päättyy);
   }

   private int getKausikuukaudet(Jakso kausi)
   {
      if (tyyppi.isJäsenmaksutyyppi())
      {
         return DateUtil.laskutusvuosiaVälissä(kausi);
      } else if (tyyppi.isHarjoittelumaksutyyppi())
      {
         return DateUtil.laskutuskuukausiaVälissä(kausi);
      } else if (tyyppi.isTreenikertoja())
      {
         return tyyppi.getOletusTreenikerrat();
      } else
      {
         return 1;
      }
   }

   private Date getKausiPäättyy(Date alkaa)
   {
      LocalDate loppu = DateUtil.Date2LocalDate(alkaa);
      if (tyyppi.isJäsenmaksutyyppi())
      {
         return DateUtil.LocalDate2Date(DateUtil.vuodenViimeinenPäivä());
      }
      if (tyyppi.isTreenikertoja())
      {
         return DateUtil.tänäänDate();
      }
      if (tyyppi.isAlkeiskurssi())
      {
         return DateUtil.LocalDate2Date(DateUtil.kuukausienPäästä(alkaa, tyyppi.getOletusKuukaudetVoimassa()));
      }
      while (loppu.isBefore(DateUtil.tänään().plusDays(1)) )
      {
         loppu = loppu.plus(maksuväli, ChronoUnit.MONTHS);
      }
      if (umpeutuu != null && DateUtil.onkoAiemmin(umpeutuu, loppu))
      {
         return umpeutuu;
      }
      return DateUtil.LocalDate2Date(loppu);
   }

   private Date getKausiAlkaa()
   {
      Date viimeLaskutus = getViimeksiLaskutettu();
      return viimeLaskutus == null ? luotu : viimeLaskutus;
   }

   public List<Laskurivi> laskuta()
   {
      List<Laskurivi> laskurivit = new ArrayList<>();
      Laskutuskausi laskutuskausi = getLaskutuskausi();
      Sopimuslasku sopimuslasku = new Sopimuslasku(this, laskutuskausi);
      laskurivit.add(new Laskurivi(sopimuslasku, laskutuskausi));
      if (laskutuskausi.isTaukopäiviä())
      {
         laskurivit.add(Laskurivi.taukohyvitys(laskutuskausi, this));
      }
      return laskurivit;
   }

   @Override
   public String toString()
   {
      return String.format("%s:%s", harrastaja.getNimi(), tyyppi);
   }

   public Date getSiirtomaksuVoimassa()
   {
      return siirtomaksuVoimassa;
   }

   public void setSiirtomaksuVoimassa(Date siirtomaksuVoimassa)
   {
      this.siirtomaksuVoimassa = siirtomaksuVoimassa;
   }

   public int getTreenikertojaTilattu()
   {
      return treenikertojaTilattu;
   }

   public void setTreenikertojaTilattu(int treenikertojaTilattu)
   {
      this.treenikertojaTilattu = treenikertojaTilattu;
   }

   public int getTreenikertojaJäljellä()
   {
      return treenikertojaJäljellä;
   }

   public void setTreenikertojaJäljellä(int treenikertojaJäljellä)
   {
      this.treenikertojaJäljellä = treenikertojaJäljellä;
   }

   public boolean isTallentamaton()
   {
      return id == 0;
   }

}
