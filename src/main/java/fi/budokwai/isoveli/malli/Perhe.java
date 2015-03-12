package fi.budokwai.isoveli.malli;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "perhe")
@NamedQueries(
{
      @NamedQuery(name = "perheet", query = "select p from Perhe p order by p.nimi"),
      @NamedQuery(name = "poista_turhat_huoltajat", query = "delete from Henkilö h where not exists (select ha from Harrastaja ha where ha.huoltaja=h)"),
      @NamedQuery(name = "poista_tyhjät_perheet", query = "delete from Perhe p where not exists (select h from Henkilö h where h.perhe.id=p.id)") })
public class Perhe
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   private String nimi;

   @OneToMany(mappedBy = "perhe", cascade =
   { CascadeType.PERSIST })
   private List<Henkilö> perheenjäsenet = new ArrayList<Henkilö>();

   @OneToOne(cascade =
   { CascadeType.PERSIST, CascadeType.MERGE })
   @JoinColumn(name = "osoite")
   private Osoite osoite = new Osoite();

   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public List<Henkilö> getPerheenjäsenet()
   {
      return perheenjäsenet;
   }

   public void setPerheenjäsenet(List<Henkilö> perheenjäsenet)
   {
      this.perheenjäsenet = perheenjäsenet;
   }

   public Osoite getOsoite()
   {
      return osoite;
   }

   public void setOsoite(Osoite osoite)
   {
      this.osoite = osoite;
   }

   public String getNimi()
   {
      return nimi;
   }

   public void setNimi(String nimi)
   {
      this.nimi = nimi;
   }

   public String getKuvaus()
   {
      String etunimet = perheenjäsenet.stream()
         .map(h -> h.isAlaikäinen() ? h.getEtunimi() : String.format("*%s", h.getEtunimi()))
         .collect(Collectors.joining(", "));
      return "".equals(etunimet) ? nimi : String.format("%s (%s)", nimi, etunimet);
   }

   public List<Henkilö> getHuoltajat()
   {
      return perheenjäsenet.stream().filter(h -> !h.isAlaikäinen()).collect(Collectors.toList());
   }

   @Override
   public int hashCode()
   {
      return Integer.valueOf(id).hashCode();
   }

   @Override
   public boolean equals(Object toinen)
   {
      if (!(toinen instanceof Perhe))
      {
         return false;
      }
      Perhe toinenPerhe = (Perhe) toinen;
      return id == toinenPerhe.getId();
   }

   public void lisääPerheenjäsen(Henkilö henkilö)
   {
      perheenjäsenet.add(henkilö);
      henkilö.setPerhe(this);
   }

   public void muodosta()
   {
      päätteleNimi();
      omaksuOsoite();
      asetaHuoltajuudet();
   }

   private void asetaHuoltajuudet()
   {
      List<Henkilö> huoltajat = perheenjäsenet.stream().filter(h -> !h.isHarrastaja()).collect(Collectors.toList());
      if (!huoltajat.isEmpty())
      {
         Henkilö huoltaja = huoltajat.iterator().next();
         perheenjäsenet.stream().filter(h -> h.isAlaikäinen()).forEach(h -> ((Harrastaja) h).setHuoltaja(huoltaja));
      }
   }

   private void omaksuOsoite()
   {
      List<Henkilö> osoitteelliset = perheenjäsenet.stream().filter(h -> h.getOsoite() != null)
         .collect(Collectors.toList());
      if (!osoitteelliset.isEmpty())
      {
         osoite = osoitteelliset.iterator().next().getOsoite();
      }
      osoitteelliset.forEach(h -> h.setOsoite(null));
   }

   private void päätteleNimi()
   {
      List<Henkilö> huoltajat = perheenjäsenet.stream().filter(h -> !h.isHarrastaja()).collect(Collectors.toList());
      if (!huoltajat.isEmpty())
      {
         nimi = huoltajat.iterator().next().getSukunimi();
      }
      if (nimi != null)
      {
         return;
      }
      List<Henkilö> täysiIkäiset = perheenjäsenet.stream().filter(h -> !h.isAlaikäinen()).collect(Collectors.toList());
      if (!täysiIkäiset.isEmpty())
      {
         nimi = täysiIkäiset.iterator().next().getSukunimi();
      }
      if (nimi != null)
      {
         return;
      }
      nimi = perheenjäsenet.iterator().next().getSukunimi();
   }

   public List<Henkilö> getHuollettavat(Henkilö henkilö)
   {
      return perheenjäsenet.stream().filter(h -> h.isHarrastaja()).map(h -> Harrastaja.class.cast(h))
         .filter(h -> h.getHuoltaja() != null && h.getHuoltaja().getId() == henkilö.getId())
         .collect(Collectors.toList());
   }

   public String getOletusICE()
   {
      Optional<Henkilö> iceHenkilö = perheenjäsenet.stream()
         .filter(h -> !h.isAlaikäinen() && h.getYhteystiedot() != null && h.getYhteystiedot().isLöytyyPuhelin())
         .findFirst();
      return iceHenkilö.isPresent() ? iceHenkilö.get().getYhteystiedot().getPuhelinnumero() : null;
   }

   public Sopimus getPerheenKertakortti()
   {
      Optional<Sopimus> sopimus = perheenjäsenet.stream().filter(h -> h.isHarrastaja())
         .map(h -> Harrastaja.class.cast(h)).flatMap(h -> h.getSopimukset().stream())
         .filter(s -> s.getTyyppi().isTreenikertoja()).sorted((s1, s2) -> {
            return Integer.valueOf(s1.getTreenikertojaJäljellä()).compareTo(Integer.valueOf(s2.getTreenikertojaJäljellä()));
         }).findFirst();
      return sopimus.isPresent() ? sopimus.get() : null;
   }

   public int getPerheenTreenikerrat()
   {
      Sopimus perheenKertakortti = getPerheenKertakortti();
      return perheenKertakortti == null ? 0 : perheenKertakortti.getTreenikertojaJäljellä();
   }

   public boolean isTallennettu()
   {
      return id > 0;
   }

   @Override
   public String toString()
   {
      return getKuvaus();
   }

}
