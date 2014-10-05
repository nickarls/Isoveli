package fi.budokwai.isoveli.malli;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Entity
@NamedQueries(
{
      @NamedQuery(name = "treenisessio", query = "select t from Treenisessio t where t.treeni = :treeni and t.p‰iv‰=:p‰iv‰"),
      @NamedQuery(name = "treenisessiot", query = "select t from Treenisessio t where t.p‰iv‰ between :alkaa and :p‰‰ttyy order by t.p‰iv‰ desc") })
public class Treenisessio
{
   @Id
   @GeneratedValue
   private int id;

   @ManyToOne(optional = false)
   @JoinColumn(name = "treeni")
   @NotNull
   private Treeni treeni;

   @Column(name = "paiva")
   @NotNull
   private Date p‰iv‰ = new Date();

   @ManyToMany
   @JoinTable(name = "treenisessiovetaja", joinColumns =
   { @JoinColumn(name = "treenisessio", referencedColumnName = "id") }, inverseJoinColumns =
   { @JoinColumn(name = "harrastaja", referencedColumnName = "id") })
   private List<Harrastaja> vet‰j‰t = new ArrayList<Harrastaja>();

   @OneToMany(cascade=CascadeType.REMOVE, mappedBy = "treenisessio", orphanRemoval = true)
   private List<Treenik‰ynti> treenik‰ynnit = new ArrayList<Treenik‰ynti>();

   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public Treeni getTreeni()
   {
      return treeni;
   }

   public void setTreeni(Treeni treeni)
   {
      this.treeni = treeni;
   }

   public Date getP‰iv‰()
   {
      return p‰iv‰;
   }

   public void setP‰iv‰(Date p‰iv‰)
   {
      this.p‰iv‰ = p‰iv‰;
   }

   public List<Harrastaja> getVet‰j‰t()
   {
      return vet‰j‰t;
   }

   public void setVet‰j‰t(List<Harrastaja> vet‰j‰t)
   {
      this.vet‰j‰t = vet‰j‰t;
   }

   public List<Treenik‰ynti> getTreenik‰ynnit()
   {
      return treenik‰ynnit;
   }

   public void setTreenik‰ynnit(List<Treenik‰ynti> treenik‰ynnit)
   {
      this.treenik‰ynnit = treenik‰ynnit;
   }

   public boolean isTallentamaton()
   {
      return id == 0;
   }

}
