/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timeclock;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;



/**
 * This class allows the admin to add class and contract numbers to the database 
 */
public class AddClass extends JFrame implements ActionListener 
    {
       private JFrame frame = new JFrame("Add Classes");
       private JTextField NewClass;
       private JTextField NewNumber;
       private JList jlist = new JList(Globals.listModel);
       private JScrollPane scrollPane1 = new JScrollPane(jlist);
       private int XCounter = 0;
       private final String DRIVER= "org.apache.derby.jdbc.EmbeddedDriver";
       private final String JDBC_URL = "jdbc:derby:student_;create=true";
      

       public AddClass()  
       {
         JLabel AddLabel;  
         JLabel AddLabel2;
         JLabel ClassesLoadedAlready;
         JButton submitButton = new JButton("Submit"); 
        // font for Submit Buton
        submitButton.setFont(new Font("Serif", Font.BOLD, 16));
        submitButton.addActionListener(this);
         // if first run import the global list into the model
       if (Globals.firstrun == 0){
         String[] CurrentClass = Globals.CurrentClassArray.toArray(new String[0]);
          for(String val : CurrentClass)
            Globals.listModel.addElement(val);
           Globals.firstrun= 1;
       }

       
       frame.setSize(800, 500);

       frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

       
      
       jlist.setFont(new Font("Serif", Font.BOLD, 24));
       Container contentPane = frame.getContentPane();
      // Define Parts of ui

       AddLabel = new JLabel("Enter New Class Name :",JLabel.LEFT);
       AddLabel.setFont(new Font("Serif", Font.BOLD, 24));
       AddLabel2 = new JLabel("Enter New Class Number:",JLabel.LEFT);
       AddLabel2.setFont(new Font("Serif", Font.BOLD, 24));
       ClassesLoadedAlready = new JLabel("Current Classes:",JLabel.LEFT);
       ClassesLoadedAlready.setFont(new Font("Serif", Font.BOLD, 24));
               
       NewClass = new JTextField(20);
       NewNumber = new JTextField(20);
       NewNumber.setPreferredSize(new Dimension(120,48));
       NewNumber.setFont(new Font("Serif", Font.BOLD, 24));
       
      

       NewClass.setPreferredSize(new Dimension(120,48));
       NewClass.setFont(new Font("Serif", Font.BOLD, 24));
       
       JScrollPane scrollPane1 = new JScrollPane(jlist);
       
       //Set up Gui
       SpringLayout layout = new SpringLayout();
       contentPane.setLayout(layout);
       
        layout.putConstraint(SpringLayout.WEST,ClassesLoadedAlready ,
                       5,
                        SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.NORTH, ClassesLoadedAlready,
                      5,
                           SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.WEST, AddLabel,
                     5,
                        SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.NORTH, AddLabel,
                      50,
                           SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.WEST, AddLabel2,
                       5,
                        SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.NORTH, AddLabel2,
                      90,
                           SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.NORTH, scrollPane1,
                        50,
                        SpringLayout.NORTH, contentPane);
       
        layout.putConstraint(SpringLayout.WEST, NewClass,
                        23,
                        SpringLayout.EAST, AddLabel);
        layout.putConstraint(SpringLayout.NORTH, NewClass,
                        50,
                        SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.SOUTH, contentPane,
                        5,
                        SpringLayout.SOUTH, scrollPane1);  

        layout.putConstraint(SpringLayout.WEST, AddLabel,
                        5,
                        SpringLayout.EAST, scrollPane1);
        layout.putConstraint(SpringLayout.WEST, AddLabel2,
                      5,
                        SpringLayout.EAST, scrollPane1);
        layout.putConstraint(SpringLayout.NORTH, scrollPane1,
                        50,
                        SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.WEST,NewNumber,
                        5,
                        SpringLayout.EAST, AddLabel2 );
        layout.putConstraint(SpringLayout.NORTH, NewNumber,
                       100,
                        SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.WEST, submitButton,
                        380,
                        SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.NORTH, submitButton,
                      210,
                           SpringLayout.NORTH, contentPane);


       // add parts to content pane
       contentPane.add(scrollPane1);
       contentPane.add(AddLabel); 
       contentPane.add(AddLabel2);
       contentPane.add(ClassesLoadedAlready);
       contentPane.add(NewClass);
       contentPane.add(NewNumber);
       contentPane.add(submitButton); 
       try {
                    // dump the list model before retrieving from database
                    Globals.listModel.removeAllElements();
                    // end dump
                    ViewClass();
                            } catch (SQLException ex) {
                    Logger.getLogger(AddClass.class.getName()).log(Level.SEVERE, null, ex);
                }


           //Display the window.
          
           frame.setVisible(true);
        }// end method add class



       /** this is the submit buttons listener
       */
      
        public void actionPerformed(ActionEvent evt) 
        {

           String text = NewClass.getText();
           String number = NewNumber.getText();

           String blank = ("");

         if (text.equals(blank)){ 
            JOptionPane.showMessageDialog(null, "You forgot to add a name");

         }
          if (number.equals(blank)){ 
            JOptionPane.showMessageDialog(null, "You forgot to add a class number");

         }
         else{
             // add catch for unique name if not add a letter X to name
            if (Globals.CurrentClassArray.contains(text)){
                XCounter= XCounter + 1;
                text =text + XCounter;


                
                Globals.CurrentClassArray.add(new String(text));
                Globals.listModel.addElement(text);
               
                


         NewClass.setText("");
          try {
            /* pane.getStyledDocument().insertString(
                 0,
                 "Zoner ["
                     + text+ "] added!\n",
                 null);*/
           } catch (Exception ex) {
             ex.printStackTrace();
           }}
            else
            {         
                Globals.CurrentClassArray.add(new String(text));
                Globals.listModel.addElement(text);
               
                try {
                    UpdateClass(text,number);
                } catch (SQLException ex) {
                    Logger.getLogger(AddClass.class.getName()).log(Level.SEVERE, null, ex);
                }


                NewClass.setText("");
                NewNumber.setText("");
                
            }
        }
      };
        
        /** this writes the new class created to the derby database
         * 
         * @param text This is the inputed class name
         * @param number This is the inputed class contract number
         * @throws SQLException 
         */
      public void UpdateClass(String text, String number) throws  SQLException
        {
        //This inserts class table
        Connection connection = DriverManager.getConnection(JDBC_URL);
        String sql2 = "INSERT INTO CLASS_ ( class_, class_number_) VALUES ( ?, ? )  " ;
        PreparedStatement ps1 = connection.prepareStatement( sql2 );
        ps1.setString( 1, text);
        ps1.setString( 2, number);
        ps1.executeUpdate();
        connection.commit();
        System.out.println(" Class record sucessfully inserted");
        ps1.close();
        connection.close();
        }
        /** this method reads all the classes already created in the derby database 
         * and puts them in the list for viewing in add and deletion in delete class
         
         * @throws SQLException
         * 
         */ 
        public  void ViewClass() throws SQLException
        {
            Connection connection2 = DriverManager.getConnection(JDBC_URL);
	    String sql3 = "SELECT class_ FROM CLASS_ ORDER BY class_";
            try 
            {
                PreparedStatement ps2 = connection2.prepareStatement(sql3);
		//Execute select SQL stetement
		ResultSet rs = ps2.executeQuery();
                while (rs.next())
                {
                    String existingClass = rs.getString("class_");
                    // add to scrollpanel
                    Globals.listModel.addElement(existingClass);
                    System.out.println("class is  : " + existingClass);
		}
                ps2.close();
                connection2.close();

            }
            catch (SQLException e) 
            {
        	System.out.println(e.getMessage());
            }

                        
        System.out.println(" Class record sucessfully retrieved");
        }// end view class
    }// end class addclass
