package fi.budokwai.isoveli.malli;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@NamedQueries(
{ @NamedQuery(name = "maksamattomat_laskut", query = "select l from Lasku l where l.maksettu is null and l.er�p�iv� > :t�n��n order by l.er�p�iv� desc"),
      @NamedQuery(name = "maksetut_laskut", query = "select l from Lasku l where l.maksettu is not null order by l.maksettu desc"),
      @NamedQuery(name = "my�h�styneet_laskut", query = "select l from Lasku l where l.maksettu is null and l.er�p�iv� < :t�n��n order by l.er�p�iv� desc") })
public class Lasku
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @OneToMany(mappedBy = "lasku", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<Laskurivi> laskurivit = new ArrayList<Laskurivi>();

   @Temporal(TemporalType.DATE)
   @Column(name = "erapaiva")
   private Date er�p�iv�;

   @Temporal(TemporalType.DATE)
   private Date maksettu;

   @OneToOne(optional = true)
   @JoinColumn(name = "harrastaja")
   private Harrastaja harrastaja;

   @Temporal(TemporalType.TIMESTAMP)
   private Date luotu = new Date();

   public Lasku()
   {
   }

   public Lasku(List<Sopimus> sopimukset)
   {
      harrastaja = sopimukset.stream().findFirst().get().getHarrastaja();
      er�p�iv� = haeEr�p�iv�(14);
      sopimukset.forEach(sopimus -> {
         Laskurivi laskurivi = new Laskurivi(sopimus);
         laskurivi.setLasku(this);
         laskurivit.add(laskurivi);
         laskurivi.setRivinumero(laskurivit.size());
      });
   }

   private Date haeEr�p�iv�(int p�ivi�)
   {
      LocalDate nyt = LocalDateTime.now().toLocalDate();
      nyt.plus(p�ivi�, ChronoUnit.DAYS);
      return Date.from(nyt.atStartOfDay().atZone(ZoneOffset.systemDefault()).toInstant());
   }

   public double getYhteishinta()
   {
      return laskurivit.stream().mapToDouble((lr) -> lr.getRivihinta()).sum();
   }

   public double getYhteisAlv()
   {
      return laskurivit.stream().mapToDouble((lr) -> lr.getAlv()).sum();
   }

   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public List<Laskurivi> getLaskurivit()
   {
      return laskurivit;
   }

   public void setLaskurivit(List<Laskurivi> laskurivit)
   {
      this.laskurivit = laskurivit;
   }

   public Date getMaksettu()
   {
      return maksettu;
   }

   public void setMaksettu(Date maksettu)
   {
      this.maksettu = maksettu;
   }

   public boolean isMaksettu()
   {
      return maksettu != null;
   }

   public Harrastaja getHarrastaja()
   {
      return harrastaja;
   }

   public void setHarrastaja(Harrastaja harrastaja)
   {
      this.harrastaja = harrastaja;
   }

   public Date getLuotu()
   {
      return luotu;
   }

   public void setLuotu(Date luotu)
   {
      this.luotu = luotu;
   }

   public Date getEr�p�iv�()
   {
      return er�p�iv�;
   }

   public void setEr�p�iv�(Date er�p�iv�)
   {
      this.er�p�iv� = er�p�iv�;
   }

}
