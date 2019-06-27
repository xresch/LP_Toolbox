package com.pengtoolbox.cfw.db.usermanagement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.db.CFWDB;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBGroup.GroupDBFields;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBUser.UserDBFields;
import com.pengtoolbox.cfw.logging.CFWLog;

public class CFWDBUserGroupMap {

	public static String TABLE_NAME = "CFW_USER_GROUP_MAP";
	
	public static Logger logger = CFWLog.getLogger(CFWDBUserGroupMap.class.getName());
	
	enum GroupUserMapDBFields{
		PK_ID, 
		FK_ID_USER,
		FK_ID_GROUP,
	}

	/********************************************************************************************
	 * Creates the table and default admin user if not already exists.
	 * This method is executed by CFW.DB.initialize().
	 * 
	 ********************************************************************************************/
	public static void initializeTable() {
			
		String createTableSQL = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+"("
							  + GroupUserMapDBFields.PK_ID + " INT PRIMARY KEY AUTO_INCREMENT, "
							  + GroupUserMapDBFields.FK_ID_USER + " INT, "
							  + GroupUserMapDBFields.FK_ID_GROUP + " INT, "
							  + "FOREIGN KEY ("+GroupUserMapDBFields.FK_ID_USER+") REFERENCES "+CFWDBUser.TABLE_NAME+"("+UserDBFields.PK_ID+"), "
							  + "FOREIGN KEY ("+GroupUserMapDBFields.FK_ID_GROUP+") REFERENCES "+CFWDBUser.TABLE_NAME+"("+GroupDBFields.PK_ID+") "
							  + ");";
		
		CFWDB.preparedExecute(createTableSQL);
				
	}
	
	/********************************************************************************************
	 * Creates a new group in the DB.
	 * @param Group with the values that should be inserted. ID will be set by the Database.
	 * @return return true if user was added, false otherwise
	 * 
	 ********************************************************************************************/
	public static boolean addUserToGroup(User user, Group group) {
		
		if(checkIsUserInGroup(user, group)) {
			new CFWLog(logger)
				.method("addUserToGroup")
				.warn("The user '"+user.username()+"' is already part of the group '"+group.name()+"'.");
			return false;
		}
		String insertGroupSQL = "INSERT INTO "+TABLE_NAME+" ("
				  + GroupUserMapDBFields.FK_ID_USER +", "
				  + GroupUserMapDBFields.FK_ID_GROUP +" "
				  + ") VALUES (?,?);";
		
		
		return CFWDB.preparedExecute(insertGroupSQL, 
				user.id(),
				group.id()
				);
	}
	
	/****************************************************************
	 * Check if the user is in the given group.
	 * 
	 * @param group to check
	 * @return true if exists, false otherwise or in case of exception.
	 ****************************************************************/
	public static boolean checkIsUserInGroup(User user, Group group) {
		if(group != null) {
			return checkIsUserInGroup(user.id(), group.id());
		}
		return false;
	}
	
