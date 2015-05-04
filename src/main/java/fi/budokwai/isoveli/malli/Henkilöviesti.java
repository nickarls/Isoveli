package fi.budokwai.isoveli.malli;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "henkiloviesti")
@DynamicInsert
@DynamicUpdate
public class Henkilöviesti
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @ManyToOne
   @JoinColumn(name = "viesti")
   @NotNull
   private Viesti viesti;

   @ManyToOne
   @JoinColumn(name = "viestilaatikko")
   @NotNull
   private Viestilaatikko viestilaatikko;

   @Type(type = "KylläEi")
   private boolean luettu;

   @Type(type = "KylläEi")
   private boolean arkistoitu;

   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public Viesti getViesti()
   {
      return viesti;
   }

   public void setViesti(Viesti viesti)
   {
      this.viesti = viesti;
   }

   public boolean isLuettu()
   {
      return luettu;
   }

   public void setLuettu(boolean luettu)
   {
      this.luettu = luettu;
   }

   public boolean isArkistoitu()
   {
      return arkistoitu;
   }

   public void setArkistoitu(boolean arkistoitu)
   {
      this.arkistoitu = arkistoitu;
   }

   public Viestilaatikko getViestilaatikko()
   {
      return viestilaatikko;
   }

   public void setViestilaatikko(Viestilaatikko viestilaatikko)
   {
      this.viestilaatikko = viestilaatikko;
   }
}
