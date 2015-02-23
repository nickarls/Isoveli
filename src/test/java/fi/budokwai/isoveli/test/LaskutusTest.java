package fi.budokwai.isoveli.test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import fi.budokwai.isoveli.admin.LaskutusAdmin;
import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Henkil�;
import fi.budokwai.isoveli.malli.Jakso;
import fi.budokwai.isoveli.malli.Lasku;
import fi.budokwai.isoveli.malli.Laskurivi;
import fi.budokwai.isoveli.malli.Perhe;
import fi.budokwai.isoveli.malli.Sopimus;
import fi.budokwai.isoveli.util.DateUtil;

public class LaskutusTest extends Perustesti
{

   @Test
   public void testLaskutushenkil�Huoltajalla()
   {
      Henkil� huoltaja = teeHenkil�("Nicklas Karlsson");
      Harrastaja harrastaja = teeAlaik�inenHarrastaja("Emil Karlsson");
      harrastaja.setHuoltaja(huoltaja);
      Perhe perhe = teePerhe("Karlsson", huoltaja, harrastaja);
      LaskutusAdmin laskutusAdmin = new LaskutusAdmin();
      List<Sopimus> sopimukset = new ArrayList<>();
      sopimukset.add(teeHarjoittelusopimus(harrastaja, "01.01.2014", 3));
      Lasku lasku = laskutusAdmin.x(sopimukset);
      Assert.assertEquals("Nicklas Karlsson", lasku.getHenkil�().getNimi());
   }

   @Test
   public void testLaskutushenkil�T�ysiik�isell�Perheenj�senell�()
   {
      Harrastaja t�ysiik�inenHarrastaja = teeT�ysiik�inenHarrastaja("Nicklas Karlsson");
      Harrastaja alaik�inenHarrastaja = teeAlaik�inenHarrastaja("Emil Karlsson");
      Perhe perhe = teePerhe("Karlsson", t�ysiik�inenHarrastaja, alaik�inenHarrastaja);
      LaskutusAdmin laskutusAdmin = new LaskutusAdmin();
      List<Sopimus> sopimukset = new ArrayList<>();
      sopimukset.add(teeHarjoittelusopimus(alaik�inenHarrastaja, "01.01.2014", 3));
      Lasku lasku = laskutusAdmin.x(sopimukset);
      Assert.assertEquals("Nicklas Karlsson", lasku.getHenkil�().getNimi());
   }

   @Test
   public void testLaskutushenkil�T�ysiik�isell�()
   {
      Harrastaja t�ysiik�inenHarrastaja = teeT�ysiik�inenHarrastaja("Nicklas Karlsson");
      Harrastaja alaik�inenHarrastaja = teeAlaik�inenHarrastaja("Emil Karlsson");
      LaskutusAdmin laskutusAdmin = new LaskutusAdmin();
      List<Sopimus> sopimukset = new ArrayList<>();
      sopimukset.add(teeHarjoittelusopimus(alaik�inenHarrastaja, "01.01.2014", 3));
      sopimukset.add(teeHarjoittelusopimus(t�ysiik�inenHarrastaja, "01.01.2014", 3));
      Lasku lasku = laskutusAdmin.x(sopimukset);
      Assert.assertEquals("Nicklas Karlsson", lasku.getHenkil�().getNimi());
   }

   @Test
   public void testLaskutushenkil�Alaik�isell�()
   {
      Harrastaja alaik�inenHarrastaja = teeAlaik�inenHarrastaja("Emil Karlsson");
      LaskutusAdmin laskutusAdmin = new LaskutusAdmin();
      List<Sopimus> sopimukset = new ArrayList<>();
      sopimukset.add(teeHarjoittelusopimus(alaik�inenHarrastaja, "01.01.2014", 3));
      Lasku lasku = laskutusAdmin.x(sopimukset);
      Assert.assertEquals("Emil Karlsson", lasku.getHenkil�().getNimi());
   }

   @Test
   public void testLaskutaYhdenVuodenJ�senmaksu()
   {
      Harrastaja t�ysiik�inenHarrastaja = teeT�ysiik�inenHarrastaja("Nicklas Karlsson");
      LaskutusAdmin laskutusAdmin = new LaskutusAdmin();
      List<Sopimus> sopimukset = new ArrayList<>();
      sopimukset.add(teeJ�senmaksusopimus(t�ysiik�inenHarrastaja, "01.01.2015"));
      Lasku lasku = laskutusAdmin.x(sopimukset);
      Assert.assertEquals(1, lasku.getLaskurivej�());
      Assert.assertEquals(1, lasku.getLaskurivit().get(0).getM��r�());
      Assert.assertEquals(10.0d, lasku.getLaskurivit().get(0).getYksikk�hinta());
      Assert.assertEquals("kpl", lasku.getLaskurivit().get(0).getYksikk�());
      Assert.assertEquals(10.0d, lasku.getLaskurivit().get(0).getRivihinta());
      Assert.assertEquals(10.0d, lasku.getYhteishinta());
      Assert.assertEquals("01.01.2015-31.12.2015", lasku.getLaskurivit().get(0).getInfotieto());
   }

