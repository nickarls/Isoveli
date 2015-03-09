package fi.budokwai.isoveli.test.yllapito.ilmoittautuminen;

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
import fi.budokwai.isoveli.admin.IlmoittautumisAdmin;
import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Treeni;
import fi.budokwai.isoveli.malli.Treenik‰ynti;
import fi.budokwai.isoveli.malli.Treenisessio;
import fi.budokwai.isoveli.test.Perustesti;
import fi.budokwai.isoveli.util.DateUtil;

@RunWith(Arquillian.class)
public class IlmoittautumisTest extends Perustesti
{
   @Inject
   private IlmoittautumisAdmin ilmoittautumisAdmin;

   @Inject
   private EntityManager entityManager;

   @Inject
   @Named("treenit")
   private List<Treeni> treenit;

   @Inject
   private Instance<List<Treenisessio>> treenisessiot;

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "nicklas.sql", "treenityyppikaytossa.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testLisaatreenisessio()
   {
      Treenisessio sessio = ilmoittautumisAdmin.lis‰‰Treenisessio();
      Harrastaja nicklas = entityManager.find(Harrastaja.class, 1);
      sessio.setTreeni(treenit.iterator().next());
      sessio.getVet‰j‰t().add(nicklas);
      ilmoittautumisAdmin.tallennaTreenisessio();
      entityManager.clear();
      Assert.assertEquals(1, treenisessiot.get().size());
      Assert.assertEquals(1, treenisessiot.get().iterator().next().getVet‰j‰t().size());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "nicklas.sql", "treenikaytossa.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testMuokkaatreenisessio()
   {
      Treenisessio sessio = treenisessiot.get().iterator().next();
      sessio.setP‰iv‰(DateUtil.silloinD("10.10.2015"));
      ilmoittautumisAdmin.setTreenisessio(sessio);
      ilmoittautumisAdmin.tallennaTreenisessio();
      entityManager.clear();
      sessio = entityManager.find(Treenisessio.class, 1);
      Assert.assertEquals("10.10.2015", DateUtil.p‰iv‰Tekstiksi(sessio.getP‰iv‰()));
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "nicklas.sql", "treenikaytossa.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testPoistatreenisessioEiKaytossa()
   {
      Treenisessio sessio = treenisessiot.get().iterator().next();
      ilmoittautumisAdmin.setTreenisessio(sessio);
      ilmoittautumisAdmin.poistaTreenisessio();
      entityManager.clear();
      sessio = entityManager.find(Treenisessio.class, 1);
      Assert.assertNull(sessio);
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "nicklas.sql", "treenisessiokaytossa.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testPoistatreenisessioKaytossa()
   {
      Treenisessio sessio = treenisessiot.get().iterator().next();
      ilmoittautumisAdmin.setTreenisessio(sessio);
      try
      {
         ilmoittautumisAdmin.poistaTreenisessio();
      } catch (IsoveliPoikkeus e)
      {
         Assert.assertEquals("Treenisessiolla on treenik‰yntej‰ ja sit‰ ei voi poistaa (1kpl: Nicklas Karlsson...)",
            e.getMessage());
      }
      entityManager.clear();
      sessio = entityManager.find(Treenisessio.class, 1);
      Assert.assertNotNull(sessio);
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "nicklas.sql", "treenisessio.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testLisaaTreenikaynti()
   {
      Harrastaja nicklas = entityManager.find(Harrastaja.class, 1);
      Treenik‰ynti k‰ynti = ilmoittautumisAdmin.lis‰‰Treenik‰ynti();
      Treenisessio sessio = treenisessiot.get().iterator().next();
      k‰ynti.setHarrastaja(nicklas);
      k‰ynti.setTreenisessio(sessio);
      ilmoittautumisAdmin.setTreenisessio(sessio);
      ilmoittautumisAdmin.tallennaTreenik‰ynti();
      entityManager.clear();
      sessio = entityManager.find(Treenisessio.class, 1);
      Assert.assertEquals(1, sessio.getTreenik‰ynnit().size());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "nicklas.sql", "treenisessiokaytossa.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testMuokkaaTreenikaynti()
   {
      Treenik‰ynti k‰ynti = entityManager.find(Treenik‰ynti.class, 1);
      k‰ynti.setAikaleima(DateUtil.silloinD("10.10.2015"));
      ilmoittautumisAdmin.setTreenik‰ynti(k‰ynti);
      ilmoittautumisAdmin.tallennaTreenik‰ynti();
      entityManager.clear();
      k‰ynti = entityManager.find(Treenik‰ynti.class, 1);
      Assert.assertEquals("10.10.2015", DateUtil.p‰iv‰Tekstiksi(k‰ynti.getAikaleima()));
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "nicklas.sql", "treenisessiokaytossa.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testPoistaTreenikaynti()
   {
      Treenik‰ynti k‰ynti = entityManager.find(Treenik‰ynti.class, 1);
      ilmoittautumisAdmin.setTreenik‰ynti(k‰ynti);
      ilmoittautumisAdmin.poistaTreenik‰ynti();
      entityManager.clear();
      k‰ynti = entityManager.find(Treenik‰ynti.class, 1);
      Assert.assertNull(k‰ynti);
   }

}
