package fi.budokwai.isoveli.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import fi.budokwai.isoveli.IsoveliPoikkeus;
import fi.budokwai.isoveli.malli.BlobData;

public class Zippaaja
{
   private ByteArrayOutputStream bos = new ByteArrayOutputStream();
   private ZipOutputStream zos = new ZipOutputStream(bos);

   public Zippaaja()
   {
   }

   public void lis‰‰ZipTiedostoon(String nimi, byte[] tieto)
   {
      ZipEntry zipTiedosto = new ZipEntry(nimi);
      try
      {
         zos.putNextEntry(zipTiedosto);
         zos.write(tieto);
         zos.closeEntry();
      } catch (IOException e)
      {
         throw new IsoveliPoikkeus(String.format("Tiedoston %s lis‰ys ep‰onnistui", nimi), e);
      }
   }

   public BlobData haeZipTiedosto(String perusnimi)
   {
      try
      {
         zos.close();
         bos.close();
      } catch (IOException e)
      {
         throw new IsoveliPoikkeus("ZIP-muodostus ep‰onnistui", e);
      }
      SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
      String nimi = String.format("%s-%s", perusnimi, sdf.format(new Date()));
      return BlobData.ZIP(nimi, bos.toByteArray());
   }
}
