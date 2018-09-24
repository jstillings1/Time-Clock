/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timeclock;

/**
 * 
 * @author jstil - this was copied off http://www.zentut.com/java-swing/simple-login-dialog/
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/** This class creates the admin log in pop up*/ 
public class LoginDialog extends JDialog {
    
 
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JLabel lbUsername;
    private JLabel lbPassword;
    private JButton btnLogin;
    private JButton btnCancel;
    private boolean succeeded;
 
   /**  this creates the floating admin log in
    * 
    * @param parent this is the jframe created in timeclock
    */
    public LoginDialog(Frame parent) {
        super(parent, "Login", true);
        // this creates the panel using GRIDBAG EWWW.. not my first choice.
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();
 
        cs.fill = GridBagConstraints.HORIZONTAL;
 
        lbUsername = new JLabel("Username: ");
        cs.gridx = 0;
        cs.gridy = 0;
        cs.gridwidth = 1;
        panel.add(lbUsername, cs);
 
        tfUsername = new JTextField(20);
        cs.gridx = 1;
        cs.gridy = 0;
        cs.gridwidth = 2;
        panel.add(tfUsername, cs);
 
        lbPassword = new JLabel("Password: ");
        cs.gridx = 0;
        cs.gridy = 1;
        cs.gridwidth = 1;
        panel.add(lbPassword, cs);
 
        pfPassword = new JPasswordField(20);
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 2;
        panel.add(pfPassword, cs);
        panel.setBorder(new LineBorder(Color.GRAY));
 
        btnLogin = new JButton("Login");
 
        /** this is the listener for clicking log in*/
        btnLogin.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) 
            {
                if (Login.authenticate(getUsername(), getPassword())) 
                {
                    JOptionPane.showMessageDialog(LoginDialog.this,
                            "Hi " + getUsername() + "! You have successfully logged in.",
                            "Login",
                            JOptionPane.INFORMATION_MESSAGE);
                    succeeded = true;
                    dispose();
                }//end if
                else 
                {
                    JOptionPane.showMessageDialog(LoginDialog.this,
                            "Invalid username or password",
                            "Login",
                            JOptionPane.ERROR_MESSAGE);
                    // reset username and password
                    tfUsername.setText("");
                    pfPassword.setText("");
                    succeeded = false;
                }
            }// end action 
        }); // close login listener
        btnCancel = new JButton("Cancel");
        /** this is the listener for cancel */
        btnCancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) 
            {
                dispose();
            }// close action
        });// close cancel listener
        JPanel bp = new JPanel();
        bp.add(btnLogin);
        bp.add(btnCancel);
        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(bp, BorderLayout.PAGE_END);
        pack();
        setResizable(false);
    }// end logindialog
 
    /** getter for user name
     * 
     * @return returns the username entered
     */
    public String getUsername() {
        return tfUsername.getText().trim();
    }
 
    /** getter for password
     * 
     * @return returns the password entered
     */
    public String getPassword() {
        return new String(pfPassword.getPassword());
    }
 
    /** getter to see if log in was successful
     * 
     * @return returns true if password is correct
     */
    public boolean isSucceeded() {
        return succeeded;
    }
}// end class logindialog

