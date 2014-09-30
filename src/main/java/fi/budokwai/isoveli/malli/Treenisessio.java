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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

@Entity
@NamedQuery(name="treenisessio", query="select t from Treenisessio t where t.treeni = :treeni and t.päivä=:päivä")
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
   private Date päivä;

   @ManyToMany
   @JoinTable(name = "treenisessiovetaja", joinColumns =
   { @JoinColumn(name = "treenisessio", referencedColumnName = "id") }, inverseJoinColumns =
   { @JoinColumn(name = "harrastaja", referencedColumnName = "id") })
   private List<Harrastaja> treenivetäjät = new ArrayList<Harrastaja>();

   @OneToMany(cascade = CascadeType.ALL, mappedBy = "treenisessio", orphanRemoval = true)
   private List<Treenikäynti> treenikäynnit = new ArrayList<Treenikäynti>();

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

   public Date getPäivä()
   {
      return päivä;
   }

   public void setPäivä(Date päivä)
   {
      this.päivä = päivä;
   }

   public List<Harrastaja> getTreenivetäjät()
   {
      return treenivetäjät;
   }

   public void setTreenivetäjät(List<Harrastaja> treenivetäjät)
   {
      this.treenivetäjät = treenivetäjät;
   }

   public List<Treenikäynti> getTreenikäynnit()
   {
      return treenikäynnit;
   }

   public void setTreenikäynnit(List<Treenikäynti> treenikäynnit)
   {
      this.treenikäynnit = treenikäynnit;
   }
}
