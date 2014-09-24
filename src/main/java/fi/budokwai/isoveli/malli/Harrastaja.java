package fi.budokwai.isoveli.malli;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.enterprise.inject.Typed;
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
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@NamedQueries(
{ @NamedQuery(name = "kortti", query = "select h from Harrastaja h where h.korttinumero=:kortti"),
      @NamedQuery(name = "harrastajat", query = "select h from Harrastaja h order by h.henkil�.etunimi") })
@SequenceGenerator(name = "harrastaja_seq", sequenceName = "harrastaja_seq", allocationSize = 1, initialValue = 2)
@Typed(
{})
public class Harrastaja
{
   public static Harrastaja Tuntematon = new Harrastaja();

   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "harrastaja_seq")
   private int id;

   @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = false)
   @JoinColumn(name = "henkilo")
   @Valid
   private Henkil� henkil� = new Henkil�();

   @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
   @JoinColumn(name = "huoltaja")
   @Valid
   private Henkil� huoltaja;

   @OneToMany(cascade = CascadeType.ALL, mappedBy = "harrastaja", orphanRemoval = true)
   @OrderBy("vyoarvo")
   private List<Vy�koe> vy�kokeet;

   @Size(max = 10)
   @Column(name="jasennumero")
   private String j�sennumero;

   @Size(max = 100)
   private String korttinumero;

   @Size(max = 10)
   private String lisenssinumero;

   @Temporal(TemporalType.DATE)
   @NotNull
   private Date syntynyt;

   @Size(max = 1)
   @NotNull
   private String sukupuoli;

   @Transient
   private boolean vaatiiHuoltajan;

   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public Henkil� getHenkil�()
   {
      return henkil�;
   }

   public void setHenkil�(Henkil� henkil�)
   {
      this.henkil� = henkil�;
   }

   public Henkil� getHuoltaja()
   {
      return huoltaja;
   }

   public void setHuoltaja(Henkil� huoltaja)
   {
      this.huoltaja = huoltaja;
   }

   public void setJ�sennumero(String j�sennumero)
   {
      this.j�sennumero = j�sennumero;
   }

   public String getKorttinumero()
   {
      return korttinumero;
   }

   public void setKorttinumero(String korttinumero)
   {
      this.korttinumero = korttinumero;
   }

   public String getLisenssinumero()
   {
      return lisenssinumero;
   }

   public void setLisenssinumero(String lisenssinumero)
   {
      this.lisenssinumero = lisenssinumero;
   }

   public Date getSyntynyt()
   {
      return syntynyt;
   }

   public void setSyntynyt(Date syntynyt)
   {
      this.syntynyt = syntynyt;
   }

   public String getSukupuoli()
   {
      return sukupuoli;
   }

   public void setSukupuoli(String sukupuoli)
   {
      this.sukupuoli = sukupuoli;
   }

   private int ika()
   {
      Calendar kalenteri = Calendar.getInstance();
      kalenteri.setTime(syntynyt);
      Calendar tanaan = Calendar.getInstance();
      int ika = tanaan.get(Calendar.YEAR) - kalenteri.get(Calendar.YEAR);
      if (tanaan.get(Calendar.MONTH) < kalenteri.get(Calendar.MONTH))
      {
         ika--;
      } else if (tanaan.get(Calendar.MONTH) == kalenteri.get(Calendar.MONTH)
         && tanaan.get(Calendar.DAY_OF_MONTH) < kalenteri.get(Calendar.DAY_OF_MONTH))
      {
         ika--;
      }
      return ika;
   }

   @Override
   public int hashCode()
   {
      return Integer.valueOf(id).hashCode();
   }

   @Override
   public boolean equals(Object toinen)
   {
      Harrastaja toinenHarrastaja = (Harrastaja) toinen;
      return id == toinenHarrastaja.getId();
   }

   public boolean isVaatiiHuoltajan()
   {
      return vaatiiHuoltajan;
   }

   public void setVaatiiHuoltajan(boolean vaatiiHuoltajan)
   {
      this.vaatiiHuoltajan = vaatiiHuoltajan;
   }

   public boolean isAlaikainen()
   {
      return ika() < 18;
   }

   public List<Vy�koe> getVy�kokeet()
   {
      return vy�kokeet;
   }

   public Vy�arvo getTuoreinVy�arvo()
   {
      return vy�kokeet.get(vy�kokeet.size() - 1).getVy�arvo();
   }

   public String getJ�sennumero()
   {
      return j�sennumero;
   }

   public void setVy�kokeet(List<Vy�koe> vy�kokeet)
   {
      this.vy�kokeet = vy�kokeet;
   }
}
