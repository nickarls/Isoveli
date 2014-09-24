package fi.budokwai.isoveli.malli;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@SequenceGenerator(name = "henkilo_seq", sequenceName = "henkilo_seq", allocationSize = 1, initialValue = 2)
@Table(name = "henkilo")
public class Henkil�
{
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "henkilo_seq")
   private int id;

   @Size(max = 50)
   @NotNull
   private String etunimi;

   @Size(max = 50)
   @NotNull
   private String sukunimi;

   @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
   @JoinColumn(name = "osoite")
   @Valid
   private Osoite osoite;

   @Size(max = 50)
   @Column(name = "sahkoposti")
   private String s�hk�posti;

   @Size(max = 1)
   @Column(name = "sahkopostilistalla")
   private String s�hk�postilistalla;

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
      if (osoite == null)
      {
         osoite = new Osoite();
      }
      return osoite;
   }

   public void setOsoite(Osoite osoite)
   {
      this.osoite = osoite;
   }

   public String getS�hk�posti()
   {
      return s�hk�posti;
   }

   public void setS�hk�posti(String s�hk�posti)
   {
      this.s�hk�posti = s�hk�posti;
   }

   public String getS�hk�postilistalla()
   {
      return s�hk�postilistalla;
   }

   public void setS�hk�postilistalla(String s�hk�postilistalla)
   {
      this.s�hk�postilistalla = s�hk�postilistalla;
   }

}
