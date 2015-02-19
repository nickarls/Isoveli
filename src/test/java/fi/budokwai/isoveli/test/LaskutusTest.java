package fi.budokwai.isoveli.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import fi.budokwai.isoveli.admin.LaskutusAdmin;
import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Henkilö;
import fi.budokwai.isoveli.malli.Lasku;
import fi.budokwai.isoveli.malli.Perhe;
import fi.budokwai.isoveli.malli.Sopimus;

public class LaskutusTest extends Perustesti
{

   @Test
   public void testLaskutushenkilöHuoltajalla()
   {
      Henkilö huoltaja = teeHenkilö("Nicklas Karlsson");
      Harrastaja harrastaja = teeAlaikäinenHarrastaja("Emil Karlsson");
      harrastaja.setHuoltaja(huoltaja);
      Perhe perhe = teePerhe("Karlsson", huoltaja, harrastaja);
      LaskutusAdmin laskutusAdmin = new LaskutusAdmin();
      List<Sopimus> sopimukset = new ArrayList<>();
      sopimukset.add(teeHarjoittelusopimus(harrastaja, "01.01.2014", 3));
      Lasku lasku = laskutusAdmin.x(sopimukset);
      Assert.assertEquals("Nicklas Karlsson", lasku.getHenkilö().getNimi());
   }

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
   public void testLaskutaYhdenVuodenJäsenmaksu()
   {
      Harrastaja täysiikäinenHarrastaja = teeTäysiikäinenHarrastaja("Nicklas Karlsson");
      LaskutusAdmin laskutusAdmin = new LaskutusAdmin();
      List<Sopimus> sopimukset = new ArrayList<>();
      sopimukset.add(teeJäsenmaksusopimus(täysiikäinenHarrastaja, "01.01.2015"));
      Lasku lasku = laskutusAdmin.x(sopimukset);
      Assert.assertEquals(1, lasku.getLaskurivejä());
      Assert.assertEquals(1, lasku.getLaskurivit().get(0).getMäärä());
      Assert.assertEquals(10.0d, lasku.getLaskurivit().get(0).getYksikköhinta());
      Assert.assertEquals("kpl", lasku.getLaskurivit().get(0).getYksikkö());
      Assert.assertEquals(10.0d, lasku.getLaskurivit().get(0).getRivihinta());
      Assert.assertEquals(10.0d, lasku.getYhteishinta());
      Assert.assertEquals("01.01.2015-31.12.2015", lasku.getLaskurivit().get(0).getInfotieto());
   }

   @Test
   public void testLaskutaKahdenVuodenJäsenmaksu()
   {
      Harrastaja täysiikäinenHarrastaja = teeTäysiikäinenHarrastaja("Nicklas Karlsson");
      LaskutusAdmin laskutusAdmin = new LaskutusAdmin();
      List<Sopimus> sopimukset = new ArrayList<>();
      sopimukset.add(teeJäsenmaksusopimus(täysiikäinenHarrastaja, "01.01.2014"));
      Lasku lasku = laskutusAdmin.x(sopimukset);
      Assert.assertEquals(1, lasku.getLaskurivejä());
      Assert.assertEquals(2, lasku.getLaskurivit().get(0).getMäärä());
      Assert.assertEquals(10.0d, lasku.getLaskurivit().get(0).getYksikköhinta());
      Assert.assertEquals("kpl", lasku.getLaskurivit().get(0).getYksikkö());
      Assert.assertEquals(20.0d, lasku.getLaskurivit().get(0).getRivihinta());
      Assert.assertEquals(20.0d, lasku.getYhteishinta());
      Assert.assertEquals("01.01.2014-31.12.2015", lasku.getLaskurivit().get(0).getInfotieto());
   }

   @Test
   public void testLaskutaHarjoittelumaksu1kkVäli()
   {
      Harrastaja täysiikäinenHarrastaja = teeTäysiikäinenHarrastaja("Nicklas Karlsson");
      LaskutusAdmin laskutusAdmin = new LaskutusAdmin();
      List<Sopimus> sopimukset = new ArrayList<>();
      sopimukset.add(teeHarjoittelusopimus(täysiikäinenHarrastaja, "01.01.2015", 1));
      Lasku lasku = laskutusAdmin.x(sopimukset);
      Assert.assertEquals(1, lasku.getLaskurivejä());
      Assert.assertEquals(2, lasku.getLaskurivit().get(0).getMäärä());
      Assert.assertEquals(100.0d, lasku.getLaskurivit().get(0).getYksikköhinta());
      Assert.assertEquals("kpl", lasku.getLaskurivit().get(0).getYksikkö());
      Assert.assertEquals(200.0d, lasku.getLaskurivit().get(0).getRivihinta());
      Assert.assertEquals(200.0d, lasku.getYhteishinta());
      Assert.assertEquals("01.01.2015-01.03.2015", lasku.getLaskurivit().get(0).getInfotieto());
   }

   @Test
   public void testLaskutaHarjoittelumaksu2kkVäli()
   {
      Harrastaja täysiikäinenHarrastaja = teeTäysiikäinenHarrastaja("Nicklas Karlsson");
      LaskutusAdmin laskutusAdmin = new LaskutusAdmin();
      List<Sopimus> sopimukset = new ArrayList<>();
      sopimukset.add(teeHarjoittelusopimus(täysiikäinenHarrastaja, "01.01.2014", 2));
      Lasku lasku = laskutusAdmin.x(sopimukset);
      Assert.assertEquals(1, lasku.getLaskurivejä());
      Assert.assertEquals(14, lasku.getLaskurivit().get(0).getMäärä());
      Assert.assertEquals(100.0d, lasku.getLaskurivit().get(0).getYksikköhinta());
      Assert.assertEquals("kpl", lasku.getLaskurivit().get(0).getYksikkö());
      Assert.assertEquals(1400.0d, lasku.getLaskurivit().get(0).getRivihinta());
      Assert.assertEquals(1400.0d, lasku.getYhteishinta());
      Assert.assertEquals("01.01.2014-01.03.2015", lasku.getLaskurivit().get(0).getInfotieto());
   }

   @Test
   public void testLaskutaHarjoittelumaksu6kkVäli()
   {
      Harrastaja täysiikäinenHarrastaja = teeTäysiikäinenHarrastaja("Nicklas Karlsson");
      LaskutusAdmin laskutusAdmin = new LaskutusAdmin();
      List<Sopimus> sopimukset = new ArrayList<>();
      sopimukset.add(teeHarjoittelusopimus(täysiikäinenHarrastaja, "01.01.2014", 6));
      Lasku lasku = laskutusAdmin.x(sopimukset);
      Assert.assertEquals(1, lasku.getLaskurivejä());
      Assert.assertEquals(18, lasku.getLaskurivit().get(0).getMäärä());
      Assert.assertEquals(100.0d, lasku.getLaskurivit().get(0).getYksikköhinta());
      Assert.assertEquals("kpl", lasku.getLaskurivit().get(0).getYksikkö());
      Assert.assertEquals(1800.0d, lasku.getLaskurivit().get(0).getRivihinta());
      Assert.assertEquals(1800.0d, lasku.getYhteishinta());
      Assert.assertEquals("01.01.2014-01.07.2015", lasku.getLaskurivit().get(0).getInfotieto());
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

   }

}
