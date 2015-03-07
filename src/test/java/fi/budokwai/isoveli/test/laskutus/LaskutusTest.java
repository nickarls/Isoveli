package fi.budokwai.isoveli.test.laskutus;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import fi.budokwai.isoveli.admin.LaskutusAdmin;
import fi.budokwai.isoveli.malli.Lasku;
import fi.budokwai.isoveli.malli.Laskurivi;
import fi.budokwai.isoveli.malli.Sopimuslasku;
import fi.budokwai.isoveli.malli.TilausTila;
import fi.budokwai.isoveli.test.Perustesti;

@RunWith(Arquillian.class)
public class LaskutusTest extends Perustesti
{

   @Inject
   private LaskutusAdmin laskutusAdmin;

   @Inject
   private EntityManager entityManager;

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "huoltajaperhe.sql", "emilsopimus.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testLaskutushenkiloHuoltajalla()
   {
      laskutusAdmin.laskutaSopimukset();
      Lasku lasku = entityManager.createQuery("select l from Lasku l", Lasku.class).getResultList().iterator().next();
      Assert.assertEquals("Nicklas Karlsson", lasku.getHenkilö().getNimi());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "huoltajaperhe.sql", "emilsopimus.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testPoistalaskurivi()
   {
      laskutusAdmin.laskutaSopimukset();
      Lasku lasku = entityManager.createQuery("select l from Lasku l", Lasku.class).getResultList().iterator().next();
      List<Sopimuslasku> sopimuslaskut = entityManager.createQuery("select s from Sopimuslasku s", Sopimuslasku.class)
         .getResultList();
      Assert.assertEquals(1, lasku.getLaskurivejä());
      Assert.assertEquals(1, sopimuslaskut.size());
      laskutusAdmin.poistaRivi(lasku.getLaskurivit().iterator().next());
      entityManager.refresh(lasku);
      Assert.assertEquals(0, lasku.getLaskurivejä());
      entityManager.clear();
      sopimuslaskut = entityManager.createQuery("select s from Sopimuslasku s", Sopimuslasku.class).getResultList();
      Assert.assertEquals(0, sopimuslaskut.size());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "harrastajaperhe.sql", "emilsopimus.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testLaskutushenkiloTaysiikaisellaPerheenjasenella()
   {
      laskutusAdmin.laskutaSopimukset();
      Lasku lasku = entityManager.createQuery("select l from Lasku l", Lasku.class).getResultList().iterator().next();
      Assert.assertEquals("Nicklas Karlsson", lasku.getHenkilö().getNimi());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "harrastajaperhe.sql", "emilsopimus.sql", "nicklassopimus.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testLaskutushenkiloTaysiikaisella()
   {
      laskutusAdmin.laskutaSopimukset();
      Lasku lasku = entityManager.createQuery("select l from Lasku l", Lasku.class).getResultList().iterator().next();
      Assert.assertEquals("Nicklas Karlsson", lasku.getHenkilö().getNimi());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "harrastajaperhe.sql", "harrastajasopimukset.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testRelaatiotJaTilat()
   {
      laskutusAdmin.laskutaSopimukset();
      Lasku lasku = entityManager.createQuery("select l from Lasku l", Lasku.class).getResultList().iterator().next();
      for (Laskurivi laskurivi : lasku.getLaskurivit())
      {
         Assert.assertNotNull(lasku.getHenkilö());
         Assert.assertNotNull(lasku.getPdf());
         Assert.assertNotNull(lasku.getViitenumero());
         Assert.assertNotNull(lasku.getEräpäivä());
         Assert.assertNotNull(lasku.getId());
         Assert.assertNotNull(lasku.getLuotu());
         Assert.assertNull(lasku.getMaksettu());
         Assert.assertEquals(TilausTila.M, lasku.getTila());
         Assert.assertNotNull(laskurivi.getLasku());
         Assert.assertNotNull(laskurivi.getSopimuslasku());
         Assert.assertNotNull(laskurivi.getSopimuslasku().getLaskurivi());
         Assert.assertNotNull(laskurivi.getSopimuslasku().getSopimus());
         Assert.assertNotNull(laskurivi.getSopimuslasku());
      }
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "harrastajaperhe.sql", "harrastajasopimukset.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testPerhealennus()
   {
      laskutusAdmin.laskutaSopimukset();
      Lasku lasku = entityManager.createQuery("select l from Lasku l", Lasku.class).getResultList().iterator().next();
      Assert.assertEquals(3, lasku.getLaskurivejä());
      Assert.assertEquals(-46.8, lasku.getLaskurivit().get(2).getRivihinta(), 0.0001);
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "harrastajaperhe.sql", "kertasopimukset.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testPerhealennusVainHarjoittelumaksuista()
   {
      laskutusAdmin.laskutaSopimukset();
      Lasku lasku = entityManager.createQuery("select l from Lasku l", Lasku.class).getResultList().iterator().next();
      Assert.assertEquals(2, lasku.getLaskurivejä());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "harrastajaperhe.sql", "erihintaisetsopimukset.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testPerhealennusKohdistuuKallimpaan()
   {
      laskutusAdmin.laskutaSopimukset();
      Lasku lasku = entityManager.createQuery("select l from Lasku l", Lasku.class).getResultList().iterator().next();
      Assert.assertEquals(3, lasku.getLaskurivejä());
      Assert.assertEquals(-46.8, lasku.getLaskurivit().get(2).getRivihinta(), 0.0001);
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "harrastajaperhe.sql", "tauko.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testTauko()
   {
      laskutusAdmin.laskutaSopimukset();
      Lasku lasku = entityManager.createQuery("select l from Lasku l", Lasku.class).getResultList().iterator().next();
      Laskurivi tauko = lasku.getLaskurivit().get(1);
      Assert.assertEquals("01.02.2015-01.03.2015", tauko.getInfotieto());
      Assert.assertEquals("Taukohyvitys (Nicklas)", tauko.getTuotenimi());
      Assert.assertEquals(28, tauko.getMäärä());
      Assert.assertEquals(-1.3, tauko.getYksikköhinta(), 0.0001);
      Assert.assertEquals(-36.4, tauko.getRivihinta(), 0.0001);
   }

}
