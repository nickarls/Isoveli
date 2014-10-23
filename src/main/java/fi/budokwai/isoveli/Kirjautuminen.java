package fi.budokwai.isoveli;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.faces.event.PhaseEvent;
import javax.inject.Named;

import org.apache.deltaspike.jsf.api.listener.phase.AfterPhase;
import org.apache.deltaspike.jsf.api.listener.phase.JsfPhaseId;

import fi.budokwai.isoveli.malli.Henkil�;
import fi.budokwai.isoveli.util.Kirjautunut;

@Stateful
@SessionScoped
public class Kirjautuminen
{
   private Henkil� kirjautunutHenkil� = Henkil�.EI_KIRJAUTUNUT;

   @Produces
   @Kirjautunut
   @Named
   public Henkil� getHenkil�()
   {
      return kirjautunutHenkil�;
   }

   public void tarkistaKirjautuminen(@Observes @AfterPhase(JsfPhaseId.RESTORE_VIEW) PhaseEvent e)
   {
      String n�kym� = e.getFacesContext().getViewRoot().getViewId();
      if (!n�kym�.endsWith("kirjatuminen.xhtml") && kirjautunutHenkil� == Henkil�.EI_KIRJAUTUNUT)
      {
         e.getFacesContext().getApplication().getNavigationHandler()
            .handleNavigation(e.getFacesContext(), null, "kirjautuminen.xhtml");
         e.getFacesContext().renderResponse();
      }
   }
}
