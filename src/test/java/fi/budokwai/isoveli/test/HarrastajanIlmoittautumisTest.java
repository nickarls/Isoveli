package fi.budokwai.isoveli.test;

import static org.junit.Assert.fail;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import fi.budokwai.isoveli.Ilmoittautuminen;
import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Ilmoittautumistulos;
import fi.budokwai.isoveli.malli.Ilmoittautumistulos.Tila;
import fi.budokwai.isoveli.malli.Treeni;
import fi.budokwai.isoveli.malli.Treenisessio;
import fi.budokwai.isoveli.malli.Treenityyppi;
import fi.budokwai.isoveli.malli.Viikonp‰iv‰;
import fi.budokwai.isoveli.util.DateUtil;

@RunWith(Arquillian.class)
public class HarrastajanIlmoittautumisTest extends Perustesti
{
   @Inject
   private Ilmoittautuminen ilmoittautuminen;

   @Inject
   private EntityManager entityManager;

   @Inject
   @Named("treeniharrastaja")
   private Instance<Harrastaja> harrastaja;

   @Inject
   @Named("tulevatTreenit")
   private Instance<List<Treeni>> treenit;

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "perustekniikka.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testKorttinumeroEiLoydy()
   {
      lis‰‰Treeni();
      ilmoittautuminen.setKorttinumero("hellurei");
      Ilmoittautumistulos tulos = ilmoittautuminen.lueKortti();
      Assert.assertEquals(Tila.KƒYTTƒJƒ_EI_L÷YTYNYT, tulos.getTila());
      Assert.assertEquals("Tuntematon korttinumero 'hellurei'", tulos.getViesti());
      Assert.assertNull(tulos.getHarrastaja());
      Assert.assertNull(harrastaja.get());
      Assert.assertEquals(0, treenit.get().size());
      Assert.assertNull(ilmoittautuminen.getKorttinumero());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "perustekniikka.sql", "nicklastauolla.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testTauolla()
   {
      lis‰‰Treeni();
      ilmoittautuminen.setKorttinumero("19750628-1");
      Ilmoittautumistulos tulos = ilmoittautuminen.lueKortti();
      Assert.assertEquals(Tila.TAUOLLA, tulos.getTila());
      Assert.assertEquals("Voisitko tulla infotiskille tarkistamaan treenitaukosi?", tulos.getViesti());
      Assert.assertNotNull(tulos.getHarrastaja());
      Assert.assertNotNull(harrastaja.get());
      Assert.assertEquals(0, treenit.get().size());
      Assert.assertNotNull(ilmoittautuminen.getKorttinumero());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "perustekniikka.sql", "nicklas.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testSopimusOngelma()
   {
      lis‰‰Treeni();
      ilmoittautuminen.setKorttinumero("19750628-1");
      Ilmoittautumistulos tulos = ilmoittautuminen.lueKortti();
      Assert.assertEquals(Tila.SOPIMUSVIRHE, tulos.getTila());
      Assert.assertEquals("Voisitko tulla infotiskille tarkistamaan sopimukset/maksut?", tulos.getViesti());
      Assert.assertNotNull(tulos.getHarrastaja());
      Assert.assertNotNull(harrastaja.get());
      Assert.assertEquals(0, treenit.get().size());
      Assert.assertNotNull(ilmoittautuminen.getKorttinumero());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testInfotiskille()
   {
      fail("not yet");
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "perustekniikka.sql", "nicklasok.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testTiedotOKJaTreenejaLoytyy()
   {
      lis‰‰Treeni();
      ilmoittautuminen.setKorttinumero("19750628-1");
      Ilmoittautumistulos tulos = ilmoittautuminen.lueKortti();
      Assert.assertEquals(Tila.OK, tulos.getTila());
      Assert.assertNotNull(tulos.getHarrastaja());
      Assert.assertNotNull(harrastaja.get());
      Assert.assertEquals(1, treenit.get().size());
      Assert.assertNotNull(ilmoittautuminen.getKorttinumero());
   }

   private void lis‰‰Treeni()
   {
      lis‰‰Treeni(null, null, null, null);
   }

   private void lis‰‰Treeni(Integer alaIk‰, Integer yl‰Ik‰, String alaVyˆ, String yl‰Vyˆ)
   {
      Treeni treeni = new Treeni();
      treeni.setIk‰Alaraja(alaIk‰);
      treeni.setIk‰Yl‰raja(yl‰Ik‰);
      if (alaVyˆ != null)
      {
         treeni.setVyˆAlaraja(teeVyˆarvo(alaVyˆ));
      }
      if (yl‰Vyˆ != null)
      {
         treeni.setVyˆYl‰raja(teeVyˆarvo(yl‰Vyˆ));
      }
      Treenityyppi tyyppi = entityManager.find(Treenityyppi.class, 1);
      LocalDateTime nyt = LocalDateTime.now();
      treeni.setAlkaa(DateUtil.LocalDateTime2Date(nyt.plus(1, ChronoUnit.HOURS)));
      treeni.setP‰‰ttyy(DateUtil.LocalDateTime2Date(nyt.plus(2, ChronoUnit.HOURS)));
      treeni.setP‰iv‰(Viikonp‰iv‰.values()[nyt.getDayOfWeek().ordinal() + 1]);
      treeni.setTyyppi(tyyppi);
      treeni.setNimi("treeni");
      Harrastaja nicklas = entityManager.find(Harrastaja.class, 1);
      treeni.getVet‰j‰t().add(nicklas);
      entityManager.persist(treeni);
      entityManager.flush();
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "nicklasok.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testTreenejaEiLoydy()
   {
      ilmoittautuminen.setKorttinumero("19750628-1");
      Ilmoittautumistulos tulos = ilmoittautuminen.lueKortti();
      Assert.assertEquals(Tila.OK, tulos.getTila());
      Assert.assertNotNull(tulos.getHarrastaja());
      Assert.assertNotNull(harrastaja.get());
      Assert.assertEquals(0, treenit.get().size());
      Assert.assertNotNull(ilmoittautuminen.getKorttinumero());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "nicklasok.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testPeruuta()
   {
      ilmoittautuminen.setKorttinumero("19750628-1");
      ilmoittautuminen.lueKortti();
      ilmoittautuminen.peruuta();
      Assert.assertNull(ilmoittautuminen.getTreeniharrastaja());
      Assert.assertEquals(0, treenit.get().size());
      Assert.assertNull(ilmoittautuminen.getKorttinumero());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "perustekniikka.sql", "nicklasok.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testEiSamaanTreeniin()
   {
      lis‰‰Treeni();
      ilmoittautuminen.peruuta();
      ilmoittautuminen.setKorttinumero("19750628-1");
      ilmoittautuminen.lueKortti();
      Assert.assertEquals(1, treenit.get().size());
      Treeni treeni = treenit.get().iterator().next();
      ilmoittautuminen.setTreeni(treeni);
      ilmoittautuminen.tallenna();
      entityManager.clear();
      Assert.assertEquals(0, treenit.get().size());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "perustekniikka.sql", "nicklasok.sql", "emilok.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testIlmoittauduTreeneihinVanhaSessio()
   {
      lis‰‰Treeni();
      ilmoittautuminen.setKorttinumero("19750628-1");
      ilmoittautuminen.lueKortti();
      Treeni treeni = treenit.get().iterator().next();
      ilmoittautuminen.setTreeni(treeni);
      ilmoittautuminen.tallenna();
      ilmoittautuminen.setKorttinumero("20051030-1");
      ilmoittautuminen.lueKortti();
      ilmoittautuminen.setTreeni(treeni);
      ilmoittautuminen.tallenna();
      entityManager.clear();
      Harrastaja nicklas = entityManager.find(Harrastaja.class, 1);
      Harrastaja emil = entityManager.find(Harrastaja.class, 2);
      List<Treenisessio> sessiot = entityManager.createQuery("select t from Treenisessio t", Treenisessio.class)
         .getResultList();
      Assert.assertEquals(1, sessiot.size());
      Assert.assertEquals(1, nicklas.getTreenik‰ynnit().size());
      Assert.assertEquals(1, nicklas.getTreenik‰ynnit().iterator().next().getTreenisessio().getVet‰j‰t().size());
      Assert.assertEquals(1, emil.getTreenik‰ynnit().size());
      Assert.assertEquals(1, emil.getTreenik‰ynnit().iterator().next().getTreenisessio().getVet‰j‰t().size());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "perustekniikka.sql", "nicklasok.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testIlmoittauduTreeneihinUusiSessio()
   {
      lis‰‰Treeni();
      ilmoittautuminen.setKorttinumero("19750628-1");
      Ilmoittautumistulos tulos = ilmoittautuminen.lueKortti();
      Assert.assertEquals(Tila.OK, tulos.getTila());
      Assert.assertNotNull(tulos.getHarrastaja());
      Assert.assertNotNull(harrastaja.get());
      Assert.assertEquals(1, treenit.get().size());
      Assert.assertNotNull(ilmoittautuminen.getKorttinumero());
      Treeni treeni = treenit.get().iterator().next();
      ilmoittautuminen.setTreeni(treeni);
      ilmoittautuminen.tallenna();
      entityManager.clear();
      Harrastaja nicklas = entityManager.find(Harrastaja.class, 1);
      List<Treenisessio> sessiot = entityManager.createQuery("select t from Treenisessio t", Treenisessio.class)
         .getResultList();
      Assert.assertEquals(1, sessiot.size());
      Assert.assertEquals(1, nicklas.getTreenik‰ynnit().size());
      Assert.assertEquals(1, nicklas.getTreenik‰ynnit().iterator().next().getTreenisessio().getVet‰j‰t().size());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "perustekniikka.sql", "nicklasok.sql", "nicklasdan.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testVyoalarajaOK()
   {
      lis‰‰Treeni(null, null, "1.kup", null);
      ilmoittautuminen.setKorttinumero("19750628-1");
      ilmoittautuminen.lueKortti();
      Assert.assertEquals(1, treenit.get().size());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "perustekniikka.sql", "nicklasok.sql", "nicklasdan.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testVyolarajaEiOK()
   {
      lis‰‰Treeni(null, null, "2.dan", null);
      ilmoittautuminen.setKorttinumero("19750628-1");
      ilmoittautuminen.lueKortti();
      Assert.assertEquals(0, treenit.get().size());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "perustekniikka.sql", "nicklasok.sql", "nicklasdan.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testVyoylarajaOK()
   {
      lis‰‰Treeni(null, null, null, "2.dan");
      ilmoittautuminen.setKorttinumero("19750628-1");
      ilmoittautuminen.lueKortti();
      Assert.assertEquals(1, treenit.get().size());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "perustekniikka.sql", "nicklasok.sql", "nicklasdan.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testVyoalarajaEiOK()
   {
      lis‰‰Treeni(null, null, "3.dan", null);
      ilmoittautuminen.setKorttinumero("19750628-1");
      ilmoittautuminen.lueKortti();
      Assert.assertEquals(0, treenit.get().size());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "perustekniikka.sql", "nicklasok.sql", "nicklasdan.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testVyorajatOK()
   {
      lis‰‰Treeni(null, null, "1.kup", "2.dan");
      ilmoittautuminen.setKorttinumero("19750628-1");
      ilmoittautuminen.lueKortti();
      Assert.assertEquals(1, treenit.get().size());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "perustekniikka.sql", "nicklasok.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testVyorajatEiOK()
   {
      lis‰‰Treeni(null, null, "1.kup", "2.dan");
      ilmoittautuminen.setKorttinumero("19750628-1");
      ilmoittautuminen.lueKortti();
      Assert.assertEquals(0, treenit.get().size());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "perustekniikka.sql", "nicklasok.sql", "nicklasdan.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testVyorajatEiOKAli()
   {
      lis‰‰Treeni(null, null, "2.dan", "3.dan");
      ilmoittautuminen.setKorttinumero("19750628-1");
      ilmoittautuminen.lueKortti();
      Assert.assertEquals(0, treenit.get().size());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "perustekniikka.sql", "nicklasok.sql", "nicklasdan.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testVyorajatEiOKYli()
   {
      lis‰‰Treeni(null, null, "8.kup", "7.kup");
      ilmoittautuminen.setKorttinumero("19750628-1");
      ilmoittautuminen.lueKortti();
      Assert.assertEquals(0, treenit.get().size());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "perustekniikka.sql", "nicklasok.sql", "nicklasdan.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testIkaalarajaOK()
   {
      lis‰‰Treeni(7, null, null, null);
      ilmoittautuminen.setKorttinumero("19750628-1");
      ilmoittautuminen.lueKortti();
      Assert.assertEquals(1, treenit.get().size());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "perustekniikka.sql", "nicklasok.sql", "nicklasdan.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testIkalarajaEiOK()
   {
      lis‰‰Treeni(50, null, null, null);
      ilmoittautuminen.setKorttinumero("19750628-1");
      ilmoittautuminen.lueKortti();
      Assert.assertEquals(0, treenit.get().size());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "perustekniikka.sql", "nicklasok.sql", "nicklasdan.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testIkaylarajaOK()
   {
      lis‰‰Treeni(null, 50, null, null);
      ilmoittautuminen.setKorttinumero("19750628-1");
      ilmoittautuminen.lueKortti();
      Assert.assertEquals(1, treenit.get().size());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "perustekniikka.sql", "nicklasok.sql", "nicklasdan.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testIkaalarajaEiOK()
   {
      lis‰‰Treeni(null, 20, null, null);
      ilmoittautuminen.setKorttinumero("19750628-1");
      ilmoittautuminen.lueKortti();
      Assert.assertEquals(0, treenit.get().size());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "perustekniikka.sql", "nicklasok.sql", "nicklasdan.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testIkarajatOK()
   {
      lis‰‰Treeni(30, 50, null, null);
      ilmoittautuminen.setKorttinumero("19750628-1");
      ilmoittautuminen.lueKortti();
      Assert.assertEquals(1, treenit.get().size());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "perustekniikka.sql", "nicklasok.sql", "nicklasdan.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testIkarajatEiOKAli()
   {
      lis‰‰Treeni(50, 60, null, null);
      ilmoittautuminen.setKorttinumero("19750628-1");
      ilmoittautuminen.lueKortti();
      Assert.assertEquals(0, treenit.get().size());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "perustekniikka.sql", "nicklasok.sql", "nicklasdan.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testIkarajatEiOKYli()
   {
      lis‰‰Treeni(20, 30, null, null);
      ilmoittautuminen.setKorttinumero("19750628-1");
      ilmoittautuminen.lueKortti();
      Assert.assertEquals(0, treenit.get().size());
   }

}
