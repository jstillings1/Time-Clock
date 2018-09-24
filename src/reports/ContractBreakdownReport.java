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
// imports

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
import timeclock.MyPrintable;
import timeclock.TimeClock;




/** This class displays the contract details from the database*/
 public class ContractBreakdownReport extends JFrame 
{
    private JFrame frame = new JFrame("Contract Breakdown Report");
    private JPanel panel;
    private JTable table;
    private JTable footerTable;
    private  final String DRIVER= "org.apache.derby.jdbc.EmbeddedDriver";
    private  final String JDBC_URL = "jdbc:derby:student_;create=true";
    private String[] colNames;  // column names from data query
    private String[][] tableData; // table Data from data query
    private String[] footerColNames;  // column names from data query
    private String[][] footerData; // table Data from data query
    private JMenuBar menuBar;
    private JScrollPane scrollPane = new JScrollPane();
    private int numRows = 0;
    private SpringLayout layout = new SpringLayout();
   
 
  
    /** view or print report
     * @throws SQLException 
     */
    public ContractBreakdownReport() throws SQLException 
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
        // add menu bar to panel
        panel.add(menuBar);
        // go get the data for main table info
        GetData();
        table = new JTable(tableData,colNames);
        scrollPane = new JScrollPane(table);
        // go get data for totals
        GetFooter();
        footerTable = new JTable(footerData,footerColNames);
        JScrollPane scrollPane2 = new JScrollPane(footerTable);
        //set up ui

        panel.setLayout(layout);
        layout.putConstraint(SpringLayout.WEST, scrollPane,
                 100,
                    SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, scrollPane,
                 50,
                       SpringLayout.NORTH, panel);
        //
        layout.putConstraint(SpringLayout.WEST, scrollPane2,
                 100,
                    SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, scrollPane2,
                300,
                       SpringLayout.NORTH ,scrollPane);
       
      
        scrollPane.setPreferredSize(new Dimension(980, 300));
        scrollPane2.setPreferredSize(new Dimension(980, 200));
        panel.add(scrollPane);
        panel.add(scrollPane2);
        panel.validate();
        panel.repaint();
        frame.setContentPane(panel);
        frame.setVisible(true);
        
    } // end ContractBreakdownReport

    /** get data for main table*/
    private  void GetData()throws  SQLException 
    {
        Connection connection = DriverManager.getConnection(JDBC_URL);
        ResultSet result = null; 
        String sql2 =  "SELECT class_.class_number_ AS \"Contract Number\",\n" +
        "       first_name_ || ' ' ||  last_name_ AS \"Student Name\",\n" +
        "       class_.class_ AS \"Class\",\n" +
        "       MAX(ALL DATE(clock_in_time_)) AS \"Latest Date\",\n" +
        "       COUNT(student_.id_) AS \"visits\",\n" +
        "       SUM({FN TIMESTAMPDIFF(SQL_TSI_MINUTE, clock_in_time_, clock_out_time_)})\n" +
        "         AS \"Total Time in Minutes\"\n" +
        "FROM student_  \n" +
        "JOIN class_ ON class_id_ = class_.id_ \n" +
        "GROUP BY class_.id_, class_.class_number_, class_.class_, \n" +
        "         student_id_number_, first_name_, last_name_";
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
    }// END GET DATA
    
    /** get footer data*/
    private  void GetFooter() throws  SQLException 
    {
        Connection connection = DriverManager.getConnection(JDBC_URL);
        ResultSet result = null; 
        String sql2 =  "SELECT class_.class_number_ AS \"Contract Number\",\n" +
        "       COUNT(student_id_number_) AS \"Total visits for this Contract Number\",\n" +
        "       SUM({FN TIMESTAMPDIFF(SQL_TSI_MINUTE, clock_in_time_, clock_out_time_)})\n" +
        "           AS \"Total time for this Contract Number\"\n" +
        "FROM student_  \n" +
        "JOIN class_ ON class_id_ = class_.id_ \n" +
        "GROUP BY class_.class_number_";
        PreparedStatement ps1 = connection.prepareStatement( sql2, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY) ;
        result = ps1.executeQuery();
        // get number of rows
        result.last();
        numRows = result.getRow();
        result.first();
        //Get metadata object for result
        ResultSetMetaData meta = result.getMetaData();
        // create an arry of string for the colum names
        footerColNames = new String[meta.getColumnCount()];
        // store column names in the new col names array
        for( int i = 0; i< meta.getColumnCount(); i++)
        {
            //get column name
            footerColNames[i] = meta.getColumnLabel(i+1);
            
        }
        // create 2 d string array for table data
        footerData = new String [numRows][meta.getColumnCount()];
        // store columns in the data
        for ( int row = 0 ; row < numRows; row++)
        {
            
            for (int col = 0; col < meta.getColumnCount(); col++)
            {
               footerData[row][col]= result.getString(col + 1);
               
            }
           
            result.next();
        }
        // close statement
        ps1.close();
        connection.close();
        System.out.println("Footer Table Loaded.");
    } // end footer data get
    
    
    /** menu listeners and key short cut enablers */
    
    class Action2 extends AbstractAction 
    {

        public  Action2(String text, Icon icon, String description,
            char accelerator) {
          super(text, icon);
          putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(accelerator,
              Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
          putValue(SHORT_DESCRIPTION, description);
        }

        public  void actionPerformed(ActionEvent e ) 
        {
            String currentAction = (String) getValue(NAME);
            if ("Print Table".equals(currentAction))
            {
                //print here
                MessageFormat header = null;
                /* if we should print a header */
                header = new MessageFormat("Contract Breakdown Report");
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
                    interactive, null) && footerTable.print(mode, header, footer);
                    /* if printing completes */
                    if (complete) 
                    {
                        /* show a success message */
                        JOptionPane.showMessageDialog(null,
                                                          "Printing Complete",
                                                          "Printing Result",
                                                          JOptionPane.INFORMATION_MESSAGE);
                    }
                    else 
                    {
                        /* show a message indicating that printing was cancelled */
                        JOptionPane.showMessageDialog(null,
                                                          "Printing Cancelled",
                                                          "Printing Result",
                                                          JOptionPane.INFORMATION_MESSAGE);
                    }
                } 
                catch (PrinterException pe) 
                {
                    /* Printing failed, report to the user */
                    JOptionPane.showMessageDialog(null,
                                                      "Printing Failed: " + pe.getMessage(),
                                                      "Printing Result",
                                                      JOptionPane.ERROR_MESSAGE);
                }
            }
            else  if ("Print Screen".equals(currentAction))
            {
                // this prints the Jpanel
                 MyPrintable prt = new MyPrintable(panel);
                try 
                {
                    /* print the table */
                    prt.print();
                } catch (PrinterException pe) 
                {
                        /* Printing failed, report to the user */
                        JOptionPane.showMessageDialog(null,
                                                      "Printing Failed: " + pe.getMessage(),
                                                      "Printing Result",
                                                      JOptionPane.ERROR_MESSAGE);
                }
            }
        };// end listener
    }// end class action
}// end class report
        
        
             
             
            
             
         
    
    
    
    
    
 
