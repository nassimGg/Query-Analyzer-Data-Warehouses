package Mini;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Formatter;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import oracle.jdbc.internal.OracleStatement;

public class Analyse extends JFrame{
	
	private JMenuBar menubar=new JMenuBar();
	private JMenu fichier=new JMenu("Fichier");
	private JMenuItem item1= new JMenuItem("Ouvrir");
	private JMenuItem item2= new JMenuItem("Fermer");
	private JPanel pan= new JPanel();
	private JFileChooser fs=new JFileChooser(new File("/home/nassim"));
	private ArrayList<String> tmp = new ArrayList<String>();
	private ArrayList<String> tables = new ArrayList<String>();
	private ArrayList<String> attributs = new ArrayList<String>();
	private ArrayList<String> predicats = new ArrayList<String>();
	private ArrayList<String> att_cle = new ArrayList<String>();
	
	private String[][] att_tabl=new String[20][2];
	private String[][] pred_att=new String[497][2];
	private boolean tab;
	private int col=0;
	private int col1=0;
	private int col2=0;
	private int col_rm=0;
	private String[][] dom_att=new String[20][2];
	String quer;
	ResultSet r;
	ResultSetMetaData rm;
	ResultSetMetaData[] rms;
	public Analyse(Connection con)
	{	
		this.setTitle("Analyseur des requetes");
		this.setSize(350, 300);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		item1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,KeyEvent.CTRL_DOWN_MASK));
		  item1.addActionListener( new ActionListener(){
		 

		public void actionPerformed(ActionEvent arg0){
			
			fs.setDialogTitle("Ouvrir un fichier");
			fs.setFileFilter(new FileTypeFilter(".sql","SQL File"));
			fs.setFileFilter(new FileTypeFilter(".SQL","SQL File"));
			fs.setMultiSelectionEnabled(true);
			int result = fs.showOpenDialog(null);
			if (result == JFileChooser.APPROVE_OPTION)
			{ 
				
				try{
					File[] fi=fs.getSelectedFiles();
					Class.forName("oracle.jdbc.driver.OracleDriver");
					
					Statement s=con.createStatement();
					String drop="drop table system.Transaction";
					String drop1="drop table system.Predicats";
					String drop2="drop table system.Attributs";
					String drop3="drop table tables";
					s.executeUpdate(drop);
					s.executeUpdate(drop1);
					s.executeUpdate(drop2);
					s.executeUpdate(drop3);
					String create="CREATE TABLE system.Tables("+
							"Nom_Table char(20) PRIMARY KEY NOT NULL,"+
							"Taille_tuple FLOAT NOT NULL,"+
							"Nombre_tuple INTEGER NOT NULL)";
					s.executeUpdate(create);
					String create1="CREATE TABLE system.Attributs("+
							"Nom_Attribut char(20) PRIMARY KEY NOT NULL,"+
							"Id_table char(20) NOT NULL,"+
							"Nombre_domaine INTEGER NOT NULL,"+
							"CONSTRAINT Nom_table FOREIGN KEY (Id_table)  REFERENCES system.Tables(Nom_Table))";
				s.executeUpdate(create1);
				String create2="CREATE TABLE system.Predicats("+
							"Nom_Predicat char(35) PRIMARY KEY NOT NULL,"+
							"Id_attribut char(20) NOT NULL,"+
							"Facteur_sel_dim FLOAT NOT NULL,"+
							"Facteur_sel_fai FLOAT NOT NULL,"+
							"CONSTRAINT Nom_attribut FOREIGN KEY (Id_attribut)  REFERENCES system.Attributs(Nom_Attribut))";
				s.execute(create2);
				String create3="CREATE TABLE system.Transaction("+
							"Id_predicat char(35) NOT NULL,"+
							"Id_requete INTEGER NOT NULL,"+
							"CONSTRAINT Nom_Predicat FOREIGN KEY (Id_predicat)  REFERENCES system.Predicats(Nom_Predicat))";
				s.executeUpdate(create3);
				for (File f:fi)
					{tab=false;
					String nam=f.getName();
					int name=Integer.parseInt(nam.substring(nam.indexOf("Q")+1,nam.indexOf(".")));
					BufferedReader br=new BufferedReader(new FileReader(f.getPath()));
				String line="";
				while((line=br.readLine())!=null)
				{	if(!line.equals(""))
					if(line.indexOf(";")==-1)
						{line=line.concat(" "+br.readLine());
					      tmp.add(line);}
					
					String[] mot=line.split("FROM");
					String[] mot2=mot[1].split("WHERE");
					mot2[0]=mot2[0].trim();
					String[] mot3=mot2[0].split(",");
					Statement sz=con.createStatement();
						
					for(int a=0;a<mot3.length;a++)
					{	String table="";
						for(int x=0;x<mot3[a].length();x++)
						{
							if(mot3[a].charAt(x)!=' ')
							{
								table=table+mot3[a].charAt(x);
								
							}
							else break;
							
						}
						mot3[a]=table;
						int rep=0;
						for(int y=0;y<tables.size();y++)
							if(!mot3[a].equals(tables.get(y)))rep++;
							if(rep==tables.size()){
								tab=true;
								tables.add(mot3[a]);
							
							
String taille="SELECT SUM(DATA_LENGTH) FROM user_tab_columns where table_name='"+mot3[a].toUpperCase()+"' GROUP BY TABLE_NAME ORDER BY TABLE_NAME";
							
							ResultSet rs0=s.executeQuery(taille);
							
							int taile=0;
							while(rs0.next()){
								taile=Integer.parseInt(rs0.getString(1));
								
							}
							
							String tuple="select count(*) from "+mot3[a];
							ResultSet rs1=s.executeQuery(tuple);
							
							int tupl=0;
							while(rs1.next())
							{
								tupl=Integer.parseInt(rs1.getString(1));
							}
							
					String insert="INSERT INTO TABLES VALUES('"+mot3[a]+"',"+taile+","+tupl+")";
							s.executeUpdate(insert);
				if(tab){			
String query=" select constraint_name from user_constraints where table_name='"+mot3[a]+"' and (constraint_type='P' or constraint_type='R')";
						Statement st=con.createStatement();
						ResultSet rsa=st.executeQuery(query);
						
						String query2="";
						while(rsa.next())
						{
							
							
 query2="select column_name from user_cons_columns where constraint_name='"+rsa.getString(1)+"'";		
						Statement stt=con.createStatement();
 					ResultSet rssa=stt.executeQuery(query2);
						int reep=0;
						while(rssa.next())
						{
							for(int x=0;x<att_cle.size();x++)
							if(!rssa.getString(1).equals(att_cle.get(x)))reep++;
						if(reep==att_cle.size())
							att_cle.add(rssa.getString(1));}
						}	}
						}}
					
					if (tab)
					{rms=new ResultSetMetaData[tables.size()];
						for(int sq=0;sq<tables.size();sq++)
					{
						quer="select * from "+tables.get(sq);
						Statement sr=con.createStatement();
						ResultSet r=sr.executeQuery(quer);
						rms[col_rm]=r.getMetaData();
						
						col_rm++;}
						
					}
					String[] mot4;
					boolean exist=mot2[1].contains(" GROUP ");
					if(exist){String[] group=mot2[1].trim().split(" GROUP ");
					mot4=group[0].trim().split(" AND ");
					}else{
						mot4=mot2[1].trim().split(" AND ");
					}
					
					
					boolean trouve1,trouve2,trouve3;
					for(int a=0;a<mot4.length;a++)
					{	trouve1=false;trouve2=false;trouve3=false;
					
						for(int x=0;x<mot4[a].length();x++)
						{	
							if(mot4[a].charAt(x)=='=' && (int)mot4[a].charAt(x+1)==39)
							{trouve3=true;}
							else if(mot4[a].charAt(x)=='='){
							trouve1=true;
							}else if(mot4[a].charAt(x)=='I' && mot4[a].charAt(x+1)=='N')
							{
								trouve2=true;
							}
							}
						
						if(trouve3)
						{
							String[] mot5=mot4[a].trim().split("=");
							int index1=mot5[0].indexOf(".");
							String mot11=mot5[0].trim().substring(index1+1, mot5[0].length());
							int index2=mot5[1].indexOf("'");
							String mot7=mot5[1].trim().substring(index2+1, mot5[1].indexOf("'", index2+1));
							int rep1=0,rep2=0;
							
							for(int x=0;x<att_cle.size();x++)
							{
								if(!mot11.equals(att_cle.get(x)))
								{
									rep1++;
								}
								
							}
							int rep3=0;
							if(rep1 == att_cle.size())
								for(int y=0;y<attributs.size();y++)
									{
									if(!mot11.equals(attributs.get(y)))rep3++;}
									if(rep3==attributs.size() && rep3!=0){
										attributs.add(mot11);
										
									for(int sq=0;sq<tables.size();sq++)
									{
										for(int z=0;z<rms[sq].getColumnCount();z++)
										{
											if(mot11.equals(rms[sq].getColumnName(z+1))){
												Statement s1=con.createStatement();
		String domain="select count(distinct("+mot11+")) from "+tables.get(sq)+"";
				r=s1.executeQuery(domain);int domaine=0;
				while(r.next()){domaine=Integer.parseInt(r.getString(1));
				dom_att[col2][0]=mot11;
				dom_att[col2][1]=r.getString(1);col2++;}
												att_tabl[col][0]=mot11;
												att_tabl[col][1]=tables.get(sq);
												
		String insert1="INSERT INTO Attributs VALUES('"+mot11+"','"+tables.get(sq)+"',"+domaine+")";
		
		s1.executeUpdate(insert1);
												col++;
												
											}
										}
									}
								}
									
									
							for(int x=0;x<predicats.size();x++)
							if(!mot7.equals(predicats.get(x)))rep2++;
							if(rep2==predicats.size()){
								predicats.add(mot7);
								pred_att[col1][0]=mot7;
							pred_att[col1][1]=mot11;col1++;
							float facteur=0;
							
							for(int y=0;y<dom_att.length;y++)
							{if(mot11.equals(dom_att[y][0])){
							facteur=(float)1/Integer.parseInt(dom_att[y][1]);
							BigDecimal bd = new BigDecimal(facteur);
							bd= bd.setScale(3,BigDecimal.ROUND_UP);
							float fc= bd.floatValue();
						String insert2="INSERT INTO Predicats VALUES ('"+mot7+"','"+mot11+"',"+fc+","+fc+")";
							Statement sa1=con.createStatement();
							sa1.execute(insert2);
							sa1.close();
							}
							}
							
							}
							String insert3="INSERT INTO Transaction VALUES ('"+mot7+"',"+name+")";
							Statement sre=con.createStatement();
							sre.executeUpdate(insert3);
							sre.close();
							
						}
						else
						if(trouve1)
						{
							String[] mot5=mot4[a].trim().split("=");
							int index1=mot5[0].indexOf(".");
							String mot6=mot5[0].trim().substring(index1+1, mot5[0].length());
							int index2=mot5[1].indexOf(".");
							String mot7=mot5[1].trim().substring(index2+1, mot5[1].length());
							int rep1=0,rep2=0;
							
							for(int x=0;x<att_cle.size();x++)
							{
								if(!mot6.equals(att_cle.get(x)))
								{
									rep1++;
								}
								if(!mot7.equals(att_cle.get(x)))
								{
									rep2++;
								}
								
							}
							
							int rep3=0,rep4=0;
							if(rep1 == att_cle.size())
							for(int y=0;y<attributs.size();y++)
								{
								if(!mot6.equals(attributs.get(y)))rep3++;}
								if(rep3==attributs.size() && rep3!=0){
									attributs.add(mot6);
									
								for(int sq=0;sq<tables.size();sq++)
								{
									for(int z=0;z<rms[sq].getColumnCount();z++)
									{
										if(mot6.equals(rms[sq].getColumnName(z+1))){
				String domain="select count(distinct("+mot6+")) from "+tables.get(sq)+"";
				Statement s1=con.createStatement();
					ResultSet r1=s1.executeQuery(domain);int domaine=0;
					while(r1.next()){domaine=Integer.parseInt(r1.getString(1));}
			String insert1="INSERT INTO Attributs VALUES('"+mot6+"','"+tables.get(sq)+"',"+domaine+")";
			s1.executeUpdate(insert1);
			s1.close();
											att_tabl[col][0]=mot6;
											att_tabl[col][1]=tables.get(sq);
											col++;
											
										}
									}
									
								
								}
								}
							if(rep2 == att_cle.size())
							for(int y=0;y<attributs.size();y++)
								{
								if(!mot7.equals(attributs.get(y)))rep4++;}
								if(rep4==attributs.size() && rep4!=0){
									attributs.add(mot7);
									
								for(int sq=0;sq<tables.size();sq++)
								{ 
									for(int z=0;z<rms[sq].getColumnCount();z++)
									{
										if(mot7.equals(rms[sq].getColumnName(z+1))){
				String domain="select count(distinct("+mot7+")) from "+tables.get(sq)+"";
				Statement s1=con.createStatement();
				ResultSet r1=s1.executeQuery(domain);int domaine=0;
				while(r1.next()){domaine=Integer.parseInt(r1.getString(1));}
				String insert1="INSERT INTO Attributs VALUES('"+mot7+"','"+tables.get(sq)+"',"+domaine+")";
				s1.executeUpdate(insert1);
				s1.close();
											att_tabl[col][0]=mot7;
											att_tabl[col][1]=tables.get(sq);
											col++;
											
										}
									}
									
								
								}
								
								}
							
						}else if(trouve2)
						{
							String[] mot8=mot4[a].split(" IN ");
							String[] mot10=mot8[1].trim().split(",");
							
							
							mot8[0]=mot8[0].trim();
							
							int index=mot8[0].indexOf(".");
							String mot9=mot8[0].trim().substring(index+1, mot8[0].length());
							int rep=0;
							for(int x=0;x<att_cle.size();x++)
							{
								if(!mot9.equals(att_cle.get(x)))
								{
									rep++;
								
								}
								
							}
							int rep44=0;
							if(rep == att_cle.size())
								for(int y=0;y<attributs.size();y++)
								{
								if(!mot9.equals(attributs.get(y)))rep44++;}
								if(rep44==attributs.size() && rep!=0)
								{
									
									attributs.add(mot9);
								for(int sq=0;sq<tables.size();sq++)
								{
								for(int z=0;z<rms[sq].getColumnCount();z++)
								{
									if(mot9.equals(rms[sq].getColumnName(z+1)))
									{
								String domain="select count(distinct("+mot9+")) from "+tables.get(sq)+"";
								Statement s1=con.createStatement();
								r=s1.executeQuery(domain);int domaine=0;
					while(r.next()){domaine=Integer.parseInt(r.getString(1));
					dom_att[col2][0]=mot9;
					dom_att[col2][1]=r.getString(1);col2++;}
					String insert3="INSERT INTO Attributs VALUES('"+mot9+"','"+tables.get(sq)+"',"+domaine+")";
										
					s1.executeUpdate(insert3);
					s1.close();
											att_tabl[col][0]=mot9;
											att_tabl[col][1]=tables.get(sq);
											col++;
											
										}
									}
								}								
								}
								
							for(int x=0;x<mot10.length;x++){
								
								int index3=mot10[x].indexOf("'");
								String pre=mot10[x].substring(index3+1, mot10[x].indexOf("'", index3+1));
								int rep2=0;
								
								for(int y=0;y<predicats.size();y++)
									if(!pre.equals(predicats.get(y)))rep2++;
									if(rep2==predicats.size()){
										predicats.add(pre);
										
										pred_att[col1][0]=pre;
									pred_att[col1][1]=mot9;col1++;
									float facteur=0;
									for(int y=0;y<dom_att.length;y++)
									{if(mot9.equals(dom_att[y][0])){
									facteur=(float)1/Integer.parseInt(dom_att[y][1]);
									BigDecimal bd = new BigDecimal(facteur);
									bd= bd.setScale(3,BigDecimal.ROUND_UP);
									float fc= bd.floatValue();
								String insert2="INSERT INTO Predicats VALUES ('"+pre+"','"+mot9+"',"+fc+","+fc+")";
									Statement sa1=con.createStatement();
									sa1.execute(insert2);
									sa1.close();
									}
									}
									}
									String insert3="INSERT INTO Transaction VALUES ('"+pre+"',"+name+")";
									Statement sre=con.createStatement();
									sre.executeUpdate(insert3);
									sre.close();
							}
						}
						
					}
										
					
					
				}
				
				
			     
			   
			   
					br.close();
					}
				
					
				     
				     
				}catch (Exception e2)
				{ 
					JOptionPane.showMessageDialog(null, e2.getMessage()+" oui fichier");
				}
				
				
			     System.out.println(tables.size());
			     System.out.println(attributs.size());
			     System.out.println(predicats.size());
			     JTable table=new JTable();
			    FenetreExecute fe=new FenetreExecute(table,con);
			    Container c=fe.getContentPane();
				JPanel panell=new  JPanel();
				panell.setLayout(new GridLayout(0,2));
				
				try {
					for(int q=0;q<4;q++){
						String tableau="";
					switch(q){
					case 0:{tableau="Tables";break;}
					case 1:{tableau="Attributs";break;}
					case 2:{tableau="Predicats";break;}
					case 3:{tableau="Transaction";break;}
					}
					String tabl="select * from "+tableau;
					Statement s=con.createStatement();
					ResultSet rs=s.executeQuery(tabl);
					ResultSetMetaData rsm=rs.getMetaData();
					String[] col=new String[rsm.getColumnCount()];
					int rowcount=0;
					while(rs.next()){
						rowcount++;
					}
					String[][] row=new String[rowcount][rsm.getColumnCount()];
						for(int y=0;y<rsm.getColumnCount();y++){
						col[y]=rsm.getColumnName(y+1).toString();
					}
					int r=0;
					ResultSet rss=s.executeQuery(tabl);
					while(rss.next()){
						for(int y=1;y<=rsm.getColumnCount();y++){
							row[r][y-1]=rss.getString(y);
						}
						r++;
					}
					DefaultTableModel dtm=new DefaultTableModel(row,col);
					JLabel label=new JLabel("Table "+tableau+" Nombre tuples:"+String.valueOf(r),JLabel.CENTER);
					label.setVerticalAlignment(JLabel.TOP);
					label.setFont(new Font("Times new Roman",Font.BOLD,14));
					panell.add(label);
					JTable tablee=new JTable(dtm);
					JScrollPane spp=new JScrollPane(tablee);
					panell.add(spp);
					
					}
					JScrollPane spf=new JScrollPane(panell);
					spf.setPreferredSize(new Dimension(800,400));
					c.add(spf);
					fe.setVisible(true);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		  }  
		  });
		this.fichier.add(item1);
		this.item2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{System.exit(0); }});
		this.fichier.add(item2);
		this.menubar.add(fichier);
		this.setJMenuBar(menubar);
		
		this.setContentPane(pan);
		this.setVisible(true);
		
	}
		
	
	
}
