package fi.budokwai.isoveli.malli;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import fi.budokwai.isoveli.util.DateUtil;

@Entity
@Table(name = "sopimuslasku")
public class Sopimuslasku
{
   @Id
   @GeneratedValue
   private int id;

   @NotNull
   @OneToOne(cascade = CascadeType.PERSIST, optional = false)
   @JoinColumn(name = "laskurivi")
   private Laskurivi laskurivi;

   @ManyToOne(optional = false)
   @JoinColumn(name = "sopimus")
   private Sopimus sopimus;

   @NotNull
   @Temporal(TemporalType.DATE)
   private Date alkaa;

   @NotNull
   @Temporal(TemporalType.DATE)
   @Column(name = "paattyy")
   private Date p‰‰ttyy;

   public Sopimuslasku()
   {
   }

   public Sopimuslasku(Sopimus sopimus, Laskutuskausi laskutuskausi)
   {
      this.sopimus = sopimus;
      alkaa = laskutuskausi.getKausi().getAlkaa();
      p‰‰ttyy = laskutuskausi.getKausi().getP‰‰ttyy();
      sopimus.getSopimuslaskut().add(this);
   }

   public String getJakso()
   {
      return DateUtil.aikav‰li2String(alkaa, p‰‰ttyy);
   }

   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public Laskurivi getLaskurivi()
   {
      return laskurivi;
   }

   public void setLaskurivi(Laskurivi laskurivi)
   {
      this.laskurivi = laskurivi;
   }

   public Sopimus getSopimus()
   {
      return sopimus;
   }

   public void setSopimus(Sopimus sopimus)
   {
      this.sopimus = sopimus;
   }

   public Date getAlkaa()
   {
      return alkaa;
   }

   public void setAlkaa(Date alkaa)
   {
      this.alkaa = alkaa;
   }

   public Date getP‰‰ttyy()
   {
      return p‰‰ttyy;
   }

   public void setP‰‰ttyy(Date p‰‰ttyy)
   {
      this.p‰‰ttyy = p‰‰ttyy;
   }

   public int getKuukausiaV‰liss‰()
   {
      return DateUtil.kuukausiaV‰liss‰(alkaa, p‰‰ttyy);
   }

}
