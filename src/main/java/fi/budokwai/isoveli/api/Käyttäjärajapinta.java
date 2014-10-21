package fi.budokwai.isoveli.api;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import fi.budokwai.isoveli.malli.Henkil�;

@Path("kayttaja")
@Stateless
public class K�ytt�j�rajapinta
{
   @PersistenceContext
   private EntityManager entityManager;

   @GET
   @Path("/sahkopostilistalla")
   @Produces("text/plain")
   public String getS�hk�postilistalla()
   {
      StringBuilder sb = new StringBuilder();
      List<Henkil�> henkil�t = entityManager.createNamedQuery("s�hk�postilistalla", Henkil�.class).getResultList();
      henkil�t
         .forEach(h -> sb.append(String.format("%s%s", h.getYhteystiedot().getS�hk�posti(), System.lineSeparator())));
      return sb.toString();
   }

}