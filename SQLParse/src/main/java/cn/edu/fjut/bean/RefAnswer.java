package cn.edu.fjut.bean;

/**
 * 参考答案，用于最终对提交的SQL进行评分的基础．
 * 相对于原始的SQL语句，删除了注释，无用部份等内容
 * @author admin-u1064462
 *
 */
public class RefAnswer {
	int exercise_id;
	int id;
	String answer;
	public int getExercise_id() {
		return exercise_id;
	}
	public void setExercise_id(int exercise_id) {
		this.exercise_id = exercise_id;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public RefAnswer(int exercise_id, int id, String answer) {
		super();
		this.exercise_id = exercise_id;
		this.id = id;
		this.answer = answer;
	}
	
	
}
