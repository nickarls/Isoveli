package fi.budokwai.isoveli.api;

import java.io.IOException;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import fi.budokwai.isoveli.admin.RaporttiAdmin;
import fi.budokwai.isoveli.malli.BlobData;
import fi.budokwai.isoveli.malli.Henkil�;
import fi.budokwai.isoveli.malli.Lasku;
import fi.budokwai.isoveli.util.AuditManager;
import fi.budokwai.isoveli.util.Lasku2PDF;
import fi.budokwai.isoveli.util.MailManager;

@Path("kayttaja")
@Stateless
public class K�ytt�j�rajapinta
{
   @PersistenceContext
   private EntityManager entityManager;

   @Inject
   private RaporttiAdmin raporttiAdmin;

   @Inject
   private AuditManager auditManager;

   @Inject
   private MailManager mailManager;

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

   @GET
   @Path("/resetoiSalasana/{avain}")
   @Produces("text/plain")
   public String resetoiSalasana(@PathParam("avain") String avain)
   {
      Integer id = auditManager.haeResetoitava(avain);
      if (id == null)
      {
         return "Resetoitavaa henkil�� ei l�ytynyt. Pyynt� on mahdollisesti vanhentunut, tee uusi yritys";
      }
      Henkil� henkil� = entityManager.find(Henkil�.class, id);
      if (henkil� == null)
      {
         return "Resetoitavaa henkil�� ei l�ytynyt tietokannasta. Omituista. Ota yhteytt� salin p�ivyst�j��n";
      }
      String salasana = henkil�.luoUusiSalasana();
      entityManager.persist(henkil�);
      mailManager.l�het�S�hk�posti(henkil�.getYhteystiedot().getS�hk�posti(), "Isoveli - uusi salasana",
         String.format("Uusi salasanasi on %s", salasana));
      return String.format("Uusi salasana on l�hetetty osoitteeseen %s", henkil�.getYhteystiedot().getS�hk�posti());
   }

   @GET
   @Path("/l")
   @Produces("application/pdf")
   public byte[] l() throws Exception
   {
      Lasku l = entityManager.find(Lasku.class, 1);
      byte[] m = entityManager.find(BlobData.class, 1).getTieto();
      return new Lasku2PDF(m, l).muodosta();
   }

   @GET
   @Path("jasenkortit")
   @Produces("application/zip")
   public Response muodostaJ�senkortit() throws RowsExceededException, WriteException, IOException
   {
      BlobData blobData = raporttiAdmin.muodostaJ�senkortit();
      ResponseBuilder response = Response.ok(blobData.getTieto(),
         MediaType.valueOf(blobData.getTyyppi().getMimetyyppi()));
      String tiedostonimi = String.format("%s.%s", blobData.getNimi(), blobData.getTyyppi().getTyyppi()).toLowerCase();
      response.header("Content-Disposition", String.format("attachment; filename=%s", tiedostonimi));
      response.header("Content-Length", blobData.getTieto().length);
      return response.build();
   }

}