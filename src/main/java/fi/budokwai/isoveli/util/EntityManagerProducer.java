package fi.budokwai.isoveli.util;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@ApplicationScoped
public class EntityManagerProducer
{
//   @PersistenceUnit
//   private EntityManagerFactory entityManagerFactory;
//
//   @Produces
//   @Default
//   @RequestScoped
//   public EntityManager create()
//   {
//      return entityManagerFactory.createEntityManager();
//   }
//
//   public void dispose(@Disposes @Default EntityManager entityManager)
//   {
//      if (entityManager.isOpen())
//      {
//         entityManager.close();
//      }
//   }
   
   @PersistenceContext
   private EntityManager em;
   
   @Produces
   public EntityManager get() {
       return em;
   }   
}