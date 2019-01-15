package cn.edu.fjut;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cn.edu.fjut.bean.ExerciseJudgement;
import cn.edu.fjut.bean.ExerciseSubmission;

public class DBHelper {
	
	public static DBHelper instance ;
	private Connection conn;
	private Statement stmt;

	private DBHelper()
	{
 
        String uid;       
        uid ="postgres";
        try{
            String url="jdbc:postgresql://127.0.0.1:5432/test";
            String user= uid;
            String password = uid;
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url, user, password);
            stmt = conn.createStatement();
           // System.out.println("是否成功连接pg数据库"+ conn);
           
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	
	public void insertExerciseJudgements(List<ExerciseJudgement> judgements)
	{
		try {
			String sql = "DELETE FROM exercises_judgement ";
			stmt.executeUpdate(sql);
			conn.setAutoCommit(false);
			sql = "INSERT INTO exercises_judgement( id, mismatch_content, mismatch_count)\n" + 
					"    VALUES (?, ?, ?);\n" ;
			PreparedStatement pst = conn.prepareStatement(sql);
			for (ExerciseJudgement exerciseJudgement : judgements) {
				pst.setInt(1, exerciseJudgement.getId());
				pst.setString(2, exerciseJudgement.getMismatch_content());
				pst.setInt(3, exerciseJudgement.getMismatch_counter());
				pst.addBatch();
			}
			pst.executeBatch();			
			conn.commit();
			conn.setAutoCommit(true);
			pst.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}	
	}
	
	public List<String> getAllSolution()
	{
		List<String> results = new ArrayList<>();
		String sql = "select answer from exercises_exercisesolution";
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next())
			{
				results.add(rs.getString(1));
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return results;
	}
	
	
	public List<ExerciseSubmission> getAllSubmission()
	{
		List<ExerciseSubmission> results = new ArrayList<>();
		String sql  = "select * from exercises_codingexercisesubmission";
		sql = "select exercisesubmission_ptr_id, submitted_answer, answer, exercises_exercisesubmission.exercise_id\n" + 
				"from exercises_codingexercisesubmission, exercises_exercisesubmission, exercises_exercisesolution\n" + 
				"where exercises_codingexercisesubmission.exercisesubmission_ptr_id = exercises_exercisesubmission.id \n" + 
				"and exercises_exercisesolution.exercise_id = exercises_exercisesubmission.exercise_id";
		try {
			ResultSet rSet = stmt.executeQuery(sql);
			while(rSet.next())
			{
				ExerciseSubmission submission = new ExerciseSubmission(rSet.getString(1), rSet.getString(2), rSet.getString(3), rSet.getString(4));
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

}
