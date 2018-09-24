package reports;

/**
 *
 * @author jstil
 */

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.print.PrinterException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import static javax.swing.Action.ACCELERATOR_KEY;
import static javax.swing.Action.NAME;
import static javax.swing.Action.SHORT_DESCRIPTION;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import timeclock.TimeClock;




/** This class displays the date and hours visited on that date */
 public class DatesHoursVisitsReport extends JFrame  {
    private JFrame frame = new JFrame("Dates Hours Visits Report");
    private JPanel panel;
    private JTable table;
    private final String DRIVER= "org.apache.derby.jdbc.EmbeddedDriver";
    private final String JDBC_URL = "jdbc:derby:student_;create=true";
    private String[] colNames;  // column names from data query
    private String[][] tableData; // table Data from data query
    private  JMenuBar menuBar;
    private JScrollPane scrollPane = new JScrollPane();
    private int numRows = 0;
    private SpringLayout layout = new SpringLayout();
   
 
  
    /** displays the report 
     * @throws SQLException
     */
    public DatesHoursVisitsReport() throws SQLException 
    {
        frame.setSize(1275, 800);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // new panel  creation be cause ContentPane contentPane = frame.getContentPane(); has been depreciated
        panel = new JPanel();
        // Creates the menu
        menuBar = new JMenuBar();
        java.net.URL imageURL3 = TimeClock.class.getResource("print.jpg");
        Action2 Print = new Action2("Print", new ImageIcon(imageURL3), "Print",'P');
        JMenu formatMenu = new JMenu("Option Menu");
        formatMenu.add(Print);
        menuBar.add(formatMenu);
        panel.add(menuBar);
        try 
        {
            GetData();
            table = new JTable(tableData,colNames);
            scrollPane = new JScrollPane(table);
             
            //set up ui
            
            panel.setLayout(layout);
            layout.putConstraint(SpringLayout.WEST, scrollPane,
                     100,
                        SpringLayout.WEST, panel);
            layout.putConstraint(SpringLayout.NORTH, scrollPane,
                     50,
                           SpringLayout.NORTH, panel);
            
            scrollPane.setPreferredSize(new Dimension(980, 300));
            panel.add(scrollPane);
            panel.validate();
            panel.repaint();
        } catch (SQLException ex) 
        {
            Logger.getLogger(DayClassReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        frame.setContentPane(panel);
        frame.setVisible(true);
        
    }// end report
    /** get data for report */
    private  void GetData()throws  SQLException 
    {
        Connection connection = DriverManager.getConnection(JDBC_URL);
        ResultSet result = null; 
        String sql2 =  "select\n" +
        "date(clock_in_time_) as \"Date\",\n" +
        "count(clock_in_time_) as \"Number of Visits for this Date\",\n" +
        "Sum({fn timestampdiff(SQL_TSI_MINUTE,clock_in_time_, clock_out_time_)}) as \"Total Time for this Date in minutes\"\n" +
        "from student_\n" +
        "group by date(clock_in_time_)";
        PreparedStatement ps1 = connection.prepareStatement( sql2, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY) ;
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
    }// end get data
    
    /** creates menu buttons usage and keystroke hot keys */
    class Action2 extends AbstractAction 
    {

        /** creates the hot keys */
        public  Action2(String text, Icon icon, String description,
            char accelerator) {
          super(text, icon);
          putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(accelerator,
              Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
          putValue(SHORT_DESCRIPTION, description);
        }
        /** allows printing */
        public  void actionPerformed(ActionEvent e ) 
        {
            String currentAction = (String) getValue(NAME);
            if ("Print".equals(currentAction))
            {
                //print here
                MessageFormat header = null;
                /* if we should print a header */
                header = new MessageFormat("Dates Hours Visits Report");
                MessageFormat footer = null;
                /* if we should print a footer */
                footer = new MessageFormat( " Page{0}");
                /* determine the print mode */
                JTable.PrintMode mode =  JTable.PrintMode.FIT_WIDTH;
                boolean showPrintDialog = true;
                boolean interactive = true;
                // print labels
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
                } catch (PrinterException pe) 
                {
                    /* Printing failed, report to the user */
                    JOptionPane.showMessageDialog(null,
                                              "Printing Failed: " + pe.getMessage(),
                                              "Printing Result",
                                              JOptionPane.ERROR_MESSAGE);
                }
            }// end if
        }// end listener
    }// end action class
 }// end report class
    
    

