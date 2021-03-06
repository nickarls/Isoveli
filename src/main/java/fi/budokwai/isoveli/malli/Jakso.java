package fi.budokwai.isoveli.malli;

import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import fi.budokwai.isoveli.util.DateUtil;

@Embeddable
public class Jakso
{
   @Column(name = "taukoalkaa")
   private Date alkaa;

   @Column(name = "taukopaattyy")
   private Date p��ttyy;

   public Jakso(Date alkaa, Date p��ttyy)
   {
      this.alkaa = alkaa;
      this.p��ttyy = p��ttyy;
   }

   public Jakso()
   {
   }

   public Jakso(Jakso jakso)
   {
      this(jakso.getAlkaa(), jakso.getP��ttyy());
   }

   public Date getAlkaa()
   {
      return alkaa;
   }

   public Date getP��ttyy()
   {
      return p��ttyy;
   }

   public String getKuvaus()
   {
      return DateUtil.aikav�li2String(alkaa, p��ttyy);
   }

   public boolean isNytV�liss�()
   {
      if (alkaa == null && p��ttyy == null)
      {
         return false;
      } else if (alkaa != null && p��ttyy == null)
      {
         return DateUtil.onkoMenneysyydess�(alkaa);
      } else if (alkaa == null && p��ttyy != null)
      {
         return DateUtil.onkoTulevaisuudessa(p��ttyy);
      } else
      {
         return DateUtil.onkoV�liss�(alkaa, p��ttyy);
      }
   }

   public long getP�ivi�V�liss�()
   {
      if (alkaa == null || p��ttyy == null)
      {
         return 0;
      }
      return DateUtil.p�ivi�V�liss�(alkaa, p��ttyy);
   }

   public Jakso getP��llekk�isyys(Jakso jakso)
   {
      if (jakso.getAlkaa() == null || jakso.getP��ttyy() == null)
      {
         return new Jakso();
      }
      boolean alkuOsuu = DateUtil.onkoV�liss�(this, jakso.getAlkaa());
      boolean loppuOsuu = DateUtil.onkoV�liss�(this, jakso.getP��ttyy());
      if (alkuOsuu && loppuOsuu)
      {
         return new Jakso(jakso);
      } else if (alkuOsuu && !loppuOsuu)
      {
         return new Jakso(jakso.getAlkaa(), p��ttyy);
      } else if (!alkuOsuu && loppuOsuu)
      {
         return new Jakso(alkaa, jakso.getP��ttyy());
      }
      return new Jakso();
   }

   public void setAlkaa(Date alkaa)
   {
      this.alkaa = alkaa;
   }

   public void setP��ttyy(Date p��ttyy)
   {
      this.p��ttyy = p��ttyy;
   }

   public boolean isAvoin()
   {
      return (alkaa == null && p��ttyy != null) || (alkaa != null && p��ttyy == null);
   }

   public boolean isRajatRistiss�()
   {
      return alkaa != null && p��ttyy != null && !DateUtil.onkoAiemmin(alkaa, p��ttyy);
   }

   @Override
   public String toString()
   {
      DateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
      String alkaaStr = alkaa == null ? "" : sdf.format(alkaa);
      String p��ttyyStr = p��ttyy == null ? "" : sdf.format(p��ttyy);
      return (alkaaStr == null && p��ttyyStr == null) ? "" : String.format("%s -> %s", alkaaStr, p��ttyyStr);
   }
}
