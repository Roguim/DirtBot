package net.dirtcraft.dirtbot.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import net.dirtcraft.dirtbot.utils.staff.OrganizedMessageUtils;

public class OrganizedMessage {
	
	LinkedHashMap<String, ArrayList<String>> table = new LinkedHashMap<>();
	ArrayList<String> columns = new ArrayList<>();
	ArrayList<ArrayList<String>> values = new ArrayList<>();
	
	//Add a new column to the end of the table
	public void addColumn(String columnName, ArrayList<String> columnValues) {
		table.put(columnName, columnValues);
		updateLists();
	}
	
	//Remove a column by name
	public void removeColumn(String columnName) {
		table.remove(columnName);
		updateLists();
	}
	
	//Change the values of a column
	public void modifyColumn(String columnName, ArrayList<String> columnValues) {
		int index = 0;
		for(int i = 0; i < columns.size(); i++) {
			if(columns.get(i).equalsIgnoreCase(columnName)) {
				index = i;
			}
		}
		values.set(index, columnValues);
		updateTable();
	}
	
	
	//Update lists from table
	private void updateLists() {
		columns.clear();
		values.clear();
		for(String columnName : table.keySet()) {
			columns.add(columnName);
		}
		for(ArrayList<String> value : table.values()) {
			values.add(value);
		}
	}
	
	//Update table from lists
	private void updateTable() {
		table.clear();
		for(int i = 0; i < columns.size(); i++) {
			table.put(columns.get(i), values.get(i));
		}
	}
	
	public String getMessage() {
		OrganizedMessageUtils util = new OrganizedMessageUtils();
		int totalRows = util.totalRows(table);
		String partition = util.getPartition(table);
		StringBuilder message = new StringBuilder("```");
		message.append(partition);
		message.append(util.getTitleRow(table));
		message.append(partition);
		for(int i = 0; i < totalRows; i++) {
			message.append(util.getRow(table, i));
			message.append(partition);
		}
		message.append("```");
		return message.toString();
	}

}
