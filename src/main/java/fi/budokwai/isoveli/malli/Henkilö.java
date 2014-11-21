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
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;

import fi.budokwai.isoveli.util.Util;

@Entity
@Table(name = "henkilo")
@NamedQueries(
{
      @NamedQuery(name = "henkil�", query = "select h from Henkil� h where h.salasana = :salasana and h.etunimi = :etunimi and h.sukunimi = :sukunimi"),
      @NamedQuery(name = "nimetty_henkil�", query = "select h from Henkil� h where h.etunimi = :etunimi and h.sukunimi = :sukunimi") })
@Inheritance(strategy = InheritanceType.JOINED)
public class Henkil� implements Serializable
{
   private static final long serialVersionUID = 1L;

   public static final Henkil� EI_KIRJAUTUNUT = new Henkil�();

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   protected int id;

   @Size(max = 50)
   @NotNull
   protected String etunimi;

   @Size(max = 50)
   @NotNull
   protected String sukunimi;

   @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
   @JoinColumn(name = "osoite")
   @Valid
   protected Osoite osoite;

   @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
   @JoinColumn(name = "yhteystiedot")
   @Valid
   protected Yhteystieto yhteystiedot;

   @ManyToMany
   @JoinTable(name = "henkilorooli", joinColumns =
   { @JoinColumn(name = "henkilo", referencedColumnName = "id") }, inverseJoinColumns =
   { @JoinColumn(name = "rooli", referencedColumnName = "id") })
   protected List<Rooli> roolit = new ArrayList<Rooli>();

   @ManyToOne(cascade = CascadeType.PERSIST)
   @JoinColumn(name = "perhe")
   protected Perhe perhe;

   @Size(max = 50)
   protected String salasana;

   @OneToOne(cascade = CascadeType.ALL, optional = true, orphanRemoval = true)
   @JoinColumn(name = "kuva")
   protected BlobData kuva;

   @Type(type = "Kyll�Ei")
   protected boolean arkistoitu;

   @Temporal(TemporalType.DATE)
   protected Date luotu = new Date();

   @Transient
   public boolean osoiteMuuttunut;

   public void siivoa()
   {
      if (osoiteMuuttunut || (osoite != null && osoite.isK�ytt�m�t�n()))
      {
         osoite = null;
      }
      if (yhteystiedot != null && yhteystiedot.isK�ytt�m�t�n())
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

   public boolean isTreenienVet�j�()
   {
      return onRoolissa("Treenien vet�j�");
   }

   public boolean isP�ivyst�j�()
   {
      return onRoolissa("P�ivyst�j�");
   }

   public boolean isYll�pit�j�()
   {
      return onRoolissa("Yll�pit�j�");
   }

   public boolean isP��syHallintaan()
   {
      return isP�ivyst�j�() || isYll�pit�j�();
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

   public boolean isPoistettavissa()
   {
      return id > 0;
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

   public boolean isAlaik�inen()
   {
      return false;
   }

   public List<Sopimus> getSopimukset()
   {
      return perhe.getPerheenj�senet().stream().filter(h -> h.isHarrastaja()).map(h -> h.getSopimukset())
         .flatMap(s -> s.stream()).collect(Collectors.toList());
   }

   public boolean isL�ytyyS�hk�posti()
   {
      return yhteystiedot != null && yhteystiedot.getS�hk�posti() != null && !"".equals(yhteystiedot.getS�hk�posti());
   }

   public String luoUusiSalasana()
   {
      salasana = Util.MD5(UUID.randomUUID().toString()).substring(0, 6);
      setSalasana(salasana);
      return salasana;
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

   public boolean isOsoiteMuuttunut()
   {
      return osoiteMuuttunut;
   }

   public void setOsoiteMuuttunut(boolean osoiteMuuttunut)
   {
      this.osoiteMuuttunut = osoiteMuuttunut;
   }

   public boolean isHuoltaja()
   {
      return perhe != null
         && perhe.getPerheenj�senet().stream().filter(h -> h.isHarrastaja())
            .filter(h -> ((Harrastaja) h).getHuoltaja().getId() == id).collect(Collectors.counting()) > 0;
   }

   public List<Henkil�> getHuollettavat()
   {
      return perhe == null ? new ArrayList<Henkil�>() : perhe.getPerheenj�senet().stream()
         .filter(h -> h.isHarrastaja()).filter(h -> ((Harrastaja) h).getHuoltaja().id == id)
         .collect(Collectors.toList());
   }

   @Override
   public String toString()
   {
      return getNimi();
   }

}
