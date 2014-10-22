package fi.budokwai.isoveli.malli;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.validation.constraints.Size;

import fi.budokwai.isoveli.api.BlobLataus;

@Entity
@NamedQuery(name = "blobdata", query = "select b from BlobData b where b.nimi=:nimi")
public class BlobData
{
   public static BlobData PDF(String nimi, byte[] data)
   {
      return new BlobData(nimi, data, Tiedostotyyppi.PDF);
   }

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @Size(max = 20)
   private String nimi;

   @Enumerated(EnumType.ORDINAL)
   private Tiedostotyyppi tyyppi;

   private byte[] tieto;

   public BlobData()
   {
   }

   public BlobData(String nimi, byte[] tieto, Tiedostotyyppi tiedostotyyppi)
   {
      this.nimi = nimi;
      this.tieto = tieto;
      this.tyyppi = tiedostotyyppi;
   }

   public BlobData(int id, String nimi, byte[] data, Tiedostotyyppi tiedostotyyppi)
   {
      this(nimi, data, tiedostotyyppi);
   }

   public BlobData(BlobLataus blobLataus)
   {
      id = Integer.valueOf(blobLataus.getId());
      nimi = blobLataus.getNimi();
      tyyppi = Tiedostotyyppi.valueOf(blobLataus.getTyyppi());
      tieto = blobLataus.getTieto();
   }

   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public String getNimi()
   {
      return nimi;
   }

   public void setNimi(String nimi)
   {
      this.nimi = nimi;
   }

   public Tiedostotyyppi getTyyppi()
   {
      return tyyppi;
   }

   public void setTyyppi(Tiedostotyyppi tyyppi)
   {
      this.tyyppi = tyyppi;
   }

   public byte[] getTieto()
   {
      return tieto;
   }

   public void setTieto(byte[] tieto)
   {
      this.tieto = tieto;
   }

}
