package fi.budokwai.isoveli.util;

import java.util.Iterator;

import javax.enterprise.event.Observes;
import javax.faces.application.FacesMessage;
import javax.faces.event.PhaseEvent;

import org.apache.deltaspike.jsf.api.listener.phase.BeforePhase;
import org.apache.deltaspike.jsf.api.listener.phase.JsfPhaseId;
import org.icefaces.util.JavaScriptRunner;

public class GrowlPhaseListener
{
   public void convertMessages(@Observes @BeforePhase(JsfPhaseId.RENDER_RESPONSE) PhaseEvent event)
   {
      Iterator<FacesMessage> messages = event.getFacesContext().getMessages(null);
      while (messages.hasNext())
      {
         FacesMessage message = messages.next();
         if (message.getSeverity() == FacesMessage.SEVERITY_ERROR)
         {
            String script = String.format("$.growl.error({ title: \"\", message: \"%s\", duration: 5000 });",
               message.getSummary());
            JavaScriptRunner.runScript(event.getFacesContext(), script);
         } else if (message.getSeverity() == FacesMessage.SEVERITY_INFO)
         {
            String script = String.format("$.growl.notice({ title: \"\", message: \"%s\", duration: 5000 });",
               message.getSummary());
            JavaScriptRunner.runScript(event.getFacesContext(), script);
         }
      }
   }

}
