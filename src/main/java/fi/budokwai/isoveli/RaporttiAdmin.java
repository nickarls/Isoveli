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
import fi.budokwai.isoveli.malli.j�senkortti.Alue;
import fi.budokwai.isoveli.malli.j�senkortti.J�senkortti;
import fi.budokwai.isoveli.malli.j�senkortti.Kuva;
import fi.budokwai.isoveli.malli.j�senkortti.Muodostusty�;
import fi.budokwai.isoveli.malli.j�senkortti.Piste;
import fi.budokwai.isoveli.malli.j�senkortti.Teksti;
import fi.budokwai.isoveli.malli.j�senkortti.Viivakoodi;

@Named
public class RaporttiAdmin
{
   @PersistenceContext
   private EntityManager entityManager;

   public void muodostaJ�senkortit() throws RowsExceededException, WriteException, IOException
   {
      File kansio = new File("c:/temp/j�senkortit");
      List<Harrastaja> harrastajat = entityManager.createNamedQuery("harrastajat", Harrastaja.class).getResultList();
      WritableWorkbook ty�kirja = Workbook.createWorkbook(new File(kansio, "j�senkortit.xls"));
      WritableSheet sivu = ty�kirja.createSheet("j�senkortit", 0);
      int rivi = 0;
      for (Harrastaja harrastaja : harrastajat)
      {
         sivu.addCell(new Label(0, rivi, harrastaja.getNimi()));
         sivu.addCell(new Label(1, rivi, harrastaja.getJ�sennumero()));
         if (harrastaja.isKuvallinen())
         {
            BufferedImage kuva = ImageIO.read(new ByteArrayInputStream(harrastaja.getKuva()));
            ImageIO.write(kuva, "png",
               new FileOutputStream(new File(kansio, String.format("%s.png", harrastaja.getNimi()))));
         }
         rivi++;
      }
      ty�kirja.write();
      ty�kirja.close();
      // Muodostusty� muodostusty� = Muodostusty�.hakemistoon(kansio, "png");
      // harrastajat.forEach(h -> muodosta(h, muodostusty�));
   }

   private void muodosta(Harrastaja harrastaja, Muodostusty� muodostusty�)
   {
      BufferedImage etusivu = muodostusty�.uusiEtusivu();
      Graphics2D kangas = (Graphics2D) etusivu.getGraphics();
      J�senkortti j�senkortti = muodostusty�.getJ�senkortti();
      j�senkortti.getKuvat().forEach(kuva -> piirr�Kuva(harrastaja, kuva, kangas, muodostusty�.getHakemisto()));
      j�senkortti.getViivakoodit().forEach(viivakoodi -> piirr�Viivakoodi(harrastaja, viivakoodi, kangas));
      j�senkortti.getTekstit().forEach(teksti -> piirr�Teksti(harrastaja, teksti, kangas));
      muodostusty�.tallenna(etusivu, harrastaja.getId());
   }

   private void piirr�Teksti(Harrastaja harrastaja, Teksti teksti, Graphics2D kangas)
   {
      Font fontti = new Font(teksti.getFontti().getNimi(), Font.PLAIN, teksti.getFontti().getKoko());
      kangas.setFont(fontti);
      Color v�ri = haeV�ri(teksti.getFontti().getV�ri());
      kangas.setColor(v�ri);
      String lopputeksti = "$NIMI".equals(teksti.getTeksti()) ? harrastaja.getNimi() : teksti.getTeksti();
      kangas.drawString(lopputeksti, teksti.getAlue().getYl�vasen().getX(), teksti.getAlue().getYl�vasen().getY());
   }

   private Color haeV�ri(String v�ri)
   {
      Field kentt� = null;
      try
      {
         kentt� = Color.class.getField(v�ri);
         return (Color) kentt�.get(null);
      } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
      {
         return Color.BLACK;
      }
   }

   private void piirr�Viivakoodi(Harrastaja harrastaja, Viivakoodi viivakoodi, Graphics2D kangas)
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
      Piste yl�vasen = alue.getYl�vasen();
      kangas.drawImage(viivakoodikangas.getBufferedImage(), yl�vasen.getX(), yl�vasen.getY(), alue.getLeveys(),
         alue.getKorkeus(), null);
   }

   private void piirr�Kuva(Harrastaja harrastaja, Kuva kuva, Graphics2D kangas, File hakemisto)
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
      Piste yl�vasen = alue.getYl�vasen();
      kangas.drawImage(loppukuva, yl�vasen.getX(), yl�vasen.getY(), alue.getLeveys(), alue.getKorkeus(), null);
   }

}
