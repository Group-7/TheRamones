package com.group7.arquillian;

import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.EJB;
import javax.inject.Inject;

import jxl.read.biff.BiffException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.group7.dao.BaseDataDAO;
import com.group7.dao.jpa.BaseDataDAOImpl;
import com.group7.databases.DataBaseProducer;
import com.group7.entities.BaseData;
import com.group7.entities.EventCause;
import com.group7.entities.Failure;
import com.group7.entities.Network;
import com.group7.entities.UE;
import com.group7.importBaseData.BaseDataExcelRead;
import com.group7.importBaseData.BaseDataValidation;
import com.group7.rest.BaseDataREST;
import com.group7.service.BaseDataServiceEJB;
import com.group7.serviceInterface.BaseDataServiceLocal;

@RunWith(Arquillian.class)
public class UserStory7Test {

	@Deployment
	public static JavaArchive createDeployment() {
		return ShrinkWrap
				.create(JavaArchive.class, "US7.jar")
				.addClasses(BaseData.class,
						BaseDataDAO.class,
						BaseDataDAOImpl.class,
						BaseDataValidation.class,
						BaseDataREST.class,
						BaseDataServiceEJB.class,
						BaseDataServiceLocal.class)
						.addPackage(BaseData.class.getPackage())
						.addPackage(DataBaseProducer.class.getPackage())
				.addAsResource("META-INF/persistence.xml")
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

	}
	@Inject
	private BaseDataServiceLocal service;

	

	
	
	@Test
	public void notNullTest(){
		assertNotNull(service);
	}
	
	
	
	@Test
	public void testUnique(){
		
		ArrayList<BigInteger> imsis=(ArrayList<BigInteger>)service.getImsiFailureOverTime("01/01/0013 12:40:20","12/12/0015 12:40:20");
	
	
		
		for(int i=0;i<imsis.size();i++){
			
			for(int j=i+1;j<imsis.size();j++){
				assertNotSame(imsis.get(i),imsis.get(j));
			}
			System.out.println("\n\n\n\n"+i);
		}
		
	}
	
	@Test
	public void testSize() {
	
		Collection<BigInteger> imsis=service.getImsiFailureOverTime("01/01/0012 12:40:20","12/12/0015 12:40:20");
		assertEquals(imsis.size(),6);
		
		imsis=service.getImsiFailureOverTime("01/01/0014 12:40:20","12/12/0015 12:40:20");
		assertEquals(imsis.size(),0);
	}
	
	/*@Test
	public void getUniqueImsiTest(){
		
		expect().
		statusCode(200).
		body(equals("344930000000011,310560000000012,"
				+ "240210000000013,"
				+ "344930000000001,"
				+ "310560000000002,"
				+ "240210000000003"))
				.when()
				.get("rest/baseData/uniqueImsi");
		//get("/rest/baseData/uniqueIMSI").then().body();
		
	}
	*/
}

