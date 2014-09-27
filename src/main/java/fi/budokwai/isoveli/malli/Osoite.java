package fi.budokwai.isoveli.malli;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Osoite
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @Size(max = 200)
   @NotNull
   private String osoite;

   @Size(max = 10)
   @NotNull
   private String postinumero;

   @Size(max = 50)
   @NotNull
   private String kaupunki;

   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public String getOsoite()
   {
      return osoite;
   }

   public void setOsoite(String osoite)
   {
      this.osoite = osoite;
   }

   public String getPostinumero()
   {
      return postinumero;
   }

   public void setPostinumero(String postinumero)
   {
      this.postinumero = postinumero;
   }

   public String getKaupunki()
   {
      return kaupunki;
   }

   public void setKaupunki(String kaupunki)
   {
      this.kaupunki = kaupunki;
   }

   public String getOsoiteString()
   {
      return String.format("%s %s %s", osoite, postinumero, kaupunki);
   }
}