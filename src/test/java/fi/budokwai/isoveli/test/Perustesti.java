package fi.budokwai.isoveli.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Henkil�;
import fi.budokwai.isoveli.malli.Lasku;
import fi.budokwai.isoveli.malli.Laskurivi;
import fi.budokwai.isoveli.malli.Perhe;
import fi.budokwai.isoveli.malli.Sopimus;
import fi.budokwai.isoveli.malli.Sopimuslasku;
import fi.budokwai.isoveli.malli.Sopimustyyppi;
import fi.budokwai.isoveli.malli.Sukupuoli;
import fi.budokwai.isoveli.malli.Vy�arvo;
import fi.budokwai.isoveli.malli.Vy�koe;

public class Perustesti
{
   @Rule
   public ExpectedException exception = ExpectedException.none();

   @PersistenceContext
   protected EntityManager entityManager;

   @Deployment
   public static WebArchive createDeployment()
   {
      return ShrinkWrap
         .create(WebArchive.class)
         .addPackages(true, "fi.budokwai.isoveli")
         .addAsWebInfResource("beans.xml")
         .addAsWebInfResource("jboss-deployment-structure.xml")
         .addAsResource("persistence.xml", "META-INF/persistence.xml")
         .addAsResource("laskupohja.pdf", "laskupohja.pdf")
         .addAsResource("jasenrekisteri.xls", "jasenrekisteri.xls")
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

   protected Vy�koe teeVy�koe(Harrastaja harrastaja, String koska, String vy�arvoNimi, int j�rjestys, boolean poom,
      boolean dan)
   {
      Vy�koe vy�koe = new Vy�koe();
      Vy�arvo vy�arvo = new Vy�arvo();
      vy�arvo.setId(j�rjestys);
      vy�arvo.setNimi(vy�arvoNimi);
      vy�arvo.setJ�rjestys(j�rjestys);
      vy�arvo.setPoom(poom);
      vy�arvo.setDan(dan);
      vy�koe.setVy�arvo(vy�arvo);
      vy�koe.setHarrastaja(harrastaja);
      try
      {
         vy�koe.setP�iv�(new SimpleDateFormat("dd.MM.yyyy").parse(koska));
      } catch (ParseException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      harrastaja.lis��Vy�koe(vy�koe);
      return vy�koe;
   }

   protected Vy�arvo teeVy�arvo(String arvo)
   {
      return entityManager.createQuery("select v from Vy�arvo v where v.nimi=:nimi", Vy�arvo.class)
         .setParameter("nimi", arvo).getSingleResult();
   }

   protected Vy�koe teeVy�koe(Harrastaja harrastaja, String koska, String vy�arvoNimi, int j�rjestys)
   {
      return teeVy�koe(harrastaja, koska, vy�arvoNimi, j�rjestys, false, false);
   }

   protected Henkil� teeHenkil�(String nimi)
   {
      Henkil� henkil� = new Henkil�();
      asetaNimi(henkil�, nimi);
      return henkil�;
   }

   protected void asetaNimi(Henkil� henkil�, String nimi)
   {
      String[] nimiosat = nimi.split(" ");
      henkil�.setEtunimi(nimiosat[0]);
      henkil�.setSukunimi(nimiosat[1]);
   }

   protected Perhe teePerhe(String nimi, Henkil�... perheenj�senet)
   {
      Perhe perhe = new Perhe();
      perhe.setNimi(nimi);
      Arrays.asList(perheenj�senet).stream().forEach(p -> perhe.lis��Perheenj�sen(p));
      return perhe;
   }

   protected Harrastaja teeAlaik�inenHarrastaja(String nimi)
   {
      return teeHarrastaja(nimi, "01.01.2010");
   }

   protected Harrastaja teeHarrastaja(String nimi, String syntynyt)
   {
      Harrastaja harrastaja = new Harrastaja();
      asetaNimi(harrastaja, nimi);
      harrastaja.setSukupuoli(Sukupuoli.M);
      try
      {
         harrastaja.setSyntynyt(new SimpleDateFormat("dd.MM.yyyy").parse(syntynyt));
      } catch (ParseException e)
      {
         e.printStackTrace();
      }
      return harrastaja;
   }

   protected Harrastaja teeT�ysiik�inenHarrastaja(String nimi)
   {
      return teeHarrastaja(nimi, "01.01.1970");
   }

   protected Sopimus teeHarjoittelusopimus(Harrastaja harrastaja, String luotu, int maksuv�li)
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
      sopimus.setMaksuv�li(maksuv�li);
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
      sopimus.setTreenikertojaJ�ljell�(10);
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
      sopimustyyppi.setOletusKuukaudetVoimassa(3);
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

   protected Sopimus teeJ�senmaksusopimus(Harrastaja harrastaja, String luotu)
   {
      Sopimus sopimus = new Sopimus();
      Sopimustyyppi sopimustyyppi = new Sopimustyyppi();
      sopimustyyppi.setLaskutettava(true);;
      sopimustyyppi.setHinta(10);
      sopimustyyppi.setOletusMaksuv�li(12);
      sopimustyyppi.setNimi("J�senmaksu");
      sopimustyyppi.setJ�senmaksu(true);
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
