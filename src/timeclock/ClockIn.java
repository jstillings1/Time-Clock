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

import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;



/**
 * This class clocks in the student
 * 
 */
public class ClockIn extends JFrame implements ActionListener 
{
    private JFrame frame = new JFrame("Clock In");
    private JTextField studentId;
    private String first;
    private String last;
    private final String DRIVER= "org.apache.derby.jdbc.EmbeddedDriver";
    private final String JDBC_URL = "jdbc:derby:student_;create=true";
    
    public ClockIn()
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
    } //clock in end
    
    /** this is the listener for after a student enters student id */
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
                    System.out.println("Student Id Does not Exist: Creating Student");
                    CreateStudent cs = new CreateStudent(number);
                    frame.dispose();
                }
                else
                {
                    // get names based on input student id
                    String firstName = FirstNameCheck(number);
                    String lastName = LastNameCheck(number);
                    String enteredId = studentId.getText();
                    String classId = ClassIdSet(number);
                    int idOfClass = Integer.valueOf(classId);
                    if (!AlreadyClockedIn(number))
                    {
                        try
                            {
                                TimeStamp(firstName, lastName, enteredId, idOfClass);
                            } catch (SQLException ex) 
                            {

                                Logger.getLogger(CreateStudent.class.getName()).log(Level.SEVERE, null, ex);
                            }
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null, "Your aleady clocked in");
                    }
                }
            } catch (SQLException ex) 
            {
                Logger.getLogger(ClockIn.class.getName()).log(Level.SEVERE, null, ex);
            }
            studentId.setText("");
            frame.dispose();
        };

   
         
    /** writes the actual clock in to the derby database
     * 
     * @param first this the students first name
     * @param last this is the students last name
     * @param number this is the students id
     * @param classId this is the primary key of the class table
     * @throws SQLException 
     */
     
    public void TimeStamp(String first, String last, String number, Integer classId)throws  SQLException 
    {
        // create time stamp
        Timestamp time = new Timestamp(System.currentTimeMillis());
        // look up class id based on name
     
        Connection connection2 = DriverManager.getConnection(JDBC_URL);
        String sql2 = "INSERT INTO STUDENT_ ( first_name_, last_name_,  student_id_number_,class_id_, clock_in_time_, clock_out_time_  ) VALUES ( ?, ?, ?, ?, ?,? )  " ;
        PreparedStatement ps1 = connection2.prepareStatement( sql2 );
        ps1.setString( 1, first );
        ps1.setString( 2, last );
        ps1.setString( 3, number );
        ps1.setObject( 4, classId );
        ps1.setObject( 5 , time ) ;
        ps1.setObject( 6 , null ) ;
        ps1.executeUpdate();
        connection2.commit();
        JOptionPane.showMessageDialog(null, "You are clocked in");
        System.out.println(" Student sucessfully clocked in");
        ps1.close();
        connection2.close();
    }// end time stamp
    /** checks to see if the student is already registered and either need to just clock in or register by passing true or false back to caller */
    private boolean  StudentCheck(String number)throws  SQLException
    {
        Connection connection = DriverManager.getConnection(JDBC_URL);
        ResultSet result = null;  
        String sql2 = "select student_id_number_ from student_ where student_id_number_ = ? "; 
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
    } // end student check
    //** looks up student first name if already registered */
    private String  FirstNameCheck(String number)throws  SQLException
    {
        Connection connection = DriverManager.getConnection(JDBC_URL);
        String sql2 = "select first_name_ from student_ where student_id_number_ = ? "; 
        PreparedStatement ps1 = connection.prepareStatement( sql2 );
        ps1.setString( 1, number);
        ResultSet result = ps1.executeQuery();
        if(!result.next())
        {
            System.out.println("No Data Found"); //data not exist
            return null;
        }
        else
        {
            String existingFirstName= result.getString("first_name_");
            System.out.println("Student First Name Found it is " + existingFirstName);
            return existingFirstName;
        }   
    }// end first name check
    /** looks up student last name if already registered */
     private String  LastNameCheck(String number)throws  SQLException
    {
        Connection connection = DriverManager.getConnection(JDBC_URL);
        String sql2 = "select last_name_ from student_ where student_id_number_ = ? "; 
        PreparedStatement ps1 = connection.prepareStatement( sql2 );
        ps1.setString( 1, number);
        ResultSet result = ps1.executeQuery();
        if(!result.next())
        {
            System.out.println("No Data Found"); //data not exist
            return null;
        }
        else
        {
            String existingLastName= result.getString("last_name_");
            System.out.println("Student Last Name Found it is "+ existingLastName );
            return existingLastName;
        }
    } // end last name check
    /** looks up students assigned class */
    private String  ClassIdSet(String number)throws  SQLException
    {
        Connection connection = DriverManager.getConnection(JDBC_URL);
        String sql2 = "select class_id_ from student_ where student_id_number_ = ? "; 
        PreparedStatement ps1 = connection.prepareStatement( sql2 );
        ps1.setString( 1, number);
        ResultSet result = ps1.executeQuery();
        if(!result.next())
        {
            System.out.println("No Data Found"); //data not exist
            return null;
        }
        else
        {
            String classId= result.getString("class_id_");
            System.out.println("Class id is  "+ classId );
            return classId;
        }   
    } // end class idset
    // looks up clock in time is student was already clocked in
    /**not currently used in this program. instead program uses alreadyclockedin()*/
     private String  ClockInTimeCheck(String number)throws  SQLException
    {
        Connection connection = DriverManager.getConnection(JDBC_URL);
        String sql2 = "select clock_in_time_ from student_ where student_id_number_ = ? "; 
        PreparedStatement ps1 = connection.prepareStatement( sql2 );
        ps1.setString( 1, number);
        ResultSet result = ps1.executeQuery();
        if(!result.next())
        {
            System.out.println("No Data Found"); //data not exist
            return null;
        }
        else
        {
            String time= result.getString("clock_in_time_");
            System.out.println("Student clock in Found it is "+ time );
            return time;
        }   
    }
    /** checks to see if the student is already clocked in */
    private boolean AlreadyClockedIn(String number)throws  SQLException 
    {
        Connection connection = DriverManager.getConnection(JDBC_URL);
        ResultSet result = null;  
        String sql2 = "select student_id_number_ from student_ WHERE  student_id_number_ =  ? and clock_out_time_  is NULL ";
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
            System.out.println("Student already clocked in!");
            return true;
        }   
    }// end already checked in
};
            


