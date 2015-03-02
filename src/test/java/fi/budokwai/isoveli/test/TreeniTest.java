package fi.budokwai.isoveli.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import fi.budokwai.isoveli.malli.Treeni;
import fi.budokwai.isoveli.malli.Treenityyppi;
import fi.budokwai.isoveli.malli.Viikonpäivä;

@RunWith(Arquillian.class)
public class TreeniTest extends Perustesti
{
   @Inject
   private PerustietoAdmin perustietoAdmin;

   @Inject
   private EntityManager entityManager;

   @Inject
   @Named("treenit")
   private Instance<List<Treeni>> treenit;

   @Test
   @ApplyScriptBefore("perustekniikka.sql")
   @ApplyScriptAfter("cleanup.sql")
   public void testLisaaTreeni() throws ParseException
   {
      Treenityyppi treenityyppi = entityManager.find(Treenityyppi.class, 1);
      Treeni treeni = new Treeni();
      treeni.setNimi("Treeni");
      SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
      treeni.setAlkaa(sdf.parse("16:00"));
      treeni.setPäättyy(sdf.parse("16:00"));
      treeni.setPäivä(Viikonpäivä.Maanantai);
      treeni.setTyyppi(treenityyppi);
      perustietoAdmin.setTreeni(treeni);
      Assert.assertEquals(0, treenit.get().size());
      perustietoAdmin.tallennaTreeni();
      entityManager.clear();
      Assert.assertEquals(1, treenit.get().size());
      Treeni testi = entityManager.createQuery("select t from Treeni t", Treeni.class).getSingleResult();
      Assert.assertNotNull(testi);
   }

   @Test
   @ApplyScriptBefore("treenityyppikaytossa.sql")
   @ApplyScriptAfter("cleanup.sql")
   public void testMuokkaaTreeni()
   {
      Treeni treeni = entityManager.find(Treeni.class, 1);
      treeni.setNimi("foo");
      perustietoAdmin.setTreeni(treeni);
      perustietoAdmin.tallennaTreeni();
      entityManager.clear();
      treeni = entityManager.find(Treeni.class, 1);
      Assert.assertEquals("foo", treeni.getNimi());
   }

   @Test
   @ApplyScriptBefore("treenityyppikaytossa.sql")
   @ApplyScriptAfter("cleanup.sql")
   public void testArkistoiTreeni()
   {
      Assert.assertEquals(1, treenit.get().size());
      Treeni treeni = entityManager.find(Treeni.class, 1);
      treeni.setArkistoitu(true);
      perustietoAdmin.setTreeni(treeni);
      perustietoAdmin.tallennaTreeni();
      entityManager.clear();
      Assert.assertEquals(0, treenit.get().size());
      treeni = entityManager.find(Treeni.class, 1);
      Assert.assertNotNull(treeni);
   }

   @Test
   @ApplyScriptBefore("treenityyppikaytossa.sql")
   @ApplyScriptAfter("cleanup.sql")
   public void testPoistaTreeniEiKaytossa()
   {
      Treeni treeni = entityManager.find(Treeni.class, 1);
      perustietoAdmin.setTreeni(treeni);
      Assert.assertEquals(1, treenit.get().size());
      perustietoAdmin.poistaTreeni();
      entityManager.clear();
      Assert.assertEquals(0, treenit.get().size());
      treeni = entityManager.find(Treeni.class, 1);
      Assert.assertNull(treeni);
      Treenityyppi treenityyppi = entityManager.find(Treenityyppi.class, 1);
      Assert.assertNotNull(treenityyppi);
   }

   @Test
   @ApplyScriptBefore("treenikaytossa.sql")
   @ApplyScriptAfter("cleanup.sql")
   public void testPoistaTreeniKaytossa()
   {
      Treeni treeni = entityManager.find(Treeni.class, 1);
      perustietoAdmin.setTreeni(treeni);
      try
      {
         perustietoAdmin.poistaTreeni();
      } catch (IsoveliPoikkeus e)
      {
         Assert.assertEquals("Treeni on käytössä ja sitä ei voi poistaa (1kpl: 12.12.2012...)", e.getMessage());
      }
      entityManager.clear();
      treeni = entityManager.find(Treeni.class, 1);
      Assert.assertNotNull(treeni);
   }

}
