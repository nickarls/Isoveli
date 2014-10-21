package fi.budokwai.isoveli.api;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import fi.budokwai.isoveli.malli.Henkilö;

@Path("kayttaja")
@Stateless
public class Käyttäjärajapinta
{
   @PersistenceContext
   private EntityManager entityManager;

   @GET
   @Path("/sahkopostilistalla")
   @Produces("text/plain")
   public String getSähköpostilistalla()
   {
      StringBuilder sb = new StringBuilder();
      List<Henkilö> henkilöt = entityManager.createNamedQuery("sähköpostilistalla", Henkilö.class).getResultList();
      henkilöt
         .forEach(h -> sb.append(String.format("%s%s", h.getYhteystiedot().getSähköposti(), System.lineSeparator())));
      return sb.toString();
   }

}