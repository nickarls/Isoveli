package fi.budokwai.isoveli.malli;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import fi.budokwai.isoveli.util.DateUtil;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "treeni")
@NamedQueries(
{
      @NamedQuery(name = "tulevat_treenit", query = "select t from Treeni t where t.arkistoitu='E' and t.p‰iv‰=:p‰iv‰ and t.p‰‰ttyy >= :kello and not exists(select tk from Treenik‰ynti tk, Treenisessio ts where tk.harrastaja=:harrastaja and tk.treenisessio=ts and ts.treeni=t and ts.p‰iv‰ = :t‰n‰‰n)"),
      @NamedQuery(name = "treenit", query = "select t from Treeni t where t.arkistoitu='E' order by t.p‰iv‰, t.alkaa"),
      @NamedQuery(name = "treenitArq", query = "select t from Treeni t order by t.p‰iv‰, t.alkaa"), })
public class Treeni
{
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @NotNull
   @Size(max = 100)
   private String nimi;

   @Size(max = 100)
   private String sijainti;

   @Column(name = "paiva")
   @NotNull
   private Viikonp‰iv‰ p‰iv‰;

   @Temporal(TemporalType.TIME)
   @NotNull
   private Date alkaa;

   @Temporal(TemporalType.TIME)
   @Column(name = "paattyy")
   @NotNull
   private Date p‰‰ttyy;

   @OneToOne
   @JoinColumn(name = "tyyppi")
   @NotNull
   private Treenityyppi tyyppi;

   @Type(type = "Kyll‰Ei")
   private boolean power;

   @Type(type = "Kyll‰Ei")
   private boolean valmennuskeskus;

   @OneToOne
   @JoinColumn(name = "vyoalaraja")
   private Vyˆarvo vyˆAlaraja;

   @OneToOne
   @JoinColumn(name = "vyoylaraja")
   private Vyˆarvo vyˆYl‰raja;

   @Column(name = "ikaalaraja")
   @Min(0)
   private Integer ik‰Alaraja;

   @Min(0)
   @Column(name = "ikaylaraja")
   private Integer ik‰Yl‰raja;

   @Temporal(TemporalType.DATE)
   private Date voimassaAlkaa;

   @Column(name = "voimassapaattyy")
   @Temporal(TemporalType.DATE)
   private Date voimassaP‰‰ttyy;

   @Type(type = "Kyll‰Ei")
   private boolean arkistoitu;

   @ManyToMany(cascade =
   { CascadeType.PERSIST })
   @JoinTable(name = "treenivetaja", joinColumns =
   { @JoinColumn(name = "treeni", referencedColumnName = "id") }, inverseJoinColumns =
   { @JoinColumn(name = "harrastaja", referencedColumnName = "id") })
   private List<Harrastaja> vet‰j‰t = new ArrayList<Harrastaja>();

   @OneToMany(mappedBy = "treeni")
   private List<Treenisessio> treenisessiot = new ArrayList<Treenisessio>();

   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public Date getAlkaa()
   {
      return alkaa;
   }

   public void setAlkaa(Date alkaa)
   {
      this.alkaa = alkaa;
   }

   @Override
   public boolean equals(Object toinen)
   {
      if (!(toinen instanceof Treeni))
      {
         return false;
      }
      Treeni toinenTreeni = (Treeni) toinen;
      return id == toinenTreeni.getId();
   }

   @Override
   public int hashCode()
   {
      return Integer.valueOf(id).hashCode();
   }

   public Viikonp‰iv‰ getP‰iv‰()
   {
      return p‰iv‰;
   }

   public void setP‰iv‰(Viikonp‰iv‰ p‰iv‰)
   {
      this.p‰iv‰ = p‰iv‰;
   }

   public Date getP‰‰ttyy()
   {
      return p‰‰ttyy;
   }

   public void setP‰‰ttyy(Date p‰‰ttyy)
   {
      this.p‰‰ttyy = p‰‰ttyy;
   }

   public String getNimi()
   {
      return nimi;
   }

   public void setNimi(String nimi)
   {
      this.nimi = nimi;
   }

   public boolean isPower()
   {
      return power;
   }

   public void setPower(boolean power)
   {
      this.power = power;
   }

   public Treenityyppi getTyyppi()
   {
      return tyyppi;
   }

   public void setTyyppi(Treenityyppi tyyppi)
   {
      this.tyyppi = tyyppi;
   }

   public List<Treenisessio> getTreenisessiot()
   {
      return treenisessiot;
   }

