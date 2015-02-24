package fi.budokwai.isoveli.test;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ApplyScriptAfter;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import fi.budokwai.isoveli.admin.LaskutusAdmin;
import fi.budokwai.isoveli.malli.Lasku;

@RunWith(Arquillian.class)
@ApplyScriptBefore("seed.sql")
@ApplyScriptAfter("cleanup.sql")
public class LaskutusTest extends Perustesti
{

   @Inject
   private LaskutusAdmin laskutusAdmin;
   
   @Inject
   private EntityManager entityManager;
   
   @Test
   @ApplyScriptBefore("emilsopimus.sql")
   @Transactional
   public void testLaskutushenkiloHuoltajalla()
   {
      laskutusAdmin.laskutaSopimukset();
      Lasku lasku = entityManager.createNamedQuery("select l from Lasku l", Lasku.class).getResultList().iterator().next();
      Assert.assertEquals("Nicklas Karlsson", lasku.getHenkilö().getNimi());
   }
/*
   
   @Test
   public void testLaskutushenkilöTäysiikäiselläPerheenjäsenellä()
   {
      Harrastaja täysiikäinenHarrastaja = teeTäysiikäinenHarrastaja("Nicklas Karlsson");
      Harrastaja alaikäinenHarrastaja = teeAlaikäinenHarrastaja("Emil Karlsson");
      Perhe perhe = teePerhe("Karlsson", täysiikäinenHarrastaja, alaikäinenHarrastaja);
      LaskutusAdmin laskutusAdmin = new LaskutusAdmin();
      List<Sopimus> sopimukset = new ArrayList<>();
      sopimukset.add(teeHarjoittelusopimus(alaikäinenHarrastaja, "01.01.2014", 3));
      Lasku lasku = laskutusAdmin.x(sopimukset);
      Assert.assertEquals("Nicklas Karlsson", lasku.getHenkilö().getNimi());
   }

   @Test
   public void testLaskutushenkilöTäysiikäisellä()
   {
      Harrastaja täysiikäinenHarrastaja = teeTäysiikäinenHarrastaja("Nicklas Karlsson");
      Harrastaja alaikäinenHarrastaja = teeAlaikäinenHarrastaja("Emil Karlsson");
      LaskutusAdmin laskutusAdmin = new LaskutusAdmin();
      List<Sopimus> sopimukset = new ArrayList<>();
      sopimukset.add(teeHarjoittelusopimus(alaikäinenHarrastaja, "01.01.2014", 3));
      sopimukset.add(teeHarjoittelusopimus(täysiikäinenHarrastaja, "01.01.2014", 3));
      Lasku lasku = laskutusAdmin.x(sopimukset);
      Assert.assertEquals("Nicklas Karlsson", lasku.getHenkilö().getNimi());
   }

   @Test
   public void testLaskutushenkilöAlaikäisellä()
   {
      Harrastaja alaikäinenHarrastaja = teeAlaikäinenHarrastaja("Emil Karlsson");
      LaskutusAdmin laskutusAdmin = new LaskutusAdmin();
      List<Sopimus> sopimukset = new ArrayList<>();
      sopimukset.add(teeHarjoittelusopimus(alaikäinenHarrastaja, "01.01.2014", 3));
      Lasku lasku = laskutusAdmin.x(sopimukset);
      Assert.assertEquals("Emil Karlsson", lasku.getHenkilö().getNimi());
   }

   @Test
   public void testPerhealennus()
   {
      Harrastaja täysiikäinenHarrastaja = teeTäysiikäinenHarrastaja("Nicklas Karlsson");
      täysiikäinenHarrastaja.setId(1);
      Harrastaja täysiikäinenHarrastaja2 = teeTäysiikäinenHarrastaja("Heidi Karlsson");
      täysiikäinenHarrastaja.setId(2);
      LaskutusAdmin laskutusAdmin = new LaskutusAdmin();
      List<Sopimus> sopimukset = new ArrayList<>();
      sopimukset.add(teeHarjoittelusopimus(täysiikäinenHarrastaja, "01.01.2014", 6));
      sopimukset.add(teeHarjoittelusopimus(täysiikäinenHarrastaja2, "01.01.2014", 6));
      Lasku lasku = laskutusAdmin.x(sopimukset);
      Assert.assertEquals(3, lasku.getLaskurivejä());
      Assert.assertEquals(-180d, lasku.getLaskurivit().get(2).getRivihinta());
   }

   @Test
   public void testPerhealennusVainHarjoittelumaksuista()
   {
      Harrastaja täysiikäinenHarrastaja = teeTäysiikäinenHarrastaja("Nicklas Karlsson");
      täysiikäinenHarrastaja.setId(1);
      Harrastaja täysiikäinenHarrastaja2 = teeTäysiikäinenHarrastaja("Heidi Karlsson");
      täysiikäinenHarrastaja.setId(2);
      // LaskutusAdmin laskutusAdmin = new LaskutusAdmin();
      List<Sopimus> sopimukset = new ArrayList<>();
      sopimukset.add(teeKertamaksusopimus(täysiikäinenHarrastaja, "01.01.2014"));
      sopimukset.add(teeKertamaksusopimus(täysiikäinenHarrastaja2, "01.01.2014"));
      // Lasku lasku = laskutusAdmin.x(sopimukset);
      // Assert.assertEquals(2, lasku.getLaskurivejä());
   }

   @Test
   public void testPerhealennusKohdistuuKallimpaan()
   {
      Harrastaja täysiikäinenHarrastaja = teeTäysiikäinenHarrastaja("Nicklas Karlsson");
      täysiikäinenHarrastaja.setId(1);
      Harrastaja täysiikäinenHarrastaja2 = teeTäysiikäinenHarrastaja("Heidi Karlsson");
      täysiikäinenHarrastaja.setId(2);
      LaskutusAdmin laskutusAdmin = new LaskutusAdmin();
      List<Sopimus> sopimukset = new ArrayList<>();
      sopimukset.add(teeHarjoittelusopimus(täysiikäinenHarrastaja, "01.01.2014", 6));
      sopimukset.add(teeHarjoittelusopimus(täysiikäinenHarrastaja2, "01.01.2014", 6));
      sopimukset.get(0).getTyyppi().setHinta(200);
      Lasku lasku = laskutusAdmin.x(sopimukset);
      Assert.assertEquals(3, lasku.getLaskurivejä());
      Assert.assertEquals(-360d, lasku.getLaskurivit().get(2).getRivihinta());
   }

   @Test
   public void testTauko()
   {
      Harrastaja harrastaja = teeTäysiikäinenHarrastaja("Nicklas Karlsson");
      LaskutusAdmin laskutusAdmin = new LaskutusAdmin();
      List<Sopimus> sopimukset = new ArrayList<>();
      sopimukset.add(teeHarjoittelusopimus(harrastaja, "01.01.2015", 6));
      Date t1 = DateUtil.silloinD("01.02.2015");
      Date t2 = DateUtil.silloinD("01.03.2015");
      harrastaja.setTauko(new Jakso(t1, t2));
      Lasku lasku = laskutusAdmin.x(sopimukset);
      Assert.assertEquals(2, lasku.getLaskurivejä());
      Laskurivi tauko = lasku.getLaskurivit().get(1);
      Assert.assertEquals("01.02.2015-01.03.2015", tauko.getInfotieto());
      Assert.assertEquals("Taukohyvitys (Nicklas)", tauko.getTuotenimi());
      Assert.assertEquals(28, tauko.getMäärä());
      Assert.assertEquals(-3.33, tauko.getYksikköhinta());
      double hinta = new BigDecimal(tauko.getRivihinta()).setScale(2, RoundingMode.HALF_UP).doubleValue();
      Assert.assertEquals(-93.24, hinta);
   }
*/
   
}
