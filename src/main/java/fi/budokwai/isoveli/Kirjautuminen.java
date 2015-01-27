package fi.budokwai.isoveli;

import java.io.IOException;
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
import javax.servlet.http.HttpServletRequest;

import org.apache.deltaspike.jsf.api.listener.phase.AfterPhase;
import org.apache.deltaspike.jsf.api.listener.phase.JsfPhaseId;

import fi.budokwai.isoveli.admin.Perustoiminnallisuus;
import fi.budokwai.isoveli.malli.Henkilö;
import fi.budokwai.isoveli.malli.Tunnukset;
import fi.budokwai.isoveli.util.AuditManager;
import fi.budokwai.isoveli.util.DSExceptionHandler;
import fi.budokwai.isoveli.util.Kirjautunut;
import fi.budokwai.isoveli.util.Loggaaja;
import fi.budokwai.isoveli.util.MailManager;

@Stateful
@SessionScoped
@Named
public class Kirjautuminen extends Perustoiminnallisuus
{
   private Henkilö kirjautunutHenkilö = Henkilö.EI_KIRJAUTUNUT;

   @PersistenceContext
   private EntityManager entityManager;

   @Inject
   private Loggaaja loggaaja;

   @Inject
   private MailManager mailManager;

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

   @Inject
   private AuditManager auditManager;

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
      String avain = auditManager.teeResetointiavain(henkilö);
      String apiUrl = haeApiUrl(avain);
      String teksti = String
         .format(
            "Olet ilmeisesti tehnyt salasanan resetointipyynnön? Jos klikkaat linkkiä <a href=\"%s\" target=\"_blank\">%s</a> niin sinulle lähetetään uusi salasana. Jos et ole tehnyt pyynntöä niin voit jättää tämän viestin huomiomatta",
            apiUrl, apiUrl);
      mailManager.lähetäSähköposti(henkilö.getYhteystiedot().getSähköposti(), "Isoveli - salasanan resetointipyyntö",
         teksti);
      info(String.format("Salasanan resetointipyyntö lähetetty osoitteeseen %s", henkilö.getYhteystiedot()
         .getSähköposti()));
   }

   private String haeApiUrl(String avain)
   {
      HttpServletRequest pyyntö = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
         .getRequest();
      String URL = pyyntö.getRequestURL().toString();
      String perusOsa = URL.substring(0, URL.lastIndexOf("/"));
      return String.format("%s/API/kayttaja/resetoiSalasana/%s", perusOsa, avain);
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
      loggaaja.loggaa("Sisäänkirjautuminen");
      info("Tervetuloa, %s", kirjautunutHenkilö.getNimi());
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
      if (!salliKirjautumatta(näkymä) && kirjautunutHenkilö == Henkilö.EI_KIRJAUTUNUT)
      {
         loginSivulle(e.getFacesContext());
         return;
      }
      if (näkymä.contains("admin/") && !kirjautunutHenkilö.isPääsyHallintaan())
      {
         poikkeukset.tapaSessio();
      }
   }

   private boolean salliKirjautumatta(String näkymä)
   {
      return näkymä.endsWith("kirjautuminen.xhtml") || näkymä.endsWith("virhe.xhtml")
         || näkymä.endsWith("ilmoittautuminen.xhtml");
   }

   private void loginSivulle(FacesContext facesContext)
   {
      try
      {
         facesContext.getExternalContext().redirect("/Isoveli");
      } catch (IOException e)
      {
         e.printStackTrace();
      }
      facesContext.renderResponse();

   }
}
