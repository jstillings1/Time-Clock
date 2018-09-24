/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
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
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import java.util.Date;
import java.util.Properties;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import timeclock.CreateStudent;
import timeclock.MyPrintable;
import timeclock.TimeClock;


/** This class allows you to pick a day and displays the classes on that day if students were logged in that day */
 public class DayClassReport extends JFrame implements ActionListener  {
    private JFrame frame = new JFrame("Specific Day And Class Report");
    private JPanel panel;
    private JTable table;
    private JTable footerTable;
    private final String DRIVER= "org.apache.derby.jdbc.EmbeddedDriver";
    private final String JDBC_URL = "jdbc:derby:student_;create=true";
    private String[] colNames;  // column names from data query
    private String[][] tableData; // table Data from data query
    private JMenuBar menuBar;
    private JComboBox classEnter;
    private JDatePickerImpl datePicker;
    private JScrollPane scrollPane = new JScrollPane();
    private int numRows = 0;
    private JLabel numStudents,sumTimeOutput;
    private double sumTime = 0;
    private SpringLayout layout = new SpringLayout();
   
 
  
    /** displays the report */
    public DayClassReport() 
    {
        frame.setSize(1275, 800);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // new panel  creation be cause ContentPane contentPane = frame.getContentPane(); has been depreciated
        panel = new JPanel();
        // Creates the menu
        menuBar = new JMenuBar();
        java.net.URL imageURL3 = TimeClock.class.getResource("print.jpg");
        Action2 Print = new Action2("Print Table", new ImageIcon(imageURL3), "Print Table",'P');
        Action2 PrintTable = new Action2("Print Screen", new ImageIcon(imageURL3), "Print Screen",'T');
        JMenu formatMenu = new JMenu("Option Menu");
        formatMenu.add(Print);
        formatMenu.add(PrintTable);
        menuBar.add(formatMenu);
        // this is for loading the class data into a combo box   
        classEnter = new JComboBox();
        JButton submitButton = new JButton("Submit"); 
        // font for Submit Buton
        submitButton.setFont(new Font("Serif", Font.BOLD, 16));
        // listener for submit
        submitButton.addActionListener(this);
        // stuff to format the date
        UtilDateModel model = new UtilDateModel();
        //model.setDate(20,04,2014);
        // Need this...
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        // 3rd party JDatePanel
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        // Don't know about the formatter, but there it is...
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        //Set up Gui
        panel.setLayout(layout);
        //
        layout.putConstraint(SpringLayout.WEST, classEnter,
                      100,
                        SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, classEnter,
                     5,
                           SpringLayout.NORTH, panel);
         //
        layout.putConstraint(SpringLayout.WEST, submitButton,
                        480,
                        SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, submitButton,
                    5,
                           SpringLayout.NORTH, panel);
         //
        layout.putConstraint(SpringLayout.WEST, datePicker,
                        200,
                        SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, datePicker,
                  5,
                           SpringLayout.NORTH, panel);
        //
        layout.putConstraint(SpringLayout.WEST, scrollPane,
                     100,
                        SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, scrollPane,
                     50,
                           SpringLayout.NORTH, panel);
        //end gui set up
        panel.add(menuBar);
        panel.add(classEnter);
        panel.add(submitButton);
        panel.add(datePicker);
        //Display the window.
        try
        {
            ViewClass();
        } catch ( SQLException ex) 
        {
                Logger.getLogger(CreateStudent.class.getName()).log(Level.SEVERE, null, ex);
        }
        frame.setContentPane(panel);
        frame.setVisible(true);
    }// end report

    /** this gets the data to display in the table 
     * @param className
     * @param dbyear
     * @param dbmonth
     * @param dbday
     * @throws SQLException
     */
    private  void GetData(String className, int dbyear, int dbmonth, int dbday)throws  SQLException 
    {
        Connection connection = DriverManager.getConnection(JDBC_URL);
        ResultSet result = null; 
       String sql2 =  "select first_name_ || ' ' || last_name_  as \"Student Name\",\n" +
        "    clock_in_time_ as \"Time In\",\n" +
        "    clock_out_time_ as \"Time Out\",\n" +
        "    class_.class_ as \"Class\",\n" +
        "    class_.class_number_ as \"Contract Number\",\n" +
        "    {fn timestampdiff(SQL_TSI_MINUTE,clock_in_time_, clock_out_time_)} as \"Total Time in Minutes\"\n" +
        "from student_\n" +
        "join class_ on class_id_ = class_.id_\n" +
        "where class_.class_ = ? and year(clock_in_time_) = ? and month(clock_in_time_) = ? and day(clock_in_time_) = ?";
        PreparedStatement ps1 = connection.prepareStatement( sql2, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY) ;
        ps1.setString (1 , className);
        ps1.setObject (2, dbyear);
        ps1.setObject (3, dbmonth );
        ps1.setObject (4, dbday );
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
             sumTime += result.getInt("Total Time in Minutes");
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
    }// end get data
      
    /** submit listener */
    @Override
    public void actionPerformed(ActionEvent ae) 
    {
        String className = classEnter.getSelectedItem().toString();
        // this block of code manipulates the date picker data
        Date selectedDate = (Date) datePicker.getModel().getValue();
        DateFormat dbyear = new SimpleDateFormat("yyyy");
        DateFormat dbmonth = new SimpleDateFormat("M");
        DateFormat dbday = new SimpleDateFormat("dd");
        String year = dbyear.format(selectedDate);
        String month = dbmonth.format(selectedDate);
        String day = dbday.format(selectedDate);
        int y = Integer.parseInt(year);
        int m = Integer.parseInt(month);
        int d = Integer.parseInt(day);
        // end manipulation
        try 
        {
            GetData(className, y, m, d);
            table = new JTable(tableData,colNames);
            scrollPane = new JScrollPane(table);
            // labels for screen
            numStudents = new JLabel("Number of visits for this date:" + numRows,JLabel.LEFT);
            numStudents.setFont(new Font("Serif", Font.BOLD, 24));
            // round time to nearest 15
            int mod = (int)sumTime % 15;
            if (mod < 8)
            {
                sumTime -= mod;
            }
            else
            {
                sumTime+= 15 - mod;
            }
            // end round time}
            // create the sum labels 
            sumTimeOutput = new JLabel("Total number of rounded minutes for this date:" + sumTime,JLabel.LEFT);
            sumTimeOutput.setFont(new Font("Serif", Font.BOLD, 24));

            // creates footer table with totals
            String[] footerColNames = {"Number of visits for this date:","Total number of rounded minutes for this date:"};
            String [][] footerData = new String [1][2];
            // populate table with info                   
            footerData[0][0] = String.valueOf(numRows);
            footerData[0][1] =  String.valueOf(sumTime);
            // create footer table
            footerTable = new JTable(footerData,footerColNames);
            JScrollPane scrollPane2 = new JScrollPane(footerTable);
            //set up ui for the data display
            panel.setLayout(layout);
            layout.putConstraint(SpringLayout.WEST, scrollPane,
                     100,
                        SpringLayout.WEST, panel);
            layout.putConstraint(SpringLayout.NORTH, scrollPane,
                     50,
                           SpringLayout.NORTH, panel);
            layout.putConstraint(SpringLayout.WEST, scrollPane2,
                     100,
                        SpringLayout.WEST, panel);
            layout.putConstraint(SpringLayout.NORTH, scrollPane2,
                    300,
                           SpringLayout.NORTH ,scrollPane);
            //
            layout.putConstraint(SpringLayout.WEST, numStudents,
                     100,
                        SpringLayout.WEST, panel);
            layout.putConstraint(SpringLayout.NORTH, numStudents,
                     600,
                           SpringLayout.NORTH, panel);
            layout.putConstraint(SpringLayout.WEST, sumTimeOutput,
                     500,
                        SpringLayout.WEST, panel);
            layout.putConstraint(SpringLayout.NORTH, sumTimeOutput,
                     600,
                           SpringLayout.NORTH, panel);
            // set sizes for scroll panes
             scrollPane.setPreferredSize(new Dimension(980, 300));
             scrollPane2.setPreferredSize(new Dimension(980, 60));
            // add components to panel           
            panel.add(scrollPane);
            panel.add(scrollPane2);
            panel.add(numStudents);
            panel.add(sumTimeOutput);
            panel.validate();
            panel.repaint();
        }// end try
        catch (SQLException ex) 
        {
            Logger.getLogger(DayClassReport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }// end submit listenr
    /** creates the menu button magic and hot key enabler */
    class Action2 extends AbstractAction 
    {
        /** hot key enabler*/
        public  Action2(String text, Icon icon, String description,
            char accelerator) 
        {
          super(text, icon);
          putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(accelerator,
              Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
          putValue(SHORT_DESCRIPTION, description);
        }

        /**menu button listener to print */
        public  void actionPerformed(ActionEvent e ) 
        {
            // this retrieves what the user clicked
            String currentAction = (String) getValue(NAME);
            if ("Print Table".equals(currentAction))
            {
                MessageFormat header = null;
                /* if we should print a header */
                header = new MessageFormat("Day Class Report");
                MessageFormat footer = null;
                /* if we should print a footer */
                footer = new MessageFormat( " Page{0}");
                /* determine the print mode */
                JTable.PrintMode mode =  JTable.PrintMode.FIT_WIDTH;
                boolean showPrintDialog = true;
                boolean interactive = true;
                // this prints just the first table
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
                    } else 
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


            }// end if  print table
            // this gives the option to print the screen
            else  if ("Print Screen".equals(currentAction))
            {
                // this prints the Jpanel itself shrunk to fit the page
                // Myprintable  is copied from examples on the internet. I  fashioned it form numerous tutiorials. No one source
                // the math of scaling was a tad over my head.
                MyPrintable prt = new MyPrintable(panel);
                try 
                {
                    /* print the table */
                    prt.print();
                }
                catch (PrinterException pe)
                {
                    /* Printing failed, report to the user */
                    JOptionPane.showMessageDialog(null,
                                                      "Printing Failed: " + pe.getMessage(),
                                                      "Printing Result",
                                                      JOptionPane.ERROR_MESSAGE);
                }

            }// end else
        };// end menu button listener
    }// end action 2 and i really dont remember why this is named action2 instead of just action.
    /** this class loads the classes in the data base to the combo box for easy selection
     * @throws SQLException
     * 
     */
    public void ViewClass() throws SQLException
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
	}//end try
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        } 
        System.out.println("Class record sucessfully retrieved");
    } // end view class
}// end report class
