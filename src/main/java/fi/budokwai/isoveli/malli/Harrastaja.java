package fi.budokwai.isoveli.malli;

import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import javax.enterprise.inject.Typed;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SortComparator;
import org.hibernate.annotations.Type;

import fi.budokwai.isoveli.IsoveliPoikkeus;
import fi.budokwai.isoveli.malli.validointi.Taukotarkistus;
import fi.budokwai.isoveli.malli.validointi.UniikitSopimukset;
import fi.budokwai.isoveli.malli.validointi.UniikitVy�kokeet;
import fi.budokwai.isoveli.malli.validointi.Vy�koej�rjestys;
import fi.budokwai.isoveli.util.DateUtil;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "harrastaja")
@NamedQueries(
{ @NamedQuery(name = "kortti", query = "select h from Harrastaja h where h.j�sennumero=:kortti"),
      @NamedQuery(name = "harrastajat_roolissa", query = "select h from Harrastaja h join h.roolit r where r.nimi=:rooli order by h.sukunimi, h.etunimi"),
      @NamedQuery(name = "sama_syntym�p�iv�", query = "select h from Harrastaja h where h.syntynyt = :p�iv�"),
      @NamedQuery(name = "harrastajat", query = "select h from Harrastaja h where h.arkistoitu='E' order by h.sukunimi, h.etunimi"),
      @NamedQuery(name = "harrastajatArq", query = "select h from Harrastaja h order by h.sukunimi, h.etunimi") })
@Typed(
{})
@UniikitVy�kokeet
@UniikitSopimukset
@Vy�koej�rjestys
@Taukotarkistus
public class Harrastaja extends Henkil�
{
   private static final long serialVersionUID = 1L;

   @OneToOne(cascade =
   { CascadeType.PERSIST, CascadeType.MERGE })
   @JoinColumn(name = "huoltaja")
   private Henkil� huoltaja;

