package cn.edu.fjut;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.apache.commons.lang3.Validate;

import cn.edu.fjut.bean.ExerciseGrade;
import cn.edu.fjut.bean.ExerciseJudgement;
import cn.edu.fjut.bean.ExerciseRemark;
import cn.edu.fjut.bean.ExerciseSubmission;
import cn.edu.fjut.bean.GradeApproach;
import cn.edu.fjut.bean.RefAnswer;
import cn.edu.fjut.bean.Validation;
import cn.edu.fjut.util.Log;

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
        	Class.forName("org.sqlite.JDBC");
        	conn = DriverManager.getConnection("jdbc:sqlite:src/main/resources/DB.sqlite");
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
	
	public ResultSet executeQuery(String sql) throws SQLException
	{
		return stmt.executeQuery(sql);
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
	
	public void exportResult(String sql)
	{
		try {
			ResultSet rs = stmt.executeQuery(sql);			
			StringBuffer sb = new StringBuffer();
			Set<String> set = new HashSet<String>();
			while(rs.next())
			{
				String str = rs.getString(1).trim();
				str = str.replaceAll("[\r\n\t]", " ");
				str= str.replaceAll(" +"," ");
				str = rs.getString(2).trim() + ";" + str;
				set.add(str);
			}			
			for (String string : set) {
				if(string.matches("^\\d*;[\\S\\s]+;[\\S\\s]+;$"))
					System.out.println(string);
				sb.append(string + "\n");
			}
			
			Log log = new Log("ref.txt", true);
			log.log(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化成绩
	 * 判断为正确为满分100，　编译不通过的０分
	 * 包含delete的SQL只有８条，手工修改．
	 * 3374那道题执行是错的，但在jdbc中却一直能得到正确的解．．待解决
	 */
	public void initialScore()
	{
		String sql ;
		sql = "update exercises_judgement set score = 100 where is_correct = 1";
		List<Integer> ids = new ArrayList<Integer>();
		try {
			stmt.executeUpdate(sql);
			List<ExerciseSubmission> submissions = getSubmissionWithCond(" where is_correct = 0 ");
			for (ExerciseSubmission exerciseSubmission : submissions) {
				System.out.format("%d / %d \n", submissions.indexOf(exerciseSubmission), submissions.size());
				String answer = exerciseSubmission.getSubmitted_answer();
				if(answer.toLowerCase().contains("delete") || exerciseSubmission.getId() == 3374  )
				{
					System.out.println(exerciseSubmission);
					continue;
				}
				if(answer.trim().length() < 18)
					ids.add(exerciseSubmission.getId());
				else
					try {
						stmt.executeQuery(answer);
					} catch (Exception e) {
						ids.add(exerciseSubmission.getId());
					}						
			}
			System.out.println(ids.size());
			sql = "update exercises_judgement set score = 0 where id  = ?";		
			conn.setAutoCommit(false);
			PreparedStatement ps = conn.prepareStatement(sql);
			for (Integer id : ids) {
				ps.setInt(1, id);
				ps.addBatch();
			}
			ps.executeBatch();
			ps.close();
			conn.commit();
			conn.setAutoCommit(true);		
			
		} catch (Exception e) {
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
	
	public void updateRemarks(List<ExerciseRemark> remarks)
	{
		String sql ;	
		try
		{			
			sql = "update exercise_remark set remark = ? where id = ?";
			conn.setAutoCommit(false);
			PreparedStatement ps = conn.prepareStatement(sql);
			for (ExerciseRemark remark : remarks) {
				ps.setString(1, remark.getRemark());
				ps.setInt(2, remark.getId());				
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);			
		} catch (Exception e)
		{
			e.printStackTrace();
		}		
	}
	
	public void insertRemarks(List<ExerciseRemark> remarks)
	{
		String sql ;
		sql = "DELETE FROM exercise_remark";
		try
		{
			stmt.executeUpdate(sql);
			sql = "INSERT INTO exercise_remark( id, remark, answer) VALUES (?, ?, ?)";
			conn.setAutoCommit(false);
			PreparedStatement ps = conn.prepareStatement(sql);
			for (ExerciseRemark remark : remarks) {
				ps.setInt(1, remark.getId());
				ps.setString(2, remark.getRemark());				
				ps.setString(3, remark.getAnswer());
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);
			
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}	
	}
	
	public void clearGrader(GradeApproach sematic)
	{
		String sql = "delete from exercises_grade where approach = '" + sematic + "'";
		try
		{
			stmt.executeUpdate(sql);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}	
	}
	
	public void insertGrader(List<ExerciseGrade> grades)
	{
		String sql = "insert into exercises_grade (submission_id, submitted_answer, grade, approach, most_similar) values (?, ?, ?, ? , ?)";
		try
		{

			conn.setAutoCommit(false);
			PreparedStatement ps = conn.prepareStatement(sql);
			for (ExerciseGrade grade : grades)
			{
				ps.setInt(1, grade.getSubmission_id());
				ps.setString(2, grade.getSubmitted_answer());
				ps.setDouble(3, grade.getGrade());
				ps.setString(4, grade.getApproach().toString());
				ps.setString(5, grade.getMost_similary());
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	

	public void insertRefAnswer(List<RefAnswer> refAnswers) {		
		String sql;
		sql = "DELETE FROM exercises_refAnswer\n" + 
				" WHERE exercise_id = "  + refAnswers.get(0).getExercise_id();		
		try {
			stmt.executeUpdate(sql);
			sql ="INSERT INTO exercises_refAnswer(exercise_id, submission_id, submitted_answer)\n" + 
					"    VALUES (?, ?, ?); ";
			conn.setAutoCommit(false);
			PreparedStatement ps = conn.prepareStatement(sql);
			for (RefAnswer refAnswer : refAnswers) {
				ps.setInt(1, refAnswer.getExercise_id());
				ps.setInt(2, refAnswer.getId());
				ps.setString(3, refAnswer.getAnswer());
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	
	public List<ExerciseSubmission> getSubmission(int exercise_id)	
	{
		List<ExerciseSubmission> results = new ArrayList<ExerciseSubmission>();
		String sql = "select submission_id, submitted_answer, answer, exercise_id , is_correct "
				+ "from exercises_result where exercise_id = " + exercise_id;
		try {
			ResultSet rSet = stmt.executeQuery(sql);
			while(rSet.next())
			{ 
				ExerciseSubmission submission = new ExerciseSubmission(rSet.getInt(1), rSet.getString(2), rSet.getString(3), rSet.getString(4), rSet.getBoolean(5));
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
		//避免对数据库进行删除或更新操作
		if(sql.toLowerCase().contains("delete") || sql.toLowerCase().contains("update"))
		{
			System.out.println("Contain Delete statement");
			System.exit(1);
		}
		Set<Set<String>> result = new HashSet<Set<String>>();
		// 一个最基本的select 语句至少有１８个字符
		if(sql.trim().length() < 18)
			return null;
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

				result.add(row);
			}
			
		}
		catch (Exception e) {
			result = null;
		}
		return result;
	}
	


	/**用于获取参考执行答案sql得到的查询结果，每个题目都有一个标准答案执行结果．
	 * 这样考生答案就可以跟这个查询结果进行直接对比．
	 * @return
	 */
	public HashMap<Integer, Set<Set<String>>> getRefExeResult()
	{
		HashMap<Integer, Set<Set<String>>> answer = new HashMap<Integer, Set<Set<String>>>();
		HashMap<Integer, String> idAndSql = new HashMap<Integer, String>();
		String sql = "select distinct(exercise_id), answer  from exercises_result ";		
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
	
	
	
	public List<ExerciseSubmission> getSubmissionWithCond(String condition)
	{
		List<ExerciseSubmission> results = new ArrayList<ExerciseSubmission>();		
		String sql = "select submission_id, submitted_answer, answer, exercise_id , is_correct "
				+ "from exercises_result " + condition;
		try {
			ResultSet rSet = stmt.executeQuery(sql);
			while(rSet.next())
			{
				ExerciseSubmission submission = new ExerciseSubmission(rSet.getInt(1), rSet.getString(2), rSet.getString(3), rSet.getString(4), rSet.getBoolean(5));
				results.add(submission);
			}
			rSet.close();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
		}		
		return results;
	}
	
	public List<Validation> getValidation()
	{
		List<Validation> result = new ArrayList<>();
		String sql = "select exercise_id, requirTables, requireCondition from validation";
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next())
			{
				Validation validation = new Validation(rs.getInt(1));
				validation.setTables(rs.getString(2));
				validation.setCondition(rs.getString(3));
				result.add(validation);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public List<RefAnswer> getCorrectAnswers(int exercise_id) {
		List<RefAnswer> refAnswers = new ArrayList<>();
		String sql = "select submission_id, submitted_answer from exercises_result where is_correct = 1 and exercise_id = " + exercise_id;
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next())
			{
				RefAnswer refAnswer = new RefAnswer(exercise_id	, rs.getInt(1), rs.getString(2));
				refAnswers.add(refAnswer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return refAnswers;
	}
	
	public String getRefAnswer(int exercise_id)
	{
		String sql = "select answer from exercises_solution where exercise_id = " + exercise_id;
		String result = null;
		try
		{			
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next())
				result = rs.getString(1);
			rs.close();
				
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}
	
	public List<RefAnswer> getRefAnswers(int exercise_id)
	{
		List<RefAnswer> refAnswers = new ArrayList<>();
		String sql = "select submission_id, submitted_answer from exercises_refAnswer  where exercise_id = " + exercise_id;
		try
		{
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next())
			{
				RefAnswer refAnswer = new RefAnswer(exercise_id, rs.getInt(1), rs.getString(2));
				refAnswers.add(refAnswer);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return refAnswers;
	}
	
	public List<ExerciseSubmission> getAllSubmission()
	{
		List<ExerciseSubmission> results = new ArrayList<ExerciseSubmission>();
		String sql  = "select * from exercises_codingexercisesubmission";

		sql = "select submission_id, submitted_answer, answer, exercise_id , is_correct "
				+ "from exercises_result ";
		try {
			ResultSet rSet = stmt.executeQuery(sql);
			while(rSet.next())
			{
				ExerciseSubmission submission = new ExerciseSubmission(rSet.getInt(1), rSet.getString(2), rSet.getString(3), rSet.getString(4), rSet.getBoolean(5));
				results.add(submission);
			}
			rSet.close();
		} catch (Exception e) {			
			System.out.println(e.getMessage());
		}		
		return results;
	}

	
	public static void main(String []args) {
		
		DBHelper helper = DBHelper.getInstance();
		
		/*List<String> updateToFalse = Arrays.asList(toFalse);
		helper.updateJudgement(updateToFalse, false);
		helper.updateScore(updateToFalse, 0);
		System.out.println("Mission Complete!");*/
		helper.exportResult("select distinct(trim(  submitted_answer)), exercisesubmission_ptr_id from submitanswer\n" + 
				"where is_correct = 1 and exercise_id = 24" );
		System.out.println("Mission Completed!");
		
        
    }
	


	public void executeUpdate(String sql) {
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void updateScore(List<ExerciseSubmission> submissions)
	{
		String sql = "update exercises_result set score = ? where submission_id  = ?";	
		try
		{
			conn.setAutoCommit(false);
			PreparedStatement ps = conn.prepareStatement(sql);
			for (ExerciseSubmission exerciseSubmission : submissions)
			{
				ps.setDouble(1, exerciseSubmission.getScore());
				ps.setInt(2, exerciseSubmission.getId());
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}

	public void updateScore(List<String> updateToTrue, float score)
	{		
		String sql;		
		sql = "update exercises_judgement set score = ? where id  = ?";		
		try {
			conn.setAutoCommit(false);
			PreparedStatement ps = conn.prepareStatement(sql);
			for (String id : updateToTrue) {
				ps.setFloat(1, score);
				ps.setString(2, id);
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

	public void updateSubmitAnswer(List<ExerciseSubmission> submissions)
	{
		String sql;		
		sql = "update exercises_result set submitted_answer = ? where submission_id  = ?";		
		try {
			conn.setAutoCommit(false);
			PreparedStatement ps = conn.prepareStatement(sql);
			for (ExerciseSubmission submission : submissions) {
				ps.setString(1, submission.getSubmitted_answer());
				ps.setInt(2, submission.getId());
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

	public void updateResult(List<ExerciseGrade> grades)
	{
		String sql;		
		sql = "update exercises_result set score = ? where submission_id  = ?";		
		try {
			conn.setAutoCommit(false);
			PreparedStatement ps = conn.prepareStatement(sql);
			for (ExerciseGrade submission : grades) {
				ps.setDouble(1, submission.getGrade());
				ps.setInt(2, submission.getSubmission_id());
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













}
