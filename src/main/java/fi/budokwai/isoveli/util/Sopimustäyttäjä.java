package fi.budokwai.isoveli.util;

import java.io.IOException;
import java.util.Map;

import org.icefaces.apache.commons.io.output.ByteArrayOutputStream;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

import fi.budokwai.isoveli.IsoveliPoikkeus;

public class Sopimustäyttäjä
{

   public static byte[] täytä(byte[] malli, Map<String, String> arvot)
   {
      PdfReader lukija;
      try
      {
         lukija = new PdfReader(malli);
      } catch (IOException e)
      {
         throw new IsoveliPoikkeus("Mallin luku lomaketäytössä epäonnistui", e);
      }
      ByteArrayOutputStream tulos = new ByteArrayOutputStream();
      PdfStamper kirjoittaja;
      try
      {
         kirjoittaja = new PdfStamper(lukija, tulos);
      } catch (DocumentException | IOException e)
      {
         throw new IsoveliPoikkeus("PDF-kirjoittajan alustus lomaketäytössä epäonnistui", e);
      }
      AcroFields kentät = kirjoittaja.getAcroFields();
      for (Map.Entry<String, String> rivi : arvot.entrySet())
      {
         try
         {
            kentät.setField(rivi.getKey(), rivi.getValue());
         } catch (IOException | DocumentException e)
         {
            throw new IsoveliPoikkeus(
               String.format("Kentän '%s' asettaminen arvoon '%s' epäonnistui", rivi.getKey(), rivi.getValue()), e);
         }
      }
      try
      {
         kirjoittaja.close();
      } catch (DocumentException | IOException e)
      {
         throw new IsoveliPoikkeus("PDF-kirjoittajan sulkeminen lomakekäytössä epäonnistui", e);
      }
      lukija.close();
      return tulos.toByteArray();
   }

}
