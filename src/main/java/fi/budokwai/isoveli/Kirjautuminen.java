package fi.budokwai.isoveli;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.faces.event.PhaseEvent;
import javax.inject.Named;

import org.apache.deltaspike.jsf.api.listener.phase.AfterPhase;
import org.apache.deltaspike.jsf.api.listener.phase.JsfPhaseId;

import fi.budokwai.isoveli.malli.Henkilö;
import fi.budokwai.isoveli.util.Kirjautunut;

@Stateful
@SessionScoped
public class Kirjautuminen
{
   private Henkilö kirjautunutHenkilö = Henkilö.EI_KIRJAUTUNUT;

   @Produces
   @Kirjautunut
   @Named
   public Henkilö getHenkilö()
   {
      return kirjautunutHenkilö;
   }

   public void tarkistaKirjautuminen(@Observes @AfterPhase(JsfPhaseId.RESTORE_VIEW) PhaseEvent e)
   {
      String näkymä = e.getFacesContext().getViewRoot().getViewId();
      if (!näkymä.endsWith("kirjatuminen.xhtml") && kirjautunutHenkilö == Henkilö.EI_KIRJAUTUNUT)
      {
         e.getFacesContext().getApplication().getNavigationHandler()
            .handleNavigation(e.getFacesContext(), null, "kirjautuminen.xhtml");
         e.getFacesContext().renderResponse();
      }
   }
}
