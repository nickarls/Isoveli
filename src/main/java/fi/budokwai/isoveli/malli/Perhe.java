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
      @NamedQuery(name = "poista_turhat_huoltajat", query = "delete from Henkil� h where not exists (select ha from Harrastaja ha where ha.huoltaja=h)"),
      @NamedQuery(name = "poista_tyhj�t_perheet", query = "delete from Perhe p where not exists (select h from Henkil� h where h.perhe.id=p.id)") })
public class Perhe
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   private String nimi;

   @OneToMany(mappedBy = "perhe", cascade =
   { CascadeType.PERSIST })
   private List<Henkil�> perheenj�senet = new ArrayList<Henkil�>();

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

   public List<Henkil�> getPerheenj�senet()
   {
      return perheenj�senet;
   }

   public void setPerheenj�senet(List<Henkil�> perheenj�senet)
   {
      this.perheenj�senet = perheenj�senet;
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
      String etunimet = perheenj�senet.stream()
         .map(h -> h.isAlaik�inen() ? h.getEtunimi() : String.format("*%s", h.getEtunimi()))
         .collect(Collectors.joining(", "));
      return "".equals(etunimet) ? nimi : String.format("%s (%s)", nimi, etunimet);
   }

   public List<Henkil�> getHuoltajat()
   {
      return perheenj�senet.stream().filter(h -> !h.isAlaik�inen()).collect(Collectors.toList());
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

   public void lis��Perheenj�sen(Henkil� henkil�)
   {
      perheenj�senet.add(henkil�);
      henkil�.setPerhe(this);
   }

   public void muodosta()
   {
      p��tteleNimi();
      omaksuOsoite();
      asetaHuoltajuudet();
   }

   private void asetaHuoltajuudet()
   {
      List<Henkil�> huoltajat = perheenj�senet.stream().filter(h -> !h.isHarrastaja()).collect(Collectors.toList());
      if (!huoltajat.isEmpty())
      {
         Henkil� huoltaja = huoltajat.iterator().next();
         perheenj�senet.stream().filter(h -> h.isAlaik�inen()).forEach(h -> ((Harrastaja) h).setHuoltaja(huoltaja));
      }
   }

   private void omaksuOsoite()
   {
      List<Henkil�> osoitteelliset = perheenj�senet.stream().filter(h -> h.getOsoite() != null)
         .collect(Collectors.toList());
      if (!osoitteelliset.isEmpty())
      {
         osoite = osoitteelliset.iterator().next().getOsoite();
      }
      osoitteelliset.forEach(h -> h.setOsoite(null));
   }

   private void p��tteleNimi()
   {
      List<Henkil�> huoltajat = perheenj�senet.stream().filter(h -> !h.isHarrastaja()).collect(Collectors.toList());
      if (!huoltajat.isEmpty())
      {
         nimi = huoltajat.iterator().next().getSukunimi();
      }
      if (nimi != null)
      {
         return;
      }
      List<Henkil�> t�ysiIk�iset = perheenj�senet.stream().filter(h -> !h.isAlaik�inen()).collect(Collectors.toList());
      if (!t�ysiIk�iset.isEmpty())
      {
         nimi = t�ysiIk�iset.iterator().next().getSukunimi();
      }
      if (nimi != null)
      {
         return;
      }
      nimi = perheenj�senet.iterator().next().getSukunimi();
   }

   public List<Henkil�> getHuollettavat(Henkil� henkil�)
   {
      return perheenj�senet.stream().filter(h -> h.isHarrastaja()).map(h -> Harrastaja.class.cast(h))
         .filter(h -> h.getHuoltaja() != null && h.getHuoltaja().getId() == henkil�.getId())
         .collect(Collectors.toList());
   }

   public String getOletusICE()
   {
      Optional<Henkil�> iceHenkil� = perheenj�senet.stream()
         .filter(h -> !h.isAlaik�inen() && h.getYhteystiedot() != null && h.getYhteystiedot().isL�ytyyPuhelin())
         .findFirst();
      return iceHenkil�.isPresent() ? iceHenkil�.get().getYhteystiedot().getPuhelinnumero() : null;
   }

   public Sopimus getPerheenKertakortti()
   {
      Optional<Sopimus> sopimus = perheenj�senet.stream().filter(h -> h.isHarrastaja())
         .map(h -> Harrastaja.class.cast(h)).flatMap(h -> h.getSopimukset().stream())
         .filter(s -> s.getTyyppi().isTreenikertoja()).sorted((s1, s2) -> {
            return Integer.valueOf(s1.getTreenikertojaJ�ljell�()).compareTo(Integer.valueOf(s2.getTreenikertojaJ�ljell�()));
         }).findFirst();
      return sopimus.isPresent() ? sopimus.get() : null;
   }

   public int getPerheenTreenikerrat()
   {
      Sopimus perheenKertakortti = getPerheenKertakortti();
      return perheenKertakortti == null ? 0 : perheenKertakortti.getTreenikertojaJ�ljell�();
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
