package fi.budokwai.isoveli.malli;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "rooli")
@NamedQueries(
{ @NamedQuery(name = "roolit", query = "select r from Rooli r order by r.nimi"),
      @NamedQuery(name = "roolikäyttö", query = "select h from Henkilö h join h.roolit r where r = :rooli") })
public class Rooli
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @Size(max = 20)
   @NotNull
   private String nimi;

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

   @Override
   public int hashCode()
   {
      return Integer.valueOf(id).hashCode();
   }

   @Override
   public boolean equals(Object toinen)
   {
      if (!(toinen instanceof Rooli))
      {
         return false;
      }
      Rooli toinenRooli = (Rooli) toinen;
      return id == toinenRooli.getId();
   }

   public boolean isPoistettavissa()
   {
      return id > 0;
   }
}
