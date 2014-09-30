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
      @NamedQuery(name = "treenivetäjät", query = "select h from Harrastaja h order by h.henkilö.sukunimi, h.henkilö.etunimi"),
      @NamedQuery(name = "harrastajat", query = "select h from Harrastaja h order by h.henkilö.sukunimi, h.henkilö.etunimi") })
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
   private Henkilö henkilö = new Henkilö();

   @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
   @JoinColumn(name = "huoltaja")
   private Henkilö huoltaja;

   @OneToMany(cascade = CascadeType.ALL, mappedBy = "harrastaja", orphanRemoval = true)
   @OrderBy("vyoarvo asc")
   private List<Vyökoe> vyökokeet = Collections.emptyList();

   @OneToMany(cascade = CascadeType.ALL, mappedBy = "harrastaja", orphanRemoval = true)
   @OrderBy("paiva desc")
   private List<Kisatulos> kisatulokset = Collections.emptyList();

   @OneToMany(cascade = CascadeType.ALL, mappedBy = "harrastaja", orphanRemoval = true)
   @OrderBy("umpeutuu desc")
   private List<Sopimus> sopimukset = Collections.emptyList();

   @OneToMany(cascade = CascadeType.ALL, mappedBy = "harrastaja", orphanRemoval = true)
   @OrderBy("aikaleima desc")
   private List<Treenikäynti> treenikäynnit = Collections.emptyList();

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

   public Harrastaja()
   {
   }

   @PostLoad
   public void init()
   {
      if (isAlaikäinen() && huoltaja == null)
      {
         huoltaja = new Henkilö();
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

   public Henkilö getHenkilö()
   {
      return henkilö;
   }

   public void setHenkilö(Henkilö henkilö)
   {
      this.henkilö = henkilö;
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

   public int getIkä()
   {
      return ikä(syntynyt);
   }

   private static int ikä(Date päivämäärä)
   {
      Calendar kalenteri = Calendar.getInstance();
      kalenteri.setTime(päivämäärä);
      Calendar tanaan = Calendar.getInstance();
      int ikä = tanaan.get(Calendar.YEAR) - kalenteri.get(Calendar.YEAR);
      if (tanaan.get(Calendar.MONTH) < kalenteri.get(Calendar.MONTH))
      {
         ikä--;
      } else if (tanaan.get(Calendar.MONTH) == kalenteri.get(Calendar.MONTH)
         && tanaan.get(Calendar.DAY_OF_MONTH) < kalenteri.get(Calendar.DAY_OF_MONTH))
      {
         ikä--;
      }
      return ikä;
   }

   public boolean isAlaikäinen()
   {
      return syntynyt == null ? false : ikä(syntynyt) < 18;
   }

   public List<Vyökoe> getVyökokeet()
   {
      return vyökokeet;
   }

   public Vyöarvo getTuoreinVyöarvo()
   {
      return vyökokeet.isEmpty() ? Vyöarvo.EI_OOTA : vyökokeet.get(vyökokeet.size() - 1).getVyöarvo();
   }

   public String getJäsennumero()
   {
      return jäsennumero;
   }

   public void setVyökokeet(List<Vyökoe> vyökokeet)
   {
      this.vyökokeet = vyökokeet;
   }

   public boolean isYlläpitäjä()
   {
      return henkilö.onRoolissa("Ylläpitäjä");
   }

   public boolean isTreenienVetäjä()
   {
      return henkilö.onRoolissa("Treenien vetäjä");
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
