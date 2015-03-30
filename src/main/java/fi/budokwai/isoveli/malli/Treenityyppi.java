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
import org.hibernate.annotations.Type;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "treenityyppi")
@NamedQueries(
{ @NamedQuery(name = "treenityypit", query = "select t from Treenityyppi t where t.arkistoitu='E' order by t.nimi"),
      @NamedQuery(name = "treenityypitArq", query = "select t from Treenityyppi t order by t.nimi") })
public class Treenityyppi
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @Size(max = 50)
   @NotNull
   private String nimi;

   @Type(type = "Kyll‰Ei")
   private boolean arkistoitu;

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

   public boolean isArkistoitu()
   {
      return arkistoitu;
   }

   public void setArkistoitu(boolean arkistoitu)
   {
      this.arkistoitu = arkistoitu;
   }

   @Override
   public String toString()
   {
      return nimi;
   }

   public boolean isTallentamaton()
   {
      return id == 0;
   }

   @Override
   public int hashCode()
   {
      return Integer.valueOf(id).hashCode();
   }

   @Override
   public boolean equals(Object toinen)
   {
      Treenityyppi toinenTreenityyppi = (Treenityyppi) toinen;
      return id == toinenTreenityyppi.getId();
   }
}
