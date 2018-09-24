/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timeclock;

import java.awt.Container;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
// view class is using globals and could be reworked as a seperate class


/**
 * This class allows admin to delete a class from the database
 * @author jstil
 */

public class DeleteClass extends JFrame{
    private final String DRIVER= "org.apache.derby.jdbc.EmbeddedDriver";
    private final String JDBC_URL = "jdbc:derby:student_;create=true";
       
    /** displays current classes and allows you to delete them */
    public DeleteClass()
    {
        // import the global list into the model
        if (Globals.firstrun == 0)
        {   
            String[] CurrentClass = Globals.CurrentClassArray.toArray(new String[0]);
            for(String val : CurrentClass)
            Globals.listModel.addElement(val);   
            Globals.firstrun = 1;
        }
        JFrame frame = new JFrame("Delete Class");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // this module was written before learning Container was depreciated by JPane
        // could be reworked to change over to JPane.
        Container contentPane = frame.getContentPane();
        // load the jlist with the known classes without a database read
        JList jlist = new JList(Globals.listModel);
        jlist.setFont(new Font("Serif", Font.BOLD, 24));
        JScrollPane scrollPane1 = new JScrollPane(jlist);
        JLabel AddLabel = new JLabel("Double Click the Class to remove from the list:",JLabel.LEFT);
        AddLabel.setFont(new Font("Serif", Font.BOLD, 24));
        //Set up Gui
        SpringLayout layout = new SpringLayout();
        contentPane.setLayout(layout);
        layout.putConstraint(SpringLayout.WEST, AddLabel,
                        5,
                         SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.NORTH, AddLabel,
                        5,
                            SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.SOUTH, contentPane,
                         5,
                         SpringLayout.SOUTH, scrollPane1);  
        layout.putConstraint(SpringLayout.WEST, AddLabel,
                         5,
                         SpringLayout.EAST, scrollPane1);
        layout.putConstraint(SpringLayout.NORTH, scrollPane1,
                         5,
                         SpringLayout.NORTH, contentPane);

        // add content to pane
        contentPane.add(scrollPane1);
        contentPane.add(AddLabel); 
        
        
            // This is a cool feature. You can double click the class you want to delete 
            // mouse event listener
            MouseListener mouseListener = new MouseAdapter() 
            {
                // mouse listener is an object so the following methods are self contained to be used by this object
                public void mouseClicked(MouseEvent mouseEvent) 
                {
                    JList theList = (JList) mouseEvent.getSource();
                    if (mouseEvent.getClickCount() == 2) 
                    {
                        // assign index to the item in the list that is double clicked  
                        int index = theList.locationToIndex(mouseEvent.getPoint());
                        if (index >= 0) 
                        {
                            Object o = theList.getModel().getElementAt(index);
                            String text =o.toString();
                            // remove the class fromt the display
                            Globals.listModel.remove(index);
                            Globals.listModel.add(index, "Deleted");
                            Globals.deleted = true;
                            try 
                            {
                                // this removes the class from the database
                                DeleteClass(text);
                            }
                            catch (SQLException ex) 
                            {
                                Logger.getLogger(DeleteClass.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }//end if
                    }// end if
                }//end mouse clicked two TIMES
                /** this method deletes the selected class from the derby data base*/
                public  void DeleteClass(String text) throws  SQLException
                {
                    Connection connection = DriverManager.getConnection(JDBC_URL);
                    String sql4 = "DELETE FROM CLASS_ WHERE class_ = ?" ;
                    PreparedStatement ps1 = connection.prepareStatement( sql4 );
                    ps1.setString( 1, text);
                    ps1.executeUpdate();
                    connection.commit();
                    System.out.println(" Class record sucessfully Deleted");
                    ps1.close();
                    connection.close();
                } // end delete class
            };// end mouse listener 
        // applys the mouselistener to the jlist
        jlist.addMouseListener(mouseListener);
        // this dumps whatever was deleted from the jlist and refreshes the screen on the next load of delete class
        // could be improved to update in real time instead of on load.
        try 
        {
            // dump the list model before retrieving from database
            Globals.listModel.removeAllElements();
            // end dump
            // this reads the saved classes from the derby database and loads them into the global list
            ViewClass();
        }
        catch (ClassNotFoundException | SQLException ex) 
        {
            Logger.getLogger(AddClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        frame.setSize(1280, 780);
        frame.setVisible(true);
        
    }// end delete method
    public  void ViewClass() throws ClassNotFoundException, SQLException
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
}// end delete class
