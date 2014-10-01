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

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.icefaces.ace.event.SelectEvent;
import org.icefaces.ace.model.table.RowStateMap;
import org.icefaces.application.PushRenderer;

import fi.budokwai.isoveli.malli.Treenik‰ynti;

@Stateful
@Named
@SessionScoped
public class IlmoittautumisAdmin extends Perustoiminnallisuus
{
   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   private Treenik‰ynti treenik‰ynti;

   private RowStateMap rowStateMap = new RowStateMap();

   @Produces
   @Named
   public Treenik‰ynti getTreenik‰ynti()
   {
      return treenik‰ynti;
   }

   @PostConstruct
   public void init()
   {
      ((Session) entityManager.getDelegate()).setFlushMode(FlushMode.MANUAL);
      PushRenderer.addCurrentSession("ilmoittautuminen");
   }

   @Produces
   @Named
   public List<Treenik‰ynti> getTreenik‰ynnit()
   {
      return entityManager.createNamedQuery("treenik‰ynnit", Treenik‰ynti.class).getResultList();
   }

   public void lis‰‰Ilmoittautuminen()
   {
      treenik‰ynti = new Treenik‰ynti();
      info("Uusi treenik‰ynti alustettu");
   }

   public void poistaTreenik‰ynti()
   {
      entityManager.remove(treenik‰ynti);
      entityManager.flush();
      treenik‰ynti = null;
      info("Treenik‰ynti poistettu");
   }

   public void tallennaTreenik‰ynti()
   {
      entityManager.persist(treenik‰ynti);
      entityManager.flush();
      info("Treenik‰ynti tallennettu");
   }

   public void treeniK‰yntiValittu(SelectEvent e)
   {
      treenik‰ynti = (Treenik‰ynti) e.getObject();
   }

   public RowStateMap getRowStateMap()
   {
      return rowStateMap;
   }

   public void setRowStateMap(RowStateMap rowStateMap)
   {
      this.rowStateMap = rowStateMap;
   }

   public void peruutaMuutos()
   {
      treenik‰ynti = null;
      rowStateMap.setAllSelected(false);
      virhe("Muutos peruttu");
   }

}
