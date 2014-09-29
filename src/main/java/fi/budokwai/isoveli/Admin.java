package fi.budokwai.isoveli;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.icefaces.ace.event.RowEditEvent;
import org.icefaces.application.PushRenderer;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Treenikäynti;

@Stateful
@Named
@SessionScoped
public class Admin
{
   @PersistenceContext
   private EntityManager entityManager;



   @PostConstruct
   public void init()
   {
      PushRenderer.addCurrentSession("ilmoittautuminen");
   }

   @Produces
   @Named
   public List<Treenikäynti> getTreenikaynnit()
   {
      return entityManager.createNamedQuery("treenikaynnit", Treenikäynti.class).getResultList();
   }

   public void tallennaHarrastaja(RowEditEvent e)
   {
      Harrastaja h = (Harrastaja) e.getObject();
      entityManager.merge(h);
   }

   public void poistaTreenikaynti(Treenikäynti treenikaynti)
   {
      treenikaynti = entityManager.merge(treenikaynti);
      entityManager.remove(treenikaynti);
   }

}
