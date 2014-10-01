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

import fi.budokwai.isoveli.malli.Treenikäynti;

@Stateful
@Named
@SessionScoped
public class IlmoittautumisAdmin extends Perustoiminnallisuus
{
   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   private Treenikäynti treenikäynti;

   private RowStateMap rowStateMap = new RowStateMap();

   @Produces
   @Named
   public Treenikäynti getTreenikäynti()
   {
      return treenikäynti;
   }

   @PostConstruct
   public void init()
   {
      ((Session) entityManager.getDelegate()).setFlushMode(FlushMode.MANUAL);
      PushRenderer.addCurrentSession("ilmoittautuminen");
   }

   @Produces
   @Named
   public List<Treenikäynti> getTreenikäynnit()
   {
      return entityManager.createNamedQuery("treenikäynnit", Treenikäynti.class).getResultList();
   }

   public void lisääIlmoittautuminen()
   {
      treenikäynti = new Treenikäynti();
      info("Uusi treenikäynti alustettu");
   }

   public void poistaTreenikäynti()
   {
      entityManager.remove(treenikäynti);
      entityManager.flush();
      treenikäynti = null;
      info("Treenikäynti poistettu");
   }

   public void tallennaTreenikäynti()
   {
      entityManager.persist(treenikäynti);
      entityManager.flush();
      info("Treenikäynti tallennettu");
   }

   public void treeniKäyntiValittu(SelectEvent e)
   {
      treenikäynti = (Treenikäynti) e.getObject();
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
      treenikäynti = null;
      rowStateMap.setAllSelected(false);
      virhe("Muutos peruttu");
   }

}
