package fi.budokwai.isoveli.malli;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@NamedQueries(
{
      @NamedQuery(name = "vyöarvot", query = "select v from Vyöarvo v order by v.järjestys"),
      @NamedQuery(name = "vyöarvokäyttö", query = "select h from Harrastaja h join h.vyökokeet k join k.vyöarvo v where v = :vyöarvo") })
@Table(name = "vyoarvo")
public class Vyöarvo
{
   public static final Vyöarvo EI_OOTA = new Vyöarvo("Ei ole");

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @Size(max = 10)
   @NotNull
   private String nimi;

   @Min(value = 1)
   private int minimikuukaudet;

   @Min(value = 1)
   private int minimitreenit;

   @Column(name = "jarjestys")
   @Min(value = 1)
   private int järjestys;

   @OneToMany(mappedBy = "vyöarvo")
   private List<Vyökoe> vyökokeet = new ArrayList<Vyökoe>();

   public Vyöarvo(String nimi)
   {
      this.nimi = nimi;
   }

   public Vyöarvo()
   {
   }

   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public String getNimi()
   {
      return nimi;
   }

   public void setNimi(String nimi)
   {
      this.nimi = nimi;
   }

   public List<Vyökoe> getVyökokeet()
   {
      return vyökokeet;
   }

   public void setVyökokeet(List<Vyökoe> vyökokeet)
   {
      this.vyökokeet = vyökokeet;
   }

   public int getMinimikuukaudet()
   {
      return minimikuukaudet;
   }

   public void setMinimikuukaudet(int minimikuukaudet)
   {
      this.minimikuukaudet = minimikuukaudet;
   }

   public int getMinimitreenit()
   {
      return minimitreenit;
   }

   public void setMinimitreenit(int minimitreenit)
   {
      this.minimitreenit = minimitreenit;
   }

   @Override
   public int hashCode()
   {
      return Integer.valueOf(id).hashCode();
   }

   @Override
   public boolean equals(Object toinen)
   {
      if (!(toinen instanceof Vyöarvo))
      {
         return false;
      }
      Vyöarvo toinenVyöarvo = (Vyöarvo) toinen;
      return id == toinenVyöarvo.getId();
   }

   public boolean isPoistettavissa()
   {
      return id > 0;
   }

   public int getJärjestys()
   {
      return järjestys;
   }

   public void setJärjestys(int järjestys)
   {
      this.järjestys = järjestys;
   }
}