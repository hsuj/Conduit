import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class LoginForm extends JFrame implements ActionListener {
	JPanel panel;
    JLabel lblname;
    JLabel lblpassword;
    JLabel lblmess;
    JTextField txtname;
    JPasswordField txtpassword;
    JButton btlogin;
	JPanel panelreg;
	JLabel lblnamereg;
	JLabel lblpasswordreg;
	JLabel lblmessreg;
	JTextField txtnamereg;
	JPasswordField txtpasswordreg;
	JButton btsubmit;

	LoginForm() {
		setTitle("Member login and Registration");
        setSize(630,250);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container content=getContentPane();
        JDesktopPane des=new JDesktopPane();

        //Login form
        JInternalFrame flog=new JInternalFrame();
        flog.setSize(300,200);
		flog.setLocation(10,2);
		flog.setTitle("Member Login");
		lblname=new JLabel("User Name:");
		lblpassword=new JLabel("Password:");
		lblmess=new JLabel("");
		btlogin=new JButton("Login");
		btlogin.addActionListener(this);               
		txtname=new JTextField(20);
		txtpassword=new JPasswordField(20);
		panel=new JPanel();
		panel.add(lblname);
		panel.add(txtname);
		panel.add(lblpassword);
		panel.add(txtpassword);
		panel.add(btlogin);
		panel.add(lblmess);
		flog.add(panel);
		flog.setVisible(true);

		//Registration form
		JInternalFrame freg=new JInternalFrame();
		freg.setSize(300,200);
		freg.setLocation(315,2);
		freg.setTitle("Member Registration");
		lblnamereg=new JLabel("User Name:");
		lblpasswordreg=new JLabel("Password:");
		lblmessreg=new JLabel("");
		btsubmit=new JButton("Submit");
		btsubmit.addActionListener(this);
		btlogin.addActionListener(this);
		txtnamereg=new JTextField(20);
		txtpasswordreg=new JPasswordField(20);
		txtpasswordreg.addKeyListener(new KeyList());
		panelreg=new JPanel();
		panelreg.add(lblnamereg);
		panelreg.add(txtnamereg);
		panelreg.add(lblpasswordreg);
		panelreg.add(txtpasswordreg);
		panelreg.add(btsubmit);
		panelreg.add(lblmessreg);            
		freg.add(panelreg);
		freg.setVisible(true);                       
		des.add(flog);
		des.add(freg);
		content.add(des, BorderLayout.CENTER);
		setVisible(true);                   
		txtname.requestFocus();    
	}

    /********
    Login button: Checks if account login information exists in file
    to print either "Valid login" or "Invalid login"

    Submit button: Checks if registration fields are blank then checks
    if user/pass already exists in file. If not, saves to file
    ********/	 
    public void actionPerformed(ActionEvent e){        
        if(e.getSource()==btsubmit){
            String uname=txtnamereg.getText();
            String passw=new String(txtpasswordreg.getPassword());
            if(!checkBlank(uname,passw, lblnamereg,lblpasswordreg)){
                if(!checkExist("accounts.txt",uname)){
                    passw=new String(encrypt(passw));                   
                    String accinfo=uname+"-"+passw;
                    saveToFile("accounts.txt",accinfo);
                }
            }          
                                   
        }
        else if(e.getSource()==btlogin){
            String uname=txtname.getText();
            String passw=new String(txtpassword.getPassword());
            if(!checkBlank(uname,passw,lblname,lblpassword)) {
            	validateUser("accounts.txt",uname,passw);
            }
        }
    }
    //allows keys to be used in checkStrength method
    public class KeyList extends KeyAdapter{
        public void keyPressed(KeyEvent ke){
            String passw=new String(txtpasswordreg.getPassword());
            String mess=checkStrength(passw);
            showMess(mess+" password",lblpasswordreg);
        }
    }

    //checks if fields are blank
    public boolean checkBlank(String name, String passw, JLabel namemess, JLabel passwmess){
        boolean hasBlank=false;
        if(name.length()<1){
            showMess("User name is required.",namemess);
            hasBlank=true;
            }
        if(passw.length()<1){
            showMess("Password is required.",passwmess);
            hasBlank=true;
            }
        return hasBlank;                                                                 
	}
    //shows red text to user if fields are correct
	public void showMess(String mess, JLabel lbl){
        lbl.setText(mess);
        lbl.setForeground(Color.RED);                
    }
    /****
    Strong: number and letter at least 8 characters
    Medium: number and letter below 8 characters
    Weak: only numbers or only letters

    note: after conditions are met, Weak changes to
    Medium or Strong when an additional character is
    entered so ""1234567a" is still Weak though
    technically Strong until another character is added 
    ****/
    public String checkStrength(String passw){
        Pattern pat=Pattern.compile("([0-9][aA-zZ]|[aA-zZ][0-9])");
        Matcher mat=pat.matcher(passw);
            if(mat.find()) {
                if(passw.length()>=8) return "Strong";
                else return "Medium";
                }
        else return "Weak";
	}
    //brings back "User Name:"" and "Password:" labels
	public void reset(JLabel lblname,JLabel lblpassw ){
        lblname.setText("User Name:");
        lblname.setForeground(Color.BLACK);
        lblpassw.setText("Password:");
        lblpassw.setForeground(Color.BLACK);
    }
    //goes to file to check if login account exists
    public void validateUser(String filename, String name, String password){
        FileReader fr;
        BufferedReader br;
        boolean valid=false;
        String accinfo;
        try{                                                       
            fr=new FileReader(filename);
            br=new BufferedReader(fr);
            while ((accinfo=br.readLine())!=null){       
                if(check(accinfo,name,password)){
                    showMess("Valid login",lblmess);
                    valid=true;
                    break;
                }
            }    
            if(!valid) showMess("Invalid login",lblmess);
            	reset(lblname,lblpassword);   
            br.close();
            fr.close();    
        } catch(Exception ie){System.out.println("Error!");}
    }
    public boolean check(String accinfo, String name, String passw){
        String[] info=accinfo.split("-");
        String uname=info[0];
        String pass=new String(decrypt(info[1]));
            if(uname.equals(name) && pass.equals(passw))
                return true;
            else return false;
    }
	public boolean checkExist(String filename, String name){
        FileReader fr;
        BufferedReader br;
        String accinfo;
        boolean exist=false;
        try{                                         
            fr=new FileReader(filename);
            br=new BufferedReader(fr);
            while ((accinfo=br.readLine())!=null){       
                if(check(accinfo,name)){
                    showMess("The account already exists.",lblmessreg);
                    exist=true;
                    break;
                }          
            }     
           br.close();
           fr.close();    
        } catch(Exception ie){System.out.println("Error!");}
        return exist;                          
	}
	public boolean check(String accinfo, String name){
        String[] info=accinfo.split("-");
        String uname=info[0];
        if(uname.equals(name))
            return true;
        else return false;                              
    }
    //saves registration information to file
    public void saveToFile(String filename,String text){
        try{
            FileWriter fw=new FileWriter(filename,true); 
            BufferedWriter bw=new BufferedWriter(fw);
            bw.write(text);
            bw.newLine();
            bw.flush();
            bw.close();
            showMess("The account is created.",lblmessreg);
            reset(lblnamereg,lblpasswordreg);
        }catch(IOException ie){System.out.println("Error in writing to file...");}         
	}
    //encrypts password by saving original password on
    //file as 1 additional byte
	public byte[] encrypt(String passw){
        byte[] sb=passw.getBytes();
        int i;                
        for(i=0;i<sb.length;i++)
            sb[i]=(byte)(sb[i]+1);                  
        return(sb);
    }
    public byte[] decrypt(String passw){
        byte[] sb=passw.getBytes();
        int i;                
        for(i=0;i<sb.length;i++) {
            sb[i]=(byte)(sb[i]-1); 
        }                      
        return(sb);
	}
	public static void main(String args[]){
   	    new LoginForm();
    }
}
