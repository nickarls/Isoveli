package fi.budokwai.isoveli.test.yllapito.harrastaja;

import static org.junit.Assert.fail;

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

import fi.budokwai.isoveli.IsoveliPoikkeus;
import fi.budokwai.isoveli.admin.HarrastajaAdmin;
import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Henkilö;
import fi.budokwai.isoveli.malli.Osoite;
import fi.budokwai.isoveli.malli.Perhe;
import fi.budokwai.isoveli.malli.Rooli;
import fi.budokwai.isoveli.malli.Sopimus;
import fi.budokwai.isoveli.malli.Sopimustyyppi;
import fi.budokwai.isoveli.malli.Sukupuoli;
import fi.budokwai.isoveli.malli.Vyöarvo;
import fi.budokwai.isoveli.malli.Yhteystieto;
import fi.budokwai.isoveli.test.Perustesti;
import fi.budokwai.isoveli.util.DateUtil;
import fi.budokwai.isoveli.util.Vyökoehelper;

@RunWith(Arquillian.class)
public class HarrastajaTest extends Perustesti
{
   @Inject
   private HarrastajaAdmin harrastajaAdmin;

   @Inject
   private EntityManager entityManager;

   @Inject
   @Named("harrastajat")
   private Instance<List<Harrastaja>> harrastajat;

   @Inject
   @Named("perheet")
   private Instance<List<Perhe>> perheet;

   @Inject
   private Vyökoehelper vyökoehelper;

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "nicklas.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testTreenikertojaJaljellaEiTreeneja()
   {
      Harrastaja nicklas = entityManager.find(Harrastaja.class, 1);
      Assert.assertEquals(15, vyökoehelper.getJäljelläVyökokeeseen(nicklas).getTreenikertoja());
   }

