package fi.budokwai.isoveli.malli;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "viestilaatikko")
@DynamicInsert
@DynamicUpdate
public class Viestilaatikko
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @ManyToOne
   @JoinColumn(name = "omistaja")
   @NotNull
   private Henkil� omistaja;

   @Enumerated(EnumType.STRING)
   private Viestilaatikkotyypi tyyppi;

   @OneToMany(mappedBy = "viestilaatikko")
   private List<Henkil�viesti> viestit = new ArrayList<>();   
   
   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public Henkil� getOmistaja()
   {
      return omistaja;
   }

   public void setOmistaja(Henkil� omistaja)
   {
      this.omistaja = omistaja;
   }

   public Viestilaatikkotyypi getTyyppi()
   {
      return tyyppi;
   }

   public void setTyyppi(Viestilaatikkotyypi tyyppi)
   {
      this.tyyppi = tyyppi;
   }

   public List<Henkil�viesti> getViestit()
   {
      return viestit;
   }

   public void setViestit(List<Henkil�viesti> viestit)
   {
      this.viestit = viestit;
   }


}
