package cn.edu.fjut.bean;

public class ExerciseJudgement {
	int id, mismatch_counter;
	String mismatch_content;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getMismatch_counter() {
		return mismatch_counter;
	}
	public void setMismatch_counter(int mismatch_counter) {
		this.mismatch_counter = mismatch_counter;
	}
	public String getMismatch_content() {
		return mismatch_content;
	}
	public void setMismatch_content(String mismatch_content) {
		this.mismatch_content = mismatch_content;
	}
	public ExerciseJudgement(int id, int mismatch_counter, String mismatch_content) {
		super();
		this.id = id;
		this.mismatch_counter = mismatch_counter;
		this.mismatch_content = mismatch_content;
	}
	
}
