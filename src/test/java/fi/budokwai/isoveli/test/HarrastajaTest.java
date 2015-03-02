package fi.budokwai.isoveli.test;

import java.util.List;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ApplyScriptAfter;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import fi.budokwai.isoveli.admin.HarrastajaAdmin;
import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Osoite;
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

   @Test
   @ApplyScriptAfter("cleanup.sql")
   public void testLisaaTaysiikainenPerheetonHarrastaja()
   {
      Harrastaja harrastaja = new Harrastaja();
      harrastaja.setEtunimi("Nicklas");
      harrastaja.setSukunimi("Karlsson");
      harrastaja.setSukupuoli(Sukupuoli.M);
      harrastaja.setSyntynyt(DateUtil.silloinD("28.06.1975"));

      harrastaja.getYhteystiedot().setPuhelinnumero("1");
      harrastaja.getYhteystiedot().setSähköposti("a");

      harrastaja.getOsoite().setOsoite("V");
      harrastaja.getOsoite().setKaupunki("K");
      harrastaja.getOsoite().setPostinumero("2");

      harrastajaAdmin.setHarrastaja(harrastaja);
      Assert.assertEquals(0, harrastajat.get().size());
      harrastajaAdmin.tallennaHarrastaja();
      entityManager.clear();
      Assert.assertEquals(1, harrastajat.get().size());

      harrastaja = entityManager.createQuery("select h from Harrastaja h", Harrastaja.class).getSingleResult();
      Assert.assertEquals("V", harrastaja.getOsoite().getOsoite());
      Assert.assertEquals("1", harrastaja.getYhteystiedot().getPuhelinnumero());
   }
   
   @Test
   @ApplyScriptAfter("cleanup.sql")
   public void testLisaaTaysiikainenYhdenHengenPerheenHarrastaja()
   {
   }
   

   @Test
   @ApplyScriptBefore("nicklas.sql")
   @ApplyScriptAfter("cleanup.sql")
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
   @ApplyScriptBefore("nicklas.sql")
   @ApplyScriptAfter("cleanup.sql")
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

}
