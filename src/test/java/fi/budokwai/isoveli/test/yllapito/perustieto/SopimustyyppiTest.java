package fi.budokwai.isoveli.test.yllapito.perustieto;

import java.util.List;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
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
import fi.budokwai.isoveli.malli.Sopimustyyppi;
import fi.budokwai.isoveli.test.Perustesti;

@RunWith(Arquillian.class)
public class SopimustyyppiTest extends Perustesti
{
   @Inject
   private PerustietoAdmin perustietoAdmin;

   @Inject
   private EntityManager entityManager;

   @Inject
   @Named("sopimustyypit")
   private Instance<List<Sopimustyyppi>> sopimustyypit;

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testLisaaSopimustyyppi()
   {
      Sopimustyyppi sopimustyyppi = new Sopimustyyppi();
      sopimustyyppi.setNimi("nimi");
      perustietoAdmin.setSopimustyyppi(sopimustyyppi);
      Assert.assertEquals(0, sopimustyypit.get().size());
      perustietoAdmin.tallennaSopimustyyppi();
      entityManager.clear();
      Assert.assertEquals(1, sopimustyypit.get().size());
      sopimustyyppi = entityManager.createQuery("select st from Sopimustyyppi st", Sopimustyyppi.class)
         .getSingleResult();
      Assert.assertNotNull(sopimustyyppi);
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "jasenmaksu.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testMuokkaaSopimustyyppi()
   {
      Sopimustyyppi sopimustyyppi = entityManager.find(Sopimustyyppi.class, 1);
      sopimustyyppi.setNimi("foo");
      perustietoAdmin.setSopimustyyppi(sopimustyyppi);
      perustietoAdmin.tallennaSopimustyyppi();
      entityManager.clear();
      sopimustyyppi = entityManager.find(Sopimustyyppi.class, 1);
      Assert.assertEquals("foo", sopimustyyppi.getNimi());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "jasenmaksu.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testArkistoiSopimustyyppi()
   {
      Assert.assertEquals(1, sopimustyypit.get().size());
      Sopimustyyppi sopimustyyppi = entityManager.find(Sopimustyyppi.class, 1);
      sopimustyyppi.setArkistoitu(true);
      perustietoAdmin.setSopimustyyppi(sopimustyyppi);
      perustietoAdmin.tallennaSopimustyyppi();
      entityManager.clear();
      Assert.assertEquals(0, sopimustyypit.get().size());
      sopimustyyppi = entityManager.find(Sopimustyyppi.class, 1);
      Assert.assertNotNull(sopimustyyppi);
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "jasenmaksu.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testPoistaSopimustyyppiEiKaytossa()
   {
      Sopimustyyppi sopimustyyppi = entityManager.find(Sopimustyyppi.class, 1);
      perustietoAdmin.setSopimustyyppi(sopimustyyppi);
      Assert.assertEquals(1, sopimustyypit.get().size());
      perustietoAdmin.poistaSopimustyyppi();
      entityManager.clear();
      Assert.assertEquals(0, sopimustyypit.get().size());
      sopimustyyppi = entityManager.find(Sopimustyyppi.class, 1);
      Assert.assertNull(sopimustyyppi);
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "sopimustyyppikaytossa.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testPoistaSopimustyyppiKaytossa()
   {
      Sopimustyyppi sopimustyyppi = entityManager.find(Sopimustyyppi.class, 1);
      perustietoAdmin.setSopimustyyppi(sopimustyyppi);
      try
      {
         perustietoAdmin.poistaSopimustyyppi();
      } catch (IsoveliPoikkeus e)
      {
         Assert.assertEquals("Sopimustyyppi on käytössä ja sitä ei voi poistaa (1kpl: Nicklas Karlsson...)",
            e.getMessage());
      }
      entityManager.clear();
      sopimustyyppi = entityManager.find(Sopimustyyppi.class, 1);
      Assert.assertNotNull(sopimustyyppi);
   }

}
