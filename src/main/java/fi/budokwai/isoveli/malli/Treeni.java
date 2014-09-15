package fi.budokwai.isoveli.malli;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@NamedQuery(name = "treenit", query = "select t from Treeni t where t.paiva=:paiva and t.paattyy >= :kello")
public class Treeni
{
   @Id
   private int id;

   private Paiva paiva;

   @Temporal(TemporalType.TIME)
   private Date alkaa;

   @Temporal(TemporalType.TIME)
   private Date paattyy;

   private String kuvaus;

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

   public Date getPaattyy()
   {
      return paattyy;
   }

   public void setPaattyy(Date paattyy)
   {
      this.paattyy = paattyy;
   }

   public String getKuvaus()
   {
      return kuvaus;
   }

   public void setKuvaus(String kuvaus)
   {
      this.kuvaus = kuvaus;
   }

   public Paiva getPaiva()
   {
      return paiva;
   }

   public void setPaiva(Paiva paiva)
   {
      this.paiva = paiva;
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
}
