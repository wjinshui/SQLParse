package cn.edu.fjut.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 通过树结构来完成两个列表的排列组合输出．
 * 一个列表list1的内容有A,B,C
 * 另一个列表list2的内容有1,2,3,4时，它们的排列结合结果应该如main函数的执行结果．
 * * @author Wang Jinshui
 *
 * @param <T>
 */
public class PermutationTree<T>
{
	public static void main(String[] args)
	{
		List<String> list1 = new ArrayList<>();
		List<String> list2 = new ArrayList<>();
		list1.add("A");
		list1.add("B");
		list1.add("C");
		list2.add("1");
		list2.add("2");
		list2.add("3");
		list2.add("4");
		PermutationTree<String> tree = new PermutationTree<>(list1, list2);		
		List<PermutationTreeNode<String>> pathNodes = new ArrayList<>();
		List<List<List<PermutationTreeNode<String>>>>  pairRsult = tree.travelTree(tree.root, pathNodes);
		System.out.println("************");
		for (List<List<PermutationTreeNode<String>>> pairs : pairRsult)
		{
			for (List<PermutationTreeNode<String>> pair : pairs)
			{
				
				for (PermutationTreeNode<String> data : pair)
				{
					System.out.print(data.getData() + ",");
				}
				
			}
			System.out.println();
		}
		
	}
	
	List<T> listA, listB;	
	PermutationTreeNode<T> root;
	
	public PermutationTree(List<T> list1, List<T> list2)
	{
		listA = list1;
		listB = list2;
		initial();
	}
	
	private void initial()
	{
		if(listA.size() == 0 || listB.size() ==0)
			return;
		LinkedList<T> temp = new LinkedList<>(listA);
		root = new PermutationTreeNode<T>(temp.pop());		
		root.setChildren(listB);
		buildTree(root, temp, listB);
		pairResult = new ArrayList<>();
		
	}
	
	/**
	 * 用来构建树
	 * @param node
	 * @param firstList
	 * @param secondList
	 */
	private void buildTree(PermutationTreeNode<T> node, LinkedList<T> firstList, List<T> secondList)
	{
		
		T first = firstList.pop();
		for (PermutationTreeNode<T> child : node.getChildren())
		{
			PermutationTreeNode<T> markNode = new PermutationTreeNode<T>(first);
			child.addChild(markNode);			
			T data = child.getData();
			LinkedList<T> temp = new LinkedList<>(secondList);
			temp.remove(data); 			
			markNode.setChildren(temp);
			if(firstList.isEmpty() == false)
				buildTree(markNode, new LinkedList<T>(firstList), temp);			
		}
		
	}


	
	private void printList(List<PermutationTreeNode<T>> list)
	{
		for (int i = 0; i < list.size(); i++)
		{
			PermutationTreeNode<T> node = list.get(i);
			if(i == list.size() -1)
			{
				System.out.print(node.getData());
				break;
			}
			if (i % 2 == 0)
				System.out.print(node.getData() + ": ");
			else
				System.out.print(node.getData() + " -> ");			
		}
		System.out.println();		
	}
	
	public  List<List<PermutationTreeNode<T>>> getPairResult(List<PermutationTreeNode<T>> list)
	{
		List<List<PermutationTreeNode<T>>> result = new ArrayList<>();
		List<PermutationTreeNode<T>> pair = null;
		for(int i=0;i< list.size(); i++)
		{
			PermutationTreeNode<T> node = list.get(i);			
			if(i == list.size() -1)
			{
				pair.add(node);
				result.add(pair);
				break;
			}
				
			if(i % 2 == 0)
			{
				pair = new ArrayList<>();
				pair.add(node);
			}
			else
			{
				pair.add(node);
				result.add(pair);
			}
		}
		return result;
	}
	List<List<List<PermutationTreeNode<T>>>> pairResult;
	public List<List<List<PermutationTreeNode<T>>>>  travelTree(PermutationTreeNode<T> node, List<PermutationTreeNode<T>> pathNodes)
	{
		List<PermutationTreeNode<T>> list = new ArrayList<>(pathNodes);		 
		list.add(node);
		//已经到了叶子节点了．．
		if(node.getChildren().size() == 0)
		{
			printList(list);		
			pairResult.add( getPairResult(list));
		}
		else
		{
			for (PermutationTreeNode child : node.getChildren())
			{
				travelTree(child, list);
			}
		}
		return pairResult;
	}
}

class PermutationTreeNode<T>
{
	List<PermutationTreeNode<T>> children;
	T data;
	
	public PermutationTreeNode(T data)
	{
		this.data = data;
		children = new ArrayList<>();
	}

	



	public void addChild(PermutationTreeNode<T> child)
	{
		children.add(child);
		
	}





	@Override
	public boolean equals(Object obj)
	{
		PermutationTreeNode<T> node = (PermutationTreeNode<T>) obj;
		return getData().equals(node.getData());
	}


	public List<PermutationTreeNode<T>> getChildren()
	{
		return children;
	}
	
	public T getData()
	{
		return data;
	}



	@Override
	public int hashCode()
	{	
		return getData().hashCode();
	}

	public PermutationTreeNode<T> setChild(T child)
	{
		PermutationTreeNode<T> node = new PermutationTreeNode<T>(child);
		children.add(node);
		return node;
	}

	public void setChildren(List<T> listB)
	{
		for (T t : listB)
		{
			PermutationTreeNode<T> node = new PermutationTreeNode<T>(t);
			children.add(node);
		}
		
	}


	public void setData(T data)
	{
		this.data = data;
	}
}