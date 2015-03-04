package fi.budokwai.isoveli.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Henkil�;
import fi.budokwai.isoveli.malli.Perhe;

public class PerheTesti extends Perustesti
{
   @Test
   public void testKuvaus() {
      Henkil� n = teeHenkil�("Nicklas Karlsson");
      Henkil� h = teeHenkil�("Heidi Karlsson");
      Perhe perhe = teePerhe("Karlsson", n, h);
      Assert.assertEquals("Karlsson (Nicklas, Heidi)", perhe.getKuvaus());
   }
   
   @Test
   public void getHuoltajat() {
      Henkil� n = teeHenkil�("Nicklas Karlsson");
      Harrastaja e = teeAlaik�inenHarrastaja("Emil Karlsson");
      Perhe p = teePerhe("Karlsson", n, e);
      List<Henkil�> huoltajat = p.getHuoltajat();
      Assert.assertEquals(1, huoltajat.size());
      Assert.assertEquals("Nicklas", huoltajat.iterator().next().getEtunimi());
   }
   
   @Test
   public void getHuoltajalettavat() {
      Henkil� n = teeHenkil�("Nicklas Karlsson");
      Harrastaja e = teeAlaik�inenHarrastaja("Emil Karlsson");
      e.setHuoltaja(n);
      Perhe p = teePerhe("Karlsson", n, e);
      List<Henkil�> huollettavat = p.getHuollettavat(n);
      Assert.assertEquals(1, huollettavat.size());
      Assert.assertEquals("Emil", huollettavat.iterator().next().getEtunimi());
   }
   
}
