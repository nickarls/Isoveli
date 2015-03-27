package fi.budokwai.isoveli.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.io.FileUtils;

import com.google.common.io.Files;

import fi.budokwai.isoveli.Asetukset;
import fi.budokwai.isoveli.IsoveliPoikkeus;
import fi.budokwai.isoveli.malli.BlobData;

public class PrintManager
{

   @Inject
   private Asetukset asetukset;

   @Inject
   private Loggaaja loggaaja;

   public void tulostaPDFt(List<BlobData> tiedostot)
   {
      File temppihakemisto = Files.createTempDir();
      try
      {
         List<File> pdfTiedostot = tallennaPDFt(tiedostot, temppihakemisto);
         for (File pdf : pdfTiedostot)
         {
            CommandLine komento = getCommandLine(pdf);
            suorita(komento, temppihakemisto);
         }
         loggaaja.loggaa("Tulosti %d tiedostoa tulostimelle '%s'", tiedostot.size(), asetukset.getTulostin());
      } catch (IOException | InterruptedException e)
      {
         throw new IsoveliPoikkeus(String.format("Virhe PDF-tulostuksessa", e));
      } finally
      {
         try
         {
            FileUtils.forceDelete(temppihakemisto);
         } catch (IOException e)
         {
            throw new IsoveliPoikkeus(String.format("Temppihakemiston '%s' poistaminen epäonnistui",
               temppihakemisto.getAbsolutePath()), e);
         }
      }
   }

   private List<File> tallennaPDFt(List<BlobData> pdft, File temppihakemisto) throws IOException
   {
      List<File> pdfTiedostot = new ArrayList<>();
      for (BlobData pdf : pdft)
      {
         File pdfTiedosto = File.createTempFile("lasku", ".pdf", temppihakemisto);
         FileUtils.writeByteArrayToFile(pdfTiedosto, pdf.getTieto());
         pdfTiedostot.add(pdfTiedosto);
      }
      return pdfTiedostot;
   }

   private void suorita(CommandLine komento, File temppihakemisto) throws ExecuteException, IOException,
      InterruptedException
   {
      DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
      ExecuteWatchdog watchdog = new ExecuteWatchdog(60 * 1000);
      Executor executor = new DefaultExecutor();
      executor.setExitValue(1);
      executor.setWatchdog(watchdog);
      executor.setWorkingDirectory(temppihakemisto);
      executor.execute(komento, resultHandler);
      resultHandler.waitFor();
   }

   private CommandLine getCommandLine(File file)
   {
      CommandLine commandLine = new CommandLine(asetukset.getGhostScript());
      commandLine.addArgument("-dPrinted");
      commandLine.addArgument("-dBATCH");
      commandLine.addArgument("-dNOPAUSE");
      commandLine.addArgument("-dNOSAFER");
      commandLine.addArgument("-q");
      commandLine.addArgument(String.format("-dNumCopies=%d", 1));
      commandLine.addArgument("-sDEVICE=ljet4");
      commandLine.addArgument(String.format("-sOutputFile=%s", asetukset.getTulostin()));
      commandLine.addArgument(file.getAbsolutePath());
      return commandLine;
   }
}
