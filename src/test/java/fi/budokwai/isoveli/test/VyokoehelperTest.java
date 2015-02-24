package fi.budokwai.isoveli.test;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ApplyScriptAfter;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import fi.budokwai.isoveli.malli.Vyöarvo;
import fi.budokwai.isoveli.util.Vyökoehelper;

@RunWith(Arquillian.class)
@ApplyScriptBefore("seed.sql")
@ApplyScriptAfter("cleanup.sql")
public class VyokoehelperTest extends Perustesti
{
   @Inject
   private Vyökoehelper vyökoehelper;

   @Inject
   private EntityManager entityManager;
  

   @Test
   public void testVyoarvotOlemassa()
   {
      Assert.assertEquals(11, vyökoehelper.getVyöarvot().size());
   }

   @Test
   public void testSeuraavaVyaarvoEiVyota()
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
      Vyöarvo dan3 = entityManager.find(Vyöarvo.class, 11);
      Vyöarvo seuraava = vyökoehelper.haeSeuraavaVyöarvo(dan3);
      Assert.assertEquals(Vyöarvo.EI_OOTA, seuraava);
   }

}
