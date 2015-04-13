/*package com.group7.arquillian;

import java.io.File;

import javax.ejb.EJB;
import javax.validation.constraints.AssertTrue;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.group7.dao.UeDAO;
import com.group7.dao.jpa.UeDAOImpl;
import com.group7.databases.DataBaseProducer;
import com.group7.entities.UE;
import com.group7.serviceInterface.UeServiceLocal;

@RunWith(Arquillian.class)
public class UeServiceEJBTest {

	@Deployment
	public static WebArchive createDeployment() {
		WebArchive archive = ShrinkWrap
                .create(WebArchive.class, "test.war")
                .addPackages(true, "com.group7")
                        .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
                        .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

       File[] libs;

        libs = Maven.resolver()
                .resolve("com.jayway.restassured:rest-assured:2.4.0")
                .withTransitivity().as(File.class);
        archive.addAsLibraries(libs);

        libs = Maven.resolver().resolve("org.apache.poi:poi:3.11")
                .withTransitivity().as(File.class);
        archive.addAsLibraries(libs);
        
        return archive;
		

  }

	@EJB
	private UeDAO dao;

	
	@Test
	public void notNullTest(){
		assertNotNull(dao);
	}
	
	// here create simple test which check method of ejb
	@Test
	public void isUETableEmpty() throws Exception {
		//Assert.assertEquals(dao.getEU().size(),);
		//assertEquals(dao.getEU().size(), 1);
		//assertFalse(!dao.getEU().isEmpty());
		//assertTrue(dao.getEU().isEmpty());	
		
	}
}

*/