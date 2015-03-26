package fi.budokwai.isoveli.malli;

import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

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
import fi.budokwai.isoveli.malli.validointi.UniikitVyökokeet;
import fi.budokwai.isoveli.malli.validointi.UniikkiHenkilö;
import fi.budokwai.isoveli.malli.validointi.Vyökoejärjestys;
import fi.budokwai.isoveli.util.DateUtil;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "harrastaja")
@NamedQueries(
{
      @NamedQuery(name = "kortti", query = "select h from Harrastaja h where h.jäsennumero=:kortti"),
      @NamedQuery(name = "treenivetäjät", query = "select h from Harrastaja h order by h.sukunimi, h.etunimi"),
      @NamedQuery(name = "sama_syntymäpäivä", query = "select h from Harrastaja h where h.syntynyt = :päivä"),
      @NamedQuery(name = "harrastajat", query = "select h from Harrastaja h where h.arkistoitu='E' order by h.sukunimi, h.etunimi"),
      @NamedQuery(name = "harrastajatArq", query = "select h from Harrastaja h order by h.sukunimi, h.etunimi") })
@Typed(
{})
@UniikitVyökokeet
@UniikitSopimukset
@Vyökoejärjestys
@Taukotarkistus
public class Harrastaja extends Henkilö
{
   private static final long serialVersionUID = 1L;

   @OneToOne(cascade =
   { CascadeType.PERSIST, CascadeType.MERGE })
   @JoinColumn(name = "huoltaja")
   private Henkilö huoltaja;

