package edu.tongji.wang.chefapi;

import java.util.List;

public class ChefResult {
	private int total;
	
	private List<Row> rows;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Row row : rows)
			sb.append(row.toString() + "\n");
		return "total=" + total +"\n" + sb.toString();
				
	}
	
	

}

class Row {
	private String name;
	private String chef_environment;
	@Override
	public String toString() {
		return name + "_"+ chef_environment;
	}

}
