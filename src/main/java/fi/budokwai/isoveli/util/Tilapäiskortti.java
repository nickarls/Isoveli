package fi.budokwai.isoveli.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import org.icefaces.apache.commons.io.output.ByteArrayOutputStream;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;

import fi.budokwai.isoveli.IsoveliPoikkeus;

public class Tilap‰iskortti
{
   public static byte[] teeKortti(String nimi, String viivakoodi)
   {
      BufferedImage kortti = lataaKorttipohja();
      Graphics2D kangas = alustaKangas(kortti);
      kirjoitaNimi(nimi, kangas);
      kirjoitaViivakoodi(viivakoodi, kangas);
      byte[] jpg = tallennaKuva(kortti);
      return kuva2PDF(jpg);
   }

   private static byte[] tallennaKuva(BufferedImage kortti)
   {
      ByteArrayOutputStream tulos = new ByteArrayOutputStream();
      ImageWriter kirjoittaja = ImageIO.getImageWritersByFormatName("jpeg").next();
      ImageWriteParam parametrit = kirjoittaja.getDefaultWriteParam();
      parametrit.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
      parametrit.setCompressionQuality(1.0F);
      MemoryCacheImageOutputStream ulos = new MemoryCacheImageOutputStream(tulos);
      kirjoittaja.setOutput(ulos);
      try
      {
         kirjoittaja.write(null, new IIOImage(kortti, null, null), parametrit);
      } catch (IOException e)
      {
         throw new IsoveliPoikkeus("Kuvan tallennus ep‰onnistui", e);
      } finally
      {
         if (kirjoittaja != null)
         {
            kirjoittaja.dispose();
         }
      }
      return tulos.toByteArray();
   }

   private static void kirjoitaViivakoodi(String viivakoodi, Graphics2D kangas)
   {
      BitmapCanvasProvider canvasProvider = new BitmapCanvasProvider(96, BufferedImage.TYPE_BYTE_BINARY, false, 0);
      Code128Bean bean = new Code128Bean();
      bean.setBarHeight(15);
      bean.setModuleWidth(0.8f);
      bean.setMsgPosition(HumanReadablePlacement.HRP_BOTTOM);
      bean.setQuietZone(10);
      bean.generateBarcode(canvasProvider, viivakoodi);
      kangas.drawImage(canvasProvider.getBufferedImage(), 450, 450, null);
   }

   private static void kirjoitaNimi(String nimi, Graphics2D kangas)
   {
      kangas.setFont(new Font("Purisa", Font.PLAIN, 60));
      kangas.setColor(Color.BLACK);
      kangas.drawString(nimi, 10, 50);
   }

   private static Graphics2D alustaKangas(BufferedImage kortti)
   {
      Graphics2D kangas = (Graphics2D) kortti.getGraphics();
      RenderingHints piirtoparametrit = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
         RenderingHints.VALUE_ANTIALIAS_ON);
      piirtoparametrit.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      kangas.setRenderingHints(piirtoparametrit);
      return kangas;
   }

   private static BufferedImage lataaKorttipohja()
   {
      try
      {
         return ImageIO.read(DBExport.class.getClassLoader().getResource("lajikokeilukortti.jpg").openStream());
      } catch (IOException e)
      {
         throw new IsoveliPoikkeus("Korttipohjan lataus ep‰onnistui", e);
      }
   }

   private static byte[] kuva2PDF(byte[] kuva)
   {
      ByteArrayOutputStream tulos = new ByteArrayOutputStream();
      Document dokumentti = new Document();
      try
      {
         PdfWriter.getInstance(dokumentti, tulos);
      } catch (DocumentException e)
      {
         throw new IsoveliPoikkeus("PDF-kirjoittajan alustus ep‰onnistui", e);
      }
      dokumentti.open();
      Image kuvainstanssi;
      try
      {
         kuvainstanssi = Image.getInstance(kuva);
      } catch (BadElementException | IOException e)
      {
         throw new IsoveliPoikkeus("Kuvan alustus ep‰onnistui", e);
      }
      kuvainstanssi.scalePercent(30);
      try
      {
         dokumentti.add(kuvainstanssi);
      } catch (DocumentException e)
      {
         throw new IsoveliPoikkeus("Kuvan lis‰‰minen PDF:‰‰n ep‰onnistui", e);
      }
      dokumentti.close();
      return tulos.toByteArray();
   }

}
