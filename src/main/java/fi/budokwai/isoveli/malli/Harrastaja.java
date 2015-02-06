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
{ @NamedQuery(name = "kortti", query = "select h from Harrastaja h where h.j�sennumero=:kortti"),
      @NamedQuery(name = "treenivet�j�t", query = "select h from Harrastaja h order by h.sukunimi, h.etunimi"),
      @NamedQuery(name = "sama_syntym�p�iv�", query = "select h from Harrastaja h where syntynyt = :p�iv�"),
      @NamedQuery(name = "harrastajat", query = "select h from Harrastaja h order by h.sukunimi, h.etunimi") })
@Typed(
{})
public class Harrastaja extends Henkil�
{
   private static final long serialVersionUID = 1L;

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

   @Size(max = 20)
   @Column(name = "jasennumero")
   private String j�sennumero;

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

   @Type(type = "Kyll�Ei")
   private boolean infotiskille;

   @Temporal(TemporalType.DATE)
   private Date tauolla;

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
      try
      {
         Date d = new Date(p�iv�m��r�.getTime());
         LocalDate p�iv� = LocalDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault()).toLocalDate();
         return p�iv�.until(LocalDate.now(), ChronoUnit.YEARS);
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
      return sopimus.isPresent() && sopimus.get().isVoimassa();
   }

   private boolean l�ytyyk�J�senmaksu()
   {
      Optional<Sopimus> sopimus = sopimukset.stream().filter(s -> s.getTyyppi().isJ�senmaksu()).findFirst();
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

   public J�ljell�Vy�kokeeseen j�ljell�Vy�kokeeseen(List<Vy�arvo> vy�arvot)
   {
      Vy�arvo nykyinenVy�arvo = getTuoreinVy�arvo();
      Vy�arvo seuraavaVy�arvo = nykyinenVy�arvo == Vy�arvo.EI_OOTA ? vy�arvot.iterator().next() : null;
      if (seuraavaVy�arvo == null)
      {
         Optional<Vy�arvo> seuraavaVy�arvoehdokas = vy�arvot.stream()
            .filter(va -> va.getJ�rjestys() == nykyinenVy�arvo.getJ�rjestys() + 1).findFirst();
         if (!seuraavaVy�arvoehdokas.isPresent())
         {
            return J�ljell�Vy�kokeeseen.EI_OOTA;
         }
         seuraavaVy�arvo = seuraavaVy�arvoehdokas.get();
      }
      long treenit = seuraavaVy�arvo.getMinimitreenit() - getTreenej�ViimeVy�kokeesta();
      // FIXME -> luotu
      LocalDate tuoreinVy�koe = getTuoreinVy�koe() == Vy�koe.EI_OOTA ? Util.date2LocalDate(luotu)
         : getTuoreinVy�koe().getKoska();
      LocalDate nyt = Util.t�n��nLD();
      Period aika = Period.between(nyt, tuoreinVy�koe.plus(seuraavaVy�arvo.getMinimikuukaudet(), ChronoUnit.MONTHS));
      long p�ivi� = ChronoUnit.DAYS.between(nyt,
         tuoreinVy�koe.plus(seuraavaVy�arvo.getMinimikuukaudet(), ChronoUnit.MONTHS));
      return new J�ljell�Vy�kokeeseen(aika, treenit, p�ivi�, seuraavaVy�arvo);
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

   public boolean isTilap�inen()
   {
      return ".".equals(huomautus);
   }

   public void setTilap�inen(boolean tilap�inen)
   {
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
      return tauolla != null && Util.getT�n��n().isBefore(Util.date2LocalDate(tauolla));
   }

}