   @OneToMany(mappedBy = "harrastaja", cascade =
   { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
   @SortComparator(value = VyökoeComparator.class)
   @Valid
   private List<Vyökoe> vyökokeet = new ArrayList<Vyökoe>();

   @OneToMany(mappedBy = "harrastaja")
   @OrderBy("paiva desc")
   private List<Kisatulos> kisatulokset = new ArrayList<Kisatulos>();

   @OneToMany(mappedBy = "harrastaja", cascade =
   { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
   @OrderBy("umpeutuu desc")
   private List<Sopimus> sopimukset = new ArrayList<Sopimus>();

   @OneToMany(mappedBy = "harrastaja")
   @OrderBy("aikaleima desc")
   private List<Treenikäynti> treenikäynnit = new ArrayList<Treenikäynti>();

   @Size(max = 20)
   @Column(name = "jasennumero")
   private String jäsennumero;

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
   private int siirtotreenejä;

   @Type(type = "KylläEi")
   private boolean infotiskille;

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

   public Henkilö getHuoltaja()
   {
      return huoltaja;
   }

   public void setHuoltaja(Henkilö huoltaja)
   {
      this.huoltaja = huoltaja;
   }

   public void setJäsennumero(String jäsennumero)
   {
      this.jäsennumero = jäsennumero;
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

   public long getIkä()
   {
      return ikä(syntynyt);
   }

   public static long ikä(Date päivämäärä)
   {
      try
      {
         return DateUtil.ikä(päivämäärä);
      } catch (Exception e)
      {
         return 0;
      }
   }

   public boolean isAlaikäinen()
   {
      return syntynyt == null ? false : ikä(syntynyt) < 18;
   }

   public List<Vyökoe> getVyökokeet()
   {
      return vyökokeet;
   }

   public Vyökoe getTuoreinVyökoe()
   {
      Optional<Vyökoe> vyökoe = vyökokeet.stream().max(
         (v1, v2) -> Integer.compare(v1.getVyöarvo().getJärjestys(), v2.getVyöarvo().getJärjestys()));
      return vyökoe.isPresent() ? vyökoe.get() : Vyökoe.EI_OOTA;
   }

   public Vyöarvo getTuoreinVyöarvo()
   {
      Vyökoe vyökoe = getTuoreinVyökoe();
      return vyökoe == Vyökoe.EI_OOTA ? Vyöarvo.EI_OOTA : vyökoe.getVyöarvo();
   }

   public String getJäsennumero()
   {
      return jäsennumero;
   }

   public void setVyökokeet(List<Vyökoe> vyökokeet)
   {
      this.vyökokeet = vyökokeet;
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

   public List<Treenikäynti> getTreenikäynnit()
   {
      return treenikäynnit;
   }

   public void setTreenikäynnit(List<Treenikäynti> treenikäynnit)
   {
      this.treenikäynnit = treenikäynnit;
   }

   public static boolean alaikäinen(Date päivämäärä)
   {
      return ikä(päivämäärä) < 18;
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
      if (!löytyyköJäsenmaksu())
      {
         sopimustarkistuset.lisää(new Sopimustarkistus("Jäsenmaksu puuttuu", false));
      }
      if (!löytyyköHarjoittelumaksu())
      {
         sopimustarkistuset.lisää(new Sopimustarkistus("Harjoittelumaksu puuttuu", false));
      }
      sopimukset.forEach(s -> {
         sopimustarkistuset.lisää(s.tarkista());
      });
      return sopimustarkistuset;
   }

   private boolean löytyyköHarjoittelumaksu()
   {
      Optional<Sopimus> sopimus = sopimukset.stream().filter(s -> s.getTyyppi().isHarjoittelumaksu()).findFirst();
      int perheenTreenikerrat = perhe == null ? 0 : perhe.getPerheenTreenikerrat();
      return perheenTreenikerrat > 0 || sopimus.isPresent();
   }

   private boolean löytyyköJäsenmaksu()
   {
      Optional<Sopimus> sopimus = sopimukset.stream().filter(s -> s.getTyyppi().isJäsenmaksu()).findFirst();
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

   public long getTreenejäViimeVyökokeesta()
   {
      Vyökoe vyökoe = getTuoreinVyökoe();
      if (vyökoe == Vyökoe.EI_OOTA)
      {
         return treenikäynnit.size();
      }
      return treenikäynnit.stream().filter(t -> t.getAikaleima().after(vyökoe.getPäivä())).count();
   }

   public Period getAikaaViimeVyökokeesta()
   {
      Vyökoe vyökoe = getTuoreinVyökoe();
      if (vyökoe == Vyökoe.EI_OOTA)
      {
         return Period.ZERO;
      }
      return DateUtil.aikaväli(vyökoe.getPäivä());

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

   public boolean isTilapäinen()
   {
      return ".".equals(huomautus);
   }

   public void setTilapäinen(boolean tilapäinen)
   {
   }

   public void muutaTilapäiseksi()
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
      return getTauko().isNytVälissä();
   }

   public Date getViimeisinTreeni()
   {
      Optional<Treenikäynti> tuorein = treenikäynnit.stream().sorted((tk1, tk2) -> {
         return tk1.getAikaleima().compareTo(tk2.getAikaleima());
      }).findFirst();
      return tuorein.isPresent() ? tuorein.get().getAikaleima() : null;
   }

   public int getTreenejäYhteensä()
   {
      return treenikäynnit.size() + siirtotreenejä;
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

   public boolean isKertakorttiKäytössä()
   {
      return getAktiivinenKertakortti() != null;
   }

   public int getTreenikertojaJäljellä()
   {
      Sopimus sopimus = getAktiivinenKertakortti();
      return sopimus == null ? 0 : sopimus.getTreenikertojaJäljellä();
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

   public boolean löytyyJoSopimus(Sopimustyyppi sopimustyyppi)
   {
      Optional<Sopimus> vanha = sopimukset.stream().filter(s -> s.getTyyppi().getId() == sopimustyyppi.getId())
         .findFirst();
      return vanha.isPresent();
   }

   public void lisääHuomautus(String huomautus)
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

   public Sopimus lisääSopimus(Sopimus sopimus)
   {
      sopimus.setHarrastaja(this);
      sopimukset.add(sopimus);
      return sopimus;
   }

   public void lisääVyökoe(Vyökoe vyökoe)
   {
      vyökoe.setHarrastaja(this);
      vyökokeet.add(vyökoe);
      Collections.sort(vyökokeet, new Comparator<Vyökoe>()
      {

         @Override
         public int compare(Vyökoe v1, Vyökoe v2)
         {
            return Integer.valueOf(v1.getVyöarvo().getJärjestys()).compareTo(
               Integer.valueOf(v2.getVyöarvo().getJärjestys()));
         }
      });
   }

   public void poistotarkistus()
   {
      tarkistaVyökoekäyttö();
      tarkistaSopimuskäyttö();
      tarkistaTreenikäyntikäyttö();
      tarkistaHuoltajakäyttö();
   }

   private void tarkistaHuoltajakäyttö()
   {
      if (perhe == null || perhe.getHuollettavat(this).isEmpty())
      {
         return;
      }
      StringJoiner stringJoiner = new StringJoiner(", ");
      perhe.getHuollettavat(this).forEach(h -> {
         stringJoiner.add(h.getEtunimi());
      });
      String viesti = String.format("Harrastaja on huoltaja ja häntä ei voi poistaa (%dkpl: %s...)", perhe
         .getHuollettavat(this).size(), stringJoiner.toString());
      throw new IsoveliPoikkeus(viesti);
   }

   private void tarkistaTreenikäyntikäyttö()
   {
      if (treenikäynnit.isEmpty())
      {
         return;
      }
      StringJoiner stringJoiner = new StringJoiner(", ");
      treenikäynnit.forEach(t -> {
         stringJoiner.add(t.getAikaleimaString());
      });
      String viesti = String.format("Harrastajalla on treenikäyntejä ja häntä ei voi poistaa (%dkpl: %s...)",
         treenikäynnit.size(), stringJoiner.toString());
      throw new IsoveliPoikkeus(viesti);
   }

   private void tarkistaSopimuskäyttö()
   {
      if (sopimukset.isEmpty())
      {
         return;
      }
      StringJoiner stringJoiner = new StringJoiner(", ");
      sopimukset.forEach(h -> {
         stringJoiner.add(h.getTyyppi().getNimi());
      });
      String viesti = String.format("Harrastajalla on sopimuksia ja häntä ei voi poistaa (%dkpl: %s...)",
         sopimukset.size(), stringJoiner.toString());
      throw new IsoveliPoikkeus(viesti);
   }

   private void tarkistaVyökoekäyttö()
   {
      if (vyökokeet.isEmpty())
      {
         return;
      }
      StringJoiner stringJoiner = new StringJoiner(", ");
      vyökokeet.forEach(v -> {
         stringJoiner.add(v.getVyöarvo().getNimi());
      });
      String viesti = String.format("Harrastajalla on vyökokeita ja häntä ei voi poistaa (%dkpl: %s...)",
         vyökokeet.size(), stringJoiner.toString());
      throw new IsoveliPoikkeus(viesti);
   }

   public void lisääTreenikäynti(Treenikäynti treenikäynti)
   {
      treenikäynti.setHarrastaja(this);
      treenikäynnit.add(treenikäynti);
   }

   public int getSiirtotreenejä()
   {
      return siirtotreenejä;
   }

   public void setSiirtotreenejä(int siirtotreenejä)
   {
      this.siirtotreenejä = siirtotreenejä;
   }

   public void lisääHuoltaja()
   {
      huoltaja = new Henkilö();
      huoltaja.setSukunimi(sukunimi);
      huoltaja.setEtunimi("Huoltaja");
      perhe.lisääPerheenjäsen(huoltaja);
   }
}
