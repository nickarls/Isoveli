package fi.budokwai.isoveli.malli;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

@Entity
@DynamicInsert
@DynamicUpdate
@NamedQueries(
{ @NamedQuery(name = "vyöarvot", query = "select v from Vyöarvo v where v.arkistoitu='E' order by v.järjestys"),
      @NamedQuery(name = "vyöarvotArk", query = "select v from Vyöarvo v order by v.järjestys") })
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

   @Size(max = 20)
   @NotNull
   private String kuvaus;

   @Min(value = 1)
   private int minimikuukaudet;

   @Min(value = 1)
   private int minimitreenit;

   @Column(name = "jarjestys")
   @Min(value = 1)
   private int järjestys;

   @Type(type = "KylläEi")
   private boolean arkistoitu;

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

   @Override
   public String toString()
   {
      return nimi;
   }

   public String getKuvaus()
   {
      return kuvaus;
   }

   public void setKuvaus(String kuvaus)
   {
      this.kuvaus = kuvaus;
   }

   public boolean isArkistoitu()
   {
      return arkistoitu;
   }

   public void setArkistoitu(boolean arkistoitu)
   {
      this.arkistoitu = arkistoitu;
   }

}