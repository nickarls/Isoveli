package fi.budokwai.isoveli.malli;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "treeni")
@NamedQueries(
{
      @NamedQuery(name = "tulevat_treenit", query = "select t from Treeni t where t.päivä=:päivä and t.päättyy >= :kello and not exists(select tk from Treenikäynti tk, Treenisessio ts where tk.harrastaja=:harrastaja and tk.treenisessio=ts and ts.treeni=t and ts.päivä = :tänään)"),
      @NamedQuery(name = "treenit", query = "select t from Treeni t order by t.päivä, t.alkaa") })
public class Treeni
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @NotNull
   private String nimi;

   @Column(name = "paiva")
   @NotNull
   private Viikonpäivä päivä;

   @Temporal(TemporalType.TIME)
   @NotNull
   private Date alkaa;

   @Temporal(TemporalType.TIME)
   @Column(name = "paattyy")
   @NotNull
   private Date päättyy;

   @OneToOne(optional = false)
   @JoinColumn(name = "tyyppi")
   @NotNull
   private Treenityyppi tyyppi;

   @Type(type = "KylläEi")
   private boolean power;

   @OneToOne
   @JoinColumn(name = "vyoalaraja")
   private Vyöarvo vyöAlaraja;

   @OneToOne
   @JoinColumn(name = "vyoylaraja")
   private Vyöarvo vyöYläraja;

   @Temporal(TemporalType.DATE)
   private Date voimassaAlkaa;

   @Column(name = "voimassapaattyy")
   @Temporal(TemporalType.DATE)
   private Date voimassaPäättyy;

   @ManyToMany
   @JoinTable(name = "treenivetaja", joinColumns =
   { @JoinColumn(name = "treeni", referencedColumnName = "id") }, inverseJoinColumns =
   { @JoinColumn(name = "harrastaja", referencedColumnName = "id") })
   private List<Harrastaja> vetäjät = new ArrayList<Harrastaja>();

   @OneToMany(cascade = CascadeType.ALL, mappedBy = "treeni", orphanRemoval = true)
   private List<Treenisessio> treenisessiot = new ArrayList<Treenisessio>();

   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public Date getAlkaa()
   {
      return alkaa;
   }

   public void setAlkaa(Date alkaa)
   {
      this.alkaa = alkaa;
   }

   @Override
   public boolean equals(Object toinen)
   {
      if (!(toinen instanceof Treeni))
      {
         return false;
      }
      Treeni toinenTreeni = (Treeni) toinen;
      return id == toinenTreeni.getId();
   }

   @Override
   public int hashCode()
   {
      return Integer.valueOf(id).hashCode();
   }

   public Viikonpäivä getPäivä()
   {
      return päivä;
   }

   public void setPäivä(Viikonpäivä päivä)
   {
      this.päivä = päivä;
   }

   public Date getPäättyy()
   {
      return päättyy;
   }

   public void setPäättyy(Date päättyy)
   {
      this.päättyy = päättyy;
   }

   public String getNimi()
   {
      return nimi;
   }

   public void setNimi(String nimi)
   {
      this.nimi = nimi;
   }

   public boolean isPower()
   {
      return power;
   }

   public void setPower(boolean power)
   {
      this.power = power;
   }

   public Treenityyppi getTyyppi()
   {
      return tyyppi;
   }

   public void setTyyppi(Treenityyppi tyyppi)
   {
      this.tyyppi = tyyppi;
   }

   public List<Treenisessio> getTreenisessiot()
   {
      return treenisessiot;
   }

   public void setTreenisessiot(List<Treenisessio> treenisessiot)
   {
      this.treenisessiot = treenisessiot;
   }

   public List<Harrastaja> getVetäjät()
   {
      return vetäjät;
   }

   public void setVetäjät(List<Harrastaja> vetäjät)
   {
      this.vetäjät = vetäjät;
   }

   public boolean isPoistettavissa()
   {
      return id > 0;
   }

   public Date getVoimassaAlkaa()
   {
      return voimassaAlkaa;
   }

   public void setVoimassaAlkaa(Date voimassaAlkaa)
   {
      this.voimassaAlkaa = voimassaAlkaa;
   }

   public Date getVoimassaPäättyy()
   {
      return voimassaPäättyy;
   }

   public void setVoimassaPäättyy(Date voimassaPäättyy)
   {
      this.voimassaPäättyy = voimassaPäättyy;
   }

   public Vyöarvo getVyöAlaraja()
   {
      return vyöAlaraja;
   }

   public void setVyöAlaraja(Vyöarvo vyöAlaraja)
   {
      this.vyöAlaraja = vyöAlaraja;
   }

   public Vyöarvo getVyöYläraja()
   {
      return vyöYläraja;
   }

   public void setVyöYläraja(Vyöarvo vyöYläraja)
   {
      this.vyöYläraja = vyöYläraja;
   }
}
