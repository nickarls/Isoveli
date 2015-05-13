package fi.budokwai.isoveli.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import org.icefaces.apache.commons.io.output.ByteArrayOutputStream;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

public class Tilapäiskortti
{
   public static byte[] teeKortti(String nimi, String viivakoodi) throws IOException
   {
      BufferedImage kortti = lataaKorttipohja();
      Graphics2D kangas = alustaKangas(kortti);
      kirjoitaNimi(nimi, kangas);
      kirjoitaViivakoodi(viivakoodi, kangas);
      return tallennaKuva(kortti);
   }

   private static byte[] tallennaKuva(BufferedImage kortti) throws FileNotFoundException, IOException
   {
      ByteArrayOutputStream tulos = new ByteArrayOutputStream();
      ImageWriter kirjoittaja = ImageIO.getImageWritersByFormatName("jpeg").next();
      ImageWriteParam parametrit = kirjoittaja.getDefaultWriteParam();
      parametrit.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
      parametrit.setCompressionQuality(1.0F);
      kirjoittaja.setOutput(tulos);
      kirjoittaja.write(null, new IIOImage(kortti, null, null), parametrit);
      kirjoittaja.dispose();
      return tulos.toByteArray();
   }

   private static void kirjoitaViivakoodi(String viivakoodi, Graphics2D kangas)
   {
      BitmapCanvasProvider canvasProvider = new BitmapCanvasProvider(96, BufferedImage.TYPE_BYTE_BINARY, false, 0);
      Code128Bean bean = new Code128Bean();
      bean.setBarHeight(15);
      bean.setMsgPosition(HumanReadablePlacement.HRP_BOTTOM);
      bean.setQuietZone(10);
      bean.generateBarcode(canvasProvider, viivakoodi);
      kangas.drawImage(canvasProvider.getBufferedImage(), 500, 450, null);
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

   private static BufferedImage lataaKorttipohja() throws IOException
   {
      return ImageIO.read(DBExport.class.getClassLoader().getResource("lajikokeilukortti.jpg").openStream());
   }

   public static void main(String... x) throws IOException
   {
      teeKortti("Nicklas Karlsson", "123123");
   }

}
