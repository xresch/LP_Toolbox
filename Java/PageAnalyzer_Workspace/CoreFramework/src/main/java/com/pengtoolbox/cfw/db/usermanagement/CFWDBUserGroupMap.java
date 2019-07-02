package com.pengtoolbox.cfw.db.usermanagement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Logger;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.db.CFWDB;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBGroup.GroupDBFields;
import com.pengtoolbox.cfw.db.usermanagement.CFWDBUser.UserDBFields;
import com.pengtoolbox.cfw.logging.CFWLog;

public class CFWDBUserGroupMap {

	public static String TABLE_NAME = "CFW_USER_GROUP_MAP";
	
	public static Logger logger = CFWLog.getLogger(CFWDBUserGroupMap.class.getName());
	
	enum UserGroupMapDBFields{
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
							  + UserGroupMapDBFields.PK_ID + " INT PRIMARY KEY AUTO_INCREMENT, "
							  + UserGroupMapDBFields.FK_ID_USER + " INT, "
							  + UserGroupMapDBFields.FK_ID_GROUP + " INT, "
							  + "FOREIGN KEY ("+UserGroupMapDBFields.FK_ID_USER+") REFERENCES "+CFWDBUser.TABLE_NAME+"("+UserDBFields.PK_ID+") ON DELETE CASCADE, "
							  + "FOREIGN KEY ("+UserGroupMapDBFields.FK_ID_GROUP+") REFERENCES "+CFWDBGroup.TABLE_NAME+"("+GroupDBFields.PK_ID+") ON DELETE CASCADE"
							  + ");";
		
		CFWDB.preparedExecute(createTableSQL);
				
	}
	
	/********************************************************************************************
	 * Adds the user to the specified group.
	 * @param user
	 * @param group
	 * @return return true if user was added, false otherwise
	 * 
	 ********************************************************************************************/
	public static boolean addUserToGroup(User user, Group group) {
		
		if(user == null || group == null ) {
			new CFWLog(logger)
				.method("addUserToGroup")
				.warn("User and group cannot be null.");
			return false;
		}
		
		if(user.id() < 0 || group.id() < 0) {
			new CFWLog(logger)
				.method("addUserToGroup")
				.warn("User-ID and group-ID are not set correctly.");
			return false;
		}
		
		if(checkIsUserInGroup(user, group)) {
			new CFWLog(logger)
				.method("addUserToGroup")
				.warn("The user '"+user.username()+"' is already part of the group '"+group.name()+"'.");
			return false;
		}
		
		String insertGroupSQL = "INSERT INTO "+TABLE_NAME+" ("
				  + UserGroupMapDBFields.FK_ID_USER +", "
				  + UserGroupMapDBFields.FK_ID_GROUP +" "
				  + ") VALUES (?,?);";
		
		return CFWDB.preparedExecute(insertGroupSQL, 
				user.id(),
				group.id()
				);
	}
	
	/********************************************************************************************
	 * Remove a user from the group.
	 * @param user
	 * @param group
	 * @return return true if user was removed, false otherwise
	 * 
	 ********************************************************************************************/
	public static boolean removeUserFromGroup(User user, Group group) {
		
		if(user == null || group == null ) {
			new CFWLog(logger)
				.method("removeUserFromGroup")
				.warn("User and group cannot be null.");
			return false;
		}
		
		if(!checkIsUserInGroup(user, group)) {
			new CFWLog(logger)
				.method("removeUserFromGroup")
				.warn("The user '"+user.username()+"' is not part of the group '"+group.name()+"' and cannot be removed.");
			return false;
		}
		
		String removeUserFromGroupSQL = "DELETE FROM "+TABLE_NAME
				+" WHERE "
				  + UserGroupMapDBFields.FK_ID_USER +" = ? "
				  + " AND "
				  + UserGroupMapDBFields.FK_ID_GROUP +" = ? "
				  + ";";
		
		return CFWDB.preparedExecute(removeUserFromGroupSQL, 
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
		
		if(user != null && group != null) {
			return checkIsUserInGroup(user.id(), group.id());
		}else {
			new CFWLog(logger)
				.method("checkIsUserInGroup")
				.severe("The user and group cannot be null. User: '"+user+"', Group: '"+group+"'");
			
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
				+" WHERE "+UserGroupMapDBFields.FK_ID_USER+" = ?"
				+" AND "+UserGroupMapDBFields.FK_ID_GROUP+" = ?";
		
		ResultSet result = CFW.DB.preparedExecuteQuery(checkIsUserInGroup, userid, groupid);
		
		try {
			if(result != null && result.next()) {
				int count = result.getInt(1);
				return (count == 0) ? false : true;
			}
		} catch (Exception e) {
			new CFWLog(logger)
			.method("groupExists")
			.severe("Exception occured while checking of group exists.", e);
			
			return false;
		}
		
		return false;
	}
	
	/***************************************************************
	 * Updates the object selecting by ID.
	 * @param group
	 * @return Hashmap with groups(key=group name, value=group object), or null on exception
	 ****************************************************************/
	public static HashMap<String, Group> selectGroupsForUser(User user) {
		
		if( user == null) {
			new CFWLog(logger)
				.method("create")
				.severe("The user cannot be null");
			return null;
		}
		
		String selectGroupsForUser = "SELECT * FROM "+CFWDBGroup.TABLE_NAME+" G "
				+ " INNER JOIN "+CFWDBUserGroupMap.TABLE_NAME+" M "
				+ " ON M.FK_ID_GROUP = G.PK_ID "
				+ " WHERE M.FK_ID_USER = ?";
		
		ResultSet result = CFWDB.preparedExecuteQuery(selectGroupsForUser, 
				user.id());
		
		HashMap<String, Group> groupMap = new HashMap<String, Group>(); 
		
		try {
			while(result != null && result.next()) {
				Group group = new Group(result);
				groupMap.put(group.name(), group);
			}
		} catch (SQLException e) {
			new CFWLog(logger)
			.method("selectGroupsForUser")
			.severe("Error while selecting groups for the user '"+user.username()+"'.", e);
			return null;
		}finally {
			CFWDB.close(result);
		}
		
		return groupMap;
	
	}
		
}