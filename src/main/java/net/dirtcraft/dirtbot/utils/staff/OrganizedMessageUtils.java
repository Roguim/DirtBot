package net.dirtcraft.dirtbot.utils.staff;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.commons.lang3.StringUtils;

public class OrganizedMessageUtils {
	
	public String getPartition(LinkedHashMap<String, ArrayList<String>> table) {
		StringBuilder partition = new StringBuilder("+");
		for(String column : table.keySet()) {
			partition.append(getSinglePartition(table.get(column), column));
			partition.append("+");
		}
		partition.append("\n");
		return partition.toString();
	}
	
	private String getSinglePartition(ArrayList<String> allValues, String column) {
		StringBuilder partition = new StringBuilder();
		allValues.add(column);
		int length = 0;
		for(String value : allValues) {
			if(length < value.length()) {
				length = value.length();
			}
		}
		
		for(int i = 0; i < length+2; i++) {
			partition.append("-");
		}
		return partition.toString();
	}
	
	public String getTitleRow(LinkedHashMap<String, ArrayList<String>> table) {
		Iterator<String> values = table.keySet().iterator();
		StringBuilder message = new StringBuilder("|");
		while(values.hasNext()) {
			String value = values.next();
			int boxSize = getSinglePartition(table.get(value), value).length();
			String slot = "";
			if((boxSize-value.length()) % 2 == 0) {
				slot = value;
				slot = StringUtils.leftPad(slot, ((boxSize-value.length())/2)+slot.length());
				slot = StringUtils.rightPad(slot, ((boxSize-value.length())/2)+slot.length());
				slot += "|";
			} else {
				slot = value;
				slot = StringUtils.leftPad(slot, ((boxSize-value.length())/2)+slot.length());
				slot = StringUtils.rightPad(slot, ((boxSize-value.length())/2)+1+slot.length());
				slot += "|";
			}
			message.append(slot);
		}
		message.append("\n");
		return message.toString();
	}
	
	
	//FIX THIS IDIOT
	public String getRow(LinkedHashMap<String, ArrayList<String>> table, int row) {
		Iterator<String> columns = table.keySet().iterator();
		Iterator<ArrayList<String>> values = table.values().iterator();
		
		StringBuilder message = new StringBuilder("|");
		
		while(values.hasNext()) {
			ArrayList<String> value = values.next();
			
			int boxSize = getSinglePartition(value, columns.next()).length();
			
			String slot = "";
			if((boxSize-value.get(row).length()) % 2 == 0) {
				slot = value.get(row);
				slot = StringUtils.leftPad(slot, ((boxSize-value.get(row).length())/2)+slot.length());
				slot = StringUtils.rightPad(slot, ((boxSize-value.get(row).length())/2)+slot.length());
				slot += "|";
			} else {
				slot = value.get(row);
				slot = StringUtils.leftPad(slot, ((boxSize-value.get(row).length())/2)+slot.length());
				slot = StringUtils.rightPad(slot, ((boxSize-value.get(row).length())/2)+1+slot.length());
				slot += "|";
			}
			message.append(slot);
		}
		message.append("\n");
		return message.toString();
	}
	
	public int totalRows(LinkedHashMap<String, ArrayList<String>> table) {
		int total = 0;
		Iterator<ArrayList<String>> iterator = table.values().iterator();
		total = iterator.next().size();
		return total;
	}

}
