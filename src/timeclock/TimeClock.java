/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*
@ author : 
Jeremiah Stillings
www.jeremiahstillings.net
jstillings1@outlook.com
919-221-8050
*/
package timeclock;
// imports
import reports.StudentSemesterAttendanceReport;
import reports.StudentNameIdReport;
import reports.DayClassReport;
import reports.DatesHoursVisitsReport;
import reports.ContractNumberReport;
import reports.ContractBreakdownReport;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JTextPane;
import javax.swing.JLabel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javax.swing.Action.ACCELERATOR_KEY;
import static javax.swing.Action.NAME;
import static javax.swing.Action.SHORT_DESCRIPTION;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import static timeclock.CreateDB.isTableExist;


/**  Globals for use in Add and remove classes  */
class Globals
{
    // set up list for combo box of class population used in add and delete class
    public static List<String> CurrentClassArray = new ArrayList<String>();
    //set up listmodel for add and delete class
    public static DefaultListModel<String> listModel = new DefaultListModel<>();
    // set up deleted class with a deleted condition
    public static Boolean deleted = false;
    // set up first run for add and delete classes
    public static int firstrun = 0; 
}
/** Main thread entry- This is the time clock program start */

public class TimeClock extends JFrame 
{
    private static JTextPane pane;
    
    private static LocalDate today = LocalDate.now();
    private static JLabel dateLabel = new JLabel(today.toString());
    private static JMenuBar menuBar;
    private static JLabel instructions = new JLabel (" Students click Clock In or Clock Out");
    // driver needed for derby data base
    private static final String DRIVER= "org.apache.derby.jdbc.EmbeddedDriver";
    // connect  string
    private static final String JDBC_URL = "jdbc:derby:student_;create=true";
  
