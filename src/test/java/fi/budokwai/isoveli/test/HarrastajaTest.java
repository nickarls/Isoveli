package fi.budokwai.isoveli.test;

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

import fi.budokwai.isoveli.admin.HarrastajaAdmin;
import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Henkilö;
import fi.budokwai.isoveli.malli.Osoite;
import fi.budokwai.isoveli.malli.Perhe;
import fi.budokwai.isoveli.malli.Sukupuoli;
import fi.budokwai.isoveli.malli.Yhteystieto;
import fi.budokwai.isoveli.util.DateUtil;

@RunWith(Arquillian.class)
public class HarrastajaTest extends Perustesti
{
   @Inject
   private HarrastajaAdmin harrastajaAdmin;

   @Inject
   private EntityManager entityManager;

   @Inject
   @Named("harrastajat")
   private Instance<List<Harrastaja>> harrastajat;

   @Inject
   @Named("perheet")
   private Instance<List<Perhe>> perheet;

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testLisaaTaysiikainenPerheetonHarrastaja()
   {
      Harrastaja harrastaja = new Harrastaja();
      harrastaja.setEtunimi("Nicklas");
      harrastaja.setSukunimi("Karlsson");
      harrastaja.setSukupuoli(Sukupuoli.M);
      harrastaja.setSyntynyt(DateUtil.silloinD("28.06.1975"));

      harrastaja.getYhteystiedot().setPuhelinnumero("0405062266");
      harrastaja.getYhteystiedot().setSähköposti("nickarls@gmail.com");

      harrastaja.getOsoite().setOsoite("Vaakunatie 10 as7");
      harrastaja.getOsoite().setPostinumero("20780");
      harrastaja.getOsoite().setKaupunki("Kaarina");

      harrastajaAdmin.setHarrastaja(harrastaja);
      Assert.assertEquals(0, harrastajat.get().size());
      harrastajaAdmin.tallennaHarrastaja();
      entityManager.clear();
      Assert.assertEquals(1, harrastajat.get().size());

      harrastaja = entityManager.createQuery("select h from Harrastaja h", Harrastaja.class).getSingleResult();
      Assert.assertEquals("Vaakunatie 10 as7", harrastaja.getOsoite().getOsoite());
      Assert.assertEquals("0405062266", harrastaja.getYhteystiedot().getPuhelinnumero());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testLisaaTaysiikainenYhdenHengenPerheenHarrastaja()
   {
      harrastajaAdmin.lisääHarrastaja();
      Harrastaja harrastaja = harrastajaAdmin.getHarrastaja();

      harrastaja.setEtunimi("Nicklas");
      harrastaja.setSukunimi("Karlsson");
      harrastaja.setSukupuoli(Sukupuoli.M);
      harrastaja.setSyntynyt(DateUtil.silloinD("28.06.1975"));

      harrastajaAdmin.lisääPerhe();
      Assert.assertEquals(1, perheet.get().size());
      harrastaja.getOsoite().setOsoite("Vaakunatie 10 as7");
      harrastaja.getOsoite().setPostinumero("20780");
      harrastaja.getOsoite().setKaupunki("Kaarina");

      harrastajaAdmin.setHarrastaja(harrastaja);
      harrastajaAdmin.tallennaHarrastaja();
      entityManager.clear();
      Assert.assertEquals(1, harrastajat.get().size());

      harrastaja = entityManager.createQuery("select h from Harrastaja h", Harrastaja.class).getSingleResult();
      Assert.assertEquals("Vaakunatie 10 as7", harrastaja.getOsoite().getOsoite());
      Perhe perhe = entityManager.createQuery("select p from Perhe p", Perhe.class).getSingleResult();
      Assert.assertEquals("Vaakunatie 10 as7", perhe.getOsoite().getOsoite());
      Assert.assertEquals(1, harrastaja.getPerhe().getPerheenjäsenet().size());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testLisaaAlaikainenHarrastaja()
   {
      harrastajaAdmin.lisääHarrastaja();
      Harrastaja harrastaja = harrastajaAdmin.getHarrastaja();

      harrastaja.setEtunimi("Emil");
      harrastaja.setSukunimi("Karlsson");
      harrastaja.setSukupuoli(Sukupuoli.M);
      harrastaja.setSyntynyt(DateUtil.silloinD("30.10.2005"));

      Assert.assertEquals(0, perheet.get().size());
      harrastajaAdmin.lisääPerhe();
      Assert.assertEquals(1, perheet.get().size());

      Assert.assertEquals(0, harrastaja.getPerhe().getHuoltajat().size());
      harrastajaAdmin.lisääHuoltaja();
      Assert.assertEquals(1, harrastaja.getPerhe().getHuoltajat().size());
      Henkilö huoltaja = harrastaja.getHuoltaja();
      huoltaja.setEtunimi("Nicklas");

      harrastaja.getOsoite().setOsoite("Vaakunatie 10 as7");
      harrastaja.getOsoite().setPostinumero("20780");
      harrastaja.getOsoite().setKaupunki("Kaarina");

      harrastajaAdmin.setHarrastaja(harrastaja);
      harrastajaAdmin.tallennaHarrastaja();
      entityManager.clear();
      Assert.assertEquals(1, harrastajat.get().size());

      harrastaja = entityManager.createQuery("select h from Harrastaja h", Harrastaja.class).getSingleResult();
      Assert.assertEquals("Vaakunatie 10 as7", harrastaja.getOsoite().getOsoite());
      Perhe perhe = entityManager.createQuery("select p from Perhe p", Perhe.class).getSingleResult();
      Assert.assertEquals("Vaakunatie 10 as7", perhe.getOsoite().getOsoite());
      Assert.assertEquals(2, harrastaja.getPerhe().getPerheenjäsenet().size());
      Assert.assertEquals("Nicklas", harrastaja.getHuoltaja().getEtunimi());
      Assert.assertEquals(1, harrastaja.getPerhe().getHuoltajat().size());
      Assert.assertEquals("Nicklas", harrastaja.getPerhe().getHuoltajat().iterator().next().getEtunimi());
      huoltaja = harrastaja.getPerhe().getHuoltajat().iterator().next();
      Assert.assertEquals(1, huoltaja.getPerhe().getHuollettavat(huoltaja).size());
      harrastaja = (Harrastaja) huoltaja.getPerhe().getHuollettavat(huoltaja).iterator().next();
      Assert.assertEquals("Emil", harrastaja.getEtunimi());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "nicklas.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testOrvotOsoitteetSiivotaan()
   {
      Harrastaja harrastaja = entityManager.find(Harrastaja.class, 1);
      Assert.assertNotNull(harrastaja.getOsoite());
      harrastaja.setOsoite(null);
      harrastajaAdmin.setHarrastaja(harrastaja);
      harrastajaAdmin.tallennaHarrastaja();
      entityManager.clear();
      harrastaja = entityManager.find(Harrastaja.class, 1);
      Assert.assertTrue(harrastaja.getOsoite().isKäyttämätön());
      Osoite osoite = entityManager.find(Osoite.class, 1);
      Assert.assertNull(osoite);
      entityManager.clear();
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "nicklas.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testOrvotYhteystiedotSiivotaan()
   {
      Harrastaja harrastaja = entityManager.find(Harrastaja.class, 1);
      Assert.assertNotNull(harrastaja.getYhteystiedot());
      harrastaja.setYhteystiedot(null);
      harrastajaAdmin.setHarrastaja(harrastaja);
      harrastajaAdmin.tallennaHarrastaja();
      entityManager.clear();
      harrastaja = entityManager.find(Harrastaja.class, 1);
      Assert.assertTrue(harrastaja.getYhteystiedot().isKäyttämätön());
      Yhteystieto yhteystiedot = entityManager.find(Yhteystieto.class, 1);
      Assert.assertNull(yhteystiedot);
      entityManager.clear();
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testHuoltajanPuhelinMuutosPaivittaaICEn()
   {
      throw new UnsupportedOperationException();
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testHuoltajaMuuttuu()
   {
      throw new UnsupportedOperationException();
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "karlsson.sql", "rosqvist.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testPerheMuuttuu()
   {
      Assert.assertEquals(2, perheet.get().size());
      perheet.get().stream().forEach(p -> Assert.assertEquals(2, p.getPerheenjäsenet().size()));
      perheet.get().stream().forEach(p -> Assert.assertEquals(1, p.getHuoltajat().size()));
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testOrpoPerhePoistuu()
   {
      throw new UnsupportedOperationException();
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testOrpoOsoitePoistuu()
   {
      throw new UnsupportedOperationException();
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testPoistaHarrastajaEiKaytossa()
   {
      throw new UnsupportedOperationException();
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testPoistaHarrastajaKaytossa()
   {
      throw new UnsupportedOperationException();
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testArkistoiHarrastaja()
   {
      throw new UnsupportedOperationException();
   }

}
