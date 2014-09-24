package fi.budokwai.isoveli.malli;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@NamedQuery(name = "treenit", query = "select t from Treeni t where t.p‰iv‰=:p‰iv‰ and t.p‰‰ttyy >= :kello and not exists(select tk from Treenik‰ynti tk where tk.harrastaja=:harrastaja and tk.treeni = t.id and tk.p‰iv‰ = :t‰n‰‰n)")
public class Treeni
{
   @Id
   private int id;

   @Column(name="paiva")
   private P‰iv‰ p‰iv‰;

   @Temporal(TemporalType.TIME)
   private Date alkaa;

   @Temporal(TemporalType.TIME)
   @Column(name="paattyy")
   private Date p‰‰ttyy;

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

   public String getKuvaus()
   {
      return kuvaus;
   }

   public void setKuvaus(String kuvaus)
   {
      this.kuvaus = kuvaus;
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

   public P‰iv‰ getP‰iv‰()
   {
      return p‰iv‰;
   }

   public void setP‰iv‰(P‰iv‰ p‰iv‰)
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
}
