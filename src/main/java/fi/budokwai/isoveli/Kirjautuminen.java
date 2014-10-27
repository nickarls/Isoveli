package fi.budokwai.isoveli;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.faces.event.PhaseEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.deltaspike.jsf.api.listener.phase.AfterPhase;
import org.apache.deltaspike.jsf.api.listener.phase.JsfPhaseId;

import fi.budokwai.isoveli.admin.Perustoiminnallisuus;
import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Henkil�;
import fi.budokwai.isoveli.malli.Tunnukset;
import fi.budokwai.isoveli.util.Kirjautunut;

@Stateful
@SessionScoped
@Named
public class Kirjautuminen extends Perustoiminnallisuus
{
   private Henkil� kirjautunutHenkil� = Henkil�.EI_KIRJAUTUNUT;

   @PersistenceContext
   private EntityManager entityManager;

   @Produces
   @Kirjautunut
   @Named
   public Henkil� getItseHenkil�()
   {
      return kirjautunutHenkil�;
   }

   @Produces
   @Kirjautunut
   @Named
   public Harrastaja getItseHarrastaja()
   {
      return (Harrastaja) kirjautunutHenkil�;
   }

   @Inject
   private Instance<Tunnukset> tunnukset;

   public String kirjaudu()
   {
      String x = tunnukset.get().getMD5Salasana();
      String[] nimiosat = tunnukset.get().getNimi().split(" ");
      if (nimiosat.length != 2)
      {
         virhe("K�ytt�j�nimi on 'etunimi sukunimi'");
         return null;
      }
      List<Henkil�> henkil�t = entityManager.createNamedQuery("henkil�", Henkil�.class)
         .setParameter("salasana", tunnukset.get().getMD5Salasana()).setParameter("etunimi", nimiosat[0])
         .setParameter("sukunimi", nimiosat[1]).getResultList();
      if (henkil�t.isEmpty())
      {
         virhe("V��r� nimi tai salasana");
         return null;
      }
      kirjautunutHenkil� = henkil�t.iterator().next();
      if (kirjautunutHenkil�.isP��syHallintaan())
      {
         return "admin/admin.xhtml?faces-redirect=true";
      } else
      {
         return "k�ytt�j�.xhtml?faces-redirect=true";
      }
   }

   public void tarkistaKirjautuminen(@Observes @AfterPhase(JsfPhaseId.RESTORE_VIEW) PhaseEvent e)
   {
      String n�kym� = e.getFacesContext().getViewRoot().getViewId();
      if (!(n�kym�.endsWith("kirjautuminen.xhtml") || n�kym�.endsWith("virhe.xhtml"))
         && kirjautunutHenkil� == Henkil�.EI_KIRJAUTUNUT)
      {
         e.getFacesContext().getApplication().getNavigationHandler()
            .handleNavigation(e.getFacesContext(), null, "kirjautuminen.xhtml");
         e.getFacesContext().renderResponse();
      }
   }
}
