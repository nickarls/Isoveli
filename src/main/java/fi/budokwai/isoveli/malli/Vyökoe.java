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
public class Vyökoe
{
   public static final Vyökoe EI_OOTA = new Vyökoe();

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @ManyToOne
   @JoinColumn(name = "vyoarvo")
   private Vyöarvo vyöarvo;

   @ManyToOne
   @JoinColumn(name = "harrastaja")
   private Harrastaja harrastaja;

   @Temporal(TemporalType.DATE)
   @Column(name = "paiva")
   private Date päivä;

   public Date getPäivä()
   {
      return päivä;
   }

   public void setPäivä(Date päivä)
   {
      this.päivä = päivä;
   }

   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public Vyöarvo getVyöarvo()
   {
      return vyöarvo;
   }

   public void setVyöarvo(Vyöarvo vyöarvo)
   {
      this.vyöarvo = vyöarvo;
   }

   public Harrastaja getHarrastaja()
   {
      return harrastaja;
   }

   public void setHarrastaja(Harrastaja harrastaja)
   {
      this.harrastaja = harrastaja;
   }

   public boolean isTuoreinVyökoe()
   {
      return harrastaja.getTuoreinVyöarvo() == vyöarvo;
   }

   @Transient
   public boolean isPoistettavissa()
   {
      return id > 0;
   }

   public LocalDate getKoska()
   {
      return DateUtil.Date2LocalDate(päivä);
   }

   private Period getAikaEdellisestäKokeesta()
   {
      Optional<Vyökoe> edellinenVyökoe = harrastaja
         .getVyökokeet()
         .stream()
         .filter(v -> v.getVyöarvo().getJärjestys() < vyöarvo.getJärjestys())
         .sorted(
            (v1, v2) -> Integer.valueOf(v2.vyöarvo.getJärjestys())
               .compareTo(Integer.valueOf(v1.vyöarvo.getJärjestys()))).findFirst();
      return edellinenVyökoe.isPresent() ? DateUtil.aikaväli(edellinenVyökoe.get().getPäivä()) : DateUtil
         .aikaväli(harrastaja.getLuotu());
   }

   public String getAikaaVälissä()
   {
      Period aika = getAikaEdellisestäKokeesta();
      return DateUtil.aikaväli2String(aika);
   }

   public boolean equals(Object toinen)
   {
      if (!(toinen instanceof Vyökoe))
      {
         return false;
      }
      Vyökoe toinenVyökoe = (Vyökoe) toinen;
      return id == toinenVyökoe.getId();
   }

   public int hashCode()
   {
      return Integer.valueOf(id).hashCode();
   }

}