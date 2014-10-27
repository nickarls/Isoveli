package fi.budokwai.isoveli.util;

import javax.enterprise.inject.Specializes;

import org.apache.deltaspike.jsf.api.config.JsfModuleConfig;
import org.apache.deltaspike.jsf.spi.scope.window.ClientWindowConfig;
import org.apache.deltaspike.jsf.spi.scope.window.ClientWindowConfig.ClientWindowRenderMode;

@Specializes
public class DSConfig extends JsfModuleConfig
{
   private static final long serialVersionUID = 1L;

   @Override
   public ClientWindowConfig.ClientWindowRenderMode getDefaultWindowMode()
   {
      return ClientWindowRenderMode.NONE;
   }
}
