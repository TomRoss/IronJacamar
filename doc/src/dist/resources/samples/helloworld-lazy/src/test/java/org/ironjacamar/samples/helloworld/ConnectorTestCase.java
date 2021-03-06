/*
 * IronJacamar, a Java EE Connector Architecture implementation
 * Copyright 2016, Red Hat Inc, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the Eclipse Public License 1.0 as
 * published by the Free Software Foundation.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Eclipse
 * Public License for more details.
 *
 * You should have received a copy of the Eclipse Public License 
 * along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.ironjacamar.samples.helloworld;

import org.ironjacamar.embedded.Deployment;
import org.ironjacamar.embedded.junit4.IronJacamar;

import java.util.UUID;
import java.util.logging.Logger;

import javax.annotation.Resource;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;


/**
 * ConnectorTestCase
 */
@RunWith(IronJacamar.class)
public class ConnectorTestCase
{
   private static Logger log = Logger.getLogger("ConnectorTestCase");

   private static String deploymentName = "ConnectorTestCase";

   /**
    * Define the deployment
    *
    * @return The deployment archive
    */
   @Deployment(order = 1)
   public static ResourceAdapterArchive createDeployment()
   {
      ResourceAdapterArchive raa =
         ShrinkWrap.create(ResourceAdapterArchive.class, deploymentName + ".rar");
      JavaArchive ja = ShrinkWrap.create(JavaArchive.class, 
         UUID.randomUUID().toString() + ".jar");
      ja.addClasses(HelloWorldResourceAdapter.class, 
         HelloWorldManagedConnectionFactory.class, 
         HelloWorldManagedConnection.class, 
         HelloWorldManagedConnectionMetaData.class, 
         HelloWorldConnectionFactory.class, 
         HelloWorldConnectionFactoryImpl.class, 
         HelloWorldConnection.class, 
         HelloWorldConnectionImpl.class);
      raa.addAsLibrary(ja);
      // Contains the default deployment information
      raa.addAsManifestResource("META-INF/ironjacamar.xml", "ironjacamar.xml");

      return raa;
   }

   /** resource */
   @Resource(mappedName = "java:/eis/HelloWorld")
   private HelloWorldConnectionFactory connectionFactory;

   /**
    * Test helloWorld
    *
    * @exception Throwable Thrown if case of an error
    */
   @Test
   public void testHelloWorldNoArgs() throws Throwable
   {
      assertNotNull(connectionFactory);
      HelloWorldConnection connection = connectionFactory.getConnection();
      assertNotNull(connection);
      String result = connection.helloWorld();
      assertEquals("Hello World, IronJacamar !", result);
      connection.close();
   }

   /**
    * Test helloWorld
    *
    * @exception Throwable Thrown if case of an error
    */
   @Test
   public void testHelloWorldNameString() throws Throwable
   {
      assertNotNull(connectionFactory);
      HelloWorldConnection connection = connectionFactory.getConnection();
      assertNotNull(connection);
      String result = connection.helloWorld("TestCase");
      assertEquals("Hello World, TestCase !", result);
      connection.close();
   }

   /**
    * Test helloWorld with two connections
    *
    * max-pool-size is 1, so once getConnection() is called
    * the second time, the managed connection for connection1
    * is dissociated.
    *
    * @exception Throwable Thrown if case of an error
    */
   @Test
   public void testHelloWorldTwoConnections() throws Throwable
   {
      assertNotNull(connectionFactory);

      HelloWorldConnection connection1 = connectionFactory.getConnection();
      assertNotNull(connection1);
      String result1 = connection1.helloWorld("Connection1");
      assertEquals("Hello World, Connection1 !", result1);

      HelloWorldConnection connection2 = connectionFactory.getConnection();
      assertNotNull(connection2);
      String result2 = connection2.helloWorld("Connection2");
      assertEquals("Hello World, Connection2 !", result2);

      connection1.close();
      connection2.close();
   }
}