   public void setTreenisessiot(List<Treenisessio> treenisessiot)
   {
      this.treenisessiot = treenisessiot;
   }

   public List<Harrastaja> getVet‰j‰t()
   {
      return vet‰j‰t;
   }

   public void setVet‰j‰t(List<Harrastaja> vet‰j‰t)
   {
      this.vet‰j‰t = vet‰j‰t;
   }

   public Date getVoimassaAlkaa()
   {
      return voimassaAlkaa;
   }

   public void setVoimassaAlkaa(Date voimassaAlkaa)
   {
      this.voimassaAlkaa = voimassaAlkaa;
   }

   public Date getVoimassaP‰‰ttyy()
   {
      return voimassaP‰‰ttyy;
   }

   public void setVoimassaP‰‰ttyy(Date voimassaP‰‰ttyy)
   {
      this.voimassaP‰‰ttyy = voimassaP‰‰ttyy;
   }

   public Vyˆarvo getVyˆAlaraja()
   {
      return vyˆAlaraja;
   }

   public void setVyˆAlaraja(Vyˆarvo vyˆAlaraja)
   {
      this.vyˆAlaraja = vyˆAlaraja;
   }

   public Vyˆarvo getVyˆYl‰raja()
   {
      return vyˆYl‰raja;
   }

   public void setVyˆYl‰raja(Vyˆarvo vyˆYl‰raja)
   {
      this.vyˆYl‰raja = vyˆYl‰raja;
   }

   public String getSijainti()
   {
      return sijainti;
   }

   public void setSijainti(String sijainti)
   {
      this.sijainti = sijainti;
   }

   public String getKesto()
   {
      Duration kesto = Duration.between(new Date(alkaa.getTime()).toInstant(), new Date(p‰‰ttyy.getTime()).toInstant());
      long tunnit = kesto.toHours();
      long minuutit = kesto.minusHours(tunnit).toMinutes();
      return String.format("%d:%02d", tunnit, minuutit);
   }

   public String getAika()
   {
      SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
      return String.format("%s-%s", sdf.format(alkaa), sdf.format(p‰‰ttyy));
   }

   public boolean isValmennuskeskus()
   {
      return valmennuskeskus;
   }

   public void setValmennuskeskus(boolean valmennuskeskus)
   {
      this.valmennuskeskus = valmennuskeskus;
   }

   public Integer getIk‰Alaraja()
   {
      return ik‰Alaraja;
   }

   public void setIk‰Alaraja(Integer ik‰Alaraja)
   {
      this.ik‰Alaraja = ik‰Alaraja;
   }

   public Integer getIk‰Yl‰raja()
   {
      return ik‰Yl‰raja;
   }

   public void setIk‰Yl‰raja(Integer ik‰Yl‰raja)
   {
      this.ik‰Yl‰raja = ik‰Yl‰raja;
   }

   public String getIk‰rajat()
   {
      if (ik‰Alaraja == null && ik‰Yl‰raja == null)
      {
         return null;
      } else if (ik‰Alaraja != null && ik‰Yl‰raja == null)
      {
         return String.format("%d+", ik‰Alaraja);
      } else if (ik‰Alaraja == null && ik‰Yl‰raja != null)
      {
         return String.format("-%d", ik‰Yl‰raja);
      } else
      {
         return String.format("%d-%d", ik‰Alaraja, ik‰Yl‰raja);
      }
   }

   public boolean isRajatRistiss‰()
   {
      return (ik‰Alaraja != null && ik‰Yl‰raja != null && (ik‰Alaraja > ik‰Yl‰raja));
   }

   public boolean isVyˆrajatRistiss‰()
   {
      return (vyˆAlaraja != null && vyˆYl‰raja != null && (vyˆAlaraja.getJ‰rjestys() > vyˆYl‰raja.getJ‰rjestys()));
   }

   public boolean isVoimassaoloRistiss‰()
   {
      return (voimassaAlkaa != null && voimassaP‰‰ttyy != null && DateUtil.onkoAiemmin(voimassaP‰‰ttyy, voimassaAlkaa));
   }

   public boolean isAjankohtaRistiss‰()
   {
      return (alkaa != null && p‰‰ttyy != null && DateUtil.onkoAikaAiemmin(p‰‰ttyy, alkaa));
   }

   public boolean isArkistoitu()
   {
      return arkistoitu;
   }

   public void setArkistoitu(boolean arkistoitu)
   {
      this.arkistoitu = arkistoitu;
   }

   @Override
   public String toString()
   {
      return nimi;
   }

   public boolean isTallentamaton()
   {
      return id == 0;
   }
}
