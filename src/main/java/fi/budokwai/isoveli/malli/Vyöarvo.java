package fi.budokwai.isoveli.malli;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@NamedQuery(name = "vyöarvot", query = "select v from Vyöarvo v order by id")
@Table(name="vyoarvo")
public class Vyöarvo
{
   @Id
   private int id;

   private String nimi;

   private int minimikuukaudet;

   private int minimitreenit;

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

   public int getMinimitreenit()
   {
      return minimitreenit;
   }

   public void setMinimitreenit(int minimitreenit)
   {
      this.minimitreenit = minimitreenit;
   }

   public int getMinimikuukaudet()
   {
      return minimikuukaudet;
   }

   public void setMinimikuukaudet(int minimikuukaudet)
   {
      this.minimikuukaudet = minimikuukaudet;
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
}
