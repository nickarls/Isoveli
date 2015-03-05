package fi.budokwai.isoveli.test;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.ByteStreams;

import fi.budokwai.isoveli.util.Tuonti;
import fi.budokwai.isoveli.util.Tuontitulos;

public class TuontiTest
{
   private static Tuontitulos tuontitulos = new Tuontitulos();

   @AfterClass
   public static void output()
   {
      // Assert.assertTrue(tuontitulos.isOK());
      // Assert.assertEquals(247, tuontitulos.getHarrastajat().size());
      tuontitulos.getVirheet().stream().forEach(v -> System.out.println(v));
      tuontitulos.getHarrastajat().stream().filter(h -> h.isInfotiskille())
         .forEach(h -> System.out.println(String.format("%s:\t%s", h.getNimi(), h.getHuomautus())));
   }

   @BeforeClass
   public static void tuo()
   {
      Tuonti tuonti = new Tuonti();
      try
      {
         ByteArrayOutputStream xls = new ByteArrayOutputStream();
         ByteStreams.copy(TuontiTest.class.getResourceAsStream("/jasenrekisteri.xls"), xls);
         tuontitulos = tuonti.tuoJäsenrekisteri(xls.toByteArray());
      } catch (Exception e)
      {
         e.printStackTrace();
         tuontitulos.lisääVirhe(e.getMessage());
      }
   }

   @Test
   public void perheenPerustiedotTest()
   {
      tuontitulos.getHarrastajat().stream().filter(h -> h.getPerhe() != null).map(h -> h.getPerhe()).forEach(p -> {
         Assert.assertNotNull(p.getNimi());
         Assert.assertNotEquals("", p.getNimi());
         Assert.assertTrue(Character.isUpperCase(p.getNimi().charAt(0)));
         Assert.assertNotNull(p.getOsoite());
         Assert.assertTrue(p.getOsoite().getOsoite().length() > 5);
         Assert.assertTrue(p.getOsoite().getPostinumero().length() == 5);
         Assert.assertTrue(p.getOsoite().getKaupunki().length() > 3);
         Assert.assertTrue(p.getPerheenjäsenet().size() >= 2);
         Assert.assertTrue(p.getHuoltajat().size() >= 1);
         p.getHuoltajat().forEach(h -> {
            Assert.assertNotNull(h.getYhteystiedot());
            Assert.assertTrue(h.getNimi().length() > 5);
         });
      });
   }

   @Test
   public void perheenOsoitetiedotTest()
   {
      tuontitulos.getHarrastajat().stream().filter(h -> h.getPerhe() != null).map(h -> h.getPerhe()).forEach(p -> {
         Assert.assertNotNull(p.getOsoite());
         Assert.assertTrue(p.getOsoite().getOsoite().length() > 5);
         Assert.assertTrue(p.getOsoite().getPostinumero().length() == 5);
         Assert.assertTrue(p.getOsoite().getKaupunki().length() > 3);
      });
   }

   @Test
   public void perheenKoostumusTest()
   {
      tuontitulos.getHarrastajat().stream().filter(h -> h.getPerhe() != null).map(h -> h.getPerhe()).forEach(p -> {
         Assert.assertTrue(p.getPerheenjäsenet().size() >= 2);
         Assert.assertTrue(p.getHuoltajat().size() >= 1);
      });
      tuontitulos.getHarrastajat().stream().forEach(h -> {
         if (h.isAlaikäinen())
         {
            Assert.assertNotNull(h.getPerhe());
            Assert.assertNotNull(h.getHuoltaja());
         } else
         {
            Assert.assertNull(h.getHuoltaja());
         }
      });
      tuontitulos.getHarrastajat().stream().forEach(h -> {
         if (h.getHuoltaja() != null)
         {
            Assert.assertNotEquals(h.getHuoltaja().getNimi(), h.getNimi());
         }
      });
   }

   @Test
   public void täysikäistenHarrastajienYhteystiedotTest()
   {
      tuontitulos.getHarrastajat().stream().filter(h -> !h.isAlaikäinen()).forEach(harr -> {
         Assert.assertNotNull(harr.getYhteystiedot());
         if (harr.getYhteystiedot().getPuhelinnumero().length() <= 3)
         {
            Assert.assertEquals(true, harr.isInfotiskille());
            Assert.assertEquals(true, harr.getHuomautus() != null && harr.getHuomautus().contains("puhelinnumero"));
         }
         if (harr.getYhteystiedot().getSähköposti().length() <= 3)
         {
            Assert.assertEquals(true, harr.isInfotiskille());
            Assert.assertEquals(true, harr.getHuomautus() != null && harr.getHuomautus().contains("sähköposti"));
         }
         Assert.assertTrue(harr.getYhteystiedot().isSähköpostilistalla());
      });
   }

   @Test
   public void huoltajienYhteystiedotTest()
   {
      tuontitulos.getHarrastajat().stream().filter(h -> h.getPerhe() != null).forEach(harr -> {
         harr.getPerhe().getPerheenjäsenet().stream().filter(h -> !h.isHarrastaja()).forEach(huo -> {
            Assert.assertNotNull(huo.getYhteystiedot());
            if (huo.getYhteystiedot().getPuhelinnumero().length() <= 3)
            {
               Assert.assertEquals(true, harr.isInfotiskille());
               Assert.assertEquals(true, harr.getHuomautus() != null && harr.getHuomautus().contains("puhelinnumero"));
            }
            if (huo.getYhteystiedot().getSähköposti().length() <= 3)
            {
               Assert.assertEquals(true, harr.isInfotiskille());
               Assert.assertEquals(true, harr.getHuomautus() != null && harr.getHuomautus().contains("sähköposti"));
            }
            Assert.assertTrue(huo.getYhteystiedot().isSähköpostilistalla());

         });
      });
   }

   @Test
   public void uniikitPerheenjäsenet()
   {
      tuontitulos.getHarrastajat().stream().filter(h -> h.getPerhe() != null).map(h -> h.getPerhe()).forEach(p -> {
         Set<String> nimet = new HashSet<>();
         p.getPerheenjäsenet().forEach(h -> {
            if (nimet.contains(h.getNimi()))
            {
               System.out.println("!");
            }
            Assert.assertFalse(nimet.contains(h.getNimi()));
            nimet.add(h.getNimi());
         });
      });
   }

}
