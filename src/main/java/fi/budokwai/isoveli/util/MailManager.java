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

   public void lähetäSähköposti(String osoite, String otsikko, String teksti)
   {
      MimeMessage viesti = new MimeMessage(mailiSessio);
      try
      {
         viesti.setRecipients(Message.RecipientType.TO, osoite);
         viesti.setFrom(new InternetAddress(asetukset.getSähköposti()));
         viesti.setSubject(otsikko);
         viesti.setSentDate(new java.util.Date());
         viesti.setText(teksti, "iso-8859-1", "html");
         Transport.send(viesti);
         loggaaja.loggaa("Lähetti sähköpostin '%s' osoitteeseen %s", otsikko, osoite);
      } catch (MessagingException e)
      {
         throw new IsoveliPoikkeus("Sähköpostin lähetys epäonnistui", e);
      }
   }

   public void lähetäSähköposti(String osoite, String otsikko, String teksti, BlobData liite)
   {
      try
      {
         MimeBodyPart tekstiosa = new MimeBodyPart();
         tekstiosa.setText(teksti);
         DataSource tietolähde = new ByteArrayDataSource(liite.getTieto(), liite.getTyyppi().getMimetyyppi());
         MimeBodyPart liiteosa = new MimeBodyPart();
         liiteosa.setDataHandler(new DataHandler(tietolähde));
         liiteosa.setFileName(liite.getNimi());
         MimeMultipart mimeosa = new MimeMultipart();
         mimeosa.addBodyPart(tekstiosa);
         mimeosa.addBodyPart(liiteosa);
         InternetAddress lähettäjä = new InternetAddress(asetukset.getSähköposti());
         InternetAddress vastaanottaja = new InternetAddress(osoite);
         MimeMessage viesti = new MimeMessage(mailiSessio);
         viesti.setSender(lähettäjä);
         viesti.setSubject(otsikko);
         viesti.setRecipient(Message.RecipientType.TO, vastaanottaja);
         viesti.setContent(mimeosa);
         Transport.send(viesti);
         loggaaja.loggaa("Lähetti sähköpostin '%s' (liite '%s') osoitteeseen %s", otsikko, liite.getNimi(), osoite);
      } catch (Exception e)
      {
         throw new IsoveliPoikkeus(String.format("Viestin lähettäminen osoitteeseen %s epäonnistui", osoite), e);
      }

   }

}