   @Test
   public void testLaskutaKahdenVuodenJ�senmaksu()
   {
      Harrastaja t�ysiik�inenHarrastaja = teeT�ysiik�inenHarrastaja("Nicklas Karlsson");
      LaskutusAdmin laskutusAdmin = new LaskutusAdmin();
      List<Sopimus> sopimukset = new ArrayList<>();
      sopimukset.add(teeJ�senmaksusopimus(t�ysiik�inenHarrastaja, "01.01.2014"));
      Lasku lasku = laskutusAdmin.x(sopimukset);
      Assert.assertEquals(1, lasku.getLaskurivej�());
      Assert.assertEquals(2, lasku.getLaskurivit().get(0).getM��r�());
      Assert.assertEquals(10.0d, lasku.getLaskurivit().get(0).getYksikk�hinta());
      Assert.assertEquals("kpl", lasku.getLaskurivit().get(0).getYksikk�());
      Assert.assertEquals(20.0d, lasku.getLaskurivit().get(0).getRivihinta());
      Assert.assertEquals(20.0d, lasku.getYhteishinta());
      Assert.assertEquals("01.01.2014-31.12.2015", lasku.getLaskurivit().get(0).getInfotieto());
   }

   @Test
   public void testLaskutaHarjoittelumaksu1kkV�li()
   {
      Harrastaja t�ysiik�inenHarrastaja = teeT�ysiik�inenHarrastaja("Nicklas Karlsson");
      LaskutusAdmin laskutusAdmin = new LaskutusAdmin();
      List<Sopimus> sopimukset = new ArrayList<>();
      sopimukset.add(teeHarjoittelusopimus(t�ysiik�inenHarrastaja, "01.01.2015", 1));
      Lasku lasku = laskutusAdmin.x(sopimukset);
      Assert.assertEquals(1, lasku.getLaskurivej�());
      Assert.assertEquals(2, lasku.getLaskurivit().get(0).getM��r�());
      Assert.assertEquals(100.0d, lasku.getLaskurivit().get(0).getYksikk�hinta());
      Assert.assertEquals("kpl", lasku.getLaskurivit().get(0).getYksikk�());
      Assert.assertEquals(200.0d, lasku.getLaskurivit().get(0).getRivihinta());
      Assert.assertEquals(200.0d, lasku.getYhteishinta());
      Assert.assertEquals("01.01.2015-01.03.2015", lasku.getLaskurivit().get(0).getInfotieto());
   }

   @Test
   public void testLaskutaHarjoittelumaksu2kkV�li()
   {
      Harrastaja t�ysiik�inenHarrastaja = teeT�ysiik�inenHarrastaja("Nicklas Karlsson");
      LaskutusAdmin laskutusAdmin = new LaskutusAdmin();
      List<Sopimus> sopimukset = new ArrayList<>();
      sopimukset.add(teeHarjoittelusopimus(t�ysiik�inenHarrastaja, "01.01.2014", 2));
      Lasku lasku = laskutusAdmin.x(sopimukset);
      Assert.assertEquals(1, lasku.getLaskurivej�());
      Assert.assertEquals(14, lasku.getLaskurivit().get(0).getM��r�());
      Assert.assertEquals(100.0d, lasku.getLaskurivit().get(0).getYksikk�hinta());
      Assert.assertEquals("kpl", lasku.getLaskurivit().get(0).getYksikk�());
      Assert.assertEquals(1400.0d, lasku.getLaskurivit().get(0).getRivihinta());
      Assert.assertEquals(1400.0d, lasku.getYhteishinta());
      Assert.assertEquals("01.01.2014-01.03.2015", lasku.getLaskurivit().get(0).getInfotieto());
   }

   @Test
   public void testLaskutaHarjoittelumaksu6kkV�li()
   {
      Harrastaja t�ysiik�inenHarrastaja = teeT�ysiik�inenHarrastaja("Nicklas Karlsson");
      LaskutusAdmin laskutusAdmin = new LaskutusAdmin();
      List<Sopimus> sopimukset = new ArrayList<>();
      sopimukset.add(teeHarjoittelusopimus(t�ysiik�inenHarrastaja, "01.01.2014", 6));
      Lasku lasku = laskutusAdmin.x(sopimukset);
      Assert.assertEquals(1, lasku.getLaskurivej�());
      Assert.assertEquals(18, lasku.getLaskurivit().get(0).getM��r�());
      Assert.assertEquals(100.0d, lasku.getLaskurivit().get(0).getYksikk�hinta());
      Assert.assertEquals("kpl", lasku.getLaskurivit().get(0).getYksikk�());
      Assert.assertEquals(1800.0d, lasku.getLaskurivit().get(0).getRivihinta());
      Assert.assertEquals(1800.0d, lasku.getYhteishinta());
      Assert.assertEquals("01.01.2014-01.07.2015", lasku.getLaskurivit().get(0).getInfotieto());
   }

