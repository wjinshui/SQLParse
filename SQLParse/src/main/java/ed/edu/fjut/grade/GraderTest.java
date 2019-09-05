package ed.edu.fjut.grade;

import cn.edu.fjut.bean.SQLTree;

public class GraderTest
{

	public static void main(String[] args)
	{
		SyntacticGrader grader = new SyntacticGrader();
		String stu_ans = " select id\n" + 
				"from person\n" + 
				"group by year_born\n" + 
				"having (year_born=1988);";
		String ref_ans1 = "select id from person\n" + 
				"where  year_born=(select year_born from person\n" + 
				"group by year_born order by year_born desc limit 1, 2);";
		String ref_ans2 = "SELECT id FROM person GROUP BY year_born ORDER BY year_born DESC LIMIT 1, 1 ";
		SQLTree stu_tree = grader.parseQuery(stu_ans);
		SQLTree ref_tree = grader.parseQuery(ref_ans1);
		SQLTree ref_tree2 = grader.parseQuery(ref_ans2);
		System.out.println(grader.calScore(ref_tree, stu_tree));
		System.out.println(grader.calScore( ref_tree2, stu_tree));
		
		
		

	}

}
