package fi.budokwai.isoveli.malli;

import java.util.ArrayList;
import java.util.List;
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

@Entity
@NamedQueries(
{
      @NamedQuery(name = "perheet", query = "select p from Perhe p order by p.nimi"),
      @NamedQuery(name = "poista_tyhj�t_perheet", query = "delete from Perhe p where not exists(select h from Henkil� h where h.perhe.id=p.id)") })
public class Perhe
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   private String nimi;

   @OneToMany(mappedBy = "perhe")
   private List<Henkil�> perheenj�senet = new ArrayList<Henkil�>();

   @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = false)
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
      return perheenj�senet
         .stream()
         .filter(h -> (!(h instanceof Harrastaja)) || ((h instanceof Harrastaja) && (!((Harrastaja) h).isAlaik�inen())))
         .collect(Collectors.toList());
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
      int x = perheenj�senet.size();
      StringBuilder sb = new StringBuilder();
      sb.append(nimi);
      if (!perheenj�senet.isEmpty())
      {
         sb.append(" (");
         for (Henkil� j�sen : perheenj�senet)
         {
            sb.append(j�sen.getEtunimi());
            if (perheenj�senet.indexOf(j�sen) != (perheenj�senet.size() - 1))
            {
               sb.append(", ");
            }
         }
         sb.append(")");
      }
      return sb.toString();
   }

   public List<Henkil�> getHuoltajat()
   {
      return perheenj�senet;
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

}