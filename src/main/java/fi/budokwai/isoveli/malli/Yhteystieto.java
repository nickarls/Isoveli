package fi.budokwai.isoveli.malli;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;

@Entity
@NamedQuery(name = "s�hk�postilistalla", query = "select h from Henkil� h where h.yhteystiedot.s�hk�postilistalla='K'")
public class Yhteystieto
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @Size(max = 20)
   private String puhelinnumero;

   @Column(name = "sahkoposti")
   @Size(max = 30)
   private String s�hk�posti;

   @Column(name = "sahkopostilistalla")
   @Type(type = "Kyll�Ei")
   private boolean s�hk�postilistalla = true;

   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public String getPuhelinnumero()
   {
      return puhelinnumero;
   }

   public void setPuhelinnumero(String puhelinnumero)
   {
      this.puhelinnumero = puhelinnumero;
   }

   public String getS�hk�posti()
   {
      return s�hk�posti;
   }

   public void setS�hk�posti(String s�hk�posti)
   {
      this.s�hk�posti = s�hk�posti;
   }

   public boolean isS�hk�postilistalla()
   {
      return s�hk�postilistalla;
   }

   public void setS�hk�postilistalla(boolean s�hk�postilistalla)
   {
      this.s�hk�postilistalla = s�hk�postilistalla;
   }

   public boolean isK�ytt�m�t�n()
   {
      return id == 0 && (puhelinnumero == null || "".equals(puhelinnumero))
         && (s�hk�posti == null || "".equals(s�hk�posti));
   }
}
