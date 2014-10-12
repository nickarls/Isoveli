package fi.budokwai.isoveli.malli;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
@NamedQuery(name = "perheet", query = "select p from Perhe p order by p.nimi")
public class Perhe
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   private String nimi;

   @OneToMany(cascade=CascadeType.ALL, mappedBy = "perhe", orphanRemoval=true)
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

   @Transient
   public String getKuvaus()
   {
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
