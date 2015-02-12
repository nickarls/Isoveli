package fi.budokwai.isoveli.admin;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import fi.budokwai.isoveli.malli.BlobData;
import fi.budokwai.isoveli.malli.Harrastaja;

@Named
public class RaporttiAdmin
{
   @Inject
   private EntityManager entityManager;

   public BlobData muodostaJäsenkortit() throws RowsExceededException, WriteException, IOException
   {
      List<Harrastaja> harrastajat = entityManager.createNamedQuery("harrastajat", Harrastaja.class).getResultList();
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ZipOutputStream zos = new ZipOutputStream(bos);
      addToZipFile("jäsenrekisteri.xls", muodostaExcel(harrastajat), zos);
      for (Harrastaja harrastaja : harrastajat)
      {
         if (harrastaja.isKuvallinen())
         {
            BufferedImage kuva = ImageIO.read(new ByteArrayInputStream(harrastaja.getKuva().getTieto()));
            ByteArrayOutputStream png = new ByteArrayOutputStream();
            ImageIO.write(kuva, "png", png);
            addToZipFile(String.format("%s.png", harrastaja.getNimi()), png.toByteArray(), zos);
         }
      }
      zos.close();
      bos.close();
      SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
      String nimi = String.format("jäsenkortit-%s", sdf.format(new Date()));
      return BlobData.ZIP(nimi, bos.toByteArray());
   }

   public static void addToZipFile(String nimi, byte[] tieto, ZipOutputStream zos) throws FileNotFoundException,
      IOException
   {
      ZipEntry zipEntry = new ZipEntry(nimi);
      zos.putNextEntry(zipEntry);
      zos.write(tieto);
      zos.closeEntry();
   }

   private byte[] muodostaExcel(List<Harrastaja> harrastajat) throws IOException, RowsExceededException, WriteException
   {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      WritableWorkbook työkirja = Workbook.createWorkbook(bos);
      WritableSheet sivu = työkirja.createSheet("jäsenkortit", 0);
      int rivi = 0;
      for (Harrastaja harrastaja : harrastajat)
      {
         sivu.addCell(new Label(0, rivi, harrastaja.getNimi()));
         sivu.addCell(new Label(1, rivi, harrastaja.getJäsennumero()));
         // if (harrastaja.isKuvallinen())
         // {
         // BufferedImage kuva = ImageIO.read(new
         // ByteArrayInputStream(harrastaja.getKuva()));
         // ImageIO.write(kuva, "png",
         // new FileOutputStream(new File(kansio, String.format("%s.png",
         // harrastaja.getNimi()))));
         // }
         rivi++;
      }
      työkirja.write();
      työkirja.close();
      byte[] tieto = bos.toByteArray();
      bos.close();
      return tieto;
   }

}
