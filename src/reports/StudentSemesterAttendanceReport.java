package reports;

/**
 *
 * @author jstil
 */

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import static javax.swing.Action.ACCELERATOR_KEY;
import static javax.swing.Action.NAME;
import static javax.swing.Action.SHORT_DESCRIPTION;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import timeclock.CreateStudent;
import timeclock.TimeClock;
/** This class displays the students record for the semester */
public class StudentSemesterAttendanceReport extends JFrame implements ActionListener  
{
    private JFrame frame = new JFrame("Student Semester Attendance Report");
    private JPanel panel;
    private JTable table;
    private final String DRIVER= "org.apache.derby.jdbc.EmbeddedDriver";
    private final String JDBC_URL = "jdbc:derby:student_;create=true";
    private String[] colNames;  // column names from data query
    private String[][] tableData; // table Data from data query
    private  JMenuBar menuBar;
    private JScrollPane scrollPane = new JScrollPane();
    private int numRows = 0;
    private JDatePickerImpl startDatePicker;
    private JDatePickerImpl endDatePicker;
    private JComboBox studentPicker;
    private SpringLayout layout = new SpringLayout();
   //** displays the data */
    public StudentSemesterAttendanceReport() throws SQLException 
    {
        frame.setSize(1275, 800);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // new panel  creation be cause ContentPane contentPane = frame.getContentPane(); has been depreciated
        panel = new JPanel();
        JLabel startDateLabel = new JLabel("Start Semester");
        JLabel endDateLabel = new JLabel("End Semester");
        // Creates the menu
        menuBar = new JMenuBar();
        java.net.URL imageURL3 = TimeClock.class.getResource("print.jpg");
        Action2 Print = new Action2("Print", new ImageIcon(imageURL3), "Print",'P');
        JMenu formatMenu = new JMenu("Option Menu");
        formatMenu.add(Print);
        menuBar.add(formatMenu);
        // set up start date
        UtilDateModel startModel = new UtilDateModel();
        Properties sp = new Properties();
        sp.put("text.today", "Today");
        sp.put("text.month", "Month");
        sp.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(startModel, sp);
        // Don't know about the formatter, but there it is...
        startDatePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        // end set up for start date
        // setup End Date
        UtilDateModel endModel = new UtilDateModel();
        Properties ep = new Properties();
        ep.put("text.today", "Today");
        ep.put("text.month", "Month");
        ep.put("text.year", "Year");
        JDatePanelImpl datePanel2 = new JDatePanelImpl(endModel, ep);
        // Don't know about the formatter, but there it is...
        endDatePicker = new JDatePickerImpl(datePanel2, new DateLabelFormatter());
        // end set up for end date
        JButton submitButton = new JButton("Submit"); 
        // font for Submit Buton
        submitButton.setFont(new Font("Serif", Font.BOLD, 16));
        // add listener for submit button
        submitButton.addActionListener(this); 
        studentPicker = new JComboBox();
        //Set up Gui
        panel.setLayout(layout);
        layout.putConstraint(SpringLayout.WEST, studentPicker,
                      100,
                        SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, studentPicker,
                     25,
                           SpringLayout.NORTH, panel);
         //
        layout.putConstraint(SpringLayout.WEST, scrollPane,
                     100,
                        SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, scrollPane,
                     50,
                           SpringLayout.NORTH, panel);
        //
        layout.putConstraint(SpringLayout.WEST, startDateLabel,
                        300,
                        SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, startDateLabel,
                  5,
                           SpringLayout.NORTH, panel);
        //
        layout.putConstraint(SpringLayout.WEST, endDateLabel,
                        500,
                        SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, endDateLabel,
                  5,
                           SpringLayout.NORTH, panel);
        //
        layout.putConstraint(SpringLayout.WEST, startDatePicker,
                        300,
                        SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, startDatePicker,
                  25,
                           SpringLayout.NORTH, panel);
        //
        layout.putConstraint(SpringLayout.WEST, endDatePicker,
                        500,
                        SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, endDatePicker,
                 25,
                           SpringLayout.NORTH, panel);
         //
        layout.putConstraint(SpringLayout.WEST, submitButton,
                        800,
                        SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, submitButton,
                    25,
                           SpringLayout.NORTH, panel);
        // add component to the panel
        panel.add(menuBar);
        panel.add(submitButton);
        panel.add(startDatePicker);
        panel.add(endDatePicker);
        panel.add(startDateLabel);
        panel.add(endDateLabel);
        panel.add(studentPicker);
        try 
        {
            // this gets they names and id of the student for easy selection and no need to have student id
            ViewStudents();
        } catch ( SQLException ex) 
        {
            Logger.getLogger(CreateStudent.class.getName()).log(Level.SEVERE, null, ex);
        }
        frame.setContentPane(panel);
        frame.setVisible(true);
        
    } // end report 

