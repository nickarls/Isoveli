package fi.budokwai.isoveli;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.icefaces.application.PushRenderer;

import fi.budokwai.isoveli.malli.Treenikaynti;

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
   @Named("treenikaynnit")
   public List<Treenikaynti> getTreenikaynnit()
   {
      return entityManager.createNamedQuery("treenikaynnit", Treenikaynti.class).getResultList();
   }

   public void poistaTreenikaynti(Treenikaynti treenikaynti)
   {
      treenikaynti = entityManager.merge(treenikaynti);
      entityManager.remove(treenikaynti);
   }
}
