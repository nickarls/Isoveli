package fi.budokwai.isoveli.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.apache.deltaspike.core.api.exception.control.ExceptionHandler;
import org.apache.deltaspike.core.api.exception.control.Handles;
import org.apache.deltaspike.core.api.exception.control.event.ExceptionEvent;
import org.jboss.logging.Logger;

@ExceptionHandler
@SessionScoped
@Named("exceptionHandler")
public class DSExceptionHandler implements Serializable
{
   private static final long serialVersionUID = 1L;

   @Inject
   private Logger logger;

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

   public static String stackTrace(Throwable aThrowable)
   {
      final Writer result = new StringWriter();
      final PrintWriter printWriter = new PrintWriter(result);
      aThrowable.printStackTrace(printWriter);
      return result.toString();
   }

}
