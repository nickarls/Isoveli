package fi.budokwai.isoveli.util;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.annotation.Resource;
import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import fi.budokwai.isoveli.Asetukset;
import fi.budokwai.isoveli.IsoveliPoikkeus;
import fi.budokwai.isoveli.malli.BlobData;

@Model
public class MailManager
{
   @Resource(name = "java:jboss/mail/Isoveli")
   private Session mailiSessio;

   @Inject
   private Asetukset asetukset;

   @Inject
   private Loggaaja loggaaja;

   public void l�het�S�hk�posti(String osoite, String otsikko, String teksti)
   {
      MimeMessage viesti = new MimeMessage(mailiSessio);
      try
      {
         viesti.setRecipients(Message.RecipientType.TO, osoite);
         viesti.setFrom(new InternetAddress(asetukset.getS�hk�posti()));
         viesti.setSubject(otsikko);
         viesti.setSentDate(new java.util.Date());
         viesti.setText(teksti, "iso-8859-1", "html");
         Transport.send(viesti);
         loggaaja.loggaa("L�hetti s�hk�postin '%s' osoitteeseen %s", otsikko, osoite);
      } catch (MessagingException e)
      {
         throw new IsoveliPoikkeus("S�hk�postin l�hetys ep�onnistui", e);
      }
   }

   public void l�het�S�hk�posti(String osoite, String otsikko, String teksti, BlobData liite)
   {
      try
      {
         MimeBodyPart tekstiosa = new MimeBodyPart();
         tekstiosa.setText(teksti);
         DataSource tietol�hde = new ByteArrayDataSource(liite.getTieto(), liite.getTyyppi().getMimetyyppi());
         MimeBodyPart liiteosa = new MimeBodyPart();
         liiteosa.setDataHandler(new DataHandler(tietol�hde));
         liiteosa.setFileName(liite.getNimi());
         MimeMultipart mimeosa = new MimeMultipart();
         mimeosa.addBodyPart(tekstiosa);
         mimeosa.addBodyPart(liiteosa);
         InternetAddress l�hett�j� = new InternetAddress(asetukset.getS�hk�posti());
         InternetAddress vastaanottaja = new InternetAddress(osoite);
         MimeMessage viesti = new MimeMessage(mailiSessio);
         viesti.setSender(l�hett�j�);
         viesti.setSubject(otsikko);
         viesti.setRecipient(Message.RecipientType.TO, vastaanottaja);
         viesti.setContent(mimeosa);
         Transport.send(viesti);
         loggaaja.loggaa("L�hetti s�hk�postin '%s' (liite '%s') osoitteeseen %s", otsikko, liite.getNimi(), osoite);
      } catch (Exception e)
      {
         throw new IsoveliPoikkeus(String.format("Viestin l�hett�minen osoitteeseen %s ep�onnistui", osoite), e);
      }

   }

}
