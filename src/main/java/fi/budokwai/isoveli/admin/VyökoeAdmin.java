package fi.budokwai.isoveli.admin;

import java.util.List;
import java.util.Set;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;

import org.icefaces.ace.event.SelectEvent;
import org.icefaces.ace.model.table.RowStateMap;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Vy�koetilaisuus;

@Stateful
@SessionScoped
@Named
public class Vy�koeAdmin extends Perustoiminnallisuus
{
   private List<Vy�koetilaisuus> vy�koetilaisuudet = null;
   private Vy�koetilaisuus vy�koetilaisuus = null;
   private RowStateMap vy�koetilaisuusRSM = new RowStateMap();

   @Inject
   private EntityManager entityManager;

   @Produces
   @Named
   public List<Vy�koetilaisuus> getVy�koetilaisuudet()
   {
      if (vy�koetilaisuudet == null)
      {
         vy�koetilaisuudet = entityManager.createNamedQuery("vy�koetilaisuudet", Vy�koetilaisuus.class).getResultList();
      }
      return vy�koetilaisuudet;
   }

   @Produces
   @Named
   public List<Harrastaja> getVy�koepit�j�t()
   {
      return entityManager.createQuery("select h from Harrastaja h", Harrastaja.class).getResultList();
   }

   @Produces
   @Named
   public Vy�koetilaisuus getVy�koetilaisuus()
   {
      return vy�koetilaisuus;
   }

   public void vy�koetilaisuusValittu(SelectEvent e)
   {
      vy�koetilaisuus = (Vy�koetilaisuus) e.getObject();
   }

   public RowStateMap getVy�koetilaisuusRSM()
   {
      return vy�koetilaisuusRSM;
   }

   public void setVy�koetilaisuusRSM(RowStateMap vy�koetilaisuusRSM)
   {
      this.vy�koetilaisuusRSM = vy�koetilaisuusRSM;
   }

   public void lis��Vy�koetilaisuus()
   {
      resetoi();
      vy�koetilaisuus = new Vy�koetilaisuus();
      info("Uusi vy�koetilaisuus alustettu");
      fokusoi("form:etunimi");
      loggaaja.loggaa("Lis�si vy�koetilaisuuden");
   }

   public void tallennaVy�koetilaisuus()
   {
      if (!validointiOK())
      {
         return;
      }
      vy�koetilaisuus = entityManager.merge(vy�koetilaisuus);
      entityManager.flush();
      vy�koetilaisuusRSM.get(vy�koetilaisuus).setSelected(true);
      vy�koetilaisuudet = null;
      info("Vy�koetilaisuus tallennettu");
      loggaaja.loggaa("Tallensi vy�koetilaisuuden '%s'", vy�koetilaisuus);
   }

   private boolean validointiOK()
   {
      Set<ConstraintViolation<Vy�koetilaisuus>> validointivirheet = validator.validate(vy�koetilaisuus);
      validointivirheet.forEach(v -> {
         virhe(v.getMessage());
      });
      return validointivirheet.isEmpty();
   }

   public void poistaVykoetilaisuus()
   {
      vy�koetilaisuus.poistotarkistus();
      vy�koetilaisuus = entityManager.merge(vy�koetilaisuus);
      entityManager.remove(vy�koetilaisuus);
      entityManager.flush();
      vy�koetilaisuus = null;
      vy�koetilaisuudet = null;
      vy�koetilaisuusRSM.setAllSelected(false);
      info("Vy�koetilaisuus poistettu");
      loggaaja.loggaa("Poisti vy�koetilaisuuden '%s'", vy�koetilaisuus);
   }

   private void resetoi()
   {
      entityManager.clear();
      vy�koetilaisuudet = null;
      vy�koetilaisuus = null;
      vy�koetilaisuusRSM = new RowStateMap();
   }
}
