package fi.budokwai.isoveli.malli;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import fi.budokwai.isoveli.util.Util;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "henkilo")
@NamedQueries(
{
      @NamedQuery(name = "henkilö", query = "select h from Henkilö h where h.salasana = :salasana and h.etunimi = :etunimi and h.sukunimi = :sukunimi"),
      @NamedQuery(name = "samanniminen_käyttäjä", query = "select h from Henkilö h where h.etunimi = :etunimi and h.sukunimi = :sukunimi and h.id <> :id"),
      @NamedQuery(name = "nimetty_henkilö", query = "select h from Henkilö h where h.etunimi = :etunimi and h.sukunimi = :sukunimi") })
@Inheritance(strategy = InheritanceType.JOINED)
public class Henkilö implements Serializable
{
   private static final long serialVersionUID = 1L;

   public static final Henkilö EI_KIRJAUTUNUT = new Henkilö();

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   protected int id;

   @Size(max = 50)
   @NotNull
   protected String etunimi;

   @Size(max = 50)
   @NotNull
   protected String sukunimi;

   @OneToOne(cascade =
   { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
   @JoinColumn(name = "osoite")
   @Valid
   protected Osoite osoite;

   @OneToOne(cascade =
   { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
   @JoinColumn(name = "yhteystiedot")
   @Valid
   protected Yhteystieto yhteystiedot;

   @ManyToMany
   @JoinTable(name = "henkilorooli", joinColumns =
   { @JoinColumn(name = "henkilo", referencedColumnName = "id") }, inverseJoinColumns =
   { @JoinColumn(name = "rooli", referencedColumnName = "id") })
   protected List<Rooli> roolit = new ArrayList<Rooli>();

   @ManyToOne(cascade =
   { CascadeType.PERSIST, CascadeType.MERGE })
   @JoinColumn(name = "perhe")
   protected Perhe perhe;

   @Size(max = 50)
   protected String salasana;

   @OneToOne
   @JoinColumn(name = "kuva")
   protected BlobData kuva;

   @Type(type = "KylläEi")
   protected boolean arkistoitu;

   @Temporal(TemporalType.DATE)
   protected Date luotu = new Date();

   public void siivoa()
   {
      if (osoite != null && osoite.isKäyttämätön())
      {
         osoite = null;
      }
      if (yhteystiedot != null && yhteystiedot.isKäyttämätön())
      {
         yhteystiedot = null;
      }
   }

   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public String getEtunimi()
   {
      return etunimi;
   }

   public void setEtunimi(String etunimi)
   {
      this.etunimi = etunimi;
   }

   public String getSukunimi()
   {
      return sukunimi;
   }

   public void setSukunimi(String sukunimi)
   {
      this.sukunimi = sukunimi;
   }

   public Osoite getOsoite()
   {
      if (osoite != null)
      {
         return osoite;
      }
      if (perhe != null)
      {
         return perhe.getOsoite();
      }
      osoite = new Osoite();
      return osoite;
   }

   public void setOsoite(Osoite osoite)
   {
      this.osoite = osoite;
   }

   public Yhteystieto getYhteystiedot()
   {
      if (yhteystiedot == null)
      {
         yhteystiedot = new Yhteystieto();
      }
      return yhteystiedot;
   }

   public void setYhteystiedot(Yhteystieto yhteystiedot)
   {
      this.yhteystiedot = yhteystiedot;
   }

   public String getSalasana()
   {
      return salasana;
   }

   public void setSalasana(String salasana)
   {
      if (!salasana.equals(this.salasana))
      {
         this.salasana = Util.MD5(salasana);
      } else
      {
         this.salasana = salasana;
      }
   }

   public List<Rooli> getRoolit()
   {
      return roolit;
   }

   public void setRoolit(List<Rooli> roolit)
   {
      this.roolit = roolit;
   }

   public boolean isTreenienVetäjä()
   {
      return onRoolissa("Treenien vetäjä");
   }

   public boolean isPäivystäjä()
   {
      return onRoolissa("Päivystäjä");
   }

   public boolean isYlläpitäjä()
   {
      return onRoolissa("Ylläpitäjä");
   }

   public boolean isPääsyHallintaan()
   {
      return isPäivystäjä() || isYlläpitäjä();
   }

   public boolean onRoolissa(String roolinimi)
   {
      boolean tulos = false;
      for (Rooli rooli : roolit)
      {
         if (roolinimi.equals(rooli.getNimi()))
         {
            return true;
         }
      }
      return tulos;
   }

   public String getNimi()
   {
      return String.format("%s %s", etunimi == null ? "Uusi" : etunimi, sukunimi);
   }

   public boolean isArkistoitu()
   {
      return arkistoitu;
   }

   public void setArkistoitu(boolean arkistoitu)
   {
      this.arkistoitu = arkistoitu;
   }

   public Perhe getPerhe()
   {
      return perhe;
   }

   public void setPerhe(Perhe perhe)
   {
      this.perhe = perhe;
   }

   public boolean isKuvallinen()
   {
      return kuva != null;
   }

   public boolean isTallentamaton()
   {
      return id == 0;
   }

   public void setKuva(BlobData kuva)
   {
      this.kuva = kuva;
   }

   public BlobData getKuva()
   {
      return kuva;
   }

   public boolean isHarrastaja()
   {
      return false;
   }

   public boolean isAlaikäinen()
   {
      return false;
   }

   public List<Sopimus> getSopimukset()
   {
      return perhe.getPerheenjäsenet().stream().filter(h -> h.isHarrastaja()).map(h -> h.getSopimukset())
         .flatMap(s -> s.stream()).collect(Collectors.toList());
   }

   public boolean isLöytyySähköposti()
   {
      return yhteystiedot != null && yhteystiedot.getSähköposti() != null && !"".equals(yhteystiedot.getSähköposti());
   }

   public String luoUusiSalasana()
   {
      String uusiSalasana = Util.MD5(UUID.randomUUID().toString()).substring(0, 6);
      salasana = Util.MD5(uusiSalasana);
      return uusiSalasana;
   }

   public String getInfo()
   {
      String roolinimet = roolit.stream().map(r -> r.getNimi()).collect(Collectors.joining(", "));
      return String.format("%s [%s]", getNimi(), roolinimet);
   }

   public Date getLuotu()
   {
      return luotu;
   }

   public void setLuotu(Date luotu)
   {
      this.luotu = luotu;
   }

   public boolean isHuollettavia()
   {
      return getHuollettavat().size() > 0;
   }

   public List<Henkilö> getHuollettavat()
   {
      return perhe == null ? new ArrayList<Henkilö>() : perhe.getHuollettavat(this);
   }

   @Override
   public String toString()
   {
      return getNimi();
   }

   @Override
   public boolean equals(Object toinen)
   {
      if (!(toinen instanceof Henkilö))
      {
         return false;
      }
      Henkilö toinenHenkilö = (Henkilö) toinen;
      return id == toinenHenkilö.getId();
   }

   @Override
   public int hashCode()
   {
      return Integer.valueOf(id).hashCode();
   }

}
