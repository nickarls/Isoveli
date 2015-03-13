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
import fi.budokwai.isoveli.util.Harrastajan;
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
   @Harrastajan
   private Instance<List<Vy�arvo>> harrastajanVy�arvot;

   @Inject
   private EntityManager entityManager;

   @Test
   public void testVyoarvotOlemassa()
   {
      Assert.assertEquals(18, vy�koehelper.getVy�arvot().size());
   }

   @Test
   public void testEiPoomArvojaYli16Vuotiaalle()
   {
      Harrastaja harrastaja = new Harrastaja();
      harrastaja.setSyntynyt(DateUtil.silloinD("01.01.1970"));
      harrastajaAdmin.setHarrastaja(harrastaja);
      harrastajanVy�arvot.get().forEach(v -> {
         Assert.assertFalse(v.isPoom());
      });
   }

   @Test
   public void testEiDanArvojaAlle16Vuotiaalle()
   {
      Harrastaja harrastaja = new Harrastaja();
      harrastaja.setSyntynyt(DateUtil.silloinD("01.01.2010"));
      harrastajaAdmin.setHarrastaja(harrastaja);
      harrastajanVy�arvot.get().forEach(v -> {
         Assert.assertFalse(v.isDan());
      });
   }

   @Test
   public void testVyoarvojenSuodatus()
   {
      Harrastaja harrastaja = new Harrastaja();
      harrastaja.setSyntynyt(DateUtil.silloinD("01.01.1970"));
      harrastajaAdmin.setHarrastaja(harrastaja);
      Vy�arvo vihre� = entityManager.find(Vy�arvo.class, 3);
      harrastaja.lis��Vy�koe(new Vy�koe(vihre�));
      Assert.assertFalse(harrastajanVy�arvot.get().contains(vihre�));
   }

   @Test
   public void testSeuraavaVyoarvoEiVyota()
   {
      Vy�arvo seuraava = vy�koehelper.haeSeuraavaVy�arvo(Vy�arvo.EI_OOTA);
      Assert.assertEquals(seuraava.getNimi(), "8.kup");
   }

   @Test
   public void testSeuraavaVyoarvoVihrea()
   {
      Vy�arvo vihre� = entityManager.find(Vy�arvo.class, 3);
      Vy�arvo seuraava = vy�koehelper.haeSeuraavaVy�arvo(vihre�);
      Assert.assertEquals(seuraava.getNimi(), "5.kup");
   }

   @Test
   public void testSeuraavaVyoarvo3dan()
   {
      Vy�arvo dan6 = entityManager.find(Vy�arvo.class, 18);
      Vy�arvo seuraava = vy�koehelper.haeSeuraavaVy�arvo(dan6);
      Assert.assertEquals(Vy�arvo.EI_OOTA, seuraava);
   }

}
