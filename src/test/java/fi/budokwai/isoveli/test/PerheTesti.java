package fi.budokwai.isoveli.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Henkilö;
import fi.budokwai.isoveli.malli.Perhe;

public class PerheTesti extends Perustesti
{
   @Test
   public void testKuvaus() {
      Henkilö n = teeHenkilö("Nicklas Karlsson");
      Henkilö h = teeHenkilö("Heidi Karlsson");
      Perhe perhe = teePerhe("Karlsson", n, h);
      Assert.assertEquals("Karlsson (Nicklas, Heidi)", perhe.getKuvaus());
   }
   
   @Test
   public void getHuoltajat() {
      Henkilö n = teeHenkilö("Nicklas Karlsson");
      Harrastaja e = teeAlaikäinenHarrastaja("Emil Karlsson");
      Perhe p = teePerhe("Karlsson", n, e);
      List<Henkilö> huoltajat = p.getHuoltajat();
      Assert.assertEquals(1, huoltajat.size());
      Assert.assertEquals("Nicklas", huoltajat.iterator().next().getEtunimi());
   }
   
   @Test
   public void getHuoltajalettavat() {
      Henkilö n = teeHenkilö("Nicklas Karlsson");
      Harrastaja e = teeAlaikäinenHarrastaja("Emil Karlsson");
      e.setHuoltaja(n);
      Perhe p = teePerhe("Karlsson", n, e);
      List<Henkilö> huollettavat = p.getHuollettavat(n);
      Assert.assertEquals(1, huollettavat.size());
      Assert.assertEquals("Emil", huollettavat.iterator().next().getEtunimi());
   }
   
}
