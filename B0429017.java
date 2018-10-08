import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
public class B0429017{
    public static String hostURL,outputDir;
    public static URL URL;
    public static URL rootUrl;
    public static JTextArea textArea;
    public static ArrayList<String> imgTemp=new ArrayList<String>();
    public static ArrayList<String> htmlTemp=new ArrayList<String>();
    public static int layerNum=1,maxlayerNum;
    public static JLabel txtFileNum ,txtFileSize,txtTime;
    public static int fileNum=0;
    public static double fileSize=0.0,time=0.0;
    public static boolean isOnlyType=false;
    public static String onlyType;
    
    public static void main(String[] args) throws IOException {
        showWindow();
    }
    //���GUI����
    public static void showWindow(){
        JFrame frame = new JFrame("Computer Network B0429017�GDD");//���f
        JPanel pnl = new JPanel();//����
        //frame�򥻳]�w
        frame.setLocation(250,200);
        frame.setSize(100,100);
        frame.setPreferredSize(new Dimension(800, 800));  
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JFrame.setDefaultLookAndFeelDecorated(true);
        
        //����]�w
        JLabel txtBlank = new JLabel("   ");
        JLabel txtDash = new JLabel("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        JLabel txtURL = new JLabel("�п�JURL:");
        final JTextField editURL = new JTextField(25);//����25����J��
        JLabel txtDir = new JLabel(" �п�J�����|:");
        final JTextField editDir = new JTextField(25);
        JLabel txtRecur = new JLabel("�O�_���j�U��:");
        final JComboBox comboBox=new JComboBox();//�U�Կﶵ
        comboBox.addItem("�O");  
        comboBox.addItem("�_");  
        JLabel txtRecurNum = new JLabel(" ���j�h��:");
        final JTextField editRecurNum = new JTextField(5);
        editRecurNum.addKeyListener(new KeyAdapter(){  //���ϥΪ̥u���J�Ʀr
            public void keyTyped(KeyEvent e) {  
                int keyChar = e.getKeyChar();                 
                if(keyChar >= KeyEvent.VK_0 && keyChar <= KeyEvent.VK_9){  
                     
                }else{  
                    e.consume();
                }  
            }  
        });  
        JLabel txtIsOnlyType = new JLabel(" �ȤU���Y����:");
        final JComboBox boxIsOnlyType=new JComboBox();  
        boxIsOnlyType.addItem("�_");  
        boxIsOnlyType.addItem("jpg");  
        boxIsOnlyType.addItem("png");  
        
        JButton btnStart = new JButton(">> �}�l�U��");
        btnStart.addActionListener(new ActionListener() { //����U���s����
        	public void actionPerformed(ActionEvent e) { 
        		hostURL=editURL.getText();
        		outputDir=editDir.getText();
        	
        		if(comboBox.getSelectedIndex()==0)//�h�ƪ��]�w
        			maxlayerNum=Integer.parseInt(editRecurNum.getText());
        		else
        			maxlayerNum=0;
        		if(boxIsOnlyType.getSelectedIndex()==0)//�U���ɮ׺����]�w
        			isOnlyType=false;
        		else {
        			isOnlyType=true;
        			onlyType=boxIsOnlyType.getSelectedItem().toString();
        			
        		}
        		new Thread(){//�N����U��������b������̭�
        			public void run(){
        				beginGet();//�}�l�U��
        			}
				}.start();
        		
        	} 
        });
        
        textArea = new JTextArea("�U�����p�G>>>\n",30, 65);//30x65�j�p����X��r��
        textArea.setBackground(Color.getHSBColor(180, 18, 90));//�]�w�I���C��
        JScrollPane scrollPlane = new JScrollPane(textArea);//�N��r�ة�b�i���ʥ���
        txtFileNum = new JLabel("�ثe�U���ɮ׼ơG0 ��     ");
        txtFileSize = new JLabel("�ثe�U���ɮפj�p�G0 B     ");
        txtTime = new JLabel("�ثe�U���ɶ��G0 sec");
        
        //�[�J����
        pnl.setBorder(new EmptyBorder(18,10,10,10));
        pnl.add(txtURL);
        pnl.add(editURL);
        pnl.add(txtBlank);
        pnl.add(txtDir);
        pnl.add(editDir);
        pnl.add(txtRecur);
        pnl.add(comboBox);
        pnl.add(txtRecurNum);
        pnl.add(editRecurNum);
        pnl.add(txtIsOnlyType);
        pnl.add(boxIsOnlyType);
        pnl.add(btnStart);
        pnl.add(txtDash);
        pnl.add(scrollPlane);
        pnl.add(txtFileNum);
        pnl.add(txtFileSize);
        pnl.add(txtTime);
        frame.add(pnl);
        
        frame.pack();//����
        frame.setVisible(true);
    }
    //�}�l�U��
    public static void beginGet(){
    	getHTML(hostURL);//���oHTML
        rootUrl=URL;
        int j;
        //�p�G�٦b��w���h�Ƥ�
        while(layerNum<=maxlayerNum){
        	layerNum++;
        	int size=htmlTemp.size();
            for(int i=0;i<size;i++){
                String htmlPath=htmlTemp.get(i);
                System.out.print(htmlTemp.get(i)+"  "+hostURL.substring(hostURL.lastIndexOf('/')+1));
                //�p�G�b�諸�h��
                if(htmlPath.substring(htmlPath.indexOf('-')+1).equals(String.valueOf(layerNum-1))&&!htmlTemp.get(i).substring(0,htmlTemp.get(i).indexOf('-')).equals(hostURL.substring(hostURL.lastIndexOf('/')+1))){
                	for(j=0;j<i;j++){
                		//�קK�U�����ƪ��ɮ�
                		if(htmlTemp.get(i).substring(0,htmlTemp.get(i).indexOf('-')).equals(htmlTemp.get(j).substring(0,htmlTemp.get(j).indexOf('-'))))
                			break;
                	}
                	//�e���S�������ɮ�
                	if(i==j){
                		//getHTML�ɮ�
                		if(htmlPath.substring(0,htmlPath.indexOf('-')).contains("http")){
                			getHTML(htmlPath.substring(0,htmlPath.indexOf('-')));
                		}
	                	else{
	                		getHTML(hostURL.substring(0,hostURL.lastIndexOf('/')+1)+htmlPath.substring(0,htmlPath.indexOf('-')));
	                	}
                	}
                }
            }
        }
        //�Ϥ���
        for(int i=0;i<imgTemp.size();i++){
        	//�p�G�S������
        	for(j=0;j<i;j++){
        		if(imgTemp.get(i).equals(imgTemp.get(j)))
        			break;
        	}
        	//�P�_�O�_�O�ϥΪ̳]�w���ȤU���ɮרäU���Ϥ���
        	if(i==j&&(!isOnlyType||imgTemp.get(i).substring(imgTemp.get(i).lastIndexOf('/')).contains(onlyType)))
        		getImg(imgTemp.get(i));
            
        }
        textArea.append("\n*****�U������*****\n");
        imgTemp.clear();
        htmlTemp.clear();

    }
    //���oHTML�ɮסAaddress�����|
    public static void getHTML(String address){
        try {
            try {
            	//��JURL
                URL=new URL(address);
                URL.getHost();
                textArea.append(" "+address.substring(address.lastIndexOf('/')+1)+"  ===");
            } catch (Exception e) {
                textArea.append("*****URL�����T*****"+"\n");textArea.paintImmediately(textArea.getBounds());   
                return ;
            }
            if (!(URL.getProtocol().equalsIgnoreCase("http"))) {
                textArea.append("�п�Jhttp���}"+"\n");textArea.paintImmediately(textArea.getBounds());   
                return ;
            }
            //�إ�socket
            Socket socket = new Socket(URL.getHost(),80);
            //�H�X�n�D
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
            out.println(createRequest(URL.getHost(),URL.getFile()));
            out.println();
            out.flush();
            //���o�^�_
            InputStream input = URL.openStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input, "UTF-8"));
            //��X�Ѽ�
            File allPath=null;
            FileOutputStream fileOutputStream=null;
            OutputStreamWriter writer = null;
            
            try{//�ˬd�������|�θ�ƬO�_�s�b�λݭn�Ы�
                File root = new File(outputDir);
                if (!root.exists()) {
                    root.mkdir();
                }
                String fileName=URL.getFile().substring(URL.getFile().lastIndexOf('/'));
                if(fileName.length()<=1)
                	fileName="root.html";
                allPath = new File(outputDir +"/"+fileName);
                if (!allPath.exists()) {
                    allPath.createNewFile();
                }
                //��X�u��
                fileOutputStream = new FileOutputStream(allPath, false);
                writer = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);

            }catch (Exception e){
                e.printStackTrace();
                textArea.append("*****��m��J���~*****");
            }
            textArea.append("==");
            String inputLine;
            long startTime = System.currentTimeMillis();
            //Ū�g��X
            while ((inputLine = in.readLine()) != null) {
                if(inputLine.contains("img")){//�p�G�Oimg
                    String imgAddr=inputLine.substring(inputLine.indexOf("\"")+1,inputLine.indexOf("\"",inputLine.indexOf("\"")+1));
                    imgTemp.add(imgAddr);
                    inputLine=inputLine.replace(imgAddr,imgAddr.substring(imgAddr.lastIndexOf("/")+1));
                }
                if(inputLine.contains("href")){//�p�G�Ohtml
                    String htmlAddr=inputLine.substring(inputLine.indexOf("\"")+1,inputLine.indexOf("\"",inputLine.indexOf("\"")+1));
                    htmlTemp.add(htmlAddr+'-'+layerNum);
                    
                }
                inputLine+='\n';
                writer.write(inputLine);//�g�J
                writer.flush();
            }
            in.close();
            writer.close();
            long endTime = System.currentTimeMillis();
            //�g�X���ɮ�
            File file = new File(outputDir +URL.getFile());
            //�榡
            DecimalFormat dq = new DecimalFormat("0.0000");
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
            //�ɶ�
            Date date = new Date();
            String strDate = sdFormat.format(date);
            //��ܸ�T
            textArea.append("=====>  "+calcuFileSize(file.length())+"B     \n\t\t----------in:" + dq.format((double)(endTime - startTime)/1000) + "s"+"   speed:"+calcuFileSize(file.length()/((double)(endTime - startTime)/1000)));textArea.append("bps   at:"+strDate+"----------\n");textArea.paintImmediately(textArea.getBounds());     
            fileNum++;fileSize+=(double) file.length();time+=(double)(endTime - startTime)/1000;
            txtFileNum.setText("�ثe�U���ɮ׼ơG"+fileNum+" ��     ");
            txtFileSize.setText("�ثe�U���ɮפj�p�G"+calcuFileSize(fileSize)+"B     ");
            txtTime.setText("�ثe�U���ɶ��G"+calcuTime(time));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //���oimg�ɮסAsrcPath�����|
    public static void getImg(String srcPath){
        try {
            try {
            	//�إ�URL
                URL=new URL(rootUrl.getProtocol()+"://"+rootUrl.getHost()+srcPath);
                textArea.append(" "+srcPath.substring(srcPath.lastIndexOf('/')+1)+"  ===");
                //URL.getHost();
            } catch (Exception e) {
                textArea.append("*****URL�����T*****"+"\n");textArea.paintImmediately(textArea.getBounds());   
                return ;
            }
            //�إ�socket
            Socket socket = new Socket(URL.getHost(),80);
            //�H�XHTTP�n�D
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
            out.println(createRequest(URL.getHost(),URL.getFile()));
            //textArea.append(URL.getHost()+" "+URL.getFile()+"\n");textArea.paintImmediately(textArea.getBounds()); 
            out.println();
            out.flush();
            //���o�^�_
            InputStream input = URL.openStream();
            File allPath=null;
            FileOutputStream fileOutputStream=null;
            try{//�ˬd�������|�θ�ƬO�_�s�b�λݭn�Ы�
                File root = new File(outputDir);
                if (!root.exists()) {
                    root.mkdir();
                }
                allPath = new File(outputDir +URL.getFile().substring(URL.getFile().lastIndexOf('/')));
                if (!allPath.exists()) {
                    allPath.createNewFile();
                }
                fileOutputStream = new FileOutputStream(allPath, false);

            }catch (Exception e){
                e.printStackTrace();
                textArea.append("*****��m��J���~*****");
            }
            
            textArea.append("==");
            long startTime = System.currentTimeMillis();
            //�Hint��k�@�Ӥ@��Ū��
            int inputInt;
            while ((inputInt=input.read()) != -1) {
                fileOutputStream.write(inputInt);
                fileOutputStream.flush();
            }
            //in.close();
            fileOutputStream.close();
            long endTime = System.currentTimeMillis();
            //��X���ɮ�
            File file = new File(outputDir +URL.getFile().substring(URL.getFile().lastIndexOf('/')));
            //�榡
            DecimalFormat dq = new DecimalFormat("0.0000");
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
            //����ɶ�
            Date date = new Date();
            String strDate = sdFormat.format(date);
            //��ܸ�T
            textArea.append("=====>  "+calcuFileSize(file.length())+"B     \n\t\t----------in:" + dq.format((double)(endTime - startTime)/1000) + "s"+"   speed:"+calcuFileSize(file.length()/((double)(endTime - startTime)/1000)));textArea.append("bps   at:"+strDate+"----------\n");textArea.paintImmediately(textArea.getBounds());   
            fileNum++;fileSize+=(double) file.length();time+=(double)(endTime - startTime)/1000;
            txtFileNum.setText("�ثe�U���ɮ׼ơG"+fileNum+" ��     ");
            txtFileSize.setText("�ثe�U���ɮפj�p�G"+calcuFileSize(fileSize)+"B     ");
            txtTime.setText("�ثe�U���ɶ��G"+calcuTime(time));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //�s�yrequest
    // host�G�D���Afile�G���|
    public static String createRequest(String host,String file){
        String request="";
        request+="GET /"+file+" HTTP/1.1\r\n" +
                "Host: "+host+"\r\n" +
                "Connection: keep-alive\r\n" +
                "\r\n";
        return request;

    }
    //�p���ɮפj�p�AfileSize�O�HBYTES�����A�^�ǭȷ|�OString���w�g�ন�U�ӳ��
    public static String calcuFileSize(double fileSize){
    	String fileSizeString = "";
    	DecimalFormat df = new DecimalFormat("0.00");
        if (fileSize < 1024) {
            fileSizeString = df.format((double) fileSize/1024) ;         }
        else if (fileSize < 1048576) {
            fileSizeString = df.format((double) fileSize / 1024) + "K";        }
        else if (fileSize < 1073741824) {
            fileSizeString = df.format((double) fileSize / 1048576) + "M";     }
        else {
            fileSizeString = df.format((double) fileSize / 1073741824) + "G";  }
        return fileSizeString;
    }
    //�p��ɶ��ATime�O�H����쪺�A�^�ǭ�String�O�w�g�ഫ����
    public static String calcuTime(double Time){
    	String TimeString="";
    	int min=0;
    	DecimalFormat df = new DecimalFormat("0.000");
    	if(Time<60)
    		TimeString=df.format(Time)+" sec ";
    	else {min=(int)Math.floor(Time/60);
    		TimeString=min+" min "+df.format(Time-(min*60))+" sec ";
    	}
    		
    	return TimeString;
    }
}