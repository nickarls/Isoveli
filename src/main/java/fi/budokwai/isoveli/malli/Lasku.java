package fi.budokwai.isoveli.malli;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Lasku
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @OneToMany(mappedBy = "lasku", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<Laskurivi> laskurivit = new ArrayList<Laskurivi>();

   private Date maksettu;

   @OneToOne(optional = true)
   @JoinColumn(name = "harrastaja")
   private Harrastaja harrastaja;

   public Lasku()
   {
   }

   public Lasku(List<Sopimus> sopimukset)
   {
      harrastaja = sopimukset.stream().findFirst().get().getHarrastaja();
      sopimukset.forEach(sopimus -> {
         Laskurivi laskurivi = new Laskurivi(sopimus);
         laskurivi.setLasku(this);
         laskurivit.add(laskurivi);
         laskurivi.setRivinumero(laskurivit.size());
      });
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

}
