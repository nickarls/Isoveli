package fi.budokwai.isoveli.test;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ApplyScriptAfter;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import fi.budokwai.isoveli.admin.LaskutusAdmin;
import fi.budokwai.isoveli.malli.Lasku;
import fi.budokwai.isoveli.malli.Laskurivi;

@RunWith(Arquillian.class)
public class LaskutusTest extends Perustesti
{

   @Inject
   private LaskutusAdmin laskutusAdmin;

   @Inject
   private EntityManager entityManager;

   @Test
   @ApplyScriptBefore(
   { "seed.sql", "huoltajaperhe.sql", "emilsopimus.sql" })
   @ApplyScriptAfter("cleanup.sql")
   public void testLaskutushenkiloHuoltajalla()
   {
      laskutusAdmin.laskutaSopimukset();
      Lasku lasku = entityManager.createQuery("select l from Lasku l", Lasku.class).getResultList().iterator().next();
      Assert.assertEquals("Nicklas Karlsson", lasku.getHenkilö().getNimi());
   }

   @Test
   @ApplyScriptBefore(
   { "seed.sql", "harrastajaperhe.sql", "emilsopimus.sql" })
   @ApplyScriptAfter("cleanup.sql")
   public void testLaskutushenkiloTaysiikaisellaPerheenjasenella()
   {
      laskutusAdmin.laskutaSopimukset();
      Lasku lasku = entityManager.createQuery("select l from Lasku l", Lasku.class).getResultList().iterator().next();
      Assert.assertEquals("Nicklas Karlsson", lasku.getHenkilö().getNimi());
   }

   @Test
   @ApplyScriptBefore(
   { "seed.sql", "harrastajaperhe.sql", "emilsopimus.sql", "nicklassopimus.sql" })
   @ApplyScriptAfter("cleanup.sql")
   public void testLaskutushenkiloTaysiikaisella()
   {
      laskutusAdmin.laskutaSopimukset();
      Lasku lasku = entityManager.createQuery("select l from Lasku l", Lasku.class).getResultList().iterator().next();
      Assert.assertEquals("Nicklas Karlsson", lasku.getHenkilö().getNimi());
   }

   @Test
   @ApplyScriptBefore(
   { "seed.sql", "harrastajaperhe.sql", "harrastajasopimukset.sql" })
   @ApplyScriptAfter("cleanup.sql")
   public void testPerhealennus()
   {
      laskutusAdmin.laskutaSopimukset();
      Lasku lasku = entityManager.createQuery("select l from Lasku l", Lasku.class).getResultList().iterator().next();
      Assert.assertEquals(3, lasku.getLaskurivejä());
      Assert.assertEquals(-46.8, lasku.getLaskurivit().get(2).getRivihinta(), 0.0001);
   }

   @Test
   @ApplyScriptBefore(
   { "seed.sql", "harrastajaperhe.sql", "kertasopimukset.sql" })
   @ApplyScriptAfter("cleanup.sql")
   public void testPerhealennusVainHarjoittelumaksuista()
   {
      laskutusAdmin.laskutaSopimukset();
      Lasku lasku = entityManager.createQuery("select l from Lasku l", Lasku.class).getResultList().iterator().next();
      Assert.assertEquals(2, lasku.getLaskurivejä());
   }

   @Test
   @ApplyScriptBefore(
   { "seed.sql", "harrastajaperhe.sql", "erihintaisetsopimukset.sql" })
   @ApplyScriptAfter("cleanup.sql")
   public void testPerhealennusKohdistuuKallimpaan()
   {
      laskutusAdmin.laskutaSopimukset();
      Lasku lasku = entityManager.createQuery("select l from Lasku l", Lasku.class).getResultList().iterator().next();
      Assert.assertEquals(3, lasku.getLaskurivejä());
      Assert.assertEquals(-46.8, lasku.getLaskurivit().get(2).getRivihinta(), 0.0001);
   }

   @Test
   @ApplyScriptBefore(
   { "seed.sql", "harrastajaperhe.sql", "tauko.sql" })
   @ApplyScriptAfter("cleanup.sql")
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
