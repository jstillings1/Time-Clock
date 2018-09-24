/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timeclock;

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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;


/**
 * This class allows a student to self register for a class
 * 
 */
public class CreateStudent extends JFrame implements ActionListener
{
    private JFrame frame = new JFrame("Add Student");
    private JTextField firstEnter;
    private JTextField lastEnter;
    private JTextField idEnter;
    private JComboBox classEnter;
    private JPanel panel;
    private final String DRIVER= "org.apache.derby.jdbc.EmbeddedDriver";
    private final String JDBC_URL = "jdbc:derby:student_;create=true";
    
    /** 
     * This creates a student in the database
     * @param studentId This is the student ID
     */
    public CreateStudent(String studentId)
    {
        JLabel AddLabel;
        JLabel AddLabel2;
        JLabel AddLabel3;
        JLabel AddLabel4;
        frame.setSize(500, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // add instructions
        AddLabel = new JLabel("Enter First Name :",JLabel.LEFT);
        AddLabel2 = new JLabel("Enter Last Name :",JLabel.LEFT);
        AddLabel3 = new JLabel("Enter your Student ID :", JLabel.LEFT);
        AddLabel4 = new JLabel("Pick the class you are here for from the menu :",JLabel.LEFT);
        firstEnter = new JTextField(20);
        lastEnter = new JTextField(20);
        idEnter = new JTextField(6);
        classEnter = new JComboBox();
        JButton submitButton = new JButton("Submit"); 
        // font for Submit Buton
        submitButton.setFont(new Font("Serif", Font.BOLD, 16));
        submitButton.addActionListener(this);
        // new panel  creation be cause ContentPane panel = frame.getContentPane(); has been depreciated
        panel = new JPanel();
        
        //Set up Gui
        SpringLayout layout = new SpringLayout();
        panel.setLayout(layout);
        layout.putConstraint(SpringLayout.WEST, AddLabel,
                       5,
                        SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, AddLabel,
                      50,
                           SpringLayout.NORTH, panel);
        // 
        layout.putConstraint(SpringLayout.WEST, firstEnter,
                       5,
                        SpringLayout.EAST, AddLabel);
        layout.putConstraint(SpringLayout.NORTH, firstEnter,
                      50,
                           SpringLayout.NORTH, panel);
        //
        layout.putConstraint(SpringLayout.WEST, AddLabel2,
                        5,
                        SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, AddLabel2,
                      90,
                           SpringLayout.NORTH, panel);
        //
        layout.putConstraint(SpringLayout.WEST, AddLabel3,
                        5,
                        SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, AddLabel3,
                      130,
                           SpringLayout.NORTH, panel);
        //
        layout.putConstraint(SpringLayout.WEST, lastEnter,
                       5,
                        SpringLayout.EAST, AddLabel2);
        layout.putConstraint(SpringLayout.NORTH, lastEnter,
                      90,
                           SpringLayout.NORTH, panel);
        //
        layout.putConstraint(SpringLayout.WEST, idEnter,
                       5,
                        SpringLayout.EAST, AddLabel3);
        layout.putConstraint(SpringLayout.NORTH, idEnter,
                      130,
                           SpringLayout.NORTH, panel);
        //
        layout.putConstraint(SpringLayout.WEST, AddLabel4,
                        5,
                        SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, AddLabel4,
                      180,
                           SpringLayout.NORTH, panel);
        //
        layout.putConstraint(SpringLayout.WEST, classEnter,
                       5,
                        SpringLayout.EAST, AddLabel4);
        layout.putConstraint(SpringLayout.NORTH, classEnter,
                      180,
                           SpringLayout.NORTH, panel);
        //
        layout.putConstraint(SpringLayout.WEST, submitButton,
                        380,
                        SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, submitButton,
                      210,
                           SpringLayout.NORTH, panel);
        
        
         // add content to swing
        panel.add(AddLabel);
        panel.add(AddLabel2);
        panel.add(AddLabel3);
        panel.add(AddLabel4);
        panel.add(firstEnter);
        panel.add(lastEnter);
        panel.add(idEnter);
        panel.add(classEnter);
        panel.add(submitButton); 
        //Display the window.
        try {
                ViewClass();
            } catch ( SQLException ex) {
                Logger.getLogger(CreateStudent.class.getName()).log(Level.SEVERE, null, ex);
            }
        pack();
        frame.setContentPane(panel);
        frame.setVisible(true);
        
    }// end createstudent
    /** listener on submit
     * @param evt event is the click of the submit button
     */
    @Override
    public void actionPerformed(ActionEvent evt) 
        {
            String blank = ("");
            String firstName = firstEnter.getText();
            String lastName = lastEnter.getText();
            String studentId =idEnter.getText();
            String className = classEnter.getSelectedItem().toString();
            String classId = null;
            try 
            {
                classId = ClassIdSet(className);
            } catch (SQLException ex) 
            {
                Logger.getLogger(CreateStudent.class.getName()).log(Level.SEVERE, null, ex);
            }
            int idOfClass = Integer.valueOf(classId);
            if (firstName.equals(blank))
            { 
                JOptionPane.showMessageDialog(null, "You forgot to add a First Name");

            }
            if (lastName.equals(blank))
            { 
                JOptionPane.showMessageDialog(null, "You forgot to add a Last Name");

            }
            System.out.println(firstName + "  and  " + lastName + " recorded" );
            try 
            {
                TimeStamp(firstName, lastName, studentId, idOfClass);
            } catch (SQLException ex) 
            {
                Logger.getLogger(CreateStudent.class.getName()).log(Level.SEVERE, null, ex);
            }
            frame.dispose();
        }; //end listener
    /** this gets the classes and loads them to the combo box
     * 
     
     * @throws SQLException 
     */
    public  void ViewClass() throws  SQLException
        {
            Connection connection3 = DriverManager.getConnection(JDBC_URL);
            String sql3 = "SELECT class_ FROM CLASS_ ORDER BY class_";
            try 
            {
                PreparedStatement ps2 = connection3.prepareStatement(sql3);
		// execute select SQL stetement
		ResultSet rs = ps2.executeQuery();
        	while (rs.next()) 
                {
                    String existingClass = rs.getString("class_");
                    // add to scrollpanel
                    classEnter.addItem(existingClass);
                    System.out.println("classes loaded to combo box");
                }
                ps2.close();
                connection3.close();

            } catch (SQLException e)
            {
        	System.out.println(e.getMessage());
            }
            System.out.println(" Class record sucessfully retrieved");
        }// end view class
    /** 
     * this inserts the timestamp  into the derby database
     *
     * @param first student first name
     * @param last student last name
     * @param number student student Id
     * @param classId primary key of the class table
     * @throws SQLException
     */
    public void TimeStamp(String first, String last, String number, Integer classId)throws  SQLException 
    {
        // create time stamp
        Timestamp time = new Timestamp(System.currentTimeMillis());
        // look up class id based on name
     
        Connection connection2 = DriverManager.getConnection(JDBC_URL);
        String sql2 = "INSERT INTO STUDENT_ ( first_name_, last_name_,  student_id_number_, class_id_, clock_in_time_, clock_out_time_  ) VALUES ( ?, ?, ?, ?, ?,? )  " ;
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
    /** retrieves the class id from database
     * @param name
     * @throws SQLException
     */
    private String ClassIdSet(String name)throws  SQLException
    {
        Connection connection = DriverManager.getConnection(JDBC_URL);
        String sql2 = "select ID_ from class_ where class_ = ? "; 
        PreparedStatement ps1 = connection.prepareStatement( sql2 );
        ps1.setString( 1, name);
        ResultSet result = ps1.executeQuery();
        if(!result.next())
        {
            System.out.println("No Data Found"); //data not exist
            return null;
        }
       else
        {
            String classId= result.getString("ID_");
            System.out.println("Class id is  "+ classId );
            return classId;
        }   
    }// end class id set
}// end create student
