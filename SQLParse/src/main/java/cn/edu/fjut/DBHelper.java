package cn.edu.fjut;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import cn.edu.fjut.bean.ExerciseJudgement;
import cn.edu.fjut.bean.ExerciseSubmission;

public class DBHelper {
	
	public static DBHelper instance ;
	private Connection conn;
	private Statement stmt;
	

	
	/*create view submitanswer as 
	select exercisesubmission_ptr_id, submitted_answer, answer, exercises_judgement.exercise_id, is_correct
	from exercises_submission, exercises_judgement, exercises_solution
	where exercises_submission.exercisesubmission_ptr_id = exercises_judgement.id 
	and exercises_solution.exercise_id = exercises_judgement.exercise_id
	*/
	private DBHelper()
	{ 
        String uid;       
        uid ="postgres";
        try{
     /*       String url="jdbc:postgresql://127.0.0.1:5432/test";
            String user= uid;
            String password = uid;
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url, user, password);
           */
        	Class.forName("org.sqlite.JDBC");
        	conn = DriverManager.getConnection("jdbc:sqlite:src/main/resources/DB.db");
            stmt = conn.createStatement();          
        }
        catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static DBHelper getInstance()
	{
		if( instance == null)
			instance = new DBHelper();
		return instance;
	}
	
	public void close()
	{
		try {
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	
	

	
	public List<String> getAllSolution()
	{
		List<String> results = new ArrayList<String>();
		String sql = "select answer from exercises_solution";
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next())
			{
				results.add(rs.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}
	
	public void updateJudgement(List<String> updateToTrue, boolean correct)
	{		
		String sql;
		if(correct)
			sql = "update exercises_judgement set is_correct = 1 where id  = ?";
		else
			sql = "update exercises_judgement set is_correct = 0 where id  = ?";
		try {
			conn.setAutoCommit(false);
			PreparedStatement ps = conn.prepareStatement(sql);
			for (String id : updateToTrue) {
				ps.setString(1, id);
				ps.addBatch();
			}
			ps.executeBatch();
			ps.clearBatch();
			ps.clearParameters();
			conn.commit();
			conn.setAutoCommit(true);			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	public List<ExerciseSubmission> getSubmission(int exercise_id)	
	{
		List<ExerciseSubmission> results = new ArrayList<ExerciseSubmission>();
		String sql = "select exercisesubmission_ptr_id, submitted_answer, answer, exercise_id , is_correct "
				+ "from submitanswer where exercise_id = " + exercise_id;
		try {
			ResultSet rSet = stmt.executeQuery(sql);
			while(rSet.next())
			{ 
				ExerciseSubmission submission = new ExerciseSubmission(rSet.getString(1), rSet.getString(2), rSet.getString(3), rSet.getString(4), rSet.getBoolean(5));
				results.add(submission);
			}
			rSet.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}
	
	public Set<Set<String>> getAnswer(String sql)
	{
		Set<Set<String>> result = new HashSet<Set<String>>();
		// 一个最基本的select 语句至少有１８个字符
		if(sql.trim().length() < 18)
			return result;
		try {
			ResultSet rs = stmt.executeQuery(sql);
			int count = rs.getMetaData().getColumnCount();
			while(rs.next())
			{
				Set<String> row = new HashSet<String>();
				for(int i =1; i<= count; i++)
				{
					// 有些字段可能没有赋值，因此存在空的情况
					if(rs.getObject(i) == null)
						row.add("NULL");
					else
						row.add(rs.getObject(i).toString() );					
				}				
				// TODO: 有些错误的sql会产生多条相同的记录，对于这种明显错误的先直接返回个空集，
				if(result.contains(row))
					return new HashSet<Set<String>>();
				result.add(row);
			}
			
		}catch (NullPointerException e) {
			e.printStackTrace();			
		} catch (java.sql.SQLException e) {
			System.out.println(e.getMessage());
			
		}
		catch (Exception e) {
			//e.printStackTrace();
		}
		return result;
	}
	
	public HashMap<Integer, Integer> getAnswer11(String sql)
	{
		HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next())
			{
				result.put(rs.getInt(1), rs.getInt(2));
			}
		} catch (Exception e) {			
			//e.printStackTrace();			
		}
		return result;
	}
	
	/**
	 * 对于一些只返回一个数字的问题，可以共用该方法．
	 * 现有的题目有10, 12．
	 * @param sql
	 * @return
	 */
	public int getScalarAnswer(String sql)
	{		
		int result = -1;
		try {
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next())
				result = rs.getInt(1);
			//　由于jdbc没有 executeScalar语句，当executeQuery返回多条时，该题肯定为错
			if(rs.next())
				result = -1;
		} catch (Exception e) {			
			System.out.println(e.getMessage());			
		}
		return result;
	}
	
	/**用于获取参考执行答案sql得到的查询结果，这样考生答案就可以跟这个查询结果进行直接对比．
	 * @return
	 */
	public HashMap<Integer, Set<Set<String>>> getRefAnswer()
	{
		HashMap<Integer, Set<Set<String>>> answer = new HashMap<Integer, Set<Set<String>>>();
		HashMap<Integer, String> idAndSql = new HashMap<Integer, String>();
		String sql = "select distinct(exercise_id), answer  from submitanswer ";		
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next())
			{
				idAndSql.put(rs.getInt(1), rs.getString(2));
			}
			rs.close();
			for (Integer key : idAndSql.keySet()) {
				Set<Set<String>> sqlResult = getAnswer(idAndSql.get(key) );
				answer.put(key, sqlResult);
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return answer;
	}
	
	public List<ExerciseSubmission> getAllSubmission()
	{
		List<ExerciseSubmission> results = new ArrayList<ExerciseSubmission>();
		String sql  = "select * from exercises_codingexercisesubmission";

		sql = "select exercisesubmission_ptr_id, submitted_answer, answer, exercise_id , is_correct "
				+ "from submitanswer ";
		try {
			ResultSet rSet = stmt.executeQuery(sql);
			while(rSet.next())
			{
				ExerciseSubmission submission = new ExerciseSubmission(rSet.getString(1), rSet.getString(2), rSet.getString(3), rSet.getString(4), rSet.getBoolean(5));
				results.add(submission);
			}
			rSet.close();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
		}		
		return results;
	}

	
	public static void main(String []args) {
		
		DBHelper helper = DBHelper.getInstance();
		List<ExerciseSubmission> submissions = helper.getAllSubmission();
		for (ExerciseSubmission exerciseSubmission : submissions) {
			System.out.println(exerciseSubmission);
		}
            
        
    }
	
	private  Set<Set<String>>  createSets(String[][] array)
	{
		Set<Set<String>>  result = new HashSet<Set<String>>();
		for(int i=0; i< array.length; i++)
		{
			Set<String> row = new HashSet<String>();
			for(int j =0; j< array[i].length; j++)
			{
				row.add(array[i][j]);
			}
			result.add(row);
		}
		return result;
	}

}
