package fi.budokwai.isoveli;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import javax.imageio.ImageIO;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.jäsenkortti.Alue;
import fi.budokwai.isoveli.malli.jäsenkortti.Jäsenkortti;
import fi.budokwai.isoveli.malli.jäsenkortti.Kuva;
import fi.budokwai.isoveli.malli.jäsenkortti.Muodostustyö;
import fi.budokwai.isoveli.malli.jäsenkortti.Piste;
import fi.budokwai.isoveli.malli.jäsenkortti.Teksti;
import fi.budokwai.isoveli.malli.jäsenkortti.Viivakoodi;

@Named
public class RaporttiAdmin
{
   @PersistenceContext
   private EntityManager entityManager;

   public void muodostaJäsenkortit() throws RowsExceededException, WriteException, IOException
   {
      File kansio = new File("c:/temp/jäsenkortit");
      List<Harrastaja> harrastajat = entityManager.createNamedQuery("harrastajat", Harrastaja.class).getResultList();
      WritableWorkbook työkirja = Workbook.createWorkbook(new File(kansio, "jäsenkortit.xls"));
      WritableSheet sivu = työkirja.createSheet("jäsenkortit", 0);
      int rivi = 0;
      for (Harrastaja harrastaja : harrastajat)
      {
         sivu.addCell(new Label(0, rivi, harrastaja.getNimi()));
         sivu.addCell(new Label(1, rivi, harrastaja.getJäsennumero()));
         if (harrastaja.isKuvallinen())
         {
            BufferedImage kuva = ImageIO.read(new ByteArrayInputStream(harrastaja.getKuva()));
            ImageIO.write(kuva, "png",
               new FileOutputStream(new File(kansio, String.format("%s.png", harrastaja.getNimi()))));
         }
         rivi++;
      }
      työkirja.write();
      työkirja.close();
      // Muodostustyö muodostustyö = Muodostustyö.hakemistoon(kansio, "png");
      // harrastajat.forEach(h -> muodosta(h, muodostustyö));
   }

   private void muodosta(Harrastaja harrastaja, Muodostustyö muodostustyö)
   {
      BufferedImage etusivu = muodostustyö.uusiEtusivu();
      Graphics2D kangas = (Graphics2D) etusivu.getGraphics();
      Jäsenkortti jäsenkortti = muodostustyö.getJäsenkortti();
      jäsenkortti.getKuvat().forEach(kuva -> piirräKuva(harrastaja, kuva, kangas, muodostustyö.getHakemisto()));
      jäsenkortti.getViivakoodit().forEach(viivakoodi -> piirräViivakoodi(harrastaja, viivakoodi, kangas));
      jäsenkortti.getTekstit().forEach(teksti -> piirräTeksti(harrastaja, teksti, kangas));
      muodostustyö.tallenna(etusivu, harrastaja.getId());
   }

   private void piirräTeksti(Harrastaja harrastaja, Teksti teksti, Graphics2D kangas)
   {
      Font fontti = new Font(teksti.getFontti().getNimi(), Font.PLAIN, teksti.getFontti().getKoko());
      kangas.setFont(fontti);
      Color väri = haeVäri(teksti.getFontti().getVäri());
      kangas.setColor(väri);
      String lopputeksti = "$NIMI".equals(teksti.getTeksti()) ? harrastaja.getNimi() : teksti.getTeksti();
      kangas.drawString(lopputeksti, teksti.getAlue().getYlävasen().getX(), teksti.getAlue().getYlävasen().getY());
   }

   private Color haeVäri(String väri)
   {
      Field kenttä = null;
      try
      {
         kenttä = Color.class.getField(väri);
         return (Color) kenttä.get(null);
      } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
      {
         return Color.BLACK;
      }
   }

   private void piirräViivakoodi(Harrastaja harrastaja, Viivakoodi viivakoodi, Graphics2D kangas)
   {
      AbstractBarcodeBean viivakoodipapu = new Code39Bean();
      switch (viivakoodi.getTyyppi())
      {
      case Code39:
         viivakoodipapu = new Code39Bean();
      }
      BitmapCanvasProvider viivakoodikangas = new BitmapCanvasProvider(viivakoodi.getDpi(),
         BufferedImage.TYPE_BYTE_BINARY, false, 0);
      String loppukoodi = "$KORTTINUMERO".equals(viivakoodi.getData()) ? harrastaja.getKorttinumero() : viivakoodi
         .getData();
      if (loppukoodi == null)
      {
         return;
      }
      viivakoodipapu.generateBarcode(viivakoodikangas, loppukoodi);
      Alue alue = viivakoodi.getAlue();
      Piste ylävasen = alue.getYlävasen();
      kangas.drawImage(viivakoodikangas.getBufferedImage(), ylävasen.getX(), ylävasen.getY(), alue.getLeveys(),
         alue.getKorkeus(), null);
   }

   private void piirräKuva(Harrastaja harrastaja, Kuva kuva, Graphics2D kangas, File hakemisto)
   {
      BufferedImage loppukuva = null;
      if ("$KUVA".equals(kuva.getTiedostonimi()))
      {
         if (!harrastaja.isKuvallinen())
         {
            return;
         }
         try
         {
            loppukuva = ImageIO.read(new ByteArrayInputStream(harrastaja.getKuva()));
         } catch (IOException e)
         {
            e.printStackTrace();
            return;
         }
      } else
      {
         try
         {
            loppukuva = ImageIO.read(new File(hakemisto, kuva.getTiedostonimi()));
         } catch (IOException e)
         {
            e.printStackTrace();
         }
      }
      Alue alue = kuva.getAlue();
      Piste ylävasen = alue.getYlävasen();
      kangas.drawImage(loppukuva, ylävasen.getX(), ylävasen.getY(), alue.getLeveys(), alue.getKorkeus(), null);
   }

}
