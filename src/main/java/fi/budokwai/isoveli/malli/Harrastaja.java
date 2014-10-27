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
      @NamedQuery(name = "treenivet�j�t", query = "select h from Harrastaja h order by h.sukunimi, h.etunimi"),
      @NamedQuery(name = "harrastajat", query = "select h from Harrastaja h order by h.sukunimi, h.etunimi") })
@Typed(
{})
public class Harrastaja extends Henkil�
{

   @OneToOne(cascade = CascadeType.PERSIST)
   @JoinColumn(name = "huoltaja")
   private Henkil� huoltaja;

   @OneToMany(cascade = CascadeType.ALL, mappedBy = "harrastaja", orphanRemoval = true)
   @OrderBy("p�iv� asc")
   private List<Vy�koe> vy�kokeet = new ArrayList<Vy�koe>();

   @OneToMany(cascade = CascadeType.ALL, mappedBy = "harrastaja", orphanRemoval = true)
   @OrderBy("paiva desc")
   private List<Kisatulos> kisatulokset = new ArrayList<Kisatulos>();

   @OneToMany(cascade = CascadeType.ALL, mappedBy = "harrastaja", orphanRemoval = true)
   @OrderBy("umpeutuu desc")
   private List<Sopimus> sopimukset = new ArrayList<Sopimus>();

   @OneToMany(cascade = CascadeType.ALL, mappedBy = "harrastaja", orphanRemoval = true)
   @OrderBy("aikaleima desc")
   private List<Treenik�ynti> treenik�ynnit = new ArrayList<Treenik�ynti>();

   @Size(max = 10)
   @Column(name = "jasennumero")
   private String j�sennumero;

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

   public long getIk�()
   {
      return ik�(syntynyt);
   }

   private static long ik�(Date p�iv�m��r�)
   {
      Date d = new Date(p�iv�m��r�.getTime());
      LocalDate p�iv� = LocalDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault()).toLocalDate();
      return p�iv�.until(LocalDate.now(), ChronoUnit.YEARS);
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
      Optional<Vy�koe> vy�koe = vy�kokeet
         .stream()
         .sorted(
            (v1, v2) -> Integer.valueOf(v2.getVy�arvo().getJ�rjestys()).compareTo(
               Integer.valueOf(v1.getVy�arvo().getJ�rjestys()))).findFirst();
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
      return MoreObjects.toStringHelper(Harrastaja.class).add("Nimi", getNimi()).toString();
   }

   public Maksutarkistus tarkistaMaksut()
   {
      Maksutarkistus j�senmaksu = tarkistaJ�senmaksu();
      if (!j�senmaksu.isOK())
      {
         return j�senmaksu;
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
         .filter(p -> !(p.getTyyppi().isJ�senmaksu() || p.getTyyppi().isPerhealennus()))
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

   private Maksutarkistus tarkistaJ�senmaksu()
   {
      Optional<Sopimus> j�senmaksut = sopimukset.stream().filter(p -> p.getTyyppi().isJ�senmaksu())
         .sorted((s1, s2) -> s1.getUmpeutuu().compareTo(s2.getUmpeutuu())).findFirst();
      if (!j�senmaksut.isPresent())
      {
         return new Maksutarkistus("J�senmaksuja ei l�ytynyt");
      }
      Sopimus j�senmaksu = j�senmaksut.get();
      if (!j�senmaksu.isVoimassa())
      {
         LocalDate pvm = j�senmaksu.getUmpeutuu().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
         String viesti = String.format("J�senmaksu umpeutui %s", pvm.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
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
      LocalDate edellinen = LocalDateTime.ofInstant(Instant.ofEpochMilli(vy�koe.getP�iv�().getTime()),
         ZoneId.systemDefault()).toLocalDate();
      return Period.between(edellinen, LocalDate.now());
   }

   public J�ljell�Vy�kokeeseen getJ�ljell�Vy�kokeeseen(List<Vy�arvo> vy�arvot)
   {
      Vy�arvo vy�arvo = getTuoreinVy�arvo();
      if (vy�arvo == Vy�arvo.EI_OOTA)
      {
         return J�ljell�Vy�kokeeseen.EI_OOTA;
      }
      Optional<Vy�arvo> seuraavaVy�arvo = vy�arvot.stream()
         .filter(va -> va.getJ�rjestys() == vy�arvo.getJ�rjestys() + 1).findFirst();
      if (!seuraavaVy�arvo.isPresent())
      {
         return J�ljell�Vy�kokeeseen.EI_OOTA;
      }
      long treenit = seuraavaVy�arvo.get().getMinimitreenit() - getTreenej�ViimeVy�kokeesta();
      LocalDate tuoreinVy�koe = getTuoreinVy�koe().getKoska();
      LocalDate nyt = LocalDateTime.now().toLocalDate().atStartOfDay().toLocalDate();
      Period aika = Period.between(nyt,
         tuoreinVy�koe.plus(seuraavaVy�arvo.get().getMinimikuukaudet(), ChronoUnit.MONTHS));
      return new J�ljell�Vy�kokeeseen(aika, treenit, seuraavaVy�arvo.get());
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
      if (osoiteMuuttunut || (osoite != null && osoite.isK�ytt�m�t�n()))
      {
         osoite = null;
      }
      if (yhteystiedot != null && yhteystiedot.isK�ytt�m�t�n())
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
