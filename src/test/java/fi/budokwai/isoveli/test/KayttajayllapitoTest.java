package fi.budokwai.isoveli.test;

import static org.junit.Assert.fail;

import javax.faces.component.UIInput;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.icefaces.ace.event.SelectEvent;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import fi.budokwai.isoveli.K�ytt�j�yll�pito;
import fi.budokwai.isoveli.malli.Henkil�viesti;
import fi.budokwai.isoveli.malli.Viesti;
import fi.budokwai.isoveli.malli.Viestilaatikko;

@RunWith(Arquillian.class)
public class KayttajayllapitoTest extends Perustesti
{

   @Inject
   private EntityManager entityManager;

   @Inject
   private K�ytt�j�yll�pito k�ytt�j�yll�pito;
   
   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "nicklas.sql", "viesti.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testViestiLoytyy()
   {
      Assert.assertEquals(1, entityManager.createQuery("select v from Viesti v", Viesti.class).getResultList().size());
      Assert.assertEquals(1, entityManager.createQuery("select v from Viestilaatikko v", Viestilaatikko.class).getResultList().size());
      Assert.assertEquals(1, entityManager.createQuery("select v from Henkil�viesti v", Henkil�viesti.class).getResultList().size());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "nicklas.sql", "viesti.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   @Transactional
   public void testViestiLuettu()
   {
      Henkil�viesti viesti = entityManager.find(Henkil�viesti.class, 1);
      SelectEvent e = new SelectEvent(new UIInput(), new Object[] { viesti });
      k�ytt�j�yll�pito.viestiValittu(e);
      entityManager.clear();
      viesti = entityManager.find(Henkil�viesti.class, 1);
      Assert.assertTrue(viesti.isLuettu());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "nicklas.sql", "viesti.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testViestiArkistoitu()
   {
      fail("not yet");
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "nicklas.sql", "viesti.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testViestiPoistettu()
   {
      fail("not yet");
   }

}
