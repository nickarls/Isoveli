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
@NamedQuery(name = "sähköpostilistalla", query = "select h from Henkilö h where h.yhteystiedot.sähköpostilistalla='K'")
public class Yhteystieto
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @Size(max = 20)
   private String puhelinnumero;

   @Column(name = "sahkoposti")
   @Size(max = 30)
   private String sähköposti;

   @Column(name = "sahkopostilistalla")
   @Type(type = "KylläEi")
   private boolean sähköpostilistalla = true;

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

   public String getSähköposti()
   {
      return sähköposti;
   }

   public void setSähköposti(String sähköposti)
   {
      this.sähköposti = sähköposti;
   }

   public boolean isSähköpostilistalla()
   {
      return sähköpostilistalla;
   }

   public void setSähköpostilistalla(boolean sähköpostilistalla)
   {
      this.sähköpostilistalla = sähköpostilistalla;
   }

   public boolean isKäyttämätön()
   {
      return id == 0 && (puhelinnumero == null || "".equals(puhelinnumero))
         && (sähköposti == null || "".equals(sähköposti));
   }
}
