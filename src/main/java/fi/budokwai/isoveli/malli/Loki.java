package fi.budokwai.isoveli.malli;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "loki")
@NamedQuery(name = "lokitapahtumat", query = "select l from Loki l order by l.koska desc")
public class Loki
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @Temporal(TemporalType.TIMESTAMP)
   @NotNull
   private Date koska;

   @Size(max = 50)
   @NotNull
   private String kuka;

   @Size(max = 1000)
   @NotNull
   @Column(name = "mita")
   private String mitä;

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

   public String getKuka()
   {
      return kuka;
   }

   public void setKuka(String kuka)
   {
      this.kuka = kuka;
   }

   public String getMitä()
   {
      return mitä;
   }

   public void setMitä(String mitä)
   {
      this.mitä = mitä;
   }
}
