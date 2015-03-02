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

import fi.budokwai.isoveli.IsoveliPoikkeus;
import fi.budokwai.isoveli.admin.PerustietoAdmin;
import fi.budokwai.isoveli.malli.Rooli;

@RunWith(Arquillian.class)
public class RooliTest extends Perustesti
{
   @Inject
   private PerustietoAdmin perustietoAdmin;

   @Inject
   private EntityManager entityManager;

   @Inject
   @Named("roolit")
   private Instance<List<Rooli>> roolit;

   @Test
   @ApplyScriptAfter("cleanup.sql")
   public void testLisaaRooli()
   {
      Rooli rooli = new Rooli();
      rooli.setNimi("rooli");
      perustietoAdmin.setRooli(rooli);
      Assert.assertEquals(0, roolit.get().size());
      perustietoAdmin.tallennaRooli();
      entityManager.clear();
      Assert.assertEquals(1, roolit.get().size());
      rooli = entityManager.createQuery("select r from Rooli r", Rooli.class).getSingleResult();
      Assert.assertNotNull(rooli);
   }

   @Test
   @ApplyScriptBefore("yllapitajarooli.sql")
   @ApplyScriptAfter("cleanup.sql")
   public void testMuokkaaRooli()
   {
      Rooli rooli = entityManager.find(Rooli.class, 1);
      rooli.setNimi("foo");
      perustietoAdmin.setRooli(rooli);
      perustietoAdmin.tallennaRooli();
      entityManager.clear();
      rooli = entityManager.find(Rooli.class, 1);
      Assert.assertEquals("foo", rooli.getNimi());
   }

   @Test
   @ApplyScriptBefore("yllapitajarooli.sql")
   @ApplyScriptAfter("cleanup.sql")
   public void testPoistaRooliEiKaytossa()
   {
      Rooli rooli = entityManager.find(Rooli.class, 1);
      perustietoAdmin.setRooli(rooli);
      Assert.assertEquals(1, roolit.get().size());
      perustietoAdmin.poistaRooli();
      entityManager.clear();
      Assert.assertEquals(0, roolit.get().size());
      rooli = entityManager.find(Rooli.class, 1);
      Assert.assertNull(rooli);
   }

   @Test
   @ApplyScriptBefore("roolikaytossa.sql")
   @ApplyScriptAfter("cleanup.sql")
   public void testPoistaRooliKaytossa()
   {
      Rooli rooli = entityManager.find(Rooli.class, 1);
      perustietoAdmin.setRooli(rooli);
      try
      {
         perustietoAdmin.poistaRooli();
      } catch (IsoveliPoikkeus e)
      {
         Assert.assertEquals("Rooli on k�yt�ss� ja sit� ei voi poistaa (1kpl: Nicklas Karlsson...)", e.getMessage());
      }
      entityManager.clear();
      rooli = entityManager.find(Rooli.class, 1);
      Assert.assertNotNull(rooli);
   }

}