   @OneToMany(mappedBy = "harrastaja", cascade =
   { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
   @SortComparator(value = Vy�koeComparator.class)
   @Valid
   private List<Vy�koe> vy�kokeet = new ArrayList<Vy�koe>();

   @OneToMany(mappedBy = "harrastaja")
   @OrderBy("paiva desc")
   private List<Kisatulos> kisatulokset = new ArrayList<Kisatulos>();

   @OneToMany(mappedBy = "harrastaja", cascade =
   { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
   @OrderBy("umpeutuu desc")
   private List<Sopimus> sopimukset = new ArrayList<Sopimus>();

   @OneToMany(mappedBy = "harrastaja")
   @OrderBy("aikaleima desc")
   private List<Treenik�ynti> treenik�ynnit = new ArrayList<Treenik�ynti>();

   @Size(max = 20)
   @Column(name = "jasennumero")
   private String j�sennumero;

   @Size(max = 20)
   private String lisenssinumero;

   @Temporal(TemporalType.DATE)
   @NotNull
   @Past(message = "Ei voi olla tulevaisuudessa")
   private Date syntynyt;

   @NotNull
   @Enumerated(EnumType.STRING)
   private Sukupuoli sukupuoli;

   @Size(max = 50)
   private String ice;

   @Size(max = 1000)
   private String huomautus;

   @Column(name = "siirtotreeneja")
   private int siirtotreenej�;

   @Type(type = "Kyll�Ei")
   private boolean infotiskille;

   @Type(type = "Kyll�Ei")
   private boolean medialupa;

   private String koulutus;

   private String tulokset;

   @Embedded
   private Jakso tauko = new Jakso();

   public Harrastaja()
   {
   }

   @PostLoad
   public void init()
   {
      // if (tauko == null)
      // {
      // tauko = new Jakso();
      // }
   }

   public Henkil� getHuoltaja()
   {
      return huoltaja;
   }

   public void setHuoltaja(Henkil� huoltaja)
   {
      this.huoltaja = huoltaja;
   }

   public void setJ�sennumero(String j�sennumero)
   {
      this.j�sennumero = j�sennumero;
   }

   public String getLisenssinumero()
   {
      return lisenssinumero;
   }

   public void setLisenssinumero(String lisenssinumero)
   {
      this.lisenssinumero = lisenssinumero;
   }

   public Date getSyntynyt()
   {
      return syntynyt;
   }

   public void setSyntynyt(Date syntynyt)
   {
      this.syntynyt = syntynyt;
   }

   public Sukupuoli getSukupuoli()
   {
      return sukupuoli;
   }

   public void setSukupuoli(Sukupuoli sukupuoli)
   {
      this.sukupuoli = sukupuoli;
   }

   public long getIk�()
   {
      return ik�(syntynyt);
   }

   public static long ik�(Date p�iv�m��r�)
   {
      try
      {
         return DateUtil.ik�(p�iv�m��r�);
      } catch (Exception e)
      {
         return 0;
      }
   }

   public boolean isAlaik�inen()
   {
      return syntynyt == null ? false : ik�(syntynyt) < 18;
   }

   public List<Vy�koe> getVy�kokeet()
   {
      return vy�kokeet;
   }

   public Vy�koe getTuoreinVy�koe()
   {
      Optional<Vy�koe> vy�koe = vy�kokeet.stream()
         .max((v1, v2) -> Integer.compare(v1.getVy�arvo().getJ�rjestys(), v2.getVy�arvo().getJ�rjestys()));
      return vy�koe.isPresent() ? vy�koe.get() : Vy�koe.EI_OOTA;
   }

   public Vy�arvo getTuoreinVy�arvo()
   {
      Vy�koe vy�koe = getTuoreinVy�koe();
      return vy�koe == Vy�koe.EI_OOTA ? Vy�arvo.EI_OOTA : vy�koe.getVy�arvo();
   }

   public String getJ�sennumero()
   {
      return j�sennumero;
   }

   public void setVy�kokeet(List<Vy�koe> vy�kokeet)
   {
      this.vy�kokeet = vy�kokeet;
   }

   public List<Kisatulos> getKisatulokset()
   {
      return kisatulokset;
   }

   public void setKisatulokset(List<Kisatulos> kisatulokset)
   {
      this.kisatulokset = kisatulokset;
   }

   public List<Sopimus> getSopimukset()
   {
      return sopimukset;
   }

   public void setSopimukset(List<Sopimus> sopimukset)
   {
      this.sopimukset = sopimukset;
   }

   public List<Treenik�ynti> getTreenik�ynnit()
   {
      return treenik�ynnit;
   }

   public void setTreenik�ynnit(List<Treenik�ynti> treenik�ynnit)
   {
      this.treenik�ynnit = treenik�ynnit;
   }

   public static boolean alaik�inen(Date p�iv�m��r�)
   {
      return ik�(p�iv�m��r�) < 18;
   }

   @Override
   public String toString()
   {
      return getNimi();
   }

   public boolean isSopimuksetOK()
   {
      return getSopimusTarkistukset().isOK();
   }

   public Sopimustarkistukset getSopimusTarkistukset()
   {
      Sopimustarkistukset sopimustarkistuset = new Sopimustarkistukset();
      if (!l�ytyyk�J�senmaksu())
      {
         sopimustarkistuset.lis��(new Sopimustarkistus("J�senmaksu puuttuu", false));
      }
      if (!l�ytyyk�Harjoittelumaksu())
      {
         sopimustarkistuset.lis��(new Sopimustarkistus("Harjoittelumaksu puuttuu", false));
      }
      sopimukset.forEach(s -> {
         sopimustarkistuset.lis��(s.tarkista());
      });
      return sopimustarkistuset;
   }

   private boolean l�ytyyk�Harjoittelumaksu()
   {
      Optional<Sopimus> sopimus = sopimukset.stream().filter(s -> s.getTyyppi().isHarjoittelumaksu()).findFirst();
      int perheenTreenikerrat = perhe == null ? 0 : perhe.getPerheenTreenikerrat();
      return perheenTreenikerrat > 0 || sopimus.isPresent();
   }

   private boolean l�ytyyk�J�senmaksu()
   {
      Optional<Sopimus> sopimus = sopimukset.stream().filter(s -> s.getTyyppi().isJ�senmaksu()).findFirst();
      return sopimus.isPresent();
   }

   public boolean isMies()
   {
      return Sukupuoli.M == sukupuoli;
   }

   public boolean isNainen()
   {
      return Sukupuoli.N == sukupuoli;
   }

   public long getTreenej�ViimeVy�kokeesta()
   {
      Vy�koe vy�koe = getTuoreinVy�koe();
      if (vy�koe == Vy�koe.EI_OOTA)
      {
         return treenik�ynnit.size();
      }
      return treenik�ynnit.stream().filter(t -> t.getAikaleima().after(vy�koe.getP�iv�())).count();
   }

   public Period getAikaaViimeVy�kokeesta()
   {
      Vy�koe vy�koe = getTuoreinVy�koe();
      if (vy�koe == Vy�koe.EI_OOTA)
      {
         return Period.ZERO;
      }
      return DateUtil.aikav�li(vy�koe.getP�iv�());

   }

   @Override
   public int hashCode()
   {
      return Integer.valueOf(id).hashCode();
   }

   @Override
   public boolean equals(Object toinen)
   {
      if (!(toinen instanceof Harrastaja))
      {
         return false;
      }
      Harrastaja toinenHarrastaja = (Harrastaja) toinen;
      return id == toinenHarrastaja.getId();
   }

   public String getIce()
   {
      return ice;
   }

   public void setIce(String ice)
   {
      this.ice = ice;
   }

   public String getHuomautus()
   {
      return huomautus;
   }

   public void setHuomautus(String huomautus)
   {
      this.huomautus = huomautus;
   }

   public boolean isHarrastaja()
   {
      return true;
   }

   public boolean isTilap�inen()
   {
      return ".".equals(huomautus);
   }

   public void setTilap�inen(boolean tilap�inen)
   {
      huomautus = tilap�inen ? "." : null;
   }

   public void muutaTilap�iseksi()
   {
      huomautus = ".";
      syntynyt = new Date();
   }

   public void muutaVakioksi()
   {
      huomautus = null;
      syntynyt = null;
      sopimukset.clear();
   }

   public boolean isInfotiskille()
   {
      return infotiskille;
   }

   public void setInfotiskille(boolean infotiskille)
   {
      this.infotiskille = infotiskille;
   }

   public boolean isTauolla()
   {
      return getTauko().isNytV�liss�();
   }

   public Date getViimeisinTreeni()
   {
      Optional<Treenik�ynti> tuorein = treenik�ynnit.stream().sorted((tk1, tk2) -> {
         return tk1.getAikaleima().compareTo(tk2.getAikaleima());
      }).findFirst();
      return tuorein.isPresent() ? tuorein.get().getAikaleima() : null;
   }

   public int getTreenej�Yhteens�()
   {
      return treenik�ynnit.size() + siirtotreenej�;
   }

   private Sopimus getAktiivinenKertakortti()
   {
      Optional<Sopimus> omaSopimus = sopimukset.stream().filter(s -> s.getTyyppi().isTreenikertoja()).findFirst();
      if (omaSopimus.isPresent())
      {
         return omaSopimus.get();
      }
      if (perhe != null)
      {
         return perhe.getPerheenKertakortti();
      }
      return Sopimus.EI_OOTA;
   }

   public boolean isKertakorttiK�yt�ss�()
   {
      return getAktiivinenKertakortti() != null;
   }

   public int getTreenikertojaJ�ljell�()
   {
      Sopimus sopimus = getAktiivinenKertakortti();
      return sopimus == null ? 0 : sopimus.getTreenikertojaJ�ljell�();
   }

   public Sopimus getHarjoitteluoikeusSopimus()
   {
      Optional<Sopimus> harjoittelusopimukset = sopimukset.stream().filter(s -> s.isSopimusOK())
         .filter(s -> s.getTyyppi().isHarjoittelumaksu()).filter(s -> !s.getTyyppi().isTreenikertoja()).findFirst();
      if (harjoittelusopimukset.isPresent())
      {
         return harjoittelusopimukset.get();
      }
      return getAktiivinenKertakortti();
   }

   public Jakso getTauko()
   {
      if (tauko == null)
      {
         tauko = new Jakso();
      }
      return tauko;
   }

   @Override
   public void siivoa()
   {
      super.siivoa();
   }

   public void setTauko(Jakso tauko)
   {
      this.tauko = tauko;
   }

   public boolean l�ytyyJoSopimus(Sopimustyyppi sopimustyyppi)
   {
      Optional<Sopimus> vanha = sopimukset.stream().filter(s -> s.getTyyppi().getId() == sopimustyyppi.getId())
         .findFirst();
      return vanha.isPresent();
   }

   public void lis��Huomautus(String huomautus)
   {
      if (this.huomautus == null)
      {
         this.huomautus = huomautus;
      } else
      {
         this.huomautus = this.huomautus + "\\n" + huomautus;
      }
      infotiskille = true;
   }

   public Sopimus lis��Sopimus(Sopimus sopimus)
   {
      sopimus.setHarrastaja(this);
      sopimukset.add(sopimus);
      return sopimus;
   }

   public void lis��Vy�koe(Vy�koe vy�koe)
   {
      vy�koe.setHarrastaja(this);
      vy�kokeet.add(vy�koe);
      Collections.sort(vy�kokeet, new Comparator<Vy�koe>()
      {

         @Override
         public int compare(Vy�koe v1, Vy�koe v2)
         {
            return Integer.valueOf(v1.getVy�arvo().getJ�rjestys())
               .compareTo(Integer.valueOf(v2.getVy�arvo().getJ�rjestys()));
         }
      });
   }

   public void poistotarkistus()
   {
      tarkistaVy�koek�ytt�();
      tarkistaSopimusk�ytt�();
      tarkistaTreenik�yntik�ytt�();
      tarkistaHuoltajak�ytt�();
   }

   private void tarkistaHuoltajak�ytt�()
   {
      if (perhe == null || perhe.getHuollettavat(this).isEmpty())
      {
         return;
      }
      StringJoiner stringJoiner = new StringJoiner(", ");
      perhe.getHuollettavat(this).forEach(h -> {
         stringJoiner.add(h.getEtunimi());
      });
      String viesti = String.format("Harrastaja on huoltaja ja h�nt� ei voi poistaa (%dkpl: %s...)",
         perhe.getHuollettavat(this).size(), stringJoiner.toString());
      throw new IsoveliPoikkeus(viesti);
   }

   private void tarkistaTreenik�yntik�ytt�()
   {
      if (treenik�ynnit.isEmpty())
      {
         return;
      }
      StringJoiner stringJoiner = new StringJoiner(", ");
      treenik�ynnit.forEach(t -> {
         stringJoiner.add(t.getAikaleimaString());
      });
      String viesti = String.format("Harrastajalla on treenik�yntej� ja h�nt� ei voi poistaa (%dkpl: %s...)",
         treenik�ynnit.size(), stringJoiner.toString());
      throw new IsoveliPoikkeus(viesti);
   }

   private void tarkistaSopimusk�ytt�()
   {
      if (sopimukset.isEmpty() || (sopimukset.size() == 1 && sopimukset.iterator().next().getTyyppi().isKoeaika()))
      {
         return;
      }
      StringJoiner stringJoiner = new StringJoiner(", ");
      sopimukset.forEach(h -> {
         stringJoiner.add(h.getTyyppi().getNimi());
      });
      String viesti = String.format("Harrastajalla on sopimuksia ja h�nt� ei voi poistaa (%dkpl: %s...)",
         sopimukset.size(), stringJoiner.toString());
      throw new IsoveliPoikkeus(viesti);
   }

   private void tarkistaVy�koek�ytt�()
   {
      if (vy�kokeet.isEmpty())
      {
         return;
      }
      StringJoiner stringJoiner = new StringJoiner(", ");
      vy�kokeet.forEach(v -> {
         stringJoiner.add(v.getVy�arvo().getNimi());
      });
      String viesti = String.format("Harrastajalla on vy�kokeita ja h�nt� ei voi poistaa (%dkpl: %s...)",
         vy�kokeet.size(), stringJoiner.toString());
      throw new IsoveliPoikkeus(viesti);
   }

   public void lis��Treenik�ynti(Treenik�ynti treenik�ynti)
   {
      treenik�ynti.setHarrastaja(this);
      treenik�ynnit.add(treenik�ynti);
   }

   public int getSiirtotreenej�()
   {
      return siirtotreenej�;
   }

   public void setSiirtotreenej�(int siirtotreenej�)
   {
      this.siirtotreenej� = siirtotreenej�;
   }

   public void lis��Huoltaja()
   {
      huoltaja = new Henkil�();
      huoltaja.setSukunimi(sukunimi);
      huoltaja.setEtunimi("Huoltaja");
      perhe.lis��Perheenj�sen(huoltaja);
   }

   public boolean isMedialupa()
   {
      return medialupa;
   }

   public void setMedialupa(boolean medialupa)
   {
      this.medialupa = medialupa;
   }

   public String getKoulutus()
   {
      return koulutus;
   }

   public void setKoulutus(String koulutus)
   {
      this.koulutus = koulutus;
   }

   public String getTulokset()
   {
      return tulokset;
   }

   public void setTulokset(String tulokset)
   {
      this.tulokset = tulokset;
   }

   public boolean onJoSuorittanut(Vy�arvo vy�arvo)
   {
      return vy�kokeet.stream().map(v -> v.getVy�arvo()).collect(Collectors.toList()).contains(vy�arvo);
   }

}
