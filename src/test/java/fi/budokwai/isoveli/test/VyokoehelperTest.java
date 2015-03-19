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

import fi.budokwai.isoveli.admin.HarrastajaAdmin;
import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Vy�arvo;
import fi.budokwai.isoveli.malli.Vy�koe;
import fi.budokwai.isoveli.util.DateUtil;
import fi.budokwai.isoveli.util.Vy�koehelper;

@RunWith(Arquillian.class)
@ApplyScriptBefore(
{ "cleanup.sql", "seed.sql" })
@Cleanup(phase = TestExecutionPhase.NONE)
public class VyokoehelperTest extends Perustesti
{
   @Inject
   private Vy�koehelper vy�koehelper;

   @Inject
   private HarrastajaAdmin harrastajaAdmin;

   @Inject
   private Instance<List<Vy�arvo>> vy�arvot;

   @Inject
   private EntityManager entityManager;

   @Test
   public void testVyoarvotOlemassa()
   {
      Assert.assertEquals(18, vy�koehelper.getVy�arvot().size());
   }

   public void testEiPoomArvojaYli16Vuotiaalle()
   {
      Harrastaja harrastaja = new Harrastaja();
      harrastaja.setSyntynyt(DateUtil.silloinD("01.01.1970"));
      harrastajaAdmin.setHarrastaja(harrastaja);
      vy�arvot.get().forEach(v -> {
         Assert.assertFalse(v.isPoom());
      });
   }

   public void testEiDanArvojaAlle16Vuotiaalle()
   {
      Harrastaja harrastaja = new Harrastaja();
      harrastaja.setSyntynyt(DateUtil.silloinD("01.01.2010"));
      harrastajaAdmin.setHarrastaja(harrastaja);
      vy�arvot.get().forEach(v -> {
         Assert.assertFalse(v.isDan());
      });
   }

   public void testVyoarvojenSuodatus()
   {
      Harrastaja harrastaja = new Harrastaja();
      harrastaja.setSyntynyt(DateUtil.silloinD("01.01.1970"));
      harrastajaAdmin.setHarrastaja(harrastaja);
      Vy�arvo vihre� = entityManager.find(Vy�arvo.class, 3);
      harrastaja.lis��Vy�koe(new Vy�koe(vihre�));
      Assert.assertFalse(vy�arvot.get().contains(vihre�));
   }

   @Test
   public void testSeuraavaVyoarvoEiVyota()
   {
      Harrastaja harrastaja = teeHarrastaja("N K", "1.1.1970");
      Vy�arvo seuraava = vy�koehelper.haeSeuraavaVy�arvo(harrastaja);
      Assert.assertEquals(seuraava.getNimi(), "8.kup");
   }

   @Test
   public void testSeuraavaVyoarvoVihrea()
   {
      Harrastaja harrastaja = teeHarrastaja("N K", "1.1.2000");
      teeVy�koe(harrastaja, "1.1.1980", "4.kup", 5);
      Vy�arvo seuraava = vy�koehelper.haeSeuraavaVy�arvo(harrastaja);
      Assert.assertEquals(seuraava.getNimi(), "3.kup");
   }

   @Test
   public void testSeuraavaVyoarvo3dan()
   {
      Harrastaja harrastaja = teeHarrastaja("N K", "1.1.1970");
      teeVy�koe(harrastaja, "1.1.1980", "2.dan", 12);
      harrastajaAdmin.setHarrastaja(harrastaja);
      Vy�arvo seuraava = vy�koehelper.haeSeuraavaVy�arvo(harrastaja);
      Assert.assertEquals(seuraava.getNimi(), "3.dan");
   }
   
   @Test
   public void testSeuraavaVyoarvo3poom()
   {
      Harrastaja harrastaja = teeHarrastaja("N K", "1.1.2010");
      teeVy�koe(harrastaja, "1.1.2011", "2.poom", 12);
      harrastajaAdmin.setHarrastaja(harrastaja);
      Vy�arvo seuraava = vy�koehelper.haeSeuraavaVy�arvo(harrastaja);
      Assert.assertEquals(seuraava.getNimi(), "3.poom");
   }
   

}
