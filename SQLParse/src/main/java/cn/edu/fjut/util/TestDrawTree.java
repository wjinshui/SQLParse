package cn.edu.fjut.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import cn.edu.fjut.bean.SQLTreeNode;

public class TestDrawTree extends JFrame{  
    
    public TestDrawTree(){  
        super("Test Draw Tree");  
        initComponents();  
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    } 
    SQLTreeNode node;
    public TestDrawTree(SQLTreeNode node)
    {
    	this.node = node;
    }
      
    public static void main(String[] args){  
        TestDrawTree frame = new TestDrawTree();  
          
        frame.setSize(800, 600);  
        frame.setVisible(true);  
        
    }   
      

    public void initComponents(){  
     
        //n.printAllNode(n);    //输出树  
          
        /* 
         * 创建一个用于绘制树的面板并将树传入,使用相对对齐方式 
         */  
        DrawTree  panel1 = new DrawTree(DrawTree.CHILD_ALIGN_RELATIVE);  
        panel1.setTree(node);  
          
        /* 
         * 创建一个用于绘制树的面板并将树传入,使用绝对对齐方式 
         */  
        DrawTree panel2 = new DrawTree(DrawTree.CHILD_ALIGN_ABSOLUTE);  
        panel2.setTree(node);  
        panel2.setBackground(Color.BLACK);  
        panel2.setGridColor(Color.WHITE);  
        panel2.setLinkLineColor(Color.WHITE);  
        panel2.setStringColor(Color.BLACK);  
          
        JPanel contentPane = new JPanel();  
        contentPane.setLayout(new GridLayout(2,1));  
        contentPane.add(panel1);  
        contentPane.add(panel2);  
          
        add(contentPane,BorderLayout.CENTER);  
    }  
}  