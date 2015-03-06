package fi.budokwai.isoveli.test;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.ByteStreams;

import fi.budokwai.isoveli.IsoveliPoikkeus;
import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Henkilö;
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
         p.getPerheenjäsenet().stream().forEach(h -> {
            Assert.assertNull(kentänArvo(h, "osoite"));
         });
      });
   }

   private Object kentänArvo(Object olio, String kenttänimi)
   {
      try
      {
         Field kenttä = Henkilö.class.getDeclaredField(kenttänimi);
         kenttä.setAccessible(true);
         return kenttä.get(olio);
      } catch (Exception e)
      {
         throw new IsoveliPoikkeus(String.format("Ei voinut lukea kenttää %s oliosta %s", kenttänimi, olio), e);
      }
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
            tarkistaTiskihuomautus(harr, "puhelinnumero");
         }
         if (harr.getYhteystiedot().getSähköposti().length() <= 3)
         {
            tarkistaTiskihuomautus(harr, "sähköposti");
         }
         Assert.assertTrue(harr.getYhteystiedot().isSähköpostilistalla());
      });
   }

   @Test
   public void testHarrastajienPerustiedot()
   {
      tuontitulos.getHarrastajat().forEach(h -> {
         Assert.assertNotNull(h.getEtunimi());
         Assert.assertNotNull(h.getSukunimi());
         Assert.assertNotNull(h.getSukupuoli());
         Assert.assertNotNull(h.getSyntynyt());
         Assert.assertNotNull(h.getJäsennumero());
         if (h.getIce() == null)
         {
            tarkistaTiskihuomautus(h, "ICE");
         }
      });
   }

   private void tarkistaTiskihuomautus(Harrastaja harrastaja, String viesti)
   {
      Assert.assertTrue(harrastaja.isInfotiskille());
      Assert.assertTrue(harrastaja.getHuomautus() != null && harrastaja.getHuomautus().contains(viesti));
   }

   @Test
   public void huoltajienYhteystiedotTest()
   {
      tuontitulos.getHarrastajat().stream().filter(h -> h.getPerhe() != null).forEach(harr -> {
         harr.getPerhe().getPerheenjäsenet().stream().filter(h -> !h.isHarrastaja()).forEach(huo -> {
            Assert.assertNotNull(huo.getYhteystiedot());
            if (huo.getYhteystiedot().getPuhelinnumero().length() <= 3)
            {
               tarkistaTiskihuomautus(harr, "puhelinnumero");
            }
            if (huo.getYhteystiedot().getSähköposti().length() <= 3)
            {
               tarkistaTiskihuomautus(harr, "sähköposti");
            }
            Assert.assertTrue(huo.getYhteystiedot().isSähköpostilistalla());

         });
      });
   }

   @Test
   public void uniikitJäsennumerotTest()
   {
      Set<String> jäsennumerot = new HashSet<>();
      tuontitulos.getHarrastajat().stream().forEach(h -> {
         Assert.assertFalse(jäsennumerot.contains(h.getJäsennumero()));
         jäsennumerot.add(h.getJäsennumero());
      });
   }

   @Test
   public void uniikitPerheenjäsenetTest()
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
