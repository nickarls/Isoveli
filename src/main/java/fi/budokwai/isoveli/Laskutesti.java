package fi.budokwai.isoveli;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Henkilö;
import fi.budokwai.isoveli.malli.Lasku;
import fi.budokwai.isoveli.malli.Laskurivi;
import fi.budokwai.isoveli.malli.Osoite;
import fi.budokwai.isoveli.malli.Sopimus;
import fi.budokwai.isoveli.malli.Sopimuslasku;
import fi.budokwai.isoveli.malli.Sopimustyyppi;
import fi.budokwai.isoveli.util.Lasku2PDF;

public class Laskutesti
{

   public static void main(String[] args) throws IOException
   {
      byte[] pohja = haePohja();
      Lasku lasku = teeLasku();
      Asetukset asetukset = teeAsetukset();
      byte[] pdf = new Lasku2PDF(pohja, lasku, asetukset).muodosta();
      kirjoitaPdf(pdf);
   }

   private static Asetukset teeAsetukset()
   {
      Asetukset asetukset = new Asetukset();
      asetukset.setSaaja("Budokwai ry Taekwondo");
      asetukset.setYläotsikko("LASKU");
      asetukset.setIBAN("FI04 5711 6140 0501 84");
      asetukset.setBIC("OKOYFIHH");
      asetukset.setHuomio("Muista aina\nkäyttää viitenumeroa");
      asetukset.setYTunnus("1012368-8");
      asetukset.setALVtunnus("FI10123688");
      asetukset.setOsoite("Kirstinkatu 1");
      asetukset.setPostinumero("20100");
      asetukset.setKaupunki("Turku");
      asetukset.setPuhelin("040 596 0298");
      asetukset.setSähköposti("laskut@budokwai.fi");
      asetukset.setKotisivu("www.budokwai.fi/taekwondo");
      return asetukset;
   }

   private static Lasku teeLasku()
   {
      Lasku lasku = new Lasku();
      Henkilö henkilö = teeHenkilö();
      lasku.setHenkilö(henkilö);
      lasku.setViitenumero("123123123");
      lasku.setEräpäivä(new Date());
      Sopimustyyppi sopimustyyppi = new Sopimustyyppi();
      sopimustyyppi.setNimi("Harjoittelumaksu");
      sopimustyyppi.setMäärä(1);
      sopimustyyppi.setHinta(101);
      sopimustyyppi.setYksikkö("kpl");
      sopimustyyppi.setVerokanta(22);
      Sopimus sopimus = new Sopimus();
      Harrastaja harrastaja = teeHarrastaja();
      sopimus.setHarrastaja(harrastaja);
      sopimus.setTyyppi(sopimustyyppi);
      Sopimuslasku sopimuslasku = new Sopimuslasku();
      sopimuslasku.setSopimus(sopimus);
      sopimuslasku.setAlkaa(new Date());
      sopimuslasku.setPäättyy(new Date());
      Laskurivi laskurivi = new Laskurivi();
      laskurivi.setSopimuslasku(sopimuslasku);
      laskurivi.setRivinumero(1);
      lasku.getLaskurivit().add(laskurivi);

      Laskurivi laskurivi2 = new Laskurivi();
      laskurivi2.setSopimuslasku(sopimuslasku);
      laskurivi2.setRivinumero(2);
      lasku.getLaskurivit().add(laskurivi2);

      Laskurivi laskurivi3 = new Laskurivi();
      laskurivi3.setSopimuslasku(sopimuslasku);
      laskurivi3.setRivinumero(3);
      lasku.getLaskurivit().add(laskurivi3);

      Laskurivi laskurivi4 = new Laskurivi();
      laskurivi4.setSopimuslasku(sopimuslasku);
      laskurivi4.setRivinumero(4);
      lasku.getLaskurivit().add(laskurivi4);

      Laskurivi laskurivi5 = new Laskurivi();
      laskurivi5.setSopimuslasku(sopimuslasku);
      laskurivi5.setRivinumero(5);
      lasku.getLaskurivit().add(laskurivi5);
      return lasku;
   }

   private static Harrastaja teeHarrastaja()
   {
      Harrastaja harrastaja = new Harrastaja();
      harrastaja.setEtunimi("Nicklas");
      harrastaja.setSukunimi("Karlsson");
      return harrastaja;
   }

   private static Henkilö teeHenkilö()
   {
      Henkilö henkilö = new Henkilö();
      henkilö.setEtunimi("Nicklas");
      henkilö.setSukunimi("Karlsson");
      Osoite osoite = teeOsoite();
      henkilö.setOsoite(osoite);
      return henkilö;
   }

   private static Osoite teeOsoite()
   {
      Osoite osoite = new Osoite();
      osoite.setOsoite("Vaakunatie 10 as 7");
      osoite.setPostinumero("20780");
      osoite.setKaupunki("Kaarina");
      return osoite;
   }

   private static byte[] haePohja() throws IOException
   {
      return Files.readAllBytes(Paths.get("c:/temp/laskupohja.pdf"));
   }

   private static void kirjoitaPdf(byte[] pdf) throws IOException
   {
      FileOutputStream out = new FileOutputStream(File.createTempFile("lasku", ".pdf", new File("c:/temp")));
      out.write(pdf);
      out.flush();
      out.close();
   }

}
