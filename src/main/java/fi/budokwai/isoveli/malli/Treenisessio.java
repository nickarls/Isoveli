package fi.budokwai.isoveli.malli;

import java.text.SimpleDateFormat;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "treenisessio")
@NamedQueries(
{
      @NamedQuery(name = "treenisessio", query = "select t from Treenisessio t where t.treeni = :treeni and t.päivä=:päivä"),
      @NamedQuery(name = "treenikäyttö", query = "select t from Treenisessio t where t.treeni = :treeni"),
      @NamedQuery(name = "kaikki_treenisessiot", query = "select t from Treenisessio t order by t.päivä desc"),
      @NamedQuery(name = "poista_tyhjät_treenisessiot", query = "delete from Treenisessio t where size(t.treenikäynnit)=0"),
      @NamedQuery(name = "treenisessiot", query = "select t from Treenisessio t where t.päivä between :alkaa and :päättyy order by t.päivä desc") })
public class Treenisessio
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @ManyToOne(optional = false)
   @JoinColumn(name = "treeni")
   @NotNull
   private Treeni treeni;

   @Column(name = "paiva")
   @NotNull
   private Date päivä = new Date();

   @ManyToMany
   @JoinTable(name = "treenisessiovetaja", joinColumns =
   { @JoinColumn(name = "treenisessio", referencedColumnName = "id") }, inverseJoinColumns =
   { @JoinColumn(name = "harrastaja", referencedColumnName = "id") })
   private List<Harrastaja> vetäjät = new ArrayList<Harrastaja>();

   @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "treenisessio", orphanRemoval = true)
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

   public List<Harrastaja> getVetäjät()
   {
      return vetäjät;
   }

   public void setVetäjät(List<Harrastaja> vetäjät)
   {
      this.vetäjät = vetäjät;
   }

   public List<Treenikäynti> getTreenikäynnit()
   {
      return treenikäynnit;
   }

   public void setTreenikäynnit(List<Treenikäynti> treenikäynnit)
   {
      this.treenikäynnit = treenikäynnit;
   }

   public boolean isTallentamaton()
   {
      return id == 0;
   }

   public String getKuvaus()
   {
      SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
      return String.format("%s: %s", treeni.getNimi(), sdf.format(päivä));
   }

   public boolean isPoistettavissa()
   {
      return id > 0;
   }

}
