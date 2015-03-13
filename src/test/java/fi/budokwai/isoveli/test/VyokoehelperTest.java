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
import fi.budokwai.isoveli.malli.Vyöarvo;
import fi.budokwai.isoveli.malli.Vyökoe;
import fi.budokwai.isoveli.util.DateUtil;
import fi.budokwai.isoveli.util.Harrastajan;
import fi.budokwai.isoveli.util.Vyökoehelper;

@RunWith(Arquillian.class)
@ApplyScriptBefore(
{ "cleanup.sql", "seed.sql" })
@Cleanup(phase = TestExecutionPhase.NONE)
public class VyokoehelperTest extends Perustesti
{
   @Inject
   private Vyökoehelper vyökoehelper;

   @Inject
   private HarrastajaAdmin harrastajaAdmin;

   @Inject
   @Harrastajan
   private Instance<List<Vyöarvo>> harrastajanVyöarvot;

   @Inject
   private EntityManager entityManager;

   @Test
   public void testVyoarvotOlemassa()
   {
      Assert.assertEquals(18, vyökoehelper.getVyöarvot().size());
   }

   @Test
   public void testEiPoomArvojaYli16Vuotiaalle()
   {
      Harrastaja harrastaja = new Harrastaja();
      harrastaja.setSyntynyt(DateUtil.silloinD("01.01.1970"));
      harrastajaAdmin.setHarrastaja(harrastaja);
      harrastajanVyöarvot.get().forEach(v -> {
         Assert.assertFalse(v.isPoom());
      });
   }

   @Test
   public void testEiDanArvojaAlle16Vuotiaalle()
   {
      Harrastaja harrastaja = new Harrastaja();
      harrastaja.setSyntynyt(DateUtil.silloinD("01.01.2010"));
      harrastajaAdmin.setHarrastaja(harrastaja);
      harrastajanVyöarvot.get().forEach(v -> {
         Assert.assertFalse(v.isDan());
      });
   }

   @Test
   public void testVyoarvojenSuodatus()
   {
      Harrastaja harrastaja = new Harrastaja();
      harrastaja.setSyntynyt(DateUtil.silloinD("01.01.1970"));
      harrastajaAdmin.setHarrastaja(harrastaja);
      Vyöarvo vihreä = entityManager.find(Vyöarvo.class, 3);
      harrastaja.lisääVyökoe(new Vyökoe(vihreä));
      Assert.assertFalse(harrastajanVyöarvot.get().contains(vihreä));
   }

   @Test
   public void testSeuraavaVyoarvoEiVyota()
   {
      Vyöarvo seuraava = vyökoehelper.haeSeuraavaVyöarvo(Vyöarvo.EI_OOTA);
      Assert.assertEquals(seuraava.getNimi(), "8.kup");
   }

   @Test
   public void testSeuraavaVyoarvoVihrea()
   {
      Vyöarvo vihreä = entityManager.find(Vyöarvo.class, 3);
      Vyöarvo seuraava = vyökoehelper.haeSeuraavaVyöarvo(vihreä);
      Assert.assertEquals(seuraava.getNimi(), "5.kup");
   }

   @Test
   public void testSeuraavaVyoarvo3dan()
   {
      Vyöarvo dan6 = entityManager.find(Vyöarvo.class, 18);
      Vyöarvo seuraava = vyökoehelper.haeSeuraavaVyöarvo(dan6);
      Assert.assertEquals(Vyöarvo.EI_OOTA, seuraava);
   }

}
