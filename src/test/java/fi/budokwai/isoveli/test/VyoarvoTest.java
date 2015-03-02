package fi.budokwai.isoveli.test;

import java.util.List;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;

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
public class VyoarvoTest extends Perustesti
{
   @Inject
   private PerustietoAdmin perustietoAdmin;

   @Inject
   private EntityManager entityManager;

   @Inject
   private Instance<List<Vy�arvo>> vy�arvot; 
   
   @Test
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
      Assert.assertEquals(0, vy�arvot.get().size());
      perustietoAdmin.tallennaVy�arvo();
      entityManager.clear();
      Assert.assertEquals(1, vy�arvot.get().size());
      Vy�arvo testi = entityManager.createQuery("select v from Vy�arvo v", Vy�arvo.class)
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
      Assert.assertEquals(1, vy�arvot.get().size());
      perustietoAdmin.poistaVy�arvo();
      entityManager.clear();
      Assert.assertEquals(0, vy�arvot.get().size());
      vy�arvo = entityManager.find(Vy�arvo.class, 1);
      Assert.assertNull(vy�arvo);
   }

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
      entityManager.clear();
      vy�arvo = entityManager.find(Vy�arvo.class, 1);
      Assert.assertNotNull(vy�arvo);
   }

}
