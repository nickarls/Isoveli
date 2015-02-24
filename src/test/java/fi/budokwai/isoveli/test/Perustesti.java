package fi.budokwai.isoveli.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Henkilˆ;
import fi.budokwai.isoveli.malli.Lasku;
import fi.budokwai.isoveli.malli.Laskurivi;
import fi.budokwai.isoveli.malli.Perhe;
import fi.budokwai.isoveli.malli.Sopimus;
import fi.budokwai.isoveli.malli.Sopimuslasku;
import fi.budokwai.isoveli.malli.Sopimustyyppi;
import fi.budokwai.isoveli.malli.Vyˆarvo;
import fi.budokwai.isoveli.malli.Vyˆkoe;

public class Perustesti
{
   @Deployment
   public static WebArchive createDeployment()
   {
      return ShrinkWrap
         .create(WebArchive.class)
         .addPackages(true, "fi.budokwai.isoveli")
         .addAsWebInfResource("beans.xml", "beans.xml")
         .addAsResource("persistence.xml", "META-INF/persistence.xml")
         .addAsResource("laskupohja.pdf", "laskupohja.pdf")
         .addAsLibraries(
            Maven
               .resolver()
               .loadPomFromFile("pom.xml")
               .resolve("org.icefaces:icefaces-ace:4.0.0", "net.sourceforge.jexcelapi:jxl:2.6.12",
                  "org.apache.deltaspike.core:deltaspike-core-impl:1.1.0", "com.google.guava:guava:18.0",
                  "com.lowagie:itext:2.1.7").withTransitivity().asFile());
   }

   protected Sopimuslasku teeSopimuslasku(Sopimus sopimus) throws ParseException
   {
      Sopimuslasku sopimuslasku = new Sopimuslasku();
      sopimuslasku.setSopimus(sopimus);
      sopimus.getSopimuslaskut().add(sopimuslasku);
      Lasku lasku = new Lasku();
      Laskurivi laskurivi = new Laskurivi();
      lasku.getLaskurivit().add(laskurivi);
      laskurivi.setLasku(lasku);
      sopimuslasku.setLaskurivi(laskurivi);
      return sopimuslasku;
   }

