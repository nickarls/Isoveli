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
import fi.budokwai.isoveli.malli.Vyökoetilaisuus;

@Stateful
@SessionScoped
@Named
public class VyökoeAdmin extends Perustoiminnallisuus
{
   private List<Vyökoetilaisuus> vyökoetilaisuudet = null;
   private Vyökoetilaisuus vyökoetilaisuus = null;
   private RowStateMap vyökoetilaisuusRSM = new RowStateMap();

   @Inject
   private EntityManager entityManager;

   @Produces
   @Named
   public List<Vyökoetilaisuus> getVyökoetilaisuudet()
   {
      if (vyökoetilaisuudet == null)
      {
         vyökoetilaisuudet = entityManager.createNamedQuery("vyökoetilaisuudet", Vyökoetilaisuus.class).getResultList();
      }
      return vyökoetilaisuudet;
   }

   @Produces
   @Named
   public List<Harrastaja> getVyökoepitäjät()
   {
      return entityManager.createQuery("select h from Harrastaja h", Harrastaja.class).getResultList();
   }

   @Produces
   @Named
   public Vyökoetilaisuus getVyökoetilaisuus()
   {
      return vyökoetilaisuus;
   }

   public void vyökoetilaisuusValittu(SelectEvent e)
   {
      vyökoetilaisuus = (Vyökoetilaisuus) e.getObject();
   }

   public RowStateMap getVyökoetilaisuusRSM()
   {
      return vyökoetilaisuusRSM;
   }

   public void setVyökoetilaisuusRSM(RowStateMap vyökoetilaisuusRSM)
   {
      this.vyökoetilaisuusRSM = vyökoetilaisuusRSM;
   }

   public void lisääVyökoetilaisuus()
   {
      resetoi();
      vyökoetilaisuus = new Vyökoetilaisuus();
      info("Uusi vyökoetilaisuus alustettu");
      fokusoi("form:etunimi");
      loggaaja.loggaa("Lisäsi vyökoetilaisuuden");
   }

   public void tallennaVyökoetilaisuus()
   {
      if (!validointiOK())
      {
         return;
      }
      vyökoetilaisuus = entityManager.merge(vyökoetilaisuus);
      entityManager.flush();
      vyökoetilaisuusRSM.get(vyökoetilaisuus).setSelected(true);
      vyökoetilaisuudet = null;
      info("Vyökoetilaisuus tallennettu");
      loggaaja.loggaa("Tallensi vyökoetilaisuuden '%s'", vyökoetilaisuus);
   }

   private boolean validointiOK()
   {
      Set<ConstraintViolation<Vyökoetilaisuus>> validointivirheet = validator.validate(vyökoetilaisuus);
      validointivirheet.forEach(v -> {
         virhe(v.getMessage());
      });
      return validointivirheet.isEmpty();
   }

   public void poistaVykoetilaisuus()
   {
      vyökoetilaisuus.poistotarkistus();
      vyökoetilaisuus = entityManager.merge(vyökoetilaisuus);
      entityManager.remove(vyökoetilaisuus);
      entityManager.flush();
      vyökoetilaisuus = null;
      vyökoetilaisuudet = null;
      vyökoetilaisuusRSM.setAllSelected(false);
      info("Vyökoetilaisuus poistettu");
      loggaaja.loggaa("Poisti vyökoetilaisuuden '%s'", vyökoetilaisuus);
   }

   private void resetoi()
   {
      entityManager.clear();
      vyökoetilaisuudet = null;
      vyökoetilaisuus = null;
      vyökoetilaisuusRSM = new RowStateMap();
   }
}
