package cs174a;

import javax.swing.*;

import java.awt.*;

import java.util.ArrayList;
import java.util.Arrays;

public class InputForm extends JPanel{
		private JButton button;
		private JLabel message;
		private ArrayList<JTextField> fields;

		public InputForm(ArrayList<String> l, JButton b){
			super(new GridLayout(3, 1));
			fields = new ArrayList<JTextField>();
			for(int i=0; i< l.size();i++){
				add(new JLabel(l.get(i)));
				JTextField t;
				t= new JTextField();
				t.setHorizontalAlignment(JTextField.CENTER);
				fields.add(i,t);
				add(t);
			}
			this.message= new JLabel();
			add(message);
			this.button= b;
			add(button);

			//Odd number of texboxes looks fucky. Gotta do this nasty shit
			if(l.size()%2 != 0){
				add(new JLabel());
				add(new JLabel());
			}
		}
		public String getInput(int l_num){
			return fields.get(l_num).getText();
		}
		public void setLabel(String text, Color c){
			this.message.setText(text);
			this.message.setForeground(c);
		}
		public void resetLabel(){
			this.message.setText("");
		}
		public void resetFields(){
			for (int i =0; i < fields.size(); i++){
				//System.out.println("Erasing fields");
				fields.get(i).setText("");
		}
		resetLabel();
	}
}