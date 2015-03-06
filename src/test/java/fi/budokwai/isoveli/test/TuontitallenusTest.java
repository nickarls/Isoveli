package fi.budokwai.isoveli.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.io.ByteStreams;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Perhe;
import fi.budokwai.isoveli.util.Tuonti;
import fi.budokwai.isoveli.util.Tuontitulos;

@RunWith(Arquillian.class)
public class TuontitallenusTest extends Perustesti
{
   @Inject
   private Tuonti tuonti;

   @Inject
   private EntityManager entityManager;

   @Test
   @Transactional
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testTuoJaTallenna() throws IOException
   {
      ByteArrayOutputStream xls = new ByteArrayOutputStream();
      ByteStreams.copy(TuontiTest.class.getResourceAsStream("/jasenrekisteri.xls"), xls);
      Tuontitulos tuontitulos = tuonti.tuoJäsenrekisteri(xls.toByteArray());
      Assert.assertTrue(tuontitulos.isOK());
      Assert.assertEquals(247, tuontitulos.getHarrastajat().size());
      tuontitulos.getHarrastajat().forEach(h -> {
         entityManager.persist(h);
      });
      entityManager.flush();
      entityManager.clear();
      List<Harrastaja> harrastajat = entityManager.createQuery("select h from Harrastaja h", Harrastaja.class)
         .getResultList();
      List<Perhe> perheet = entityManager.createQuery("select p from Perhe p", Perhe.class).getResultList();
      Assert.assertEquals(247, harrastajat.size());
      Assert.assertEquals(126, perheet.size());
   }

}
