package fi.budokwai.isoveli.malli;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
public class Vy�koeavain implements Serializable
{
   private static final long serialVersionUID = 1L;

   private Vy�arvo vy�arvo;
   private Harrastaja harrastaja;

   @ManyToOne
   public Vy�arvo getVy�arvo()
   {
      return vy�arvo;
   }

   public void setVy�arvo(Vy�arvo vy�arvo)
   {
      this.vy�arvo = vy�arvo;
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
      Vy�koeavain that = (Vy�koeavain) o;
      if (vy�arvo != null ? !vy�arvo.equals(that.vy�arvo) : that.vy�arvo != null)
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
      result = (vy�arvo != null ? vy�arvo.hashCode() : 0);
      result = 31 * result + (harrastaja != null ? harrastaja.hashCode() : 0);
      return result;
   }
}