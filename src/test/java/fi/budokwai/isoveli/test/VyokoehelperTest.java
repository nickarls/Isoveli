package fi.budokwai.isoveli.test;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import fi.budokwai.isoveli.malli.Vy�arvo;
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
   private EntityManager entityManager;

   @Test
   public void testVyoarvotOlemassa()
   {
      Assert.assertEquals(11, vy�koehelper.getVy�arvot().size());
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
      Vy�arvo dan3 = entityManager.find(Vy�arvo.class, 11);
      Vy�arvo seuraava = vy�koehelper.haeSeuraavaVy�arvo(dan3);
      Assert.assertEquals(Vy�arvo.EI_OOTA, seuraava);
   }

}
