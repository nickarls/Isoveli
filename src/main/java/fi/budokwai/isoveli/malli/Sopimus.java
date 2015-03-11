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
         + "and sl.p‰‰ttyy < :nyt "
         + "and sl.p‰‰ttyy = (select max(sl2.p‰‰ttyy) from Sopimuslasku sl2 where sl2.sopimus=s)"),
      @NamedQuery(name = "laskuttamattomat_kymppikerrat", query = "select s from Sopimus s, Harrastaja h where s.harrastaja = h and s.tyyppi.treenikertoja='K' and s.treenikertojaJ‰ljell‰ = 0 and h.arkistoitu = 'E' and s.sopimuslaskut is not empty") })
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
   private int treenikertojaJ‰ljell‰;

   @Column(name = "maksuvali")
   private int maksuv‰li;

   @Type(type = "Kyll‰Ei")
   private boolean arkistoitu;

   private Date siirtomaksuVoimassa;

   public Sopimus(Harrastaja harrastaja, Sopimustyyppi sopimustyyppi)
   {
      this.harrastaja = harrastaja;
      tyyppi = sopimustyyppi;
      maksuv‰li = tyyppi.getOletusMaksuv‰li();
      treenikertojaTilattu = tyyppi.getOletusTreenikerrat();
      treenikertojaJ‰ljell‰ = treenikertojaTilattu;
   }

   public Sopimus()
   {
      // TODO Auto-generated constructor stub
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

   public int getMaksuv‰li()
   {
      return maksuv‰li;
   }

   public void setMaksuv‰li(int maksuv‰li)
   {
      this.maksuv‰li = maksuv‰li;
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
      if (harrastaja.isAlaik‰inen())
      {
         return harrastaja.getPerhe().getOsoite();
      } else
      {
         return harrastaja.getOsoite();
      }
   }

   public Henkilˆ getLaskutushenkilˆ()
   {
      if (harrastaja.isAlaik‰inen())
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
      tarkistukset.lis‰‰(tarkista());
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
      if (tyyppi.isKoeaika() && treenikertojaJ‰ljell‰ <= 0)
      {
         String viesti = String.format("Koeajan treenikertoja j‰ljell‰ %d", treenikertojaJ‰ljell‰);
         tulos.add(new Sopimustarkistus(viesti, false));
      }
      if (tyyppi.isTreenikertoja())
      {
         int perheKertoja = harrastaja.getPerhe() == null ? 0 : harrastaja.getPerhe().getPerheenTreenikerrat();
         int yhteens‰ = perheKertoja + treenikertojaJ‰ljell‰;
         if (yhteens‰ <= 0)
         {
            String viesti = String.format("Treenikertoja j‰ljell‰ %d", treenikertojaJ‰ljell‰);
            tulos.add(new Sopimustarkistus(viesti, false));
         }
      }
      if (tyyppi.getAlaik‰raja() > 0 && harrastaja.getIk‰() < tyyppi.getAlaik‰raja())
      {
         String viesti = String.format("%s: alaik‰raja on %d mutta ik‰‰ on %d", tyyppi.getNimi(),
            tyyppi.getAlaik‰raja(), harrastaja.getIk‰());
         tulos.add(new Sopimustarkistus(viesti, false));
      }
      if (tyyppi.getYl‰ik‰raja() > 0 && harrastaja.getIk‰() > tyyppi.getYl‰ik‰raja())
      {
         String viesti = String.format("%s: yl‰ik‰raja on %d mutta ik‰‰ on %d", tyyppi.getNimi(),
            tyyppi.getYl‰ik‰raja(), harrastaja.getIk‰());
         tulos.add(new Sopimustarkistus(viesti, false));
      }

      for (Sopimuslasku sopimuslasku : sopimuslaskut)
      {
         if (sopimuslasku.getLaskurivi().getLasku().isLaskuMyˆh‰ss‰())
         {
            String viesti = String.format("%s: lasku myˆh‰ss‰ %d p‰iv‰‰", tyyppi.getNimi(), sopimuslasku.getLaskurivi()
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
         .sorted((Sopimuslasku s1, Sopimuslasku s2) -> -1 * s1.getP‰‰ttyy().compareTo(s2.getP‰‰ttyy())).findFirst()
         .get().getP‰‰ttyy();
   }

   public boolean valmiiksiLaskutettu()
   {
      return getViimeksiLaskutettu() == null || DateUtil.onkoTulevaisuudessa(getViimeksiLaskutettu());
   }

   public void lis‰‰Treenikertoja()
   {
      treenikertojaJ‰ljell‰ += treenikertojaTilattu;
   }

   public boolean isArkistoitu()
   {
      return arkistoitu;
   }

   public void setArkistoitu(boolean arkistoitu)
   {
      this.arkistoitu = arkistoitu;
   }

   public void asetaP‰‰ttymisp‰iv‰()
   {
      if (tyyppi.getOletusKuukaudetVoimassa() > 0)
      {
         umpeutuu = DateUtil.kuukausienP‰‰st‰(tyyppi.getOletusKuukaudetVoimassa());
      } else
      {
         umpeutuu = null;
      }
      if (tyyppi.getYl‰ik‰raja() > 0 && !tyyppi.isAlkeiskurssi())
      {
         umpeutuu = DateUtil.vuosienP‰‰st‰(harrastaja.getSyntynyt(), tyyppi.getYl‰ik‰raja());
      }
   }

   public void k‰yt‰Treenikerta()
   {
      treenikertojaJ‰ljell‰--;
   }

   public Laskutuskausi getLaskutuskausi()
   {
      Jakso kausi = getKausirajat();
      int kausikuukausia = getKausikuukaudet(kausi);
      Jakso tauko = kausi.getP‰‰llekk‰isyys(harrastaja.getTauko());
      long taukop‰ivi‰ = tauko.getP‰ivi‰V‰liss‰();
      return new Laskutuskausi(kausi, kausikuukausia, tauko, taukop‰ivi‰);
   }

   private Jakso getKausirajat()
   {
      Date alkaa = getKausiAlkaa();
      Date p‰‰ttyy = getKausiP‰‰ttyy(alkaa);
      return new Jakso(alkaa, p‰‰ttyy);
   }

   private int getKausikuukaudet(Jakso kausi)
   {
      if (tyyppi.isJ‰senmaksutyyppi())
      {
         return DateUtil.laskutusvuosiaV‰liss‰(kausi);
      } else if (tyyppi.isHarjoittelumaksutyyppi())
      {
         return DateUtil.laskutuskuukausiaV‰liss‰(kausi);
      } else if (tyyppi.isTreenikertoja())
      {
         return tyyppi.getOletusTreenikerrat();
      } else
      {
         return 1;
      }
   }

   private Date getKausiP‰‰ttyy(Date alkaa)
   {
      LocalDate loppu = DateUtil.Date2LocalDate(alkaa);
      if (tyyppi.isJ‰senmaksutyyppi())
      {
         return DateUtil.LocalDate2Date(DateUtil.vuodenViimeinenP‰iv‰());
      }
      if (tyyppi.isTreenikertoja())
      {
         return DateUtil.t‰n‰‰nDate();
      }
      if (tyyppi.isAlkeiskurssi())
      {
         return DateUtil.LocalDate2Date(DateUtil.kuukausienP‰‰st‰(alkaa, tyyppi.getOletusKuukaudetVoimassa()));
      }
      while (loppu.isBefore(DateUtil.t‰n‰‰n()) || DateUtil.samat(alkaa, loppu))
      {
         loppu = loppu.plus(maksuv‰li, ChronoUnit.MONTHS);
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
      if (laskutuskausi.isTaukop‰ivi‰())
      {
         laskurivit.add(Laskurivi.taukohyvitys(laskutuskausi, this));
      }
      return laskurivit;
   }

   public boolean isTallennettu()
   {
      return id > 0;
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

   public int getTreenikertojaJ‰ljell‰()
   {
      return treenikertojaJ‰ljell‰;
   }

   public void setTreenikertojaJ‰ljell‰(int treenikertojaJ‰ljell‰)
   {
      this.treenikertojaJ‰ljell‰ = treenikertojaJ‰ljell‰;
   }

}
