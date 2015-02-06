package fi.budokwai.isoveli.malli;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.enterprise.inject.Typed;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;

import com.google.common.base.MoreObjects;

import fi.budokwai.isoveli.util.Util;

@Entity
@Table(name = "harrastaja")
@NamedQueries(
{ @NamedQuery(name = "kortti", query = "select h from Harrastaja h where h.jäsennumero=:kortti"),
      @NamedQuery(name = "treenivetäjät", query = "select h from Harrastaja h order by h.sukunimi, h.etunimi"),
      @NamedQuery(name = "sama_syntymäpäivä", query = "select h from Harrastaja h where syntynyt = :päivä"),
      @NamedQuery(name = "harrastajat", query = "select h from Harrastaja h order by h.sukunimi, h.etunimi") })
@Typed(
{})
public class Harrastaja extends Henkilö
{
   private static final long serialVersionUID = 1L;

   @OneToOne(cascade = CascadeType.PERSIST)
   @JoinColumn(name = "huoltaja")
   private Henkilö huoltaja;

   @OneToMany(cascade = CascadeType.ALL, mappedBy = "harrastaja", orphanRemoval = true)
   @OrderBy("päivä asc")
   private List<Vyökoe> vyökokeet = new ArrayList<Vyökoe>();

   @OneToMany(cascade = CascadeType.ALL, mappedBy = "harrastaja", orphanRemoval = true)
   @OrderBy("paiva desc")
   private List<Kisatulos> kisatulokset = new ArrayList<Kisatulos>();

   @OneToMany(cascade = CascadeType.ALL, mappedBy = "harrastaja", orphanRemoval = true)
   @OrderBy("umpeutuu desc")
   private List<Sopimus> sopimukset = new ArrayList<Sopimus>();

   @OneToMany(cascade = CascadeType.ALL, mappedBy = "harrastaja", orphanRemoval = true)
   @OrderBy("aikaleima desc")
   private List<Treenikäynti> treenikäynnit = new ArrayList<Treenikäynti>();

   @Size(max = 20)
   @Column(name = "jasennumero")
   private String jäsennumero;

   @Size(max = 20)
   private String lisenssinumero;

   @Temporal(TemporalType.DATE)
   @NotNull
   private Date syntynyt;

   @NotNull
   @Enumerated(EnumType.STRING)
   private Sukupuoli sukupuoli;

   @Size(max = 50)
   private String ice;

   @Size(max = 1000)
   private String huomautus;

   @Type(type = "KylläEi")
   private boolean infotiskille;

   @Temporal(TemporalType.DATE)
   private Date tauolla;

   public Harrastaja()
   {
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

   private static long ikä(Date päivämäärä)
   {
      try
      {
         Date d = new Date(päivämäärä.getTime());
         LocalDate päivä = LocalDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault()).toLocalDate();
         return päivä.until(LocalDate.now(), ChronoUnit.YEARS);
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
      Optional<Vyökoe> vyökoe = vyökokeet
         .stream()
         .sorted(
            (v1, v2) -> Integer.valueOf(v2.getVyöarvo().getJärjestys()).compareTo(
               Integer.valueOf(v1.getVyöarvo().getJärjestys()))).findFirst();
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
      return MoreObjects.toStringHelper(Harrastaja.class).add("Nimi", getNimi()).toString();
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
      return sopimus.isPresent() && sopimus.get().isVoimassa();
   }

   private boolean löytyyköJäsenmaksu()
   {
      Optional<Sopimus> sopimus = sopimukset.stream().filter(s -> s.getTyyppi().isJäsenmaksu()).findFirst();
      return sopimus.isPresent() && sopimus.get().isVoimassa();
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
      LocalDate edellinen = LocalDateTime.ofInstant(Instant.ofEpochMilli(vyökoe.getPäivä().getTime()),
         ZoneId.systemDefault()).toLocalDate();
      return Period.between(edellinen, LocalDate.now());
   }

   public JäljelläVyökokeeseen jäljelläVyökokeeseen(List<Vyöarvo> vyöarvot)
   {
      Vyöarvo nykyinenVyöarvo = getTuoreinVyöarvo();
      Vyöarvo seuraavaVyöarvo = nykyinenVyöarvo == Vyöarvo.EI_OOTA ? vyöarvot.iterator().next() : null;
      if (seuraavaVyöarvo == null)
      {
         Optional<Vyöarvo> seuraavaVyöarvoehdokas = vyöarvot.stream()
            .filter(va -> va.getJärjestys() == nykyinenVyöarvo.getJärjestys() + 1).findFirst();
         if (!seuraavaVyöarvoehdokas.isPresent())
         {
            return JäljelläVyökokeeseen.EI_OOTA;
         }
         seuraavaVyöarvo = seuraavaVyöarvoehdokas.get();
      }
      long treenit = seuraavaVyöarvo.getMinimitreenit() - getTreenejäViimeVyökokeesta();
      // FIXME -> luotu
      LocalDate tuoreinVyökoe = getTuoreinVyökoe() == Vyökoe.EI_OOTA ? Util.date2LocalDate(luotu)
         : getTuoreinVyökoe().getKoska();
      LocalDate nyt = Util.tänäänLD();
      Period aika = Period.between(nyt, tuoreinVyökoe.plus(seuraavaVyöarvo.getMinimikuukaudet(), ChronoUnit.MONTHS));
      long päiviä = ChronoUnit.DAYS.between(nyt,
         tuoreinVyökoe.plus(seuraavaVyöarvo.getMinimikuukaudet(), ChronoUnit.MONTHS));
      return new JäljelläVyökokeeseen(aika, treenit, päiviä, seuraavaVyöarvo);
   }

   public boolean isOsoiteMuuttunut()
   {
      return osoiteMuuttunut;
   }

   public void setOsoiteMuuttunut(boolean osoiteMuuttunut)
   {
      this.osoiteMuuttunut = osoiteMuuttunut;
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

   public Date getTauolla()
   {
      return tauolla;
   }

   public void setTauolla(Date tauolla)
   {
      this.tauolla = tauolla;
   }

   public boolean isTauollaNyt()
   {
      return tauolla != null && Util.getTänään().isBefore(Util.date2LocalDate(tauolla));
   }

}
