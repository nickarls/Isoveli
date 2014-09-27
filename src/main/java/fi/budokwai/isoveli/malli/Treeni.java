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
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import fi.budokwai.isoveli.Kyll‰EiTyyppi;

@Entity
@TypeDef(name = "Kyll‰Ei", typeClass = Kyll‰EiTyyppi.class)
// @NamedQuery(name = "treenit", query =
// "select t from Treeni t where t.p‰iv‰=:p‰iv‰ and t.p‰‰ttyy >= :kello and not exists(select tk from Treenik‰ynti tk where tk.harrastaja=:harrastaja and tk.treeni = t.id and tk.p‰iv‰ = :t‰n‰‰n)")
@NamedQuery(name = "treenit", query = "select t from Treeni t order by t.p‰iv‰, t.alkaa")
public class Treeni
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @NotNull
   private String nimi;

   @Column(name = "paiva")
   @NotNull
   private Viikonpaiva p‰iv‰;

   @Temporal(TemporalType.TIME)
   @NotNull
   private Date alkaa;

   @Temporal(TemporalType.TIME)
   @Column(name = "paattyy")
   @NotNull
   private Date p‰‰ttyy;

   @OneToOne(optional = false)
   @JoinColumn(name = "tyyppi")
   @NotNull
   private Treenityyppi tyyppi;

   @Type(type = "Kyll‰Ei")
   private boolean power;

   @ManyToMany
   @JoinTable(name = "treenivetaja", joinColumns =
   { @JoinColumn(name = "treeni", referencedColumnName = "id") }, inverseJoinColumns =
   { @JoinColumn(name = "harrastaja", referencedColumnName = "id") })
   private List<Harrastaja> vet‰j‰t = new ArrayList<Harrastaja>();

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

   public Viikonpaiva getP‰iv‰()
   {
      return p‰iv‰;
   }

   public void setP‰iv‰(Viikonpaiva p‰iv‰)
   {
      this.p‰iv‰ = p‰iv‰;
   }

   public Date getP‰‰ttyy()
   {
      return p‰‰ttyy;
   }

   public void setP‰‰ttyy(Date p‰‰ttyy)
   {
      this.p‰‰ttyy = p‰‰ttyy;
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

   public List<Harrastaja> getVet‰j‰t()
   {
      return vet‰j‰t;
   }

   public void setVet‰j‰t(List<Harrastaja> vet‰j‰t)
   {
      this.vet‰j‰t = vet‰j‰t;
   }

   public boolean isPoistettavissa()
   {
      return id > 0;
   }
}
