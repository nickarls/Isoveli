package fi.budokwai.isoveli.malli;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.enterprise.inject.Typed;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PostLoad;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fi.budokwai.isoveli.SukupuoliConverter;

@Entity
@NamedQueries(
{
      @NamedQuery(name = "kortti", query = "select h from Harrastaja h where h.korttinumero=:kortti"),
      @NamedQuery(name = "treenivet�j�t", query = "select h from Harrastaja h order by h.henkil�.sukunimi, h.henkil�.etunimi"),
      @NamedQuery(name = "harrastajat", query = "select h from Harrastaja h order by h.henkil�.sukunimi, h.henkil�.etunimi") })
@Typed(
{})
public class Harrastaja
{
   @Id
   @GeneratedValue
   private int id;

   @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = false)
   @JoinColumn(name = "henkilo")
   @NotNull
   @Valid
   private Henkil� henkil� = new Henkil�();

   @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
   @JoinColumn(name = "huoltaja")
   private Henkil� huoltaja;

   @OneToMany(cascade = CascadeType.ALL, mappedBy = "harrastaja", orphanRemoval = true)
   @OrderBy("vyoarvo asc")
   private List<Vy�koe> vy�kokeet = Collections.emptyList();

   @OneToMany(cascade = CascadeType.ALL, mappedBy = "harrastaja", orphanRemoval = true)
   @OrderBy("paiva desc")
   private List<Kisatulos> kisatulokset = Collections.emptyList();

   @OneToMany(cascade = CascadeType.ALL, mappedBy = "harrastaja", orphanRemoval = true)
   @OrderBy("umpeutuu desc")
   private List<Sopimus> sopimukset = Collections.emptyList();

   @OneToMany(cascade = CascadeType.ALL, mappedBy = "harrastaja", orphanRemoval = true)
   @OrderBy("aikaleima desc")
   private List<Treenik�ynti> treenik�ynnit = Collections.emptyList();

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

   public Harrastaja()
   {
   }

   @PostLoad
   public void init()
   {
      if (isAlaik�inen() && huoltaja == null)
      {
         huoltaja = new Henkil�();
      }
   }

   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public Henkil� getHenkil�()
   {
      return henkil�;
   }

   public void setHenkil�(Henkil� henkil�)
   {
      this.henkil� = henkil�;
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

   public int getIk�()
   {
      return ik�(syntynyt);
   }

   private static int ik�(Date p�iv�m��r�)
   {
      Calendar kalenteri = Calendar.getInstance();
      kalenteri.setTime(p�iv�m��r�);
      Calendar tanaan = Calendar.getInstance();
      int ik� = tanaan.get(Calendar.YEAR) - kalenteri.get(Calendar.YEAR);
      if (tanaan.get(Calendar.MONTH) < kalenteri.get(Calendar.MONTH))
      {
         ik�--;
      } else if (tanaan.get(Calendar.MONTH) == kalenteri.get(Calendar.MONTH)
         && tanaan.get(Calendar.DAY_OF_MONTH) < kalenteri.get(Calendar.DAY_OF_MONTH))
      {
         ik�--;
      }
      return ik�;
   }

   public boolean isAlaik�inen()
   {
      return syntynyt == null ? false : ik�(syntynyt) < 18;
   }

   public List<Vy�koe> getVy�kokeet()
   {
      return vy�kokeet;
   }

   public Vy�arvo getTuoreinVy�arvo()
   {
      return vy�kokeet.isEmpty() ? Vy�arvo.EI_OOTA : vy�kokeet.get(vy�kokeet.size() - 1).getVy�arvo();
   }

   public String getJ�sennumero()
   {
      return j�sennumero;
   }

   public void setVy�kokeet(List<Vy�koe> vy�kokeet)
   {
      this.vy�kokeet = vy�kokeet;
   }

   public boolean isYll�pit�j�()
   {
      return henkil�.onRoolissa("Yll�pit�j�");
   }

   public boolean isTreenienVet�j�()
   {
      return henkil�.onRoolissa("Treenien vet�j�");
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

   public boolean isPoistettavissa()
   {
      return id > 0;
   }

   @Override
   public int hashCode()
   {
      return Integer.valueOf(id).hashCode();
   }

   @Override
   public boolean equals(Object toinen)
   {
      Harrastaja toinenHarrastaja = (Harrastaja) toinen;
      return id == toinenHarrastaja.getId();
   }

}
