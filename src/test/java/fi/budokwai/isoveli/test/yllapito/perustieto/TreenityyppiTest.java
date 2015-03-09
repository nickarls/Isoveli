package fi.budokwai.isoveli.test.yllapito.perustieto;

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
import fi.budokwai.isoveli.malli.Treenityyppi;
import fi.budokwai.isoveli.test.Perustesti;

@RunWith(Arquillian.class)
public class TreenityyppiTest extends Perustesti
{
   @Inject
   private PerustietoAdmin perustietoAdmin;

   @Inject
   private EntityManager entityManager;

   @Inject
   private Instance<List<Treenityyppi>> treenityypit;

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testLisaaTreenityyppi()
   {
      Treenityyppi treenityyppi = new Treenityyppi();
      treenityyppi.setId(1);
      treenityyppi.setNimi("foo");
      perustietoAdmin.setTreenityyppi(treenityyppi);
      Assert.assertEquals(0, treenityypit.get().size());
      perustietoAdmin.tallennaTreenityyppi();
      entityManager.clear();
      Assert.assertEquals(1, treenityypit.get().size());
      Treenityyppi testi = entityManager.createQuery("select t from Treenityyppi t", Treenityyppi.class)
         .getSingleResult();
      Assert.assertNotNull(testi);
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "perustekniikka.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testMuokkaaTreenityyppi()
   {
      Treenityyppi treenityyppi = entityManager.find(Treenityyppi.class, 1);
      treenityyppi.setNimi("foo");
      perustietoAdmin.setTreenityyppi(treenityyppi);
      perustietoAdmin.tallennaTreenityyppi();
      entityManager.clear();
      treenityyppi = entityManager.find(Treenityyppi.class, 1);
      Assert.assertEquals("foo", treenityyppi.getNimi());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "perustekniikka.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testArkistoiTreenityyppi()
   {
      Assert.assertEquals(1, treenityypit.get().size());
      Treenityyppi treenityyppi = entityManager.find(Treenityyppi.class, 1);
      treenityyppi.setArkistoitu(true);
      perustietoAdmin.setTreenityyppi(treenityyppi);
      perustietoAdmin.tallennaTreenityyppi();
      entityManager.clear();
      Assert.assertEquals(0, treenityypit.get().size());
      treenityyppi = entityManager.find(Treenityyppi.class, 1);
      Assert.assertNotNull(treenityyppi);
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "perustekniikka.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testPoistaVyoarvoEiKaytossa()
   {
      Treenityyppi treenityyppi = entityManager.find(Treenityyppi.class, 1);
      perustietoAdmin.setTreenityyppi(treenityyppi);
      Assert.assertEquals(1, treenityypit.get().size());
      perustietoAdmin.poistaTreenityyppi();
      entityManager.clear();
      Assert.assertEquals(0, treenityypit.get().size());
      treenityyppi = entityManager.find(Treenityyppi.class, 1);
      Assert.assertNull(treenityyppi);
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "treenityyppikaytossa.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testPoistaVyoarvoKaytossa()
   {
      Treenityyppi treenityyppi = entityManager.find(Treenityyppi.class, 1);
      perustietoAdmin.setTreenityyppi(treenityyppi);
      exception.expect(IsoveliPoikkeus.class);
      exception.expectMessage("Treenityyppi on käytössä ja sitä ei voi poistaa (1kpl: reeni...)");
      perustietoAdmin.poistaTreenityyppi();
      entityManager.clear();
      treenityyppi = entityManager.find(Treenityyppi.class, 1);
      Assert.assertNotNull(treenityyppi);
   }

}
