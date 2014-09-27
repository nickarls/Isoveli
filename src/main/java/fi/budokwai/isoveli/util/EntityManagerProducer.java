package fi.budokwai.isoveli.util;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.jboss.logging.Logger;

public class EntityManagerProducer
{
   @PersistenceUnit
   private EntityManagerFactory entityManagerFactory;

   @Inject
   private Logger logger;
   
   @Produces
   @SessionScoped
   public EntityManager produceEntityManager()
   {
      EntityManager entityManager = entityManagerFactory.createEntityManager();
      Session session = (Session) entityManager.getDelegate();
      session.setFlushMode(FlushMode.MANUAL);
      logger.infof("Created EM %s", entityManager);
      return entityManager;
   }

   public void closeEntityManager(@Disposes EntityManager entityManager)
   {
      if (entityManager.isOpen())
      {
         logger.infof("Destroyed EM %s", entityManager);
         entityManager.close();
      }
   }
}
