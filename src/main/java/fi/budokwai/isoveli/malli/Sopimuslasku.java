package fi.budokwai.isoveli.malli;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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

import fi.budokwai.isoveli.util.Util;

@Entity
@Table(name = "sopimuslasku")
public class Sopimuslasku
{
   @Id
   @GeneratedValue
   private int id;

   @NotNull
   @OneToOne(cascade = CascadeType.PERSIST, mappedBy = "sopimuslasku", optional = false)
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

   public Sopimuslasku(Sopimus sopimus)
   {
      this.sopimus = sopimus;
      Date viimeLaskutus = sopimus.getViimeksiLaskutettu();
      alkaa = viimeLaskutus == null ? sopimus.getLuotu() : viimeLaskutus;
      p‰‰ttyy = haeP‰‰ttymisp‰iv‰(sopimus);
      sopimus.getSopimuslaskut().add(this);
   }

   public String getJakso()
   {
      SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
      return String.format("%s-%s", sdf.format(alkaa), sdf.format(p‰‰ttyy));
   }

   private Date haeP‰‰ttymisp‰iv‰(Sopimus sopimus)
   {
      LocalDate sopimusLoppuu;
      LocalDate jaksoLoppuu = LocalDateTime.ofInstant(new Date(alkaa.getTime()).toInstant(), ZoneId.systemDefault()).plus(sopimus.getMaksuv‰li(), ChronoUnit.MONTHS).toLocalDate();
      if (sopimus.getUmpeutuu() == null)
      {
         sopimusLoppuu = jaksoLoppuu;
      } else
      {
         sopimusLoppuu = Util.date2LocalDateTime(sopimus.getUmpeutuu());
      }
      LocalDate loppuuEnsin = sopimusLoppuu.isBefore(jaksoLoppuu) ? sopimusLoppuu : jaksoLoppuu;
      return Date.from(loppuuEnsin.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
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
   
}
