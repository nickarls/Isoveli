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
import fi.budokwai.isoveli.malli.Vyöarvo;

@RunWith(Arquillian.class)
public class VyoarvoTest extends Perustesti
{
   @Inject
   private PerustietoAdmin perustietoAdmin;

   @Inject
   private EntityManager entityManager;

   @Inject
   private Instance<List<Vyöarvo>> vyöarvot;

   @Test
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
      Assert.assertEquals(0, vyöarvot.get().size());
      perustietoAdmin.tallennaVyöarvo();
      entityManager.clear();
      Assert.assertEquals(1, vyöarvot.get().size());
      Vyöarvo testi = entityManager.createQuery("select v from Vyöarvo v", Vyöarvo.class).getSingleResult();
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
   public void testArkistoiVyoarvo()
   {
      Assert.assertEquals(1, vyöarvot.get().size());
      Vyöarvo vyöarvo = entityManager.find(Vyöarvo.class, 1);
      vyöarvo.setArkistoitu(true);
      perustietoAdmin.setVyöarvo(vyöarvo);
      perustietoAdmin.tallennaVyöarvo();
      entityManager.clear();
      Assert.assertEquals(0, vyöarvot.get().size());
      vyöarvo = entityManager.find(Vyöarvo.class, 1);
      Assert.assertNotNull(vyöarvo);
   }

   @Test
   @ApplyScriptBefore("keltainenvyo.sql")
   @ApplyScriptAfter("cleanup.sql")
   public void testPoistaVyoarvoEiKaytossa()
   {
      Vyöarvo vyöarvo = entityManager.find(Vyöarvo.class, 1);
      perustietoAdmin.setVyöarvo(vyöarvo);
      Assert.assertEquals(1, vyöarvot.get().size());
      perustietoAdmin.poistaVyöarvo();
      entityManager.clear();
      Assert.assertEquals(0, vyöarvot.get().size());
      vyöarvo = entityManager.find(Vyöarvo.class, 1);
      Assert.assertNull(vyöarvo);
   }

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
      entityManager.clear();
      vyöarvo = entityManager.find(Vyöarvo.class, 1);
      Assert.assertNotNull(vyöarvo);
   }

}
