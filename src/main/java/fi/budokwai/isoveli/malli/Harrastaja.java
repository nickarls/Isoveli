package fi.budokwai.isoveli.malli;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.enterprise.inject.Typed;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.google.common.base.MoreObjects;

import fi.budokwai.isoveli.util.SukupuoliConverter;

@Entity
@NamedQueries(
{ @NamedQuery(name = "kortti", query = "select h from Harrastaja h where h.korttinumero=:kortti"),
      @NamedQuery(name = "treenivetäjät", query = "select h from Harrastaja h order by h.sukunimi, h.etunimi"),
      @NamedQuery(name = "harrastajat", query = "select h from Harrastaja h order by h.sukunimi, h.etunimi") })
@Typed(
{})
public class Harrastaja extends Henkilö
{

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

   @Size(max = 10)
   @Column(name = "jasennumero")
   private String jäsennumero;

   @Size(max = 100)
   private String korttinumero;

   @Size(max = 10)
   private String lisenssinumero;

   @Temporal(TemporalType.DATE)
   @NotNull
   private Date syntynyt;

   @NotNull
   @Convert(converter = SukupuoliConverter.class)
   private Sukupuoli sukupuoli;

   @Size(max = 50)
   private String ice;

   @Size(max = 1000)
   private String huomautus;

   @Transient
   public boolean osoiteMuuttunut;

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

   public String getKorttinumero()
   {
      return korttinumero;
   }

   public void setKorttinumero(String korttinumero)
   {
      this.korttinumero = korttinumero;
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
      Date d = new Date(päivämäärä.getTime());
      LocalDate päivä = LocalDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault()).toLocalDate();
      return päivä.until(LocalDate.now(), ChronoUnit.YEARS);
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

   public Maksutarkistus tarkistaMaksut()
   {
      Maksutarkistus jäsenmaksu = tarkistaJäsenmaksu();
      if (!jäsenmaksu.isOK())
      {
         return jäsenmaksu;
      }
      return tarkistaMaksu();
   }

   public boolean isMaksutOK()
   {
      return tarkistaMaksut().isOK();
   }

   private Maksutarkistus tarkistaMaksu()
   {
      Optional<Sopimus> maksut = sopimukset.stream()
         .filter(p -> !(p.getTyyppi().isJäsenmaksu() || p.getTyyppi().isPerhealennus()))
         .sorted((s1, s2) -> s1.getUmpeutuu().compareTo(s2.getUmpeutuu())).findFirst();
      if (!maksut.isPresent())
      {
         return new Maksutarkistus("Ei sopimuksia");
      }
      Sopimus maksu = maksut.get();
      if (!maksu.isVoimassa())
      {
         LocalDate pvm = maksu.getUmpeutuu().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
         String viesti = String.format("Sopimus '%s' umpeutui %s", maksu.getTyyppi().getNimi(),
            pvm.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
         return new Maksutarkistus(viesti);
      }
      return new Maksutarkistus();
   }

   private Maksutarkistus tarkistaJäsenmaksu()
   {
      Optional<Sopimus> jäsenmaksut = sopimukset.stream().filter(p -> p.getTyyppi().isJäsenmaksu())
         .sorted((s1, s2) -> s1.getUmpeutuu().compareTo(s2.getUmpeutuu())).findFirst();
      if (!jäsenmaksut.isPresent())
      {
         return new Maksutarkistus("Jäsenmaksuja ei löytynyt");
      }
      Sopimus jäsenmaksu = jäsenmaksut.get();
      if (!jäsenmaksu.isVoimassa())
      {
         LocalDate pvm = jäsenmaksu.getUmpeutuu().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
         String viesti = String.format("Jäsenmaksu umpeutui %s", pvm.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
         return new Maksutarkistus(viesti);
      }
      return new Maksutarkistus();
   }

   public boolean isMies()
   {
      return Sukupuoli.Mies == sukupuoli;
   }

   public boolean isNainen()
   {
      return Sukupuoli.Nainen == sukupuoli;
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

   public JäljelläVyökokeeseen getJäljelläVyökokeeseen(List<Vyöarvo> vyöarvot)
   {
      Vyöarvo vyöarvo = getTuoreinVyöarvo();
      if (vyöarvo == Vyöarvo.EI_OOTA)
      {
         return JäljelläVyökokeeseen.EI_OOTA;
      }
      Optional<Vyöarvo> seuraavaVyöarvo = vyöarvot.stream()
         .filter(va -> va.getJärjestys() == vyöarvo.getJärjestys() + 1).findFirst();
      if (!seuraavaVyöarvo.isPresent())
      {
         return JäljelläVyökokeeseen.EI_OOTA;
      }
      long treenit = seuraavaVyöarvo.get().getMinimitreenit() - getTreenejäViimeVyökokeesta();
      LocalDate tuoreinVyökoe = getTuoreinVyökoe().getKoska();
      LocalDate nyt = LocalDateTime.now().toLocalDate().atStartOfDay().toLocalDate();
      Period aika = Period.between(nyt,
         tuoreinVyökoe.plus(seuraavaVyöarvo.get().getMinimikuukaudet(), ChronoUnit.MONTHS));
      return new JäljelläVyökokeeseen(aika, treenit, seuraavaVyöarvo.get());
   }

   public boolean isOsoiteMuuttunut()
   {
      return osoiteMuuttunut;
   }

   public void setOsoiteMuuttunut(boolean osoiteMuuttunut)
   {
      this.osoiteMuuttunut = osoiteMuuttunut;
   }

   public void siivoa()
   {
      if (osoiteMuuttunut || (osoite != null && osoite.isKäyttämätön()))
      {
         osoite = null;
      }
      if (yhteystiedot != null && yhteystiedot.isKäyttämätön())
      {
         yhteystiedot = null;
      }
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
   

}
