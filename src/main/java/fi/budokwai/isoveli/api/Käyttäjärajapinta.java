package fi.budokwai.isoveli.api;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Henkilö;

@Path("kayttaja")
@Stateless
public class Käyttäjärajapinta
{
   @PersistenceContext
   private EntityManager entityManager;

   @POST
   @Path("/kuva/{id}")
   @Consumes("multipart/form-data")
   public Response tallennaKuva(@MultipartForm Kuvatieto kuvatieto, @PathParam("id") int id)
   {
      Harrastaja henkilö = entityManager.find(Harrastaja.class, id);
      henkilö.setKuva(kuvatieto.getTieto());
      entityManager.persist(henkilö);
      return Response.status(200).entity("OK").build();
   }

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