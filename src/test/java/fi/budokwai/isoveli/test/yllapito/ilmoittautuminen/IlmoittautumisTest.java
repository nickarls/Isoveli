package fi.budokwai.isoveli.test.yllapito.ilmoittautuminen;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.junit.Test;
import org.junit.runner.RunWith;

import fi.budokwai.isoveli.admin.IlmoittautumisAdmin;
import fi.budokwai.isoveli.test.Perustesti;

@RunWith(Arquillian.class)
public class IlmoittautumisTest extends Perustesti
{
   @Inject
   private IlmoittautumisAdmin IlmoittautumisAdmin;

   @Inject
   private EntityManager entityManager;

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "nicklas.sql", "treenikaytossa.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testOrvotYhteystiedotSiivotaan()
   {
   }

}
