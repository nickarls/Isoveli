package fi.budokwai.isoveli;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.deltaspike.jsf.api.listener.phase.AfterPhase;
import org.apache.deltaspike.jsf.api.listener.phase.JsfPhaseId;

import fi.budokwai.isoveli.admin.Perustoiminnallisuus;
import fi.budokwai.isoveli.malli.Henkil�;
import fi.budokwai.isoveli.malli.Tunnukset;
import fi.budokwai.isoveli.util.DSExceptionHandler;
import fi.budokwai.isoveli.util.Kirjautunut;

@Stateful
@SessionScoped
@Named
public class Kirjautuminen extends Perustoiminnallisuus
{
   private Henkil� kirjautunutHenkil� = Henkil�.EI_KIRJAUTUNUT;

   @PersistenceContext
   private EntityManager entityManager;

   @Inject
   private DSExceptionHandler poikkeukset;

   @Produces
   @Kirjautunut
   @Named
   public Henkil� getKirjautunutHenkil�()
   {
      return kirjautunutHenkil�;
   }

   @Inject
   private Instance<Tunnukset> tunnukset;

   public void l�het�Resetointipyynt�()
   {
      String[] nimiosat = tunnukset.get().getNimiosat();
      if (nimiosat.length != 2)
      {
         virhe(String.format("Nimi '%s' ei ole oikeassa muodossa, k�yt� 'etunimi sukunimi'-muotoa", tunnukset.get()
            .getNimi()));;
         return;
      }
      List<Henkil�> henkil�t = entityManager.createNamedQuery("nimetty_henkil�", Henkil�.class)
         .setParameter("etunimi", nimiosat[0]).setParameter("sukunimi", nimiosat[1]).getResultList();
      if (henkil�t.isEmpty())
      {
         virhe(String.format("K�ytt�j� '%s' ei l�ytynyt, ota yhteytt� salip�ivyst�j��n", tunnukset.get().getNimi()));
         return;
      }
      Henkil� henkil� = henkil�t.iterator().next();
      if (henkil�.isL�ytyyS�hk�posti())
      {
         l�het�SalasananResetointivahvistus(henkil�);
      } else
      {
         virhe("K�ytt�j�ll� ei ole s�hk�postiosoitetta, ota yhteytt� salip�ivyst�j��n");
         return;
      }
   }

   private void l�het�SalasananResetointivahvistus(Henkil� henkil�)
   {
      info(String.format("Salasanan resetointipyynt� l�hetetty osoitteeseen %s", henkil�.getYhteystiedot()
         .getS�hk�posti()));
   }

   public String kirjaudu()
   {
      String[] nimiosat = tunnukset.get().getNimiosat();
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
         return "k�ytt�j�/k�ytt�j�.xhtml?faces-redirect=true";
      }
   }

   public void tarkistaKirjautuminen(@Observes @AfterPhase(JsfPhaseId.RESTORE_VIEW) PhaseEvent e)
   {
      String n�kym� = e.getFacesContext().getViewRoot().getViewId();
      if (!(n�kym�.endsWith("kirjautuminen.xhtml") || n�kym�.endsWith("virhe.xhtml"))
         && kirjautunutHenkil� == Henkil�.EI_KIRJAUTUNUT)
      {
         loginSivulle(e.getFacesContext());
      }
      if (n�kym�.contains("admin/") && !kirjautunutHenkil�.isP��syHallintaan())
      {
         poikkeukset.tapaSessio();
      }
   }

   private void loginSivulle(FacesContext facesContext)
   {
      facesContext.getApplication().getNavigationHandler().handleNavigation(facesContext, null, "kirjautuminen.xhtml");
      facesContext.renderResponse();

   }
}
