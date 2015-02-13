package fi.budokwai.isoveli.malli;

import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import fi.budokwai.isoveli.util.DateUtil;

@Entity
@Table(name = "vyokoe")
public class Vy�koe
{
   public static final Vy�koe EI_OOTA = new Vy�koe();

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @ManyToOne
   @JoinColumn(name = "vyoarvo")
   private Vy�arvo vy�arvo;

   @ManyToOne
   @JoinColumn(name = "harrastaja")
   private Harrastaja harrastaja;

   @Temporal(TemporalType.DATE)
   @Column(name = "paiva")
   private Date p�iv�;

   public Date getP�iv�()
   {
      return p�iv�;
   }

   public void setP�iv�(Date p�iv�)
   {
      this.p�iv� = p�iv�;
   }

   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public Vy�arvo getVy�arvo()
   {
      return vy�arvo;
   }

   public void setVy�arvo(Vy�arvo vy�arvo)
   {
      this.vy�arvo = vy�arvo;
   }

   public Harrastaja getHarrastaja()
   {
      return harrastaja;
   }

   public void setHarrastaja(Harrastaja harrastaja)
   {
      this.harrastaja = harrastaja;
   }

   public boolean isTuoreinVy�koe()
   {
      return harrastaja.getTuoreinVy�arvo() == vy�arvo;
   }

   @Transient
   public boolean isPoistettavissa()
   {
      return id > 0;
   }

   public LocalDate getKoska()
   {
      return DateUtil.Date2LocalDate(p�iv�);
   }

   private Period getAikaEdellisest�Kokeesta()
   {
      Optional<Vy�koe> edellinenVy�koe = harrastaja
         .getVy�kokeet()
         .stream()
         .filter(v -> v.getVy�arvo().getJ�rjestys() < vy�arvo.getJ�rjestys())
         .sorted(
            (v1, v2) -> Integer.valueOf(v2.vy�arvo.getJ�rjestys())
               .compareTo(Integer.valueOf(v1.vy�arvo.getJ�rjestys()))).findFirst();
      return edellinenVy�koe.isPresent() ? DateUtil.aikav�li(edellinenVy�koe.get().getP�iv�()) : DateUtil
         .aikav�li(harrastaja.getLuotu());
   }

   public String getAikaaV�liss�()
   {
      Period aika = getAikaEdellisest�Kokeesta();
      return DateUtil.aikav�li2String(aika);
   }

   public boolean equals(Object toinen)
   {
      if (!(toinen instanceof Vy�koe))
      {
         return false;
      }
      Vy�koe toinenVy�koe = (Vy�koe) toinen;
      return id == toinenVy�koe.getId();
   }

   public int hashCode()
   {
      return Integer.valueOf(id).hashCode();
   }

}