package fi.budokwai.isoveli.test;

import java.util.List;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

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
   @ApplyScriptBefore(
   { "cleanup.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
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
   @ApplyScriptBefore(
   { "cleanup.sql", "keltainenvyo.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
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
   @ApplyScriptBefore(
   { "cleanup.sql", "keltainenvyo.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
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
   @ApplyScriptBefore(
   { "cleanup.sql", "keltainenvyo.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
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
   @ApplyScriptBefore(
   { "cleanup.sql", "vyoarvokaytossa.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
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
