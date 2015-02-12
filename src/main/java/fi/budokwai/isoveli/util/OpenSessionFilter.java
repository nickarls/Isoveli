package fi.budokwai.isoveli.util;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

public class OpenSessionFilter implements Filter
{

   @Override
   public void destroy()
   {

   }

   @Resource
   private UserTransaction utx;

   @Override
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
      ServletException
   {
      try
      {
         utx.begin();
         chain.doFilter(request, response);
         utx.commit();
      } catch (NotSupportedException | SystemException | SecurityException | IllegalStateException | RollbackException
         | HeuristicMixedException | HeuristicRollbackException e)
      {
         try
         {
            utx.rollback();
         } catch (IllegalStateException | SecurityException | SystemException e1)
         {
            e1.printStackTrace();
         }
      }
   }

   @Override
   public void init(FilterConfig arg0) throws ServletException
   {

   }
}