   @Test
   @ApplyScriptBefore(
      { "cleanup.sql", "seed.sql", "nicklas.sql", "treenisessiokaytossa.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testTreenikertojaJaljellaTreeneja()
   {
      Harrastaja nicklas = entityManager.find(Harrastaja.class, 1);
      Assert.assertEquals(14, vyökoehelper.getJäljelläVyökokeeseen(nicklas).getTreenikertoja());
   }

   @Test
   @ApplyScriptBefore(
      { "cleanup.sql", "seed.sql", "vanhanicklas.sql", "treenisessiokaytossa.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testTreenikertojaJaljellaSiirtokertoja()
   {
      Harrastaja nicklas = entityManager.find(Harrastaja.class, 1);
      Assert.assertEquals(10, vyökoehelper.getJäljelläVyökokeeseen(nicklas).getTreenikertoja());
   }
   
   @Test
   @ApplyScriptBefore(
   { "cleanup.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testLisaaTaysiikainenPerheetonHarrastaja()
   {
      Harrastaja harrastaja = new Harrastaja();
      harrastaja.setEtunimi("Nicklas");
      harrastaja.setSukunimi("Karlsson");
      harrastaja.setSukupuoli(Sukupuoli.M);
      harrastaja.setSyntynyt(DateUtil.silloinD("28.06.1975"));

      harrastaja.getYhteystiedot().setPuhelinnumero("0405062266");
      harrastaja.getYhteystiedot().setSähköposti("nickarls@gmail.com");

      harrastaja.getOsoite().setOsoite("Vaakunatie 10 as7");
      harrastaja.getOsoite().setPostinumero("20780");
      harrastaja.getOsoite().setKaupunki("Kaarina");

      harrastajaAdmin.setHarrastaja(harrastaja);
      Assert.assertEquals(0, harrastajat.get().size());
      harrastajaAdmin.tallennaHarrastaja();
      entityManager.clear();
      Assert.assertEquals(1, harrastajat.get().size());

      harrastaja = entityManager.createQuery("select h from Harrastaja h", Harrastaja.class).getSingleResult();
      Assert.assertEquals("Vaakunatie 10 as7", harrastaja.getOsoite().getOsoite());
      Assert.assertEquals("0405062266", harrastaja.getYhteystiedot().getPuhelinnumero());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testLisaaTaysiikainenYhdenHengenPerheenHarrastaja()
   {
      harrastajaAdmin.lisääHarrastaja();
      Harrastaja harrastaja = harrastajaAdmin.getHarrastaja();

      harrastaja.setEtunimi("Nicklas");
      harrastaja.setSukunimi("Karlsson");
      harrastaja.setSukupuoli(Sukupuoli.M);
      harrastaja.setSyntynyt(DateUtil.silloinD("28.06.1975"));

      harrastajaAdmin.lisääPerhe();
      Assert.assertEquals(1, perheet.get().size());
      harrastaja.getOsoite().setOsoite("Vaakunatie 10 as7");
      harrastaja.getOsoite().setPostinumero("20780");
      harrastaja.getOsoite().setKaupunki("Kaarina");

      harrastajaAdmin.setHarrastaja(harrastaja);
      harrastajaAdmin.tallennaHarrastaja();
      entityManager.clear();
      Assert.assertEquals(1, harrastajat.get().size());

      harrastaja = entityManager.createQuery("select h from Harrastaja h", Harrastaja.class).getSingleResult();
      Assert.assertEquals("Vaakunatie 10 as7", harrastaja.getOsoite().getOsoite());
      Perhe perhe = entityManager.createQuery("select p from Perhe p", Perhe.class).getSingleResult();
      Assert.assertEquals("Vaakunatie 10 as7", perhe.getOsoite().getOsoite());
      Assert.assertEquals(1, harrastaja.getPerhe().getPerheenjäsenet().size());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testLisaaAlaikainenHarrastaja()
   {
      harrastajaAdmin.lisääHarrastaja();
      Harrastaja harrastaja = harrastajaAdmin.getHarrastaja();

      harrastaja.setEtunimi("Emil");
      harrastaja.setSukunimi("Karlsson");
      harrastaja.setSukupuoli(Sukupuoli.M);
      harrastaja.setSyntynyt(DateUtil.silloinD("30.10.2005"));

      Assert.assertEquals(0, perheet.get().size());
      harrastajaAdmin.lisääPerhe();
      Assert.assertEquals(1, perheet.get().size());

      Assert.assertEquals(0, harrastaja.getPerhe().getHuoltajat().size());
      harrastajaAdmin.lisääHuoltaja();
      Assert.assertEquals(1, harrastaja.getPerhe().getHuoltajat().size());
      Henkilö huoltaja = harrastaja.getHuoltaja();
      huoltaja.setEtunimi("Nicklas");

      harrastaja.getOsoite().setOsoite("Vaakunatie 10 as7");
      harrastaja.getOsoite().setPostinumero("20780");
      harrastaja.getOsoite().setKaupunki("Kaarina");

      harrastajaAdmin.setHarrastaja(harrastaja);
      harrastajaAdmin.tallennaHarrastaja();
      entityManager.clear();
      Assert.assertEquals(1, harrastajat.get().size());

      harrastaja = entityManager.createQuery("select h from Harrastaja h", Harrastaja.class).getSingleResult();
      Assert.assertEquals("Vaakunatie 10 as7", harrastaja.getOsoite().getOsoite());
      Perhe perhe = entityManager.createQuery("select p from Perhe p", Perhe.class).getSingleResult();
      Assert.assertEquals("Vaakunatie 10 as7", perhe.getOsoite().getOsoite());
      Assert.assertEquals(2, harrastaja.getPerhe().getPerheenjäsenet().size());
      Assert.assertEquals("Nicklas", harrastaja.getHuoltaja().getEtunimi());
      Assert.assertEquals(1, harrastaja.getPerhe().getHuoltajat().size());
      Assert.assertEquals("Nicklas", harrastaja.getPerhe().getHuoltajat().iterator().next().getEtunimi());
      huoltaja = harrastaja.getPerhe().getHuoltajat().iterator().next();
      Assert.assertEquals(1, huoltaja.getPerhe().getHuollettavat(huoltaja).size());
      harrastaja = (Harrastaja) huoltaja.getPerhe().getHuollettavat(huoltaja).iterator().next();
      Assert.assertEquals("Emil", harrastaja.getEtunimi());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "nicklas.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testOrvotOsoitteetSiivotaan()
   {
      Harrastaja harrastaja = entityManager.find(Harrastaja.class, 1);
      Assert.assertNotNull(harrastaja.getOsoite());
      harrastaja.setOsoite(null);
      harrastajaAdmin.setHarrastaja(harrastaja);
      harrastajaAdmin.tallennaHarrastaja();
      entityManager.clear();
      harrastaja = entityManager.find(Harrastaja.class, 1);
      Assert.assertTrue(harrastaja.getOsoite().isKäyttämätön());
      Osoite osoite = entityManager.find(Osoite.class, 1);
      Assert.assertNull(osoite);
      entityManager.clear();
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "nicklas.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testOrvotYhteystiedotSiivotaan()
   {
      Harrastaja harrastaja = entityManager.find(Harrastaja.class, 1);
      Assert.assertNotNull(harrastaja.getYhteystiedot());
      harrastaja.setYhteystiedot(null);
      harrastajaAdmin.setHarrastaja(harrastaja);
      harrastajaAdmin.tallennaHarrastaja();
      entityManager.clear();
      harrastaja = entityManager.find(Harrastaja.class, 1);
      Assert.assertTrue(harrastaja.getYhteystiedot().isKäyttämätön());
      Yhteystieto yhteystiedot = entityManager.find(Yhteystieto.class, 1);
      Assert.assertNull(yhteystiedot);
      entityManager.clear();
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "karlsson.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testHuoltajanPuhelinMuutosPaivittaaICEn()
   {
      Harrastaja emil = entityManager.find(Harrastaja.class, 2);
      harrastajaAdmin.setHarrastaja(emil);
      emil.getHuoltaja().getYhteystiedot().setPuhelinnumero("666");
      harrastajaAdmin.huoltajanPuhelinMuuttui(null);
      harrastajaAdmin.tallennaHarrastaja();
      entityManager.clear();
      emil = entityManager.find(Harrastaja.class, 2);
      Assert.assertEquals("666", emil.getIce());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "huoltajaperhe.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testHuoltajaMuuttuu()
   {
      Harrastaja emil = entityManager.find(Harrastaja.class, 3);
      Henkilö heidi = entityManager.find(Henkilö.class, 2);
      harrastajaAdmin.setHarrastaja(emil);
      emil.setHuoltaja(heidi);
      harrastajaAdmin.huoltajaMuuttui(null);
      harrastajaAdmin.tallennaHarrastaja();
      entityManager.clear();
      emil = entityManager.find(Harrastaja.class, 3);
      Assert.assertEquals("0407218809", emil.getIce());
      Assert.assertEquals("Heidi", emil.getHuoltaja().getEtunimi());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "huoltajaperhe.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testHuoltajanNimiMuuttuu()
   {
      Harrastaja emil = entityManager.find(Harrastaja.class, 3);
      harrastajaAdmin.setHarrastaja(emil);
      emil.getHuoltaja().setEtunimi("Nix");
      harrastajaAdmin.tallennaHarrastaja();
      entityManager.clear();
      emil = entityManager.find(Harrastaja.class, 3);
      Assert.assertEquals("Nix", emil.getHuoltaja().getEtunimi());
   }
   
   
   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "huoltajaperhe.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testHuoltajanYhteystiedotMuuttuu()
   {
      Harrastaja emil = entityManager.find(Harrastaja.class, 3);
      harrastajaAdmin.setHarrastaja(emil);
      emil.getHuoltaja().getYhteystiedot().setPuhelinnumero("123");
      harrastajaAdmin.tallennaHarrastaja();
      entityManager.clear();
      emil = entityManager.find(Harrastaja.class, 3);
      Assert.assertEquals("123", emil.getHuoltaja().getYhteystiedot().getPuhelinnumero());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "huoltajaperhe.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testPerheenOsoiteMuuttuu()
   {
      Harrastaja emil = entityManager.find(Harrastaja.class, 3);
      harrastajaAdmin.setHarrastaja(emil);
      emil.getPerhe().getOsoite().setKaupunki("foo");
      harrastajaAdmin.tallennaHarrastaja();
      entityManager.clear();
      emil = entityManager.find(Harrastaja.class, 3);
      Assert.assertEquals("foo", emil.getPerhe().getOsoite().getKaupunki());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "karlsson.sql", "rosqvist.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testPerheMuuttuu()
   {
      Assert.assertEquals(2, perheet.get().size());
      perheet.get().stream().forEach(p -> Assert.assertEquals(2, p.getPerheenjäsenet().size()));
      perheet.get().stream().forEach(p -> Assert.assertEquals(1, p.getHuoltajat().size()));
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testOrpoPerhePoistuu()
   {
      fail("todo");
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testOrpoOsoitePoistuu()
   {
      fail("todo");
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "nicklas.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testPoistaHarrastajaEiKaytossa()
   {
      Harrastaja nicklas = entityManager.find(Harrastaja.class, 1);
      harrastajaAdmin.setHarrastaja(nicklas);
      harrastajaAdmin.poistaHarrastaja();
      entityManager.clear();
      nicklas = entityManager.find(Harrastaja.class, 1);
      Assert.assertNull(nicklas);
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "nicklas.sql", "yllapitajarooli.sql", "nicklasyllapitaja.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testPoistaHarrastajaRoolilla()
   {
      Harrastaja nicklas = entityManager.find(Harrastaja.class, 1);
      harrastajaAdmin.setHarrastaja(nicklas);
      harrastajaAdmin.poistaHarrastaja();
      entityManager.clear();
      nicklas = entityManager.find(Harrastaja.class, 1);
      Assert.assertNull(nicklas);
      int rooleja = entityManager.createNativeQuery("select count(1) from henkilorooli", Integer.class)
         .getFirstResult();
      Assert.assertEquals(0, rooleja);
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "huoltajaharrastaja.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testPoistaHuoltajaHarrastaja()
   {
      Harrastaja emil = entityManager.find(Harrastaja.class, 2);
      Assert.assertNotNull(emil.getHuoltaja());
      Harrastaja nicklas = entityManager.find(Harrastaja.class, 1);
      harrastajaAdmin.setHarrastaja(nicklas);
      exception.expect(IsoveliPoikkeus.class);
      exception.expectMessage("Harrastaja on huoltaja ja häntä ei voi poistaa (1kpl: Emil...)");
      harrastajaAdmin.poistaHarrastaja();
      entityManager.clear();
      emil = entityManager.find(Harrastaja.class, 2);
      nicklas = entityManager.find(Harrastaja.class, 1);
      Assert.assertNotNull(nicklas);
      Assert.assertNotNull(emil.getHuoltaja());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "nicklas.sql", "nicklassopimus.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testPoistaHarrastajaKaytossaSopimus()
   {
      Harrastaja nicklas = entityManager.find(Harrastaja.class, 1);
      harrastajaAdmin.setHarrastaja(nicklas);
      exception.expect(IsoveliPoikkeus.class);
      exception.expectMessage("Harrastajalla on sopimuksia ja häntä ei voi poistaa (1kpl: Harjoittelu (18+v)...)");
      harrastajaAdmin.poistaHarrastaja();
      entityManager.clear();
      nicklas = entityManager.find(Harrastaja.class, 1);
      Assert.assertNotNull(nicklas);
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "nicklas.sql", "nicklas7kup.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testPoistaHarrastajaKaytossaVyokoe()
   {
      Harrastaja nicklas = entityManager.find(Harrastaja.class, 1);
      harrastajaAdmin.setHarrastaja(nicklas);
      exception.expect(IsoveliPoikkeus.class);
      exception.expectMessage("Harrastajalla on vyökokeita ja häntä ei voi poistaa (1kpl: 7.kup...)");
      harrastajaAdmin.poistaHarrastaja();
      entityManager.clear();
      nicklas = entityManager.find(Harrastaja.class, 1);
      Assert.assertNotNull(nicklas);
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "nicklas.sql", "treenikaytossa.sql", "treenikaynti.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testPoistaHarrastajaKaytossaTreenikaynti()
   {
      Harrastaja nicklas = entityManager.find(Harrastaja.class, 1);
      harrastajaAdmin.setHarrastaja(nicklas);
      exception.expect(IsoveliPoikkeus.class);
      exception.expectMessage("Harrastajalla on treenikäyntejä ja häntä ei voi poistaa (1kpl: 12.12.2012 12:12...)");
      harrastajaAdmin.poistaHarrastaja();
      entityManager.clear();
      nicklas = entityManager.find(Harrastaja.class, 1);
      Assert.assertNotNull(nicklas);
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "karlsson.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testArkistoiHarrastaja()
   {
      Assert.assertEquals(1, harrastajat.get().size());
      Harrastaja emil = entityManager.find(Harrastaja.class, 2);
      emil.setArkistoitu(true);
      harrastajaAdmin.setHarrastaja(emil);
      harrastajaAdmin.tallennaHarrastaja();
      entityManager.clear();
      Assert.assertEquals(0, harrastajat.get().size());
      List<Harrastaja> arq = entityManager.createNamedQuery("harrastajatArq", Harrastaja.class).getResultList();
      Assert.assertEquals(1, arq.size());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "nicklas.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testLisaaSopimus()
   {
      Harrastaja harrastaja = entityManager.find(Harrastaja.class, 1);
      List<Sopimustyyppi> sopimustyypit = entityManager.createNamedQuery("sopimustyypit", Sopimustyyppi.class)
         .getResultList();
      Sopimustyyppi jäsenmaksu = sopimustyypit.stream().filter(s -> s.isJäsenmaksutyyppi()).findFirst().get();
      harrastajaAdmin.setSopimus(new Sopimus(jäsenmaksu));
      harrastajaAdmin.setHarrastaja(harrastaja);
      harrastajaAdmin.tallennaSopimus();
      entityManager.clear();
      harrastaja = entityManager.find(Harrastaja.class, 1);
      Assert.assertEquals(1, harrastaja.getSopimukset().size());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "nicklas.sql", "nicklassopimus.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testMuokkaaSopimus()
   {
      Harrastaja harrastaja = entityManager.find(Harrastaja.class, 1);
      Sopimus sopimus = harrastaja.getSopimukset().iterator().next();
      sopimus.setUmpeutuu(DateUtil.silloinD("31.12.2105"));
      harrastajaAdmin.setSopimus(sopimus);
      harrastajaAdmin.setHarrastaja(harrastaja);
      harrastajaAdmin.tallennaSopimus();
      entityManager.clear();
      harrastaja = entityManager.find(Harrastaja.class, 1);
      Assert.assertNotNull(harrastaja.getSopimukset().iterator().next().getUmpeutuu());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "nicklas.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testUusiOletusSopimusOnJasenmaksu()
   {
      Harrastaja harrastaja = entityManager.find(Harrastaja.class, 1);
      harrastajaAdmin.setHarrastaja(harrastaja);
      harrastajaAdmin.lisääSopimus();
      harrastajaAdmin.tallennaSopimus();
      entityManager.clear();
      harrastaja = entityManager.find(Harrastaja.class, 1);
      Assert.assertEquals(1, harrastaja.getSopimukset().size());
      Assert.assertTrue(harrastaja.getSopimukset().iterator().next().getTyyppi().isJäsenmaksutyyppi());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "nicklas.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testSeuraavaSopimusOnHarrastusmaksu()
   {
      Harrastaja harrastaja = entityManager.find(Harrastaja.class, 1);
      harrastajaAdmin.setHarrastaja(harrastaja);
      harrastajaAdmin.lisääSopimus();
      harrastajaAdmin.tallennaSopimus();
      harrastajaAdmin.lisääSopimus();
      harrastajaAdmin.tallennaSopimus();
      entityManager.clear();
      harrastaja = entityManager.find(Harrastaja.class, 1);
      Assert.assertEquals(2, harrastaja.getSopimukset().size());
      Assert.assertTrue(harrastaja.getSopimukset().get(0).getTyyppi().isJäsenmaksutyyppi());
      Assert.assertTrue(harrastaja.getSopimukset().get(1).getTyyppi().isHarjoittelumaksutyyppi());
      Assert.assertTrue(harrastaja.getSopimukset().get(1).getTyyppi().getNimi().contains("18+"));
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "nicklas.sql", "nicklassopimus.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testPoistaSopimusEiKaytossa()
   {
      Harrastaja harrastaja = entityManager.find(Harrastaja.class, 1);
      harrastajaAdmin.setHarrastaja(harrastaja);
      Sopimus sopimus = entityManager.find(Sopimus.class, 1);
      harrastajaAdmin.setSopimus(sopimus);
      harrastajaAdmin.poistaSopimus();
      entityManager.clear();
      harrastaja = entityManager.find(Harrastaja.class, 1);
      Assert.assertEquals(0, harrastaja.getSopimukset().size());
      sopimus = entityManager.find(Sopimus.class, 1);
      Assert.assertNull(sopimus);
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "nicklas.sql", "nicklassopimuskaytossa.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testPoistaSopimusKaytossa()
   {
      Harrastaja harrastaja = entityManager.find(Harrastaja.class, 1);
      harrastajaAdmin.setHarrastaja(harrastaja);
      Sopimus sopimus = entityManager.find(Sopimus.class, 1);
      harrastajaAdmin.setSopimus(sopimus);
      exception.expect(IsoveliPoikkeus.class);
      exception.expectMessage("Sopimuksella on sopimuslaskuja ja sitä ei voi poistaa (1kpl: 01.01.2013-30.05.2013...)");
      harrastajaAdmin.poistaSopimus();
      entityManager.clear();
      harrastaja = entityManager.find(Harrastaja.class, 1);
      Assert.assertEquals(1, harrastaja.getSopimukset().size());
      sopimus = entityManager.find(Sopimus.class, 1);
      Assert.assertNotNull(sopimus);
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "nicklas.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testLisaaVyokoe()
   {
      Harrastaja harrastaja = entityManager.find(Harrastaja.class, 1);
      harrastajaAdmin.setHarrastaja(harrastaja);
      harrastajaAdmin.lisääVyökoe();
      harrastajaAdmin.tallennaVyökoe();
      entityManager.clear();
      harrastaja = entityManager.find(Harrastaja.class, 1);
      Assert.assertEquals(1, harrastaja.getVyökokeet().size());
      Assert.assertEquals("8.kup", harrastaja.getVyökokeet().iterator().next().getVyöarvo().getNimi());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "nicklas.sql", "nicklas8kup.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testLisaaToinenVyokoe()
   {
      Harrastaja harrastaja = entityManager.find(Harrastaja.class, 1);
      harrastajaAdmin.setHarrastaja(harrastaja);
      harrastajaAdmin.lisääVyökoe();
      harrastajaAdmin.tallennaVyökoe();
      entityManager.clear();
      harrastaja = entityManager.find(Harrastaja.class, 1);
      Assert.assertEquals(2, harrastaja.getVyökokeet().size());
      Assert.assertEquals("8.kup", harrastaja.getVyökokeet().get(0).getVyöarvo().getNimi());
      Assert.assertEquals("7.kup", harrastaja.getVyökokeet().get(1).getVyöarvo().getNimi());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "nicklas.sql", "nicklas7kup.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testLisatynVyokokeenJarjestys()
   {
      Harrastaja harrastaja = entityManager.find(Harrastaja.class, 1);
      harrastajaAdmin.setHarrastaja(harrastaja);
      harrastajaAdmin.lisääVyökoe();
      Vyöarvo vyöarvo = entityManager.find(Vyöarvo.class, 1);
      harrastajaAdmin.getVyökoe().setVyöarvo(vyöarvo);
      harrastajaAdmin.tallennaVyökoe();
      entityManager.clear();
      harrastaja = entityManager.find(Harrastaja.class, 1);
      Assert.assertEquals(2, harrastaja.getVyökokeet().size());
      Assert.assertEquals("8.kup", harrastaja.getVyökokeet().get(0).getVyöarvo().getNimi());
      Assert.assertEquals("7.kup", harrastaja.getVyökokeet().get(1).getVyöarvo().getNimi());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "nicklas.sql", "nicklas8kup.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testMuokkaaVyokoe()
   {
      Harrastaja harrastaja = entityManager.find(Harrastaja.class, 1);
      harrastajaAdmin.setHarrastaja(harrastaja);
      Vyöarvo kup7 = entityManager.find(Vyöarvo.class, 2);
      harrastajaAdmin.setVyökoe(harrastaja.getVyökokeet().iterator().next());
      harrastajaAdmin.getVyökoe().setVyöarvo(kup7);
      harrastajaAdmin.tallennaVyökoe();
      entityManager.clear();
      harrastaja = entityManager.find(Harrastaja.class, 1);
      Assert.assertEquals(1, harrastaja.getVyökokeet().size());
      Assert.assertEquals("7.kup", harrastaja.getVyökokeet().iterator().next().getVyöarvo().getNimi());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "seed.sql", "nicklas.sql", "nicklas8kup.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testPoistaVyokoe()
   {
      Harrastaja harrastaja = entityManager.find(Harrastaja.class, 1);
      harrastajaAdmin.setHarrastaja(harrastaja);
      harrastajaAdmin.setVyökoe(harrastaja.getVyökokeet().iterator().next());
      harrastajaAdmin.poistaVyökoe();
      entityManager.clear();
      harrastaja = entityManager.find(Harrastaja.class, 1);
      Assert.assertEquals(0, harrastaja.getVyökokeet().size());
   }

   @Test
   @ApplyScriptBefore(
   { "cleanup.sql", "nicklas.sql", "yllapitajarooli.sql" })
   @Cleanup(phase = TestExecutionPhase.NONE)
   public void testLisaaRooli()
   {
      Harrastaja harrastaja = entityManager.find(Harrastaja.class, 1);
      Rooli admin = entityManager.find(Rooli.class, 1);
      harrastajaAdmin.setHarrastaja(harrastaja);
      harrastaja.getRoolit().add(admin);
      harrastajaAdmin.tallennaHarrastaja();
      entityManager.clear();
      harrastaja = entityManager.find(Harrastaja.class, 1);
      Assert.assertEquals(1, harrastaja.getRoolit().size());
   }

}
