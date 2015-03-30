package fi.budokwai.isoveli.malli;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

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
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import fi.budokwai.isoveli.IsoveliPoikkeus;
import fi.budokwai.isoveli.malli.validointi.Ikärajajärjestys;
import fi.budokwai.isoveli.malli.validointi.Vyörajajärjestys;
import fi.budokwai.isoveli.util.Util;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "vyokoetilaisuus")
@Ikärajajärjestys(alaraja = "ikäAlaraja", yläraja = "ikäYläraja")
@Vyörajajärjestys(alaraja = "vyöAlaraja", yläraja = "vyöYläraja")
@NamedQueries(
{ @NamedQuery(name = "vyökoetilaisuudet", query = "select v from Vyökoetilaisuus v order by v.koska desc") })
public class Vyökoetilaisuus
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @Temporal(TemporalType.TIMESTAMP)
   @NotNull
   private Date koska;

   @ManyToOne
   @JoinColumn(name = "vyoalaraja")
   private Vyöarvo vyöAlaraja;

   @ManyToOne
   @JoinColumn(name = "vyoylaraja")
   private Vyöarvo vyöYläraja;

   @Column(name = "ikaalaraja")
   private Integer ikäAlaraja;

   @Column(name = "ikaylaraja")
   private Integer ikäYläraja;

   @ManyToOne
   @JoinColumn(name = "pitaja")
   @NotNull
   private Harrastaja vyökokeenPitäjä;

   @OneToMany(mappedBy = "vyökoetilaisuus")
   private List<Vyökokelas> vyökokelaat = new ArrayList<Vyökokelas>();

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

   public Vyöarvo getVyöAlaraja()
   {
      return vyöAlaraja;
   }

   public void setVyöAlaraja(Vyöarvo vyöAlaraja)
   {
      this.vyöAlaraja = vyöAlaraja;
   }

   public Vyöarvo getVyöYläraja()
   {
      return vyöYläraja;
   }

   public void setVyöYläraja(Vyöarvo vyöYläraja)
   {
      this.vyöYläraja = vyöYläraja;
   }

   public Integer getIkäAlaraja()
   {
      return ikäAlaraja;
   }

   public void setIkäAlaraja(Integer ikäAlaraja)
   {
      this.ikäAlaraja = ikäAlaraja;
   }

   public Integer getIkäYläraja()
   {
      return ikäYläraja;
   }

   public void setIkäYläraja(Integer ikäYläraja)
   {
      this.ikäYläraja = ikäYläraja;
   }

   public Harrastaja getVyökokeenPitäjä()
   {
      return vyökokeenPitäjä;
   }

   public void setVyökokeenPitäjä(Harrastaja vyökokeenPitäjä)
   {
      this.vyökokeenPitäjä = vyökokeenPitäjä;
   }

   public String getIkärajat()
   {
      return Util.getIkärajat(ikäAlaraja, ikäYläraja);
   }

   public boolean isVyöarvorajoitettu()
   {
      return vyöAlaraja != null || vyöYläraja != null;
   }

   public void poistotarkistus()
   {
      tarkistaVyökokelaat();
   }

   private void tarkistaVyökokelaat()
   {
      if (vyökokelaat.isEmpty())
      {
         return;
      }
      StringJoiner stringJoiner = new StringJoiner(", ");
      vyökokelaat.forEach(v -> {
         stringJoiner.add(v.toString());
      });
      String viesti = String.format("Vyökoetilaisuudessa on vyökokelaita ja sitä ei voi poistaa (%dkpl: %s...)",
         vyökokelaat.size(), stringJoiner.toString());
      throw new IsoveliPoikkeus(viesti);
   }
}
