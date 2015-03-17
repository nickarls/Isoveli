package fi.budokwai.isoveli.admin;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.inject.Model;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.icefaces.util.JavaScriptRunner;

import fi.budokwai.isoveli.IsoveliPoikkeus;
import fi.budokwai.isoveli.malli.Henkil�;
import fi.budokwai.isoveli.malli.Tunnusvaihto;
import fi.budokwai.isoveli.util.Util;

@Model
@Stateful
public class Salasananvaihto extends Perustoiminnallisuus
{
   private Tunnusvaihto tunnusvaihto = new Tunnusvaihto();

   @Inject
   private EntityManager entityManager;

   public Tunnusvaihto getTunnusvaihto()
   {
      return tunnusvaihto;
   }

   public static void main(String... x)
   {
      System.out.println(Util.MD5("x"));
      System.out.println(Util.MD5("y"));
   }

   public void vaihda()
   {
      String[] nimiosat = tunnusvaihto.getNimiosat();
      if (nimiosat.length != 2)
      {
         throw new IsoveliPoikkeus("K�ytt�j�nimi oltava muotoa 'etunimi sukunimi'");
      }
      if (!tunnusvaihto.oikeinToistettuSalasana())
      {
         throw new IsoveliPoikkeus("V��rin toistettu uusi salasana");
      }
      List<Henkil�> henkil�t = entityManager.createNamedQuery("henkil�", Henkil�.class)
         .setParameter("salasana", tunnusvaihto.getMD5Salasana()).setParameter("etunimi", nimiosat[0])
         .setParameter("sukunimi", nimiosat[1]).getResultList();
      if (henkil�t.isEmpty())
      {
         throw new IsoveliPoikkeus("Tuntematon k�ytt�j� tai v��r� salasana");
      }
      Henkil� henkil� = henkil�t.iterator().next();
      henkil�.setSalasana(tunnusvaihto.getUusiMD5Salasana());
      entityManager.persist(henkil�);
      entityManager.flush();
      info("Salasana vaihdettu");
      JavaScriptRunner.runScript(FacesContext.getCurrentInstance(),
         "ice.ace.instance('vaihtoform:salasanavaihto').hide();document.getElementById('form:k�ytt�j�nimi').focus();");
   }
}
