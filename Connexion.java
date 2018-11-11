package Mini;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;




public class Connexion extends JFrame{
   private Connection con;
   public JPasswordField passwordField;
   public JTextField textField;
   public JButton blogin;
   JLabel label;

   public Connexion() {
       initialize();
       this.setVisible(true);
   }

   public void initialize() {
	 setLocation(450,250);
     setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
     setLayout(null);
     setTitle("Connexion");
     setSize(350,300); 
     label=new JLabel("Connexion");
     Font f=new Font("Verdana",Font.BOLD,20);
     label.setBounds(100, 30, 200, 30);
     label.setFont(f);
     add(label);
     passwordField = new JPasswordField();
     passwordField.setBounds(90, 114, 105, 22);
     add(passwordField);

     textField = new JTextField();
     textField.setBounds(90, 79, 105, 22);
     add(textField);
     textField.setColumns(10);

     JLabel lblUsername = new JLabel("Username");
     lblUsername.setBounds(220, 82, 76, 16);
     add(lblUsername);

     JLabel lblPassword = new JLabel("Password");
     lblPassword.setBounds(220, 117, 76, 16);
     add(lblPassword);

     JButton blogin = new JButton("Login");
     blogin.setBounds(144, 158, 97, 25);
     blogin.addActionListener(new ActionListener() { 
         public void actionPerformed(ActionEvent ae){
            if(textField.getText().equals("system") && 
            		passwordField.getText().equals("Nassim1993"))
            {	try{
            	Class.forName("oracle.jdbc.driver.OracleDriver");
            	con=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl","system","Nassim1993");
            	Analyse fo=new Analyse(con);
            	
            	dispose();
            }catch(Exception e)
            {
            	
            }
            	
            }
            else
            {
            	JOptionPane.showMessageDialog(null, "Mot de passe ou username sont érronés");
            }
         }
     });

     add(blogin);
     add(passwordField);
     add(textField);
   }
   public Connection getConnexion()
   {
	   return this.con;
   }
}