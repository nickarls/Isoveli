package fi.budokwai.isoveli.test;

import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ApplyScriptAfter;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Exposed;

import fi.budokwai.isoveli.IsoveliPoikkeus;
import fi.budokwai.isoveli.admin.PerustietoAdmin;
import fi.budokwai.isoveli.malli.Vyöarvo;

@RunWith(Arquillian.class)
public class PerustietoTest extends Perustesti
{
   @Inject
   private PerustietoAdmin perustietoAdmin;

   @Inject
   private EntityManager entityManager;

   @Test
   @ApplyScriptBefore("keltainenvyo.sql")
   @ApplyScriptAfter("cleanup.sql")
   public void testLisaaVyoarvo()
   {
      Vyöarvo vyöarvo = new Vyöarvo();
      vyöarvo.setJärjestys(2);
      vyöarvo.setNimi("vyö");
      vyöarvo.setKuvaus("kellertävä");
      vyöarvo.setMinimikuukaudet(1);
      vyöarvo.setMinimitreenit(1);
      perustietoAdmin.setVyöarvo(vyöarvo);
      perustietoAdmin.tallennaVyöarvo();
      Vyöarvo testi = entityManager.createQuery("select v from Vyöarvo v where v.id=2", Vyöarvo.class)
         .getSingleResult();
      Assert.assertNotNull(testi);
   }

   @Test
   @ApplyScriptBefore("keltainenvyo.sql")
   @ApplyScriptAfter("cleanup.sql")
   public void testMuokkaaVyoarvo()
   {
      Vyöarvo vyöarvo = entityManager.find(Vyöarvo.class, 1);
      vyöarvo.setNimi("vyöx");
      perustietoAdmin.setVyöarvo(vyöarvo);
      perustietoAdmin.tallennaVyöarvo();
      entityManager.clear();
      vyöarvo = entityManager.find(Vyöarvo.class, 1);
      Assert.assertEquals("vyöx", vyöarvo.getNimi());
   }

   @Test
   @ApplyScriptBefore("keltainenvyo.sql")
   @ApplyScriptAfter("cleanup.sql")
   public void testPoistaVyoarvoEiKaytossa()
   {
      Vyöarvo vyöarvo = entityManager.find(Vyöarvo.class, 1);
      perustietoAdmin.setVyöarvo(vyöarvo);
      perustietoAdmin.poistaVyöarvo();
      entityManager.clear();
      vyöarvo = entityManager.find(Vyöarvo.class, 1);
      Assert.assertNull(vyöarvo);
   }

   @PersistenceContext
   private EntityManager freshEntityManager;
   
   @Test
   @ApplyScriptBefore("vyoarvokaytossa.sql")
   @ApplyScriptAfter("cleanup.sql")
   @Exposed
   public void testPoistaVyoarvoKaytossa()
   {
      Vyöarvo vyöarvo = entityManager.find(Vyöarvo.class, 1);
      perustietoAdmin.setVyöarvo(vyöarvo);
      try
      {
         perustietoAdmin.poistaVyöarvo();
      } catch (IsoveliPoikkeus e)
      {
         Assert.assertEquals("Vyöarvo on käytössä ja sitä ei voi poistaa (1kpl: Nicklas Karlsson...)", e.getMessage());
      }
      vyöarvo = freshEntityManager.find(Vyöarvo.class, 1);
      Assert.assertNotNull(vyöarvo);
   }

}
