/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timeclock;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.swing.JOptionPane;


/** This class creates the database*/
public  class CreateDB {
    private static final String DRIVER= "org.apache.derby.jdbc.EmbeddedDriver";
    private static final String JDBC_URL = "jdbc:derby:student_;create=true";
    
    public static void main(String[] args) throws ClassNotFoundException, SQLException
    {
        Class.forName(DRIVER);
        isTableExist("STUDENT_");
    }
    /** checks if data base exists if not it creates it and seeds it
     * 
     * @param sTablename This is the table name we are checking against
     * @return this returns true when its done its work
     * @throws SQLException 
     */
    public static boolean isTableExist(String sTablename) throws SQLException{
        Connection connection = DriverManager.getConnection(JDBC_URL);
        if(connection!=null)
        {
            DatabaseMetaData dbmd = connection.getMetaData();
            ResultSet rs = dbmd.getTables(null, null, sTablename.toUpperCase(),null);
            if(rs.next())
            {
                System.out.println("Table "+rs.getString("TABLE_NAME")+" already exists !!");
                JOptionPane.showMessageDialog(null, "Database already exists! ");
            }
            else
            {
                // create tables
                Timestamp timeIn = new Timestamp(System.currentTimeMillis());
                Timestamp timeOut = new Timestamp(System.currentTimeMillis());
                connection.createStatement().execute("create table STUDENT_(id_ INT not null primary key GENERATED ALWAYS AS IDENTITY(START WITH 1, INCREMENT BY 1), first_name_ varchar(20), last_name_ varchar(20), student_id_number_ varchar(10), class_id_  INT, clock_in_time_ timestamp, clock_out_time_ timestamp)");
                System.out.println(" Students Table created");
                connection.createStatement().execute("create table CLASS_(id_ INT not null primary key GENERATED ALWAYS AS IDENTITY(START WITH 1, INCREMENT BY 1), class_ varchar(20), class_number_ INT )");
                System.out.println(" Class Table created");
                JOptionPane.showMessageDialog(null, "Database created! Please add your classes.");
                System.out.println(timeIn);
                
                // seed database
                String sql = "INSERT INTO STUDENT_ ( first_name_, last_name_,  student_id_number_, class_id_, clock_in_time_, clock_out_time_  ) VALUES ( ?, ?, ?, ?, ?, ? )  " ;
                PreparedStatement ps = connection.prepareStatement( sql );
                ps.setString( 1, "Ben" );
                ps.setString( 2, "Tester");
                ps.setObject( 3, "649619");
                ps.setObject( 4, "1");
                ps.setObject( 5 , timeIn ) ;
                ps.setObject( 6 , timeOut ) ;
                ps.executeUpdate();
                System.out.println(" Students record sucessfully inserted");
                //This inserts class table
                
                String sql2 = "INSERT INTO CLASS_ ( class_, class_number_) VALUES ( ?, ? )  " ;
                PreparedStatement ps1 = connection.prepareStatement( sql2 );
                ps1.setString( 1, "Test");
                ps1.setObject( 2, 123456);
                ps1.executeUpdate();
                connection.commit();
                System.out.println(" Class record sucessfully inserted");
            }
            return true;
        }
        return false;
}
}

    
