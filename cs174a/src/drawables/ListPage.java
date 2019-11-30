package cs174a;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.awt.*;

import java.util.ArrayList;
import java.util.Arrays;

public class ListPage extends JPanel{
	private JTextField input;
	private JScrollPane scroll;
	private JTable table;
	private JList list;
	private JLabel label;
	private ArrayList <ArrayList<String>> row_elements;
	private String previous_search;

	public ListPage(JButton b, String l, String[] col_names, ArrayList <ArrayList<String>> disp_list){
		//add Layout manager if need
		super();

		//Create search bar/action field
		JPanel holder = new JPanel(new GridLayout(1, 1));
		input = new JTextField();
		input.setHorizontalAlignment(JTextField.CENTER);	
		holder.add(new JLabel(l));
		holder.add(input);

		//Button and label
		JPanel b_label= new JPanel(new GridLayout(1,1));
		label= new JLabel();
		b_label.add(label);
		b_label.add(b);

		JPanel button_text_holder= new JPanel(new GridLayout(2, 1));
		button_text_holder.add(holder);
		button_text_holder.add(b_label);
		add(button_text_holder);
		
		createTable(col_names, disp_list);	
		row_elements = new ArrayList<ArrayList<String>>(disp_list.size());
		//Copy info to make less queries
		for(ArrayList<String> p : disp_list) {
			row_elements.add((ArrayList<String>)disp_list.clone());
		}


		/*
	    DefaultListModel listModel;
	    listModel = new DefaultListModel();
	    for(int i =0; i < disp_list.size(); i++){
	    	listModel.addElement(disp_list.get(i));
	    }
	    //Create the list and put it in a scroll pane.
	    list = new JList(listModel);
	    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    list.setSelectedIndex(0);
	    //list.addListSelectionListener(this);
	    list.setVisibleRowCount(5);
	    JPanel list_holder= new JPanel();
	    list_holder.setLayout(new BorderLayout());
	    scroll = new JScrollPane(list);*/


	}
	public ListPage(JButton b, String l){
		//add Layout manager if need
		super();
		//Create search bar/action field
		JPanel holder = new JPanel(new GridLayout(1, 1));
		input = new JTextField();
		input.setHorizontalAlignment(JTextField.CENTER);	
		holder.add(new JLabel(l));
		holder.add(input);

		//Button and label
		JPanel b_label= new JPanel(new GridLayout(1,1));
		label= new JLabel();
		b_label.add(label);
		b_label.add(b);

		JPanel button_text_holder= new JPanel(new GridLayout(2, 1));
		button_text_holder.add(holder);
		button_text_holder.add(b_label);
		add(button_text_holder);



	}
	//Allows the creation of a table dynamically if not created in constructor.
	public void createTable(String[] col_names,  ArrayList< ArrayList<String> > disp_list){

			DefaultTableModel tableModel = new CustomTableModel(col_names, 0);
		    //Init table with tableModel
		    if(scroll != null){
		    	System.out.println("Table Exists");
		    	remove(scroll);		    
		    }
		    table= new JTable(tableModel);
		    //All should be of equal length for algorithm to work
		    //Outer loop goes for the length of the last lists and inner over the len of list of lists
		    for(int i =0; i < disp_list.get(0).size(); i++){
		    	//Sise of array is the number of col
		    	Object[] row_data= new Object[col_names.length];
		    	//Get all the values for a row. When loop over we move to next row
		    	for (int j=0; j < col_names.length; j++){
		    		row_data[j]= disp_list.get(j).get(i);
		    	}
		    	tableModel.addRow(row_data);
		    }
		    scroll = new JScrollPane();
		    scroll.setViewportView(table);
		    //JPanel list_holder= new JPanel();
		    //list_holder.add(scroll, BorderLayout.NORTH);

		    //Make the strings in a cells be centered
		    setCellsAlignment();
		    table.setRowHeight(25); 
		    //Select how many rows to display at a time
		    //table.setPreferredScrollableViewportSize(table.getPreferredSize());
		    //table.setBackground(Color.gray);
		    this.add(scroll);
		    this.revalidate();
		    this.repaint();
		    row_elements = new ArrayList<ArrayList<String>>(disp_list.size());
			//Copy info to make less queries
			for(ArrayList<String> p : disp_list) {
				row_elements.add((ArrayList<String>)disp_list.clone());
			}
	}
	private void setCellsAlignment()
    {
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        TableModel tableModel = table.getModel();

        for (int columnIndex = 0; columnIndex < tableModel.getColumnCount(); columnIndex++)
        {
            table.getColumnModel().getColumn(columnIndex).setCellRenderer(rightRenderer);
        }
    }
   
    public void setPrevious(String s){
    	this.previous_search= s;
    }
    public String getSearch(){
    	return previous_search;
    }
    public String getInput(){
		return input.getText();
	}
	public void setLabel(String text, Color c){
		this.label.setText(text);
		this.label.setForeground(c);
	}
	public void resetLabel(){
		this.label.setText("");
	}
	public void resetFields(){
		input.setText("");
		resetLabel();
	}


    class CustomTableModel extends DefaultTableModel{
    	public CustomTableModel(String[] col_names, int action){
    		super(col_names, action);
    	}
    	//Cells cannot be edited
    	@Override
    	public boolean isCellEditable(int row, int column) {
		    return false;
		}
		@Override
		public void moveRow(int start, int end, int to){
		  	return;
		}
    }
		
}