    //String temp ="";
   /** main constructor */
    public  TimeClock() 
    {
    menuBar = new JMenuBar();
    java.net.URL imageURL = TimeClock.class.getResource("add.gif");
    java.net.URL imageURL2 = TimeClock.class.getResource("remove.gif");
    java.net.URL imageURL3 = TimeClock.class.getResource("print.jpg");
    Action AddClass = new Action("Add Class", new ImageIcon(imageURL), "Add New Class",'C');
    Action DeleteClass = new Action("Delete Class", new ImageIcon(imageURL2), "Delete Class", 'D');
    Action CreateDB = new Action("CreateDB", new ImageIcon(imageURL2), "Create Database", 'P');
    
    Action dayClassReport = new Action("Specfic Day and Class Report", new ImageIcon(imageURL3), "Specfic Day and Class Reports",'S');
    Action ContractNumberReport = new Action("Class Name to Contract Number Report", new ImageIcon(imageURL3), "Class Name to Contract Number Report",'I');
    Action DatesHoursVisitsReport = new Action("Dates Hours Visits Report", new ImageIcon(imageURL3), "Date Hours Visists Report",'V');
    Action ContractBreakdownReport = new Action("Contract Breakdown Report", new ImageIcon(imageURL3), "Contract Breakdown Report",'B');
    Action StudentSemesterAttendanceReport = new Action("Student Semester Attendance", new ImageIcon(imageURL3), "Student Semester Attendance",'M');
    Action StudentNameIdReport = new Action("Student Name to Student ID", new ImageIcon(imageURL3), "Student Name to Student ID",'N');
    JMenu formatMenu = new JMenu("Class Menu");
    
    formatMenu.add(AddClass);
    formatMenu.add(DeleteClass);
    formatMenu.add(CreateDB);
    JMenu formatMenu2= new JMenu("Reports Menu");
    formatMenu2.add(dayClassReport);
    formatMenu2.add(ContractNumberReport);
    formatMenu2.add(DatesHoursVisitsReport);
    formatMenu2.add(StudentSemesterAttendanceReport);
    formatMenu2.add(ContractBreakdownReport);
    formatMenu2.add(StudentNameIdReport);
    menuBar.add(formatMenu);
    menuBar.add(formatMenu2);
  

    }// end timeclock
  
    
     
 
        
   
                
        
    /** main program entry 
     * 
     * @param args  None
     */
    public static void main(String[] args) 
    {
    
    // create the object time clock
    TimeClock form = new TimeClock();
    form.pane = new JTextPane();
    form.pane.setPreferredSize(new Dimension(1280, 780));
  
    JFrame frame = new JFrame("Transitional Programs for College and Career Time Clock");
    frame.setSize(800, 780);
    frame.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
    Container contentPane = frame.getContentPane();
    // add listener for mistaken exit
    frame.addWindowListener(new WindowAdapter()
    {
      /** this catching exiting */
      @Override
      public void windowClosing(WindowEvent arg0) 
        {
            int result = JOptionPane.showConfirmDialog((Component) null, "Do you REALLY want to exit ?!",
                    "Confirmation", JOptionPane.YES_NO_OPTION);
            if (result == 0) {
                System.exit(0);
            } else {

            }
        }
    });
    
    frame.setJMenuBar(form.menuBar);
    // this hides the menu from students and only shows when admin " bob " logs in
    menuBar.setVisible(false);
  
    //Set up Gui
    SpringLayout layout = new SpringLayout();
    contentPane.setLayout(layout);
       
    dateLabel.setFont(new Font("Serif", Font.BOLD, 24));
    contentPane.add(dateLabel);
    // log in button
       java.net.URL imageURL3 = TimeClock.class.getResource("Clock_in.jpg");
       ImageIcon wicon = new ImageIcon(imageURL3);
       JButton logInButton = new JButton("Clock In", wicon); 
       // font for logInButon
       logInButton.setFont(new Font("Serif", Font.BOLD, 16));
       // font for instructions
       instructions.setFont(new Font("Serif", Font.BOLD, 26));
       // log in button event listener
       logInButton.addActionListener(new ActionListener()
       {
           @Override
           public void actionPerformed(ActionEvent e) 
           {
           ClockIn CI = new ClockIn();
           //CI.setVisible(true);

           }          


       });// end listener
       //spring layout set up

        // Column space   
        Integer c1= 210;
        Integer c2 = 400;
        Integer c3 = 600;
        Integer c4 = 800;
        Integer plusC = 900;
        Integer minusC = 1000;
        // controls row spacing
        Integer rowSpace = 40;
        // Row space
        Integer r0 = 50;
        Integer r1 = r0 + rowSpace;
        Integer r2 = r1 + rowSpace;
        Integer r3 = r2 + rowSpace;
        Integer r4 = r3 + rowSpace;
        Integer r5 = r4 + rowSpace;
        Integer r6 = r5 + rowSpace;
        Integer r7 = r6 + rowSpace;
        Integer r8 = r7 + rowSpace;
        Integer r9 = r8 + rowSpace;
        Integer r10 = r9 + rowSpace;
        Integer r11 = r10 + rowSpace;
        Integer r12 = r11 + rowSpace;
        Integer r13 = r12 + rowSpace;
        Integer r14 = r13 + rowSpace;
        Integer r15 = r14 + rowSpace;
        Integer r16 = r15 + rowSpace;
        // space below row to put seperator
        Integer sepadd = 35;
       // log out button
        java.net.URL imageURL4 = TimeClock.class.getResource("Clock_out.jpg");
        ImageIcon wicon2 = new ImageIcon(imageURL4);
        JButton logOutButton = new JButton("Clock Out", wicon2); 
        logOutButton.setFont(new Font("Serif", Font.BOLD, 16));
        // log out button listener
        logOutButton.addActionListener(new ActionListener() 
        {

                @Override
            public void actionPerformed(ActionEvent e) 
            {
            ClockOut CO = new ClockOut();
            //CI.setVisible(true);

            }      // add database time out stamp

        });
        //spring laypout

        layout.putConstraint(SpringLayout.WEST, dateLabel ,
                    c1 + 100,
                     SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.NORTH, dateLabel ,
                    r3,
                     SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.WEST, logInButton,
                c1,
                 SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.NORTH, logInButton,
             r4,
                    SpringLayout.NORTH, contentPane);

        layout.putConstraint(SpringLayout.WEST, logOutButton,
               c2,
                 SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.NORTH, logOutButton,
               r4,
                 SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.WEST, instructions,
               c1 - 30,
                 SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.NORTH, instructions,
               r2,
                 SpringLayout.NORTH, contentPane);

         // add parts to content pane
        contentPane.add(logInButton);
        contentPane.add(logOutButton);
        contentPane.add(instructions);

        frame.setVisible(true);
        // create the admin log in floater
        final JFrame frame2 = new JFrame("ADMIN LOG IN");
        final JButton btnLogin = new JButton("Click to login");
        final JButton btnLogOut = new JButton("Click to log out");
        // log in listener
        btnLogin.addActionListener
        (
            new ActionListener()
            {
                
                public void actionPerformed(ActionEvent e) 
                {
                    LoginDialog loginDlg = new LoginDialog(frame);
                    loginDlg.setVisible(true);
                    // if logon successfully
                    if(loginDlg.isSucceeded())
                    {
                        btnLogin.setText("Hi " + loginDlg.getUsername() + "!");
                        btnLogin.setVisible(false);
                        frame2.add(btnLogOut);
                      menuBar.setVisible(true);
                    }
                }
            }
        );
        //log out listenr
        btnLogOut.addActionListener
        (
            new ActionListener()
            {
                public void actionPerformed(ActionEvent e) 
                {
                    btnLogin.setVisible(true);
                    btnLogin.setText("Click to Log In");
                    menuBar.setVisible(false);


                }

            }
        );
 
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame2.setSize(300, 100);
        frame2.setLayout(new FlowLayout());
        frame2.setLocation(250,50);
        frame2.getContentPane().add(btnLogin);
        frame2.setVisible(true);
     
  } // end main
    
/** this is for the menus and the actions the buttons fire.*/
    class Action extends AbstractAction 
    {

