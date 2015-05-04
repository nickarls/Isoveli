package fi.budokwai.isoveli.malli;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "viesti")
@DynamicInsert
@DynamicUpdate
public class Viesti
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @NotNull
   private String otsikko;

   @NotNull
   @Column(name = "sisalto")
   private String sisältö;

   @ManyToOne
   @JoinColumn(name = "lahettaja")
   @NotNull
   private Harrastaja lähettäjä;

   @NotNull
   private String vastaanottajat;

   @Temporal(TemporalType.TIMESTAMP)
   private Date luotu = new Date();

   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public String getOtsikko()
   {
      return otsikko;
   }

   public void setOtsikko(String otsikko)
   {
      this.otsikko = otsikko;
   }

   public String getSisältö()
   {
      return sisältö;
   }

   public void setSisältö(String sisältö)
   {
      this.sisältö = sisältö;
   }

   public Harrastaja getLähettäjä()
   {
      return lähettäjä;
   }

   public void setLähettäjä(Harrastaja lähettäjä)
   {
      this.lähettäjä = lähettäjä;
   }

   public Date getLuotu()
   {
      return luotu;
   }

   public void setLuotu(Date luotu)
   {
      this.luotu = luotu;
   }

   public String getVastaanottajat()
   {
      return vastaanottajat;
   }

   public void setVastaanottajat(String vastaanottajat)
   {
      this.vastaanottajat = vastaanottajat;
   }
}
