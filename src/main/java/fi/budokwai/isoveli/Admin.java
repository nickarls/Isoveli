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

import org.icefaces.ace.event.RowEditEvent;
import org.icefaces.application.PushRenderer;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Treenik�ynti;

@Stateful
@Named
@SessionScoped
public class Admin
{
   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   @PostConstruct
   public void init()
   {
      PushRenderer.addCurrentSession("ilmoittautuminen");
   }

   @Produces
   @Named
   public List<Treenik�ynti> getTreenik�ynnit()
   {
      return entityManager.createNamedQuery("treenik�ynnit", Treenik�ynti.class).getResultList();
   }

   public void poistaTreenik�ynti(Treenik�ynti treenik�ynti)
   {
      entityManager.remove(treenik�ynti);
      entityManager.flush();
   }
   
   public void tallennaHarrastaja(RowEditEvent e)
   {
      Harrastaja h = (Harrastaja) e.getObject();
      entityManager.merge(h);
   }

   public void poistaTreenikaynti(Treenik�ynti treenikaynti)
   {
      treenikaynti = entityManager.merge(treenikaynti);
      entityManager.remove(treenikaynti);
   }

}
