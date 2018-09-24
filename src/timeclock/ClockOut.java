/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timeclock;

/**
 *
 * @author jstil
 */

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
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

/** This class clocks out the student */
public class ClockOut  extends JFrame implements ActionListener 
{
    private JFrame frame = new JFrame("Clock Out");
    private JTextField studentId;
    private String first;
    private String last;
    private final String DRIVER= "org.apache.derby.jdbc.EmbeddedDriver";
    private final String JDBC_URL = "jdbc:derby:student_;create=true";
    
    public ClockOut()
    {
        JLabel AddLabel; 
        frame.setSize(500, 200);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Container contentPane = frame.getContentPane();
        // add instructions
        AddLabel = new JLabel("Enter Student ID :",JLabel.LEFT);
        AddLabel.setFont(new Font("Serif", Font.BOLD, 24));
        studentId = new JTextField(6);
        studentId.setPreferredSize(new Dimension(120,48));
        studentId.addActionListener(this);
        //Set up Gui
        SpringLayout layout = new SpringLayout();
        contentPane.setLayout(layout);
        layout.putConstraint(SpringLayout.WEST, AddLabel,
                       30,
                        SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.NORTH, AddLabel,
                     50,
                           SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.WEST, studentId,
                       5,
                        SpringLayout.EAST, AddLabel);
        layout.putConstraint(SpringLayout.NORTH, studentId,
                      50,
                           SpringLayout.NORTH, contentPane);
         
         // add content to swing
        contentPane.add(AddLabel);
        contentPane.add(studentId);
        //Display the window.
        pack();
        frame.setVisible(true);
    
    }// end clock out
    /** listener for the clock out button press*/
   public void actionPerformed(ActionEvent evt) 
        {
        String number = studentId.getText();
        String blank = ("");
        System.out.println("Action Fired");
        // check for blank number
        if (number.equals(blank))
        { 
            JOptionPane.showMessageDialog(null, "You forgot to add a number");

        }
        try 
        {
            if (!StudentCheck(number))
            {
                System.out.println("Student Id Does not Exist: Try again");
                 JOptionPane.showMessageDialog(null, "Your clock in punch could not be found. Try entering your student id carefully");
            }
            else
            {
                String enteredId = studentId.getText();
                try
                {
                    TimeStamp( enteredId);
                    frame.dispose();
                } catch (SQLException ex) 
                {
                
                    Logger.getLogger(CreateStudent.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (SQLException ex) 
        {
            Logger.getLogger(ClockIn.class.getName()).log(Level.SEVERE, null, ex);
        }
        studentId.setText("");
    }; //end listener
   
   /** writes the clock out to the correct row in the data base
    * 
    * @param number this is the student id number
    * @throws SQLException 
    */
   
   public void TimeStamp( String number)throws  SQLException 
    {
        // create time stamp
        Timestamp time = new Timestamp(System.currentTimeMillis());
        Connection connection2 = DriverManager.getConnection(JDBC_URL);
        String sql2 = "UPDATE student_ SET clock_out_time_ = ? WHERE  student_id_number_ =  ? and clock_out_time_  is NULL ";
        PreparedStatement ps1 = connection2.prepareStatement( sql2 );
        ps1.setObject( 1, time );
        ps1.setObject( 2, number);
        ps1.executeUpdate();
        connection2.commit();
        JOptionPane.showMessageDialog(null, "You are clocked Out");
        System.out.println(" Student sucessfully clocked Out");
        ps1.close();
        connection2.close();
    }// end time stamp
   
   /** checks if student exists as clocked in */
   private boolean  StudentCheck(String number)throws  SQLException
    {
        Connection connection = DriverManager.getConnection(JDBC_URL);
        Statement stmt = connection.createStatement();
        ResultSet result = null;  
        String sql2 = "select student_id_number_ from student_ where student_id_number_ =  ? ORDER BY student_id_number_ DESC FETCH FIRST 1 ROWS ONLY "; 
        PreparedStatement ps1 = connection.prepareStatement( sql2 );
        ps1.setString( 1, number);
        result = ps1.executeQuery();
        if(!result.next())
        {
            System.out.println("No Data Found"); //data not exist
            return false;
        }
        else
        {
            System.out.println("Student Found");
            return true;
        }   
    }// end student check
}// end clock out
