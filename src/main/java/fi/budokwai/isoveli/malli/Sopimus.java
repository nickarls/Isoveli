package fi.budokwai.isoveli.malli;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import fi.budokwai.isoveli.util.Util;

@Entity
@NamedQuery(name = "laskuttamattomat_sopimukset", query = "select s from Sopimus s, Sopimustyyppi st, Harrastaja h "
   + "where s.tyyppi = st and s.harrastaja=h and st.laskutettava='K' and h.arkistoitu='E' "
   + "and not exists (select 1 from Sopimus s2, Sopimustyyppi st2 where s2.tyyppi=st2 and st2.vapautus='K' and s2.harrastaja=h) "
   + "and s.sopimuslaskut is empty")
public class Sopimus
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @ManyToOne(optional = false)
   @JoinColumn(name = "harrastaja")
   private Harrastaja harrastaja;

   @OneToOne(optional = false)
   @JoinColumn(name = "tyyppi")
   @NotNull
   private Sopimustyyppi tyyppi;

   @OneToMany(mappedBy = "sopimus")
   private List<Sopimuslasku> sopimuslaskut = new ArrayList<Sopimuslasku>();

   @Temporal(TemporalType.DATE)
   private Date umpeutuu;

   private int treenikertoja;

   @Column(name = "maksuvali")
   private int maksuväli;

   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public Harrastaja getHarrastaja()
   {
      return harrastaja;
   }

   public void setHarrastaja(Harrastaja harrastaja)
   {
      this.harrastaja = harrastaja;
   }

   public Date getUmpeutuu()
   {
      return umpeutuu;
   }

   public void setUmpeutuu(Date umpeutuu)
   {
      this.umpeutuu = umpeutuu;
   }

   public int getTreenikertoja()
   {
      return treenikertoja;
   }

   public void setTreenikertoja(int treenikertoja)
   {
      this.treenikertoja = treenikertoja;
   }

   public int getMaksuväli()
   {
      return maksuväli;
   }

   public void setMaksuväli(int maksuväli)
   {
      this.maksuväli = maksuväli;
   }

   public Sopimustyyppi getTyyppi()
   {
      return tyyppi;
   }

   public void setTyyppi(Sopimustyyppi tyyppi)
   {
      this.tyyppi = tyyppi;
   }

   public boolean isPoistettavissa()
   {
      return id > 0 && sopimuslaskut.isEmpty();
   }

   @Override
   public int hashCode()
   {
      return Integer.valueOf(id).hashCode();
   }

   @Override
   public boolean equals(Object toinen)
   {
      if (!(toinen instanceof Sopimus))
      {
         return false;
      }
      Sopimus toinenSopimus = (Sopimus) toinen;
      return id == toinenSopimus.getId();
   }

   public boolean isVoimassa()
   {
      if (umpeutuu == null)
      {
         return true;
      }
      return umpeutuu.after(new Date());
   }

   public String getTuotenimi()
   {
      return String.format("%s (%s)", tyyppi.getNimi(), harrastaja.getEtunimi());
   }

   public List<Sopimuslasku> getSopimuslaskut()
   {
      return sopimuslaskut;
   }

   public void setSopimuslaskut(List<Sopimuslasku> sopimuslaskut)
   {
      this.sopimuslaskut = sopimuslaskut;
   }

   public Osoite getLaskutusosoite()
   {
      if (harrastaja.isAlaikäinen())
      {
         return harrastaja.getPerhe().getOsoite();
      } else
      {
         return harrastaja.getOsoite();
      }
   }

   public Henkilö getLaskutushenkilö()
   {
      if (harrastaja.isAlaikäinen())
      {
         return harrastaja.getHuoltaja();
      } else
      {
         return harrastaja;
      }
   }

   public List<Sopimustarkistus> tarkista()
   {
      List<Sopimustarkistus> tulos = new ArrayList<Sopimustarkistus>();
      if (!isVoimassa())
      {
         LocalDate pvm = Util.date2LocalDateTime(umpeutuu);
         String viesti = String.format("%s: sopimus umpeutui %s", tyyppi.getNimi(),
            pvm.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
         tulos.add(new Sopimustarkistus(viesti, false));
      }
      for (Sopimuslasku sopimuslasku : sopimuslaskut)
      {
         if (sopimuslasku.getLaskurivi().getLasku().isLaskuMyöhässä())
         {
            String viesti = String.format("%s: lasku myöhässä %d päivää", tyyppi.getNimi(), sopimuslasku.getLaskurivi()
               .getLasku().getMaksuaikaa());
            tulos.add(new Sopimustarkistus(viesti, false));
         }
      }
      return tulos;
   }
}
