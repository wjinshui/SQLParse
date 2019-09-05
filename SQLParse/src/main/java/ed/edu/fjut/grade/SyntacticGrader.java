package ed.edu.fjut.grade;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.edu.fjut.bean.ExerciseGrade;
import cn.edu.fjut.bean.ExerciseSubmission;
import cn.edu.fjut.bean.GradeApproach;
import cn.edu.fjut.bean.SQLTree;

public class SyntacticGrader extends BaseSyntacticGrader
{
	public SyntacticGrader()
	{
		super();
		approach = GradeApproach.syntatic;		
	}
	
	

	@Override
	protected void gradeStatement(int exercise_id)
	{
		gradeNoninterpretable(exercise_id);
		gradeInterpretable(exercise_id);
	}

	private void gradeInterpretable(int exercise_id)
	{
		List<ExerciseSubmission> submissions = dbHelper
				.getSubmissionWithCond(" where remark != 'noninterpretable' and exercise_id =   " + exercise_id);
		for (ExerciseSubmission submission : submissions)
		{			
			SQLTree stuTree = parser.parse(submission.getSubmitted_answer().toLowerCase());
			SQLTree refTree = parser.parse( submission.getRef_answer().toLowerCase());
			ExerciseGrade grade = new ExerciseGrade(submission.getId(), submission.getSubmitted_answer(), approach);
			grade.setGrade( calScore(stuTree, refTree) );
			grade.setMost_similary( submission.getRef_answer() );
			grades.add(grade);				
		}		
	}
	
	
	
	
	/**
	 * score = | treeNodes inter refNodes | / |refNodes| - |treeNodes - refNodes | / |treeNodes|
	 * @param tree
	 * @param refTree
	 * @return
	 */
	public double calScore(SQLTree tree, SQLTree refTree)
	{		
		Set<String> stuNodes =  tree.getNodes();
		Set<String> refNodes = refTree.getNodes();
		stuNodes.remove("ROOT");
		refNodes.remove("ROOT");
		Set<String> intersection = new HashSet<>(stuNodes);		
		intersection.retainAll(refNodes);
		double inter = intersection.size() * 1.0 / refNodes.size();
		Set<String> diffSection = new HashSet<>(stuNodes);
		diffSection.removeAll(refNodes);
		double diff = diffSection.size() * 1.0 / stuNodes.size();
		return inter - diff > 0 ? inter -diff: 0;		
	}

}