        /** this unique piece of code allows the the menu to use hot keys*/
        public  Action(String text, Icon icon, String description,
            char accelerator) 
        {
          super(text, icon);
          putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(accelerator,
              Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
          putValue(SHORT_DESCRIPTION, description);
        }

        /** this is where the menu button clicks go and do what they are supposed to do when clicked*/
        public  void actionPerformed(ActionEvent e ) 
        {
            String currentAction = (String) getValue(NAME);
            // user hit add class
            if ("Add Class".equals(currentAction))
            {
                AddClass AZ= new AddClass();
            }
            // user wants a report
            else if  ("Specfic Day and Class Report".equals(currentAction))
            {
                DayClassReport DCR = new DayClassReport();
            }
            // user wants a report
            else if  ("Class Name to Contract Number Report".equals(currentAction))
            {   
                try 
                {
                    ContractNumberReport CNR = new ContractNumberReport();
                }
                catch (SQLException ex) 
                {
                    Logger.getLogger(TimeClock.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            // user wants a report
            else if  ("Dates Hours Visits Report".equals(currentAction))
            {
                try 
                {
                    DatesHoursVisitsReport DHVR = new DatesHoursVisitsReport();
                }
                catch (SQLException ex) 
                {
                    Logger.getLogger(TimeClock.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            // user wants to delete a class
            else if  ("Delete Class".equals(currentAction))
            {
                DeleteClass DZ= new DeleteClass();
            }
            // user wants a report
            else if  ("Student Semester Attendance".equals(currentAction))
            {
                try 
                {
                    StudentSemesterAttendanceReport SSAR =new StudentSemesterAttendanceReport();
                }
                catch (SQLException ex) 
                {
                    Logger.getLogger(TimeClock.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            // user wants a report
            else if  ("Contract Breakdown Report".equals(currentAction))
            {
                try 
                {
                    ContractBreakdownReport CBR = new ContractBreakdownReport();
                }
                catch (SQLException ex) 
                {
                    Logger.getLogger(TimeClock.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            // user wants a report
            else if  ("Student Name to Student ID".equals(currentAction))
            {
                try
                {
                    StudentNameIdReport SNTOID =new StudentNameIdReport();
                }
                catch (SQLException ex) 
                {
                    Logger.getLogger(TimeClock.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            // this is where the user decided to either dump the database and start over or they are setting up for the first time
            else if  ("CreateDB".equals(currentAction))
            {
                try 
                {
                    isTableExist("STUDENT_");
                } 
                catch (SQLException ex) 
                {
                    Logger.getLogger(TimeClock.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
            // if this ever runs we have a serious error in coding
            else        
                 {
                    JOptionPane.showMessageDialog(null, "You forgot to select a menu choice");
                 }
        }// end action
    }// end class action
  
  
}// end timeclock class.

       


	
	
