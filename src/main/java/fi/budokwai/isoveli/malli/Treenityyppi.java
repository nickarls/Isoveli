package fi.budokwai.isoveli.malli;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@NamedQueries(
{ @NamedQuery(name = "treenityypit", query = "select t from Treenityyppi t order by t.nimi"),
      @NamedQuery(name = "treenityyppikäyttö", query = "select t from Treeni t where t.tyyppi = :treenityyppi") })
public class Treenityyppi
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @Size(max = 50)
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

   @Transient
   public boolean isPoistettavissa()
   {
      return id > 0;
   }
}
