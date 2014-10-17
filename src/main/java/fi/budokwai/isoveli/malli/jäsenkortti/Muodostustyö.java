package fi.budokwai.isoveli.malli.jäsenkortti;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.thoughtworks.xstream.XStream;

import fi.budokwai.isoveli.Isovelipoikkeus;

public class Muodostustyö
{
   private static final String MÄÄRITYSTIEDOSTO = "jäsenkortti.xml";

   private String tallennustyyppi;
   private File hakemisto;
   private BufferedImage etusivu;
   private Jäsenkortti jäsenkortti;

   private Muodostustyö(File hakemisto, String tallennustyyppi)
   {
      this.hakemisto = hakemisto;
      this.tallennustyyppi = tallennustyyppi;
      XStream xstream = alustaXStream();
      try
      {
         jäsenkortti = (Jäsenkortti) xstream.fromXML(new File(hakemisto, MÄÄRITYSTIEDOSTO));
      } catch (Exception e)
      {
         throw new Isovelipoikkeus(String.format("Määritystiedosto %s ei löytynyt hakemistosta %s", MÄÄRITYSTIEDOSTO,
            hakemisto.getAbsolutePath()), e);
      }
      try
      {
         etusivu = ImageIO.read(new File(hakemisto, jäsenkortti.getEtusivunTiedostonimi()));
      } catch (IOException e)
      {
         throw new Isovelipoikkeus(String.format("Etusivu %s ei löytynyt hakemistosta %s",
            jäsenkortti.getEtusivunTiedostonimi(), hakemisto.getAbsolutePath()), e);
      }
   }

   public BufferedImage uusiEtusivu()
   {
      BufferedImage uusiKuva = new BufferedImage(etusivu.getWidth(), etusivu.getHeight(), etusivu.getType());
      Graphics kangas = uusiKuva.getGraphics();
      kangas.drawImage(etusivu, 0, 0, null);
      kangas.dispose();
      Graphics2D uusiKangas = (Graphics2D) uusiKuva.getGraphics();
      // uusiKangas.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      // uusiKangas.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
      // uusiKangas.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
      uusiKangas.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
      return uusiKuva;
   }

   public static XStream alustaXStream()
   {
      XStream xstream = new XStream();
      xstream.alias("jäsenkortti", Jäsenkortti.class);
      xstream.alias("kuva", Kuva.class);
      xstream.alias("teksti", Teksti.class);
      xstream.alias("viivakodi", Viivakoodi.class);
      xstream.alias("alue", Alue.class);
      xstream.alias("fontti", Fontti.class);
      return xstream;
   }

   public static void malli(File hakemisto)
   {
      Jäsenkortti jäsenkortti = new Jäsenkortti("etusivu.png");
      jäsenkortti.getKuvat().add(new Kuva("foo.jpg", new Alue(100, 100, 200, 200)));
      jäsenkortti.getTekstit().add(new Teksti("foo", new Fontti("Arial", 12, "WHITE"), new Alue(150, 150, 180, 300)));
      jäsenkortti.getViivakoodit().add(
         new Viivakoodi("0123", 300, new Alue(100, 100, 200, 200), Viivakoodityyppi.Code39));
      XStream xstream = alustaXStream();
      try
      {
         xstream.toXML(jäsenkortti, new FileOutputStream(new File(hakemisto, MÄÄRITYSTIEDOSTO)));
      } catch (FileNotFoundException e)
      {
         e.printStackTrace();
      }
   }

   public void tallenna(BufferedImage kuva, int id)
   {
      try
      {
         ImageIO.write(kuva, tallennustyyppi, new File(hakemisto, String.format("etusivu-%d.%s", id, tallennustyyppi)));
      } catch (IOException e)
      {
         throw new Isovelipoikkeus(String.format("Kuvan %d tallennus hakemistoon %s epäonnistui", id,
            hakemisto.getAbsolutePath()), e);
      }
   }

   public static Muodostustyö hakemistoon(File hakemisto, String tallennustyyppi)
   {
      return new Muodostustyö(hakemisto, tallennustyyppi);
   }

   public File getHakemisto()
   {
      return hakemisto;
   }

   public Jäsenkortti getJäsenkortti()
   {
      return jäsenkortti;
   }
}
