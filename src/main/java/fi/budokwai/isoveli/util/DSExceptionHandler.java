package fi.budokwai.isoveli.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.net.ConnectException;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;

import org.apache.deltaspike.core.api.exception.control.ExceptionHandler;
import org.apache.deltaspike.core.api.exception.control.Handles;
import org.apache.deltaspike.core.api.exception.control.event.ExceptionEvent;
import org.h2.jdbc.JdbcSQLException;
import org.jboss.logging.Logger;

import fi.budokwai.isoveli.IsoveliPoikkeus;
import fi.budokwai.isoveli.admin.Perustoiminnallisuus;

@ExceptionHandler
@SessionScoped
@Named("exceptionHandler")
public class DSExceptionHandler extends Perustoiminnallisuus implements Serializable
{
   private static final long serialVersionUID = 1L;

   @Inject
   private Logger logger;

   @Inject
   private Loggaaja loggaaja;

   public void tapaSessio()
   {
      loggaaja.loggaa("Kirjautui ulos");
      HttpSession sessio = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
      sessio.invalidate();
      try
      {
         FacesContext.getCurrentInstance().getExternalContext().redirect("/Isoveli");
      } catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   void handleValidationException(@Handles ExceptionEvent<ConstraintViolationException> e)
   {
      virhe(e.getException().getMessage());
      e.handled();
   }

   void handleIsovelipoikkeus(@Handles ExceptionEvent<IsoveliPoikkeus> e)
   {
      logger.error("Virhe", e.getException());
      virhe(e.getException().getMessage());
      e.handled();
   }

   void handleNoDB(@Handles ExceptionEvent<ConnectException> e)
   {
      e.handled();
      try
      {
         FacesContext.getCurrentInstance().getExternalContext().redirect("/Isoveli/eikantaa.xhtml");
      } catch (IOException x)
      {
      }
   }

   void handleException(@Handles ExceptionEvent<Throwable> e)
   {
      logger.error("Virhe", e.getException());
      HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
      session.setAttribute("exception", e.getException());
      try
      {
         FacesContext.getCurrentInstance().getExternalContext().redirect("/Isoveli/virhe.xhtml");
      } catch (IOException e1)
      {
         e1.printStackTrace();
      }
      e.handled();
   }

   void handleUniqueConstraintViolatedExpired(@Handles ExceptionEvent<JdbcSQLException> e)
   {
      String viesti = e.getException().getMessage();
      if (viesti.startsWith("Unique"))
      {
         int a = viesti.indexOf(":") + 3;
         int b = viesti.indexOf(" ", a);
         String indeksi = viesti.substring(a, b);
         virhe("Tietue on jo olemassa (%s)", indeksi);
         e.handled();
      }
   }

   void handleViewExpired(@Handles ExceptionEvent<ViewExpiredException> e)
   {
      e.handled();
   }

   public static String stackTrace(Throwable aThrowable)
   {
      final Writer result = new StringWriter();
      final PrintWriter printWriter = new PrintWriter(result);
      aThrowable.printStackTrace(printWriter);
      return result.toString();
   }

}
