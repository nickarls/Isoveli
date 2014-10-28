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
import fi.budokwai.isoveli.malli.Henkilö;
import fi.budokwai.isoveli.malli.Tunnukset;
import fi.budokwai.isoveli.util.DSExceptionHandler;
import fi.budokwai.isoveli.util.Kirjautunut;

@Stateful
@SessionScoped
@Named
public class Kirjautuminen extends Perustoiminnallisuus
{
   private Henkilö kirjautunutHenkilö = Henkilö.EI_KIRJAUTUNUT;

   @PersistenceContext
   private EntityManager entityManager;

   @Inject
   private DSExceptionHandler poikkeukset;

   @Produces
   @Kirjautunut
   @Named
   public Henkilö getKirjautunutHenkilö()
   {
      return kirjautunutHenkilö;
   }

   @Inject
   private Instance<Tunnukset> tunnukset;

   public void lähetäResetointipyyntö()
   {
      String[] nimiosat = tunnukset.get().getNimiosat();
      if (nimiosat.length != 2)
      {
         virhe(String.format("Nimi '%s' ei ole oikeassa muodossa, käytä 'etunimi sukunimi'-muotoa", tunnukset.get()
            .getNimi()));;
         return;
      }
      List<Henkilö> henkilöt = entityManager.createNamedQuery("nimetty_henkilö", Henkilö.class)
         .setParameter("etunimi", nimiosat[0]).setParameter("sukunimi", nimiosat[1]).getResultList();
      if (henkilöt.isEmpty())
      {
         virhe(String.format("Käyttäjä '%s' ei löytynyt, ota yhteyttä salipäivystäjään", tunnukset.get().getNimi()));
         return;
      }
      Henkilö henkilö = henkilöt.iterator().next();
      if (henkilö.isLöytyySähköposti())
      {
         lähetäSalasananResetointivahvistus(henkilö);
      } else
      {
         virhe("Käyttäjällä ei ole sähköpostiosoitetta, ota yhteyttä salipäivystäjään");
         return;
      }
   }

   private void lähetäSalasananResetointivahvistus(Henkilö henkilö)
   {
      info(String.format("Salasanan resetointipyyntö lähetetty osoitteeseen %s", henkilö.getYhteystiedot()
         .getSähköposti()));
   }

   public String kirjaudu()
   {
      String[] nimiosat = tunnukset.get().getNimiosat();
      if (nimiosat.length != 2)
      {
         virhe("Käyttäjänimi on 'etunimi sukunimi'");
         return null;
      }
      List<Henkilö> henkilöt = entityManager.createNamedQuery("henkilö", Henkilö.class)
         .setParameter("salasana", tunnukset.get().getMD5Salasana()).setParameter("etunimi", nimiosat[0])
         .setParameter("sukunimi", nimiosat[1]).getResultList();
      if (henkilöt.isEmpty())
      {
         virhe("Väärä nimi tai salasana");
         return null;
      }
      kirjautunutHenkilö = henkilöt.iterator().next();
      if (kirjautunutHenkilö.isPääsyHallintaan())
      {
         return "admin/admin.xhtml?faces-redirect=true";
      } else
      {
         return "käyttäjä/käyttäjä.xhtml?faces-redirect=true";
      }
   }

   public void tarkistaKirjautuminen(@Observes @AfterPhase(JsfPhaseId.RESTORE_VIEW) PhaseEvent e)
   {
      String näkymä = e.getFacesContext().getViewRoot().getViewId();
      if (!(näkymä.endsWith("kirjautuminen.xhtml") || näkymä.endsWith("virhe.xhtml"))
         && kirjautunutHenkilö == Henkilö.EI_KIRJAUTUNUT)
      {
         loginSivulle(e.getFacesContext());
      }
      if (näkymä.contains("admin/") && !kirjautunutHenkilö.isPääsyHallintaan())
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
