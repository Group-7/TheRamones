package com.group7.dao.jpa;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;

import com.group7.dao.BaseDataDAO;
import com.group7.entities.BaseData;
import com.group7.entities.EventCause;
import com.group7.entities.Failure;
import com.group7.entities.Network;
import com.group7.entities.UE;
import com.group7.importBaseData.BaseDataValidation;

@Named
@NamedQueries({ @NamedQuery(name = "BaseData.getAll", query = "select bd from baseData bd") })
@Local
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)

public class BaseDataDAOImpl implements BaseDataDAO {

	@Inject
	EntityManager em;

	BaseDataValidation bdv = BaseDataValidation.getInstance();

	public Collection<BaseData> getAllBaseData() {
		return (Collection<BaseData>) em.createQuery(
				"select bd from BaseData bd").getResultList();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void addBaseData(BaseData basedata) {
		em.persist(basedata);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void putData(Collection<BaseData> bd) {
		//System.out.println("valid size "+bd.size());
		int count = 0;
		for (BaseData basedata : bd) {
			try{
				em.merge(basedata.deepCopy());
			}
			catch(EntityExistsException eee){
				//System.out.println("***************\nEntity exixts exception "+"\n**************");
				count++;
			}
		}
		//System.out.println("dupicates "+count);
	}


	/*
	 * SELECT Base_Data.IMSI, Base_Data.Cause_Code, Base_Data.EventID,
	 * Event_Cause_Table.Description FROM Base_Data, Event_Cause_Table WHERE
	 * Base_Data.Cause_Code = Event_Cause_Table.Cause_Code AND Base_Data.EventID
	 * = Event_Cause_Table.EventID AND Base_Data.IMSI = 344930000000001;
	 */
	public Collection<Object> getAllCauseCodeAndEventIdByIMSI(BigInteger imsi) {
		return em
				.createQuery(
						"SELECT bd.imsi, bd.causeCode, bd.eventId, ec.description FROM EventCause ec, BaseData bd WHERE bd.causeCode = ec.causeCode AND bd.eventId = ec.eventId AND bd.imsi = :imsi").setParameter("imsi", imsi).getResultList();
	}

	public Collection<BigInteger> getUniqueAffectedImsi() {
		return (Collection<BigInteger>) em.createQuery(
				"SELECT DISTINCT bd.imsi FROM BaseData bd").getResultList();

	}
	

	@Override
	public Collection<BigInteger> getImsiFailureOverTime(String from, String to) {
		// TODO Auto-generated method stub
		Timestamp start=new Timestamp(dateFormatter(from).getTime());
		Timestamp end=new Timestamp(dateFormatter(to).getTime());
		
		return (Collection<BigInteger>)em.createQuery("select DISTINCT bd.imsi from BaseData bd "
				+ "where bd.dateAndTime between :from and :to"
				).setParameter("from", start, TemporalType.TIMESTAMP)
				.setParameter("to", end, TemporalType.TIMESTAMP)
				.getResultList();
	}
	
	
	
	
	

	@Override
	public void putNetworkData(Collection<Network> networkData) {
		for (Network n : networkData) {
			if ((bdv.isNetworkFirst()) ||!bdv.persistCandidateKeysToNetworkTable(""+n.getMcc() + n.getMnc())) // works
				em.persist(n);
		}
		bdv.setNetworkFirst(false);
	}

	@Override
	public void putUEData(Collection<UE> ueData) {
		for (UE u : ueData) {
			if ((bdv.isUeFirst())|| (!bdv.persistEventCausePrimaryKey(Integer.toString(u.getTac())))) // Failed
				em.persist(u);

		}
		bdv.setUeFirst(false);
	}

	@Override
	public void putEventCauseData(Collection<EventCause> eventCauseData) {
		for (EventCause e : eventCauseData) {
			if ((bdv.isEventCauseFirst()) || !bdv.persistCandidateKeysToEventCauseTable(""+e.getCauseCode() + e.getEventId()))
				em.persist(e);
		}
		bdv.setEventCauseFirst(false);
	}

	@Override
	public void putFailureData(Collection<Failure> failureData) {
		for (Failure f : failureData) {
			if ((bdv.isFailureFirst())|| !bdv.persistFailurePrimaryKey(Integer.toString(f.getFailureCode()))) // failed
				em.persist(f);
		}
		bdv.setFailureFirst(false);
	}
	
	/**
	 * Returns the total number of call failures within a certain
	 * time period based on the phoneType.
	 */
	public Collection<Long> getTotalFailuresOfSpecificPhone(BigInteger phoneType, String startDate, String endDate) {
		
		Timestamp dbStartDate=new Timestamp(dateFormatter(startDate).getTime());
		Timestamp dbEndDate=new Timestamp(dateFormatter(endDate).getTime());
	
		return em.createQuery("SELECT COUNT(*) FROM BaseData bd WHERE bd.tac LIKE :tac AND bd.dateAndTime > :startdate AND bd.dateAndTime < :enddate")
				.setParameter("tac", phoneType)
				.setParameter("startdate", dbStartDate, TemporalType.TIMESTAMP)
				.setParameter("enddate", dbEndDate, TemporalType.TIMESTAMP)
				.getResultList();
	
		}

	
	/**
	 * Returns the total number of call failures within a certain
	 * time period based on the imsi number.
	 */
	public Collection<Long> getTotalFailuresOfSpecificIMSI(BigInteger imsi, String startDate, String endDate) {
		
		Timestamp dbStartDate=new Timestamp(dateFormatter(startDate).getTime());
		Timestamp dbEndDate=new Timestamp(dateFormatter(endDate).getTime());
				
		return em.createQuery("SELECT COUNT(*) FROM BaseData bd WHERE bd.imsi LIKE :imsi AND bd.dateAndTime > :startdate AND bd.dateAndTime < :enddate")
				.setParameter("imsi", imsi)
				.setParameter("startdate", dbStartDate, TemporalType.TIMESTAMP)
				.setParameter("enddate", dbEndDate, TemporalType.TIMESTAMP)
				.getResultList();
			}
	
	
	/** 
	 * Returns for a given phone type all the unique failure Event Id and Cause Code combinations 
	 * they have exhibited and the number of occurrences.
	 */
	public Collection<Object> getAllCallFailuresAndTotalDurationPerIMSI(BigInteger imsi, String startDate, String endDate) {
		
		Timestamp dbStartDate=new Timestamp(dateFormatter(startDate).getTime());
		Timestamp dbEndDate=new Timestamp(dateFormatter(endDate).getTime());
				
		return em.createQuery("SELECT imsi, COUNT(*), SUM(duration) FROM BaseData bd WHERE bd.imsi LIKE :imsi AND bd.dateAndTime > :startdate AND bd.dateAndTime < :enddate")
				.setParameter("imsi", imsi)
				.setParameter("startdate", dbStartDate, TemporalType.TIMESTAMP)
				.setParameter("enddate", dbEndDate, TemporalType.TIMESTAMP)
				.getResultList();
			}
	
	
	public Collection<Object> getAllUniqueEventCausecodeCombinations(String model) {
		
		return em.createQuery("select u.model, b.failureClass, b.causeCode , count(*) as occurences from BaseData b, UE_Table u where b.tac = u.tac and u.model = :phoneModel group by b.failureClass, b.causeCode")
				.setParameter("phoneModel", model)
				.getResultList();
			}
	

	public Date dateFormatter(String date){
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date newDate = null;
		try {
			newDate = format.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return newDate;
//		DateTimeFormatter parser = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");
//	    dt = DateTime.parse("10/02/2013 20:00:00", parser); 
	}
	
	
	
}