    /** this is a denormalized database still so two SQL requests are necessary to get the data then to get the totals
    * this gets the actual data for the main table
    * @param studentName
    */
    private  void GetData(String studentName)throws  SQLException 
    {
        Connection connection = DriverManager.getConnection(JDBC_URL);
        ResultSet result = null; 
        String sql2 =  "select\n" +
        "student_id_number_ as \"Student ID\",\n" +
        "first_name_|| ' ' ||  last_name_  as \"Student Name\",\n" +
        "date(clock_in_time_) as \"Date\",\n" +
        "class_ as \"Class\",\n" +
        "class_number_ as \"Contract #\",\n" +
        "time(clock_in_time_) as \"Time In\",\n" +
        "time(clock_out_time_) as \"Time Out\",\n" +
        "{fn timestampdiff(SQL_TSI_MINUTE,clock_in_time_, clock_out_time_)} as \"Time Used Minutes\"\n" +
        "from student_\n" +
        "join class_ on class_id_ = class_.id_ \n" +
        "where (first_name_|| ' ' ||  last_name_) = ? \n" +
                        "and date(clock_in_time_) > ? \n"+
                        "and date(clock_in_time_) < ?";
        PreparedStatement ps1 = connection.prepareStatement( sql2, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY) ;
        ps1.setString (1 , studentName);
        ps1.setObject (2 , startDatePicker.getModel().getValue());
        ps1.setObject (3 , endDatePicker.getModel().getValue());
        result = ps1.executeQuery();
        // get number of rows
        result.last();
        numRows = result.getRow();
        result.first();
        //Get metadata object for result
        ResultSetMetaData meta = result.getMetaData();
        // create an arry of string for the colum names
        colNames = new String[meta.getColumnCount()];
        // store column names in the new col names array
        for( int i = 0; i< meta.getColumnCount(); i++)
        {
            //get column name
            colNames[i] = meta.getColumnLabel(i+1);
            
        }
        // create 2 d string array for table data
        tableData = new String [numRows][meta.getColumnCount()];
        // store columns in the data
        for ( int row = 0 ; row < numRows; row++)
        {
            
            for (int col = 0; col < meta.getColumnCount(); col++)
            {
                tableData[row][col]= result.getString(col + 1);
               
            }
           
            result.next();
        }
        // close statement
        ps1.close();
        connection.close();
        System.out.println("Data Table Loaded.");
    } // end get data

    @Override
    /** this populates tables on click of the student name */
    public void actionPerformed(ActionEvent ae) 
    {
        String studentName = studentPicker.getSelectedItem().toString();
        try 
        {
            // gets the data for the main table based on the picked student
            GetData(studentName);
            table = new JTable(tableData,colNames);
            scrollPane = new JScrollPane(table);
            //set up ui
            panel.setLayout(layout);
            layout.putConstraint(SpringLayout.WEST, scrollPane,
                     100,
                        SpringLayout.WEST, panel);
            layout.putConstraint(SpringLayout.NORTH, scrollPane,
                     100,
                           SpringLayout.NORTH, panel);
            scrollPane.setPreferredSize(new Dimension(980, 300));
            // add to panel        
            panel.add(scrollPane);
            panel.validate();
            panel.repaint();
        }//end try
        catch (SQLException ex) 
        {
            Logger.getLogger(DayClassReport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }// end action 
    /** this sets up the menu and short cut keys */
    class Action2 extends AbstractAction 
    {
        /**  set up shortcuts */
        public  Action2(String text, Icon icon, String description,
        char accelerator) 
        {
            super(text, icon);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(accelerator,
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            putValue(SHORT_DESCRIPTION, description);
        }

        /** this sets up the menu buttons to print  */
        public  void actionPerformed(ActionEvent e ) 
        {
            String currentAction = (String) getValue(NAME);
            if ("Print".equals(currentAction))
            {
                MessageFormat header = null;
                /* if we should print a header */
                header = new MessageFormat("Student Semester Attendance Report");
                MessageFormat footer = null;
                /* if we should print a footer */
                footer = new MessageFormat( " Page{0}");
                /* determine the print mode */
                JTable.PrintMode mode =  JTable.PrintMode.FIT_WIDTH;
                boolean showPrintDialog = true;
                boolean interactive = true;
                try 
                {
                    /* print the table */
                    boolean complete = table.print(mode, header, footer,
                                                     showPrintDialog, null,
                                                     interactive, null);
                    /* if printing completes */
                    if (complete) 
                    {
                        /* show a success message */
                        JOptionPane.showMessageDialog(null,
                                                  "Printing Complete",
                                                  "Printing Result",
                                                  JOptionPane.INFORMATION_MESSAGE);
                    }//end if
                    else
                    {
                        /* show a message indicating that printing was cancelled */
                        JOptionPane.showMessageDialog(null,
                                                  "Printing Cancelled",
                                                  "Printing Result",
                                                  JOptionPane.INFORMATION_MESSAGE);
                    }
                }// end try
                catch (PrinterException pe) 
                {
                    /* Printing failed, report to the user */
                    JOptionPane.showMessageDialog(null,
                                              "Printing Failed: " + pe.getMessage(),
                                              "Printing Result",
                                              JOptionPane.ERROR_MESSAGE);
                }   
            }// end if 
        }// end action
    }// end class action2
    /** this method is cool. it loads all the students in the derby database into the student picker 
     * @throws SQLException
     * 
     */
    public void ViewStudents() throws SQLException
    {
        Connection connection3 = DriverManager.getConnection(JDBC_URL);
	String sql3 = "select distinct(first_name_ || ' ' ||  last_name_)  as \"Student Name\"  from student_";
        try 
        {
            PreparedStatement ps2 = connection3.prepareStatement(sql3);
            // execute select SQL stetement
            ResultSet rs = ps2.executeQuery();
            while (rs.next())
            {
                String existingClass = rs.getString("Student Name");
                // add to scrollpanel
                studentPicker.addItem(existingClass);
                System.out.println("Students loaded to combo box");
            }
            //close connection to DB
            ps2.close();
            connection3.close();
        }// end try
        catch (SQLException e)
        {

            System.out.println(e.getMessage());
        } 
        System.out.println("Student records sucessfully retrieved");
    }// end view students
}// end class report

   
    
 
