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
{ @NamedQuery(name = "vy�arvot", query = "select v from Vy�arvo v where v.arkistoitu='E' order by v.j�rjestys"),
      @NamedQuery(name = "vy�arvotArk", query = "select v from Vy�arvo v order by v.j�rjestys") })
@Table(name = "vyoarvo")
public class Vy�arvo
{
   public static final Vy�arvo EI_OOTA = new Vy�arvo("Ei ole");

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
   private int j�rjestys;

   @Type(type = "Kyll�Ei")
   private boolean arkistoitu;

   public Vy�arvo(String nimi)
   {
      this.nimi = nimi;
   }

   public Vy�arvo()
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
      if (!(toinen instanceof Vy�arvo))
      {
         return false;
      }
      Vy�arvo toinenVy�arvo = (Vy�arvo) toinen;
      return id == toinenVy�arvo.getId();
   }

   public boolean isPoistettavissa()
   {
      return id > 0;
   }

   public int getJ�rjestys()
   {
      return j�rjestys;
   }

   public void setJ�rjestys(int j�rjestys)
   {
      this.j�rjestys = j�rjestys;
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