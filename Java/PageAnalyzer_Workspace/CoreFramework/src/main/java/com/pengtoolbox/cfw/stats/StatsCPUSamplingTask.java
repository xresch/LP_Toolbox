package com.pengtoolbox.cfw.stats;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.config.Configuration;
import com.pengtoolbox.cfw.db.CFWDB;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.schedule.CFWScheduledTask;

public class StatsCPUSamplingTask extends CFWScheduledTask {
	
	private static long lastSave = System.currentTimeMillis();
	private static Logger logger = CFWLog.getLogger(StatsCPUSamplingTask.class.getName());
	
	// Contains "parentID -> signatureID" as key and the number of occurences as value.
	private static LinkedHashMap<String, StatsCPUSample> counterMap = new LinkedHashMap<String, StatsCPUSample>();
	
	// Contains the stack element signature with ID as in the DB
	private static HashMap<Object, Object> signatureIDMap = CFWDBStatsCPUSampleSignature.getSignaturesAsKeyValueMap();
	private static int samplingSeconds = CFW.DB.Config.getConfigAsInt(Configuration.CPU_SAMPLING_SECONDS);
	
	@Override
	public void execute() {
		long currentTime = System.currentTimeMillis();
		// minutes to millis
		int aggregationMillis = CFW.DB.Config.getConfigAsInt(Configuration.CPU_SAMPLING_AGGREGATION) *60000;
		
		//-----------------------------------
		// Check Saving
		if( (currentTime - lastSave) > aggregationMillis ) {
			saveAndResetCounters();
			lastSave = currentTime;
		}
		
		//-----------------------------------
		// Check Saving
		updateCounters();
		
		//System.out.println(dumpCounters());
	}
	
	/***********************************************************************************
	 * Save the current count to the DB and reset the counters.
	 * 
	 ***********************************************************************************/
	private static void saveAndResetCounters() {
		
		Timestamp time = new Timestamp(System.currentTimeMillis());
		int periodMinutes = CFW.DB.Config.getConfigAsInt(Configuration.CPU_SAMPLING_AGGREGATION);
		
		CFWDB.beginTransaction();
			for(StatsCPUSample entry : counterMap.values()) {
				if(entry.count() != 0) {
					if(entry.time(time)
					  .prepareStatistics(samplingSeconds)
					  .granularity(periodMinutes).insert()) {
						
						entry.count(0);
					}	
				}
			}
		CFWDB.commitTransaction();
		
	}
	
	/***********************************************************************************
	 * Traverse all stack traces and update the respective counters.
	 ***********************************************************************************/
	private static void updateCounters() {
		
		Map<Thread, StackTraceElement[]> traceMap = Thread.getAllStackTraces();

		//---------------------------------------
		// Iterate all stack trace elements
		for(StackTraceElement[] elements : traceMap.values()) {
			
			Integer parentID = null;
			
			for(int i = elements.length-1; i > 0; i--) {
				StackTraceElement element = elements[i];
				String signatureString = element.toString();
				signatureString = signatureString.substring(signatureString.lastIndexOf('/')+1);
				Integer signatureID = null;
				
				//---------------------------------------
				// Create DB entry for Signature if not exists
				if(!signatureIDMap.containsKey(signatureString)) {
					
					signatureID = new StatsCPUSampleSignature()
							.signature(signatureString)
							.insertGetPrimaryKey();
					
					if(signatureID != null) {
						
						signatureIDMap.put(signatureString, signatureID);
					}else {
						new CFWLog(logger)
							.method("run")
							.severe("Insert of new signature failed.");
					}
				}else {
					signatureID = (Integer)signatureIDMap.get(signatureString);
				}
				
				//---------------------------------------
				// Manage Counter
				String counterID = parentID + " -> "+signatureID;
				if(counterMap.containsKey(counterID)) {
					counterMap.get(counterID).increaseCount();
					parentID = signatureID;
				}else {
															
					//---------------------------------------
					// Add counter to map
					StatsCPUSample  methodStats = new StatsCPUSample()
							.foreignKeySignature(signatureID)
							.foreignKeyParent(parentID)
							.count(1);
					
					counterMap.put(counterID, methodStats);
					
					parentID = signatureID;
					
				}
				
			}
		}
	}
	
	public static String dumpCounters() {
		
		StringBuilder builder = new StringBuilder();
		for(Entry<String, StatsCPUSample> entry : counterMap.entrySet()) {
			builder.append(entry.getKey()).append(": ").append(entry.getValue().count()).append("\n");
		}
		
		return builder.toString();
		
	}

}
