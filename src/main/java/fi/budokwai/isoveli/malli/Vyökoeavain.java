package fi.budokwai.isoveli.malli;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
public class Vyökoeavain implements Serializable
{
   private static final long serialVersionUID = 1L;

   private Vyöarvo vyöarvo;
   private Harrastaja harrastaja;

   @ManyToOne
   public Vyöarvo getVyöarvo()
   {
      return vyöarvo;
   }

   public void setVyöarvo(Vyöarvo vyöarvo)
   {
      this.vyöarvo = vyöarvo;
   }

   @ManyToOne
   public Harrastaja getHarrastaja()
   {
      return harrastaja;
   }

   public void setHarrastaja(Harrastaja harrastaja)
   {
      this.harrastaja = harrastaja;
   }

   public boolean equals(Object o)
   {
      if (this == o)
      {
         return true;
      }
      if (o == null || getClass() != o.getClass())
      {
         return false;
      }
      Vyökoeavain that = (Vyökoeavain) o;
      if (vyöarvo != null ? !vyöarvo.equals(that.vyöarvo) : that.vyöarvo != null)
      {
         return false;
      }
      if (harrastaja != null ? !harrastaja.equals(that.harrastaja) : that.harrastaja != null)
      {
         return false;
      }
      return true;
   }

   public int hashCode()
   {
      int result;
      result = (vyöarvo != null ? vyöarvo.hashCode() : 0);
      result = 31 * result + (harrastaja != null ? harrastaja.hashCode() : 0);
      return result;
   }
}