package Mini;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JWindow;
import javax.swing.table.DefaultTableModel;

public class FenetreExecute extends JFrame {
	private JPanel panel=new JPanel();
	private JMenuBar menubar=new JMenuBar();
	private JMenu fermer=new JMenu("Fermer");
	private JTable table;
	private Connection con;
	
	
	public FenetreExecute(JTable table,Connection con)
	{	this.table=table;
		this.con=con;
		this.fermer.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e)
			{if (e.getClickCount()==1)
				dispose();
			}
		});
		menubar.add(fermer);
		this.setJMenuBar(menubar);
		
		this.setSize(800,500);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
	}
	
}
