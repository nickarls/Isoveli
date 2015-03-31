package fi.budokwai.isoveli.malli;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import fi.budokwai.isoveli.IsoveliPoikkeus;
import fi.budokwai.isoveli.malli.validointi.Ik�rajaj�rjestys;
import fi.budokwai.isoveli.malli.validointi.Vy�rajaj�rjestys;
import fi.budokwai.isoveli.util.Util;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "vyokoetilaisuus")
@Ik�rajaj�rjestys(alaraja = "ik�Alaraja", yl�raja = "ik�Yl�raja")
@Vy�rajaj�rjestys(alaraja = "vy�Alaraja", yl�raja = "vy�Yl�raja")
@NamedQueries(
{ @NamedQuery(name = "vy�koetilaisuudet", query = "select v from Vy�koetilaisuus v order by v.koska desc") })
public class Vy�koetilaisuus
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @Temporal(TemporalType.TIMESTAMP)
   @NotNull
   private Date koska;

   @ManyToOne
   @JoinColumn(name = "vyoalaraja")
   private Vy�arvo vy�Alaraja;

   @ManyToOne
   @JoinColumn(name = "vyoylaraja")
   private Vy�arvo vy�Yl�raja;

   @Column(name = "ikaalaraja")
   private Integer ik�Alaraja;

   @Column(name = "ikaylaraja")
   private Integer ik�Yl�raja;

   @ManyToOne
   @JoinColumn(name = "pitaja")
   @NotNull
   private Harrastaja vy�kokeenPit�j�;

   @OneToMany(mappedBy = "vy�koetilaisuus", cascade =
   { CascadeType.MERGE }, orphanRemoval = true)
   private List<Vy�kokelas> vy�kokelaat = new ArrayList<Vy�kokelas>();

   @Transient
   private String harrastajaHaku;

   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public Date getKoska()
   {
      return koska;
   }

   public void setKoska(Date koska)
   {
      this.koska = koska;
   }

   public Vy�arvo getVy�Alaraja()
   {
      return vy�Alaraja;
   }

   public void setVy�Alaraja(Vy�arvo vy�Alaraja)
   {
      this.vy�Alaraja = vy�Alaraja;
   }

   public Vy�arvo getVy�Yl�raja()
   {
      return vy�Yl�raja;
   }

   public void setVy�Yl�raja(Vy�arvo vy�Yl�raja)
   {
      this.vy�Yl�raja = vy�Yl�raja;
   }

   public Integer getIk�Alaraja()
   {
      return ik�Alaraja;
   }

   public void setIk�Alaraja(Integer ik�Alaraja)
   {
      this.ik�Alaraja = ik�Alaraja;
   }

   public Integer getIk�Yl�raja()
   {
      return ik�Yl�raja;
   }

   public void setIk�Yl�raja(Integer ik�Yl�raja)
   {
      this.ik�Yl�raja = ik�Yl�raja;
   }

   public Harrastaja getVy�kokeenPit�j�()
   {
      return vy�kokeenPit�j�;
   }

   public void setVy�kokeenPit�j�(Harrastaja vy�kokeenPit�j�)
   {
      this.vy�kokeenPit�j� = vy�kokeenPit�j�;
   }

   public String getIk�rajat()
   {
      return Util.getIk�rajat(ik�Alaraja, ik�Yl�raja);
   }

   public boolean isVy�arvorajoitettu()
   {
      return vy�Alaraja != null || vy�Yl�raja != null;
   }

   public void poistotarkistus()
   {
      tarkistaVy�kokelaat();
   }

   private void tarkistaVy�kokelaat()
   {
      if (vy�kokelaat.isEmpty())
      {
         return;
      }
      StringJoiner stringJoiner = new StringJoiner(", ");
      vy�kokelaat.forEach(v -> {
         stringJoiner.add(v.toString());
      });
      String viesti = String.format("Vy�koetilaisuudessa on vy�kokelaita ja sit� ei voi poistaa (%dkpl: %s...)",
         vy�kokelaat.size(), stringJoiner.toString());
      throw new IsoveliPoikkeus(viesti);
   }

   public int getOsallistujia()
   {
      return vy�kokelaat.size();
   }

   public List<Vy�kokelas> getVy�kokelaat()
   {
      return vy�kokelaat;
   }

   public void setVy�kokelaat(List<Vy�kokelas> vy�kokelaat)
   {
      this.vy�kokelaat = vy�kokelaat;
   }

   public String getHarrastajaHaku()
   {
      return harrastajaHaku;
   }

   public void setHarrastajaHaku(String harrastajaHaku)
   {
      this.harrastajaHaku = harrastajaHaku;
   }

   public void lis��Vy�kokelas(Harrastaja harrastaja, Vy�arvo uusiVy�arvo)
   {
      Optional<Harrastaja> olemassa = vy�kokelaat.stream().map(v -> v.getHarrastaja())
         .filter(h -> h.getNimi().equals(harrastaja.getNimi())).findFirst();
      if (olemassa.isPresent())
      {
         throw new IsoveliPoikkeus(String.format("Harrastaja %s on jo ilmoitettu kokeeseen", harrastaja.getNimi()));
      }
      Vy�kokelas vy�kokelas = new Vy�kokelas(harrastaja);
      vy�kokelas.setVy�koetilaisuus(this);
      vy�kokelas.setTavoite(uusiVy�arvo);
      vy�kokelaat.add(vy�kokelas);
   }

   @Override
   public String toString()
   {
      String pvm = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(koska);
      String alaIk� = ik�Alaraja == null ? "" : ik�Alaraja.toString();
      String yl�Ik� = ik�Yl�raja == null ? "" : ik�Yl�raja.toString();
      String alaVy� = vy�Alaraja == null ? "" : vy�Alaraja.getNimi();
      String yl�Vy� = vy�Yl�raja == null ? "" : vy�Yl�raja.getNimi();
      return String.format("Vy�koetilaisuus %s (%d kpl), ik� [%s-%s], vy�t [%s-%s], pit�j� %s", pvm,
         vy�kokelaat.size(), alaIk�, yl�Ik�, alaVy�, yl�Vy�, vy�kokeenPit�j�.getNimi());
   }

}