   @Test
   public void testPerhealennus()
   {
      Harrastaja t�ysiik�inenHarrastaja = teeT�ysiik�inenHarrastaja("Nicklas Karlsson");
      t�ysiik�inenHarrastaja.setId(1);
      Harrastaja t�ysiik�inenHarrastaja2 = teeT�ysiik�inenHarrastaja("Heidi Karlsson");
      t�ysiik�inenHarrastaja.setId(2);
      LaskutusAdmin laskutusAdmin = new LaskutusAdmin();
      List<Sopimus> sopimukset = new ArrayList<>();
      sopimukset.add(teeHarjoittelusopimus(t�ysiik�inenHarrastaja, "01.01.2014", 6));
      sopimukset.add(teeHarjoittelusopimus(t�ysiik�inenHarrastaja2, "01.01.2014", 6));
      Lasku lasku = laskutusAdmin.x(sopimukset);
      Assert.assertEquals(3, lasku.getLaskurivej�());
      Assert.assertEquals(-180d, lasku.getLaskurivit().get(2).getRivihinta());
   }

   @Test
   public void testPerhealennusVainHarjoittelumaksuista()
   {
      Harrastaja t�ysiik�inenHarrastaja = teeT�ysiik�inenHarrastaja("Nicklas Karlsson");
      t�ysiik�inenHarrastaja.setId(1);
      Harrastaja t�ysiik�inenHarrastaja2 = teeT�ysiik�inenHarrastaja("Heidi Karlsson");
      t�ysiik�inenHarrastaja.setId(2);
      // LaskutusAdmin laskutusAdmin = new LaskutusAdmin();
      List<Sopimus> sopimukset = new ArrayList<>();
      sopimukset.add(teeKertamaksusopimus(t�ysiik�inenHarrastaja, "01.01.2014"));
      sopimukset.add(teeKertamaksusopimus(t�ysiik�inenHarrastaja2, "01.01.2014"));
      // Lasku lasku = laskutusAdmin.x(sopimukset);
      // Assert.assertEquals(2, lasku.getLaskurivej�());
   }

   @Test
   public void testPerhealennusKohdistuuKallimpaan()
   {
      Harrastaja t�ysiik�inenHarrastaja = teeT�ysiik�inenHarrastaja("Nicklas Karlsson");
      t�ysiik�inenHarrastaja.setId(1);
      Harrastaja t�ysiik�inenHarrastaja2 = teeT�ysiik�inenHarrastaja("Heidi Karlsson");
      t�ysiik�inenHarrastaja.setId(2);
      LaskutusAdmin laskutusAdmin = new LaskutusAdmin();
      List<Sopimus> sopimukset = new ArrayList<>();
      sopimukset.add(teeHarjoittelusopimus(t�ysiik�inenHarrastaja, "01.01.2014", 6));
      sopimukset.add(teeHarjoittelusopimus(t�ysiik�inenHarrastaja2, "01.01.2014", 6));
      sopimukset.get(0).getTyyppi().setHinta(200);
      Lasku lasku = laskutusAdmin.x(sopimukset);
      Assert.assertEquals(3, lasku.getLaskurivej�());
      Assert.assertEquals(-360d, lasku.getLaskurivit().get(2).getRivihinta());
   }

   @Test
   public void testTauko()
   {
      Harrastaja harrastaja = teeT�ysiik�inenHarrastaja("Nicklas Karlsson");
      LaskutusAdmin laskutusAdmin = new LaskutusAdmin();
      List<Sopimus> sopimukset = new ArrayList<>();
      sopimukset.add(teeHarjoittelusopimus(harrastaja, "01.01.2015", 6));
      Date t1 = DateUtil.silloinD("01.02.2015");
      Date t2 = DateUtil.silloinD("01.03.2015");
      harrastaja.setTauko(new Jakso(t1, t2));
      Lasku lasku = laskutusAdmin.x(sopimukset);
      Assert.assertEquals(2, lasku.getLaskurivej�());
      Laskurivi tauko = lasku.getLaskurivit().get(1);
      Assert.assertEquals("01.02.2015-01.03.2015", tauko.getInfotieto());
      Assert.assertEquals("Taukohyvitys (Nicklas)", tauko.getTuotenimi());
      Assert.assertEquals(28, tauko.getM��r�());
      Assert.assertEquals(-3.33, tauko.getYksikk�hinta());
      double hinta = new BigDecimal(tauko.getRivihinta()).setScale(2, RoundingMode.HALF_UP).doubleValue();
      Assert.assertEquals(-93.24, hinta);

   }

}
