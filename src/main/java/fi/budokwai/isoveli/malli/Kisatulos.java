package fi.budokwai.isoveli.malli;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "kisatulos")
public class Kisatulos
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @ManyToOne
   @JoinColumn(name = "harrastaja", insertable=false, updatable=false)
   @NotNull
   private Harrastaja harrastaja;
   
   @OneToOne
   @JoinColumn(name = "tyyppi")
   @NotNull
   private Kisatyyppi kisatyyppi;
   
   @Temporal(TemporalType.DATE)
   @NotNull
   private Date paiva;
   
   @Size(max=50)
   @NotNull
   private String kisa;
   
   @Size(max=50)
   @NotNull
   private String sarja;
   
   @Size(max=10)
   @NotNull
   private String tulos;

}
