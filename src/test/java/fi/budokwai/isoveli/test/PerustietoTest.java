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
import fi.budokwai.isoveli.malli.Vy�arvo;

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
      Vy�arvo vy�arvo = new Vy�arvo();
      vy�arvo.setJ�rjestys(2);
      vy�arvo.setNimi("vy�");
      vy�arvo.setKuvaus("kellert�v�");
      vy�arvo.setMinimikuukaudet(1);
      vy�arvo.setMinimitreenit(1);
      perustietoAdmin.setVy�arvo(vy�arvo);
      perustietoAdmin.tallennaVy�arvo();
      Vy�arvo testi = entityManager.createQuery("select v from Vy�arvo v where v.id=2", Vy�arvo.class)
         .getSingleResult();
      Assert.assertNotNull(testi);
   }

   @Test
   @ApplyScriptBefore("keltainenvyo.sql")
   @ApplyScriptAfter("cleanup.sql")
   public void testMuokkaaVyoarvo()
   {
      Vy�arvo vy�arvo = entityManager.find(Vy�arvo.class, 1);
      vy�arvo.setNimi("vy�x");
      perustietoAdmin.setVy�arvo(vy�arvo);
      perustietoAdmin.tallennaVy�arvo();
      entityManager.clear();
      vy�arvo = entityManager.find(Vy�arvo.class, 1);
      Assert.assertEquals("vy�x", vy�arvo.getNimi());
   }

   @Test
   @ApplyScriptBefore("keltainenvyo.sql")
   @ApplyScriptAfter("cleanup.sql")
   public void testPoistaVyoarvoEiKaytossa()
   {
      Vy�arvo vy�arvo = entityManager.find(Vy�arvo.class, 1);
      perustietoAdmin.setVy�arvo(vy�arvo);
      perustietoAdmin.poistaVy�arvo();
      entityManager.clear();
      vy�arvo = entityManager.find(Vy�arvo.class, 1);
      Assert.assertNull(vy�arvo);
   }

   @PersistenceContext
   private EntityManager freshEntityManager;
   
   @Test
   @ApplyScriptBefore("vyoarvokaytossa.sql")
   @ApplyScriptAfter("cleanup.sql")
   @Exposed
   public void testPoistaVyoarvoKaytossa()
   {
      Vy�arvo vy�arvo = entityManager.find(Vy�arvo.class, 1);
      perustietoAdmin.setVy�arvo(vy�arvo);
      try
      {
         perustietoAdmin.poistaVy�arvo();
      } catch (IsoveliPoikkeus e)
      {
         Assert.assertEquals("Vy�arvo on k�yt�ss� ja sit� ei voi poistaa (1kpl: Nicklas Karlsson...)", e.getMessage());
      }
      vy�arvo = freshEntityManager.find(Vy�arvo.class, 1);
      Assert.assertNotNull(vy�arvo);
   }

}
