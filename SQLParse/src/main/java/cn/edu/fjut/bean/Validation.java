package cn.edu.fjut.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.spi.DirStateFactory.Result;

import org.apache.commons.text.CaseUtils;

import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Condition;
import com.alibaba.druid.stat.TableStat.Name;
import com.alibaba.druid.util.JdbcConstants;

public class Validation {
	private int exercise_id;
	private List<String> tables;
	private String condition;
	
	
	
	public Validation(int exercise_id) {		
		this.exercise_id = exercise_id;
		tables = new ArrayList<>();
	}
	public int getExercise_id() {
		return exercise_id;
	}
	public void setExercise_id(int exercise_id) {
		this.exercise_id = exercise_id;
	}
	public List<String> getTables() {
		return tables;
	}
	
	public void addTable(String table)
	{
		tables.add(table.toLowerCase());
	}
	
	
	
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public void setTables(String tabs) {
		for (String tab : tabs.split(",")) {
			tables.add(tab.trim().toLowerCase());
		}
		
	}
	
	@Override
	public String toString() {
		String str = "id: " + exercise_id + " tables: " + tables.toString() + " condition: " + condition;
		return str;
	}
	public boolean validateTables(Map<Name, TableStat> tableSet) {
		if(tableSet.size() != tables.size())
			return false;
		for (Name name : tableSet.keySet()) {
			if(tables.contains(name.toString().toLowerCase()) == false) 
				return false;
		}
		return true;
	}
	public boolean validateCondition(List<Condition> conditions) {
		
		for (Condition condition : conditions) {
			String condiStr = buildConditionStr(condition);	
			boolean satify = false;
			if(condiStr.length() > 0)
				satify = (boolean) SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, condiStr, this.condition);
			if(satify)
				return true;			
		}
		
		return false;
	}
	private String buildConditionStr(Condition condition2) {
		String result = "? " + condition2.getOperator()+ " ";
		switch (condition2.getValues().size()) {
		case 0:
			result = "";
			break;
		case 1:
			result = result + condition2.getValues().get(0);
			break;
		default:
			System.out.println(condition2.toString());
			break;
		}		
		
		return result;
	}
	
	
}
