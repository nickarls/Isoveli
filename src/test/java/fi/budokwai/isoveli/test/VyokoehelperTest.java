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
   private Instance<List<Vyöarvo>> vyöarvot;

   @Inject
   private EntityManager entityManager;

   @Test
   public void testVyoarvotOlemassa()
   {
      Assert.assertEquals(18, vyökoehelper.getVyöarvot().size());
   }

   public void testEiPoomArvojaYli16Vuotiaalle()
   {
      Harrastaja harrastaja = new Harrastaja();
      harrastaja.setSyntynyt(DateUtil.silloinD("01.01.1970"));
      harrastajaAdmin.setHarrastaja(harrastaja);
      vyöarvot.get().forEach(v -> {
         Assert.assertFalse(v.isPoom());
      });
   }

   public void testEiDanArvojaAlle16Vuotiaalle()
   {
      Harrastaja harrastaja = new Harrastaja();
      harrastaja.setSyntynyt(DateUtil.silloinD("01.01.2010"));
      harrastajaAdmin.setHarrastaja(harrastaja);
      vyöarvot.get().forEach(v -> {
         Assert.assertFalse(v.isDan());
      });
   }

   public void testVyoarvojenSuodatus()
   {
      Harrastaja harrastaja = new Harrastaja();
      harrastaja.setSyntynyt(DateUtil.silloinD("01.01.1970"));
      harrastajaAdmin.setHarrastaja(harrastaja);
      Vyöarvo vihreä = entityManager.find(Vyöarvo.class, 3);
      harrastaja.lisääVyökoe(new Vyökoe(vihreä));
      Assert.assertFalse(vyöarvot.get().contains(vihreä));
   }

   @Test
   public void testSeuraavaVyoarvoEiVyota()
   {
      Harrastaja harrastaja = teeHarrastaja("N K", "1.1.1970");
      Vyöarvo seuraava = vyökoehelper.haeSeuraavaVyöarvo(harrastaja);
      Assert.assertEquals(seuraava.getNimi(), "8.kup");
   }

   @Test
   public void testSeuraavaVyoarvoVihrea()
   {
      Harrastaja harrastaja = teeHarrastaja("N K", "1.1.2000");
      teeVyökoe(harrastaja, "1.1.1980", "4.kup", 5);
      Vyöarvo seuraava = vyökoehelper.haeSeuraavaVyöarvo(harrastaja);
      Assert.assertEquals(seuraava.getNimi(), "3.kup");
   }

   @Test
   public void testSeuraavaVyoarvo3dan()
   {
      Harrastaja harrastaja = teeHarrastaja("N K", "1.1.1970");
      teeVyökoe(harrastaja, "1.1.1980", "2.dan", 12);
      harrastajaAdmin.setHarrastaja(harrastaja);
      Vyöarvo seuraava = vyökoehelper.haeSeuraavaVyöarvo(harrastaja);
      Assert.assertEquals(seuraava.getNimi(), "3.dan");
   }
   
   @Test
   public void testSeuraavaVyoarvo3poom()
   {
      Harrastaja harrastaja = teeHarrastaja("N K", "1.1.2010");
      teeVyökoe(harrastaja, "1.1.2011", "2.poom", 12);
      harrastajaAdmin.setHarrastaja(harrastaja);
      Vyöarvo seuraava = vyökoehelper.haeSeuraavaVyöarvo(harrastaja);
      Assert.assertEquals(seuraava.getNimi(), "3.poom");
   }
   

}