	/****************************************************************
	 * Check if the group exists by name.
	 * 
	 * @param group to check
	 * @return true if exists, false otherwise or in case of exception.
	 ****************************************************************/
	public static boolean checkIsUserInGroup(int userid, int groupid) {
		
		String checkIsUserInGroup = "SELECT COUNT(*) FROM "+TABLE_NAME
				+" WHERE "+GroupUserMapDBFields.FK_ID_USER+" = ?"
				+" AND "+GroupUserMapDBFields.FK_ID_GROUP+" = ?";
		
		ResultSet result = CFW.DB.preparedExecuteQuery(checkIsUserInGroup, userid, groupid);
		
		try {
			if(result.next()) {
				int count = result.getInt(1);
				return (count == 0) ? false : true;
			}
		} catch (SQLException e) {
			new CFWLog(logger)
			.method("groupExists")
			.severe("Exception occured while checking of group exists.", e);
			
			return false;
		}
		
		return false;
	}
	
	
	
//	/***************************************************************
//	 * Select a group by it's name.
//	 * @param id of the group
//	 * @return Returns a group or null if not found or in case of exception.
//	 ****************************************************************/
//	public static Group selectGroupsForUser(String userID) {
//		
//		String selectByName = 
//				"SELECT "
//				  + GroupUserMapDBFields.PK_ID +", "
//				  + GroupUserMapDBFields.NAME +", "
//				  + GroupUserMapDBFields.DESCRIPTION +", "
//				  + GroupUserMapDBFields.IS_DELETABLE +" "
//				+" FROM "+TABLE_NAME
//				+" WHERE "
//				+ GroupUserMapDBFields.NAME + " = ?";
//		
//		ResultSet result = CFWDB.preparedExecuteQuery(selectByName, name);
//		
//		if(result == null) {
//			return null;
//		}
//		
//		try {
//			if(result.next()) {
//				return new Group(result);
//			}
//		} catch (SQLException e) {
//			new CFWLog(logger)
//			.method("selectByName")
//			.severe("Error reading group from database.", e);;
//			
//		}
//		
//		return null;
//		
//	}
//	
//	/***************************************************************
//	 * Select a group by it's ID.
//	 * @param id of the group
//	 * @return Returns a group or null if not found or in case of exception.
//	 ****************************************************************/
//	public static Group selectByID(int id ) {
//		
//		String selectByName = 
//				"SELECT "
//				  + GroupUserMapDBFields.PK_ID +", "
//				  + GroupUserMapDBFields.NAME +", "
//				  + GroupUserMapDBFields.DESCRIPTION +", "
//				  + GroupUserMapDBFields.IS_DELETABLE +" "
//				+" FROM "+TABLE_NAME
//				+" WHERE "
//				+ GroupUserMapDBFields.PK_ID + " = ?";
//		
//		ResultSet result = CFWDB.preparedExecuteQuery(selectByName, id);
//		
//		if(result == null) {
//			return null;
//		}
//		
//		try {
//			if(result.next()) {
//				return new Group(result);
//			}
//		} catch (SQLException e) {
//			new CFWLog(logger)
//			.method("selectByID")
//			.severe("Error reading group from database.", e);;
//			
//		}
//		
//		return null;
//		
//	}
//	
//	/***************************************************************
//	 * Updates the object selecting by ID.
//	 * @param group
//	 * @return true or false
//	 ****************************************************************/
//	public static boolean update(Group group) {
//		
//		String updateByID = 
//				"UPDATE "+TABLE_NAME
//				+" SET ("
//				  + GroupUserMapDBFields.NAME +", "
//				  + GroupUserMapDBFields.DESCRIPTION +", "
//				  + GroupUserMapDBFields.IS_DELETABLE +" "
//				  + ") = (?,?,?) "
//				+" WHERE "
//					+ GroupUserMapDBFields.PK_ID+" = ?";
//		
//		boolean result = CFWDB.preparedExecute(updateByID, 
//				group.name(),
//				group.description(),
//				group.isDeletable(),
//				group.id());
//		
//		
//		return result;
//		
//	}
//	
//	/****************************************************************
//	 * Deletes the group by id.
//	 * @param id of the user
//	 * @return true if successful, false otherwise.
//	 ****************************************************************/
//	public static boolean deleteByID(int id) {
//		
//		String deleteByID = 
//				"DELETE FROM "+TABLE_NAME
//				+" WHERE "
//					+ GroupUserMapDBFields.PK_ID+" = ? ";
//		
//		return CFWDB.preparedExecute(deleteByID, id);
//			
//	}
//	
//	
//	/****************************************************************
//	 * Check if the group exists by name.
//	 * 
//	 * @param group to check
//	 * @return true if exists, false otherwise or in case of exception.
//	 ****************************************************************/
//	public static boolean checkGroupExists(Group group) {
//		if(group != null) {
//			return checkGroupExists(group.name());
//		}
//		return false;
//	}
//	
//	/****************************************************************
//	 * Check if the group exists by name.
//	 * 
//	 * @param groupname to check
//	 * @return true if exists, false otherwise or in case of exception.
//	 ****************************************************************/
//	public static boolean checkGroupExists(String groupName) {
//		String checkExistsSQL = "SELECT COUNT(*) FROM "+TABLE_NAME+" WHERE "+GroupUserMapDBFields.NAME+" = ?";
//		ResultSet result = CFW.DB.preparedExecuteQuery(checkExistsSQL, groupName);
//		
//		try {
//			if(result.next()) {
//				int count = result.getInt(1);
//				return (count == 0) ? false : true;
//			}
//		} catch (SQLException e) {
//			new CFWLog(logger)
//			.method("groupExists")
//			.severe("Exception occured while checking of group exists.", e);
//			
//			return false;
//		}
//		
//		return false; 
//	}
	
}
