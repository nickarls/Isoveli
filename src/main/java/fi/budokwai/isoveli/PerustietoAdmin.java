package fi.budokwai.isoveli;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.icefaces.ace.event.SelectEvent;

import fi.budokwai.isoveli.malli.Henkilö;
import fi.budokwai.isoveli.malli.Rooli;

@Named
@SessionScoped
@Stateful
public class PerustietoAdmin extends Perustoiminnallisuus
{
   private Rooli rooli;

   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   private List<Rooli> roolit;

   @PostConstruct
   public void alusta()
   {
      haeRoolit();
   }

   @Produces
   @Named
   public List<Rooli> getKaikkiRoolit()
   {
      return roolit;
   }

   @Produces
   @Named
   public Rooli getRooli()
   {
      return rooli;
   }

   public void peruutaMuutos()
   {
      rooli = null;
      virhe("Muutokset peruttu");
   }

   public void piilotaRooli()
   {
      rooli = null;
   }

   public void lisääRooli()
   {
      rooli = new Rooli();
      info("Uusi rooli alustettu");
   }

   public void tallennaRooli()
   {
      entityManager.persist(rooli);
      haeRoolit();
      info("Rooli tallennettu");
   }

   private void haeRoolit()
   {
      roolit = entityManager.createNamedQuery("roolit", Rooli.class).getResultList();
   }

   public void poistaRooli()
   {
      entityManager.remove(rooli);
      haeRoolit();
      info("Rooli poistettu");
   }

   @Produces
   @Named
   public List<Henkilö> getRooliKäyttö()
   {
      return entityManager.createNamedQuery("roolikäyttö", Henkilö.class).setParameter("rooli", rooli).getResultList();
   }

   public void rooliValittu(SelectEvent e)
   {
      rooli = (Rooli) e.getObject();
   }

}