   protected void teeVyˆkoe(Harrastaja harrastaja, String koska, String vyˆarvoNimi, int j‰rjestys)
   {
      Vyˆkoe vyˆkoe = new Vyˆkoe();
      Vyˆarvo vyˆarvo = new Vyˆarvo();
      vyˆarvo.setNimi(vyˆarvoNimi);
      vyˆarvo.setJ‰rjestys(j‰rjestys);
      vyˆkoe.setVyˆarvo(vyˆarvo);
      vyˆkoe.setHarrastaja(harrastaja);
      try
      {
         vyˆkoe.setP‰iv‰(new SimpleDateFormat("dd.MM.yyyy").parse(koska));
      } catch (ParseException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      harrastaja.getVyˆkokeet().add(vyˆkoe);
   }

   protected Henkilˆ teeHenkilˆ(String nimi)
   {
      Henkilˆ henkilˆ = new Henkilˆ();
      asetaNimi(henkilˆ, nimi);
      return henkilˆ;
   }

   protected void asetaNimi(Henkilˆ henkilˆ, String nimi)
   {
      String[] nimiosat = nimi.split(" ");
      henkilˆ.setEtunimi(nimiosat[0]);
      henkilˆ.setSukunimi(nimiosat[1]);
   }

   protected Perhe teePerhe(String nimi, Henkilˆ... perheenj‰senet)
   {
      Perhe perhe = new Perhe();
      perhe.setNimi(nimi);
      Arrays.asList(perheenj‰senet).stream().forEach(p -> perhe.lis‰‰Perheenj‰sen(p));
      return perhe;
   }

   protected Harrastaja teeAlaik‰inenHarrastaja(String nimi)
   {
      return teeHarrastaja(nimi, "01.01.2010");
   }

   protected Harrastaja teeHarrastaja(String nimi, String pvm)
   {
      Harrastaja harrastaja = new Harrastaja();
      asetaNimi(harrastaja, nimi);
      try
      {
         harrastaja.setSyntynyt(new SimpleDateFormat("dd.MM.yyyy").parse(pvm));
      } catch (ParseException e)
      {
         e.printStackTrace();
      }
      return harrastaja;
   }

   protected Harrastaja teeT‰ysiik‰inenHarrastaja(String nimi)
   {
      return teeHarrastaja(nimi, "01.01.1970");
   }

   protected Sopimus teeHarjoittelusopimus(Harrastaja harrastaja, String luotu, int maksuv‰li)
   {
      Sopimus sopimus = new Sopimus();
      Sopimustyyppi sopimustyyppi = new Sopimustyyppi();
      sopimustyyppi.setLaskutettava(true);
      sopimustyyppi.setHinta(100);
      sopimustyyppi.setNimi("Harjoittelumaksu");
      sopimustyyppi.setHarjoittelumaksu(true);
      sopimus.setTyyppi(sopimustyyppi);
      try
      {
         sopimus.setLuotu(new SimpleDateFormat("dd.MM.yyyy").parse(luotu));
      } catch (ParseException e)
      {
         e.printStackTrace();
      }
      sopimus.setHarrastaja(harrastaja);
      sopimus.setMaksuv‰li(maksuv‰li);
      harrastaja.getSopimukset().add(sopimus);
      return sopimus;
   }

   protected Sopimus teeKertamaksusopimus(Harrastaja harrastaja, String luotu)
   {
      Sopimus sopimus = new Sopimus();
      Sopimustyyppi sopimustyyppi = new Sopimustyyppi();
      sopimustyyppi.setLaskutettava(true);
      sopimustyyppi.setHarjoittelumaksu(true);
      sopimustyyppi.setHinta(6);
      sopimustyyppi.setOletusTreenikerrat(10);
      sopimustyyppi.setNimi("Kertamaksu");
      sopimustyyppi.setTreenikertoja(true);
      sopimus.setTyyppi(sopimustyyppi);
      sopimus.setTreenikertoja(10);
      try
      {
         sopimus.setLuotu(new SimpleDateFormat("dd.MM.yyyy").parse(luotu));
      } catch (ParseException e)
      {
         e.printStackTrace();
      }
      sopimus.setHarrastaja(harrastaja);
      harrastaja.getSopimukset().add(sopimus);
      return sopimus;
   }

   protected Sopimus teeAlkeiskurssisopimus(Harrastaja harrastaja, String luotu, String umpeutuu)
   {
      Sopimus sopimus = new Sopimus();
      Sopimustyyppi sopimustyyppi = new Sopimustyyppi();
      sopimustyyppi.setLaskutettava(true);
      sopimustyyppi.setHinta(200);
      sopimustyyppi.setNimi("Alkeiskurssi");
      sopimustyyppi.setAlkeiskurssi(true);
      sopimus.setTyyppi(sopimustyyppi);
      try
      {
         sopimus.setLuotu(new SimpleDateFormat("dd.MM.yyyy").parse(luotu));
         sopimus.setUmpeutuu(new SimpleDateFormat("dd.MM.yyyy").parse(umpeutuu));
      } catch (ParseException e)
      {
         e.printStackTrace();
      }
      sopimus.setHarrastaja(harrastaja);
      harrastaja.getSopimukset().add(sopimus);
      return sopimus;
   }

   protected Sopimus teeJ‰senmaksusopimus(Harrastaja harrastaja, String luotu)
   {
      Sopimus sopimus = new Sopimus();
      Sopimustyyppi sopimustyyppi = new Sopimustyyppi();
      sopimustyyppi.setLaskutettava(true);;
      sopimustyyppi.setHinta(10);
      sopimustyyppi.setOletusMaksuv‰li(12);
      sopimustyyppi.setNimi("J‰senmaksu");
      sopimustyyppi.setJ‰senmaksu(true);
      sopimus.setTyyppi(sopimustyyppi);
      try
      {
         sopimus.setLuotu(new SimpleDateFormat("dd.MM.yyyy").parse(luotu));
      } catch (ParseException e)
      {
         e.printStackTrace();
      }
      sopimus.setHarrastaja(harrastaja);
      harrastaja.getSopimukset().add(sopimus);
      return sopimus;
   }

}
