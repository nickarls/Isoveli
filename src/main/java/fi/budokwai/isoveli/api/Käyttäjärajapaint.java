package fi.budokwai.isoveli.api;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import fi.budokwai.isoveli.malli.Harrastaja;

@Path("käyttäjä")
@Stateless
public class Käyttäjärajapaint
{
   @javax.persistence.PersistenceContext
   private EntityManager entityManager;

   @POST
   @Path("/kuva/{id}")
   @Consumes("multipart/form-data")
   public Response tallennaKuva(@MultipartForm Kuvatieto kuvatieto, @PathParam("id") int id)
   {
      Harrastaja henkilö = entityManager.find(Harrastaja.class, id);
      henkilö.getHenkilö().setKuva(kuvatieto.getTieto());
      entityManager.persist(henkilö);
      return Response.status(200).entity("OK").build();
   }

}