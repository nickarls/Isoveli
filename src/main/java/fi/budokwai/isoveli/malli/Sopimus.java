package fi.budokwai.isoveli.malli;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
@NamedQuery(name = "laskuttamattomat_sopimukset", query = "select s from Sopimus s where s.tyyppi.laskutettava='K' and s.laskurivi is null")
public class Sopimus
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @ManyToOne(optional = false)
   @JoinColumn(name = "harrastaja")
   private Harrastaja harrastaja;

   @OneToOne(optional = false)
   @JoinColumn(name = "tyyppi")
   @NotNull
   private Sopimustyyppi tyyppi;

   @OneToOne(optional = true)
   @JoinColumn(name = "laskurivi")
   private Laskurivi laskurivi;

   @Temporal(TemporalType.DATE)
   private Date umpeutuu;

   private int treenikertoja;

   @Column(name = "maksuvali")
   private int maksuväli;

   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public Harrastaja getHarrastaja()
   {
      return harrastaja;
   }

   public void setHarrastaja(Harrastaja harrastaja)
   {
      this.harrastaja = harrastaja;
   }

   public Date getUmpeutuu()
   {
      return umpeutuu;
   }

   public void setUmpeutuu(Date umpeutuu)
   {
      this.umpeutuu = umpeutuu;
   }

   public int getTreenikertoja()
   {
      return treenikertoja;
   }

   public void setTreenikertoja(int treenikertoja)
   {
      this.treenikertoja = treenikertoja;
   }

   public int getMaksuväli()
   {
      return maksuväli;
   }

   public void setMaksuväli(int maksuväli)
   {
      this.maksuväli = maksuväli;
   }

   public Sopimustyyppi getTyyppi()
   {
      return tyyppi;
   }

   public void setTyyppi(Sopimustyyppi tyyppi)
   {
      this.tyyppi = tyyppi;
   }

   public boolean isPoistettavissa()
   {
      return id > 0 && laskurivi == null;
   }

   @Override
   public int hashCode()
   {
      return Integer.valueOf(id).hashCode();
   }

   @Override
   public boolean equals(Object toinen)
   {
      if (!(toinen instanceof Sopimus))
      {
         return false;
      }
      Sopimus toinenSopimus = (Sopimus) toinen;
      return id == toinenSopimus.getId();
   }

   public boolean isVoimassa()
   {
      if (umpeutuu == null)
      {
         return true;
      }
      return umpeutuu.after(new Date());
   }

   public String getTuotenimi()
   {
      return String.format("%s (%s)", tyyppi.getNimi(), harrastaja.getEtunimi());
   }

   public Laskurivi getLaskurivi()
   {
      return laskurivi;
   }

   public void setLaskurivi(Laskurivi laskurivi)
   {
      this.laskurivi = laskurivi;
   }

}
