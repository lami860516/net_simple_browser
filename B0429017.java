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
    //顯示GUI介面
    public static void showWindow(){
        JFrame frame = new JFrame("Computer Network B0429017：DD");//窗口
        JPanel pnl = new JPanel();//平面
        //frame基本設定
        frame.setLocation(250,200);
        frame.setSize(100,100);
        frame.setPreferredSize(new Dimension(800, 800));  
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JFrame.setDefaultLookAndFeelDecorated(true);
        
        //元件設定
        JLabel txtBlank = new JLabel("   ");
        JLabel txtDash = new JLabel("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        JLabel txtURL = new JLabel("請輸入URL:");
        final JTextField editURL = new JTextField(25);//長度25的輸入框
        JLabel txtDir = new JLabel(" 請輸入文件路徑:");
        final JTextField editDir = new JTextField(25);
        JLabel txtRecur = new JLabel("是否遞迴下載:");
        final JComboBox comboBox=new JComboBox();//下拉選項
        comboBox.addItem("是");  
        comboBox.addItem("否");  
        JLabel txtRecurNum = new JLabel(" 遞迴層數:");
        final JTextField editRecurNum = new JTextField(5);
        editRecurNum.addKeyListener(new KeyAdapter(){  //讓使用者只能輸入數字
            public void keyTyped(KeyEvent e) {  
                int keyChar = e.getKeyChar();                 
                if(keyChar >= KeyEvent.VK_0 && keyChar <= KeyEvent.VK_9){  
                     
                }else{  
                    e.consume();
                }  
            }  
        });  
        JLabel txtIsOnlyType = new JLabel(" 僅下載某種檔:");
        final JComboBox boxIsOnlyType=new JComboBox();  
        boxIsOnlyType.addItem("否");  
        boxIsOnlyType.addItem("jpg");  
        boxIsOnlyType.addItem("png");  
        
        JButton btnStart = new JButton(">> 開始下載");
        btnStart.addActionListener(new ActionListener() { //當按下按鈕執行
        	public void actionPerformed(ActionEvent e) { 
        		hostURL=editURL.getText();
        		outputDir=editDir.getText();
        	
        		if(comboBox.getSelectedIndex()==0)//層數的設定
        			maxlayerNum=Integer.parseInt(editRecurNum.getText());
        		else
        			maxlayerNum=0;
        		if(boxIsOnlyType.getSelectedIndex()==0)//下載檔案種類設定
        			isOnlyType=false;
        		else {
        			isOnlyType=true;
        			onlyType=boxIsOnlyType.getSelectedItem().toString();
        			
        		}
        		new Thread(){//將執行下載部分放在執行緒裡面
        			public void run(){
        				beginGet();//開始下載
        			}
				}.start();
        		
        	} 
        });
        
        textArea = new JTextArea("下載情況：>>>\n",30, 65);//30x65大小的輸出文字框
        textArea.setBackground(Color.getHSBColor(180, 18, 90));//設定背景顏色
        JScrollPane scrollPlane = new JScrollPane(textArea);//將文字框放在可捲動平面
        txtFileNum = new JLabel("目前下載檔案數：0 個     ");
        txtFileSize = new JLabel("目前下載檔案大小：0 B     ");
        txtTime = new JLabel("目前下載時間：0 sec");
        
        //加入元件
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
        
        frame.pack();//執行
        frame.setVisible(true);
    }
    //開始下載
    public static void beginGet(){
    	getHTML(hostURL);//取得HTML
        rootUrl=URL;
        int j;
        //如果還在選定的層數內
        while(layerNum<=maxlayerNum){
        	layerNum++;
        	int size=htmlTemp.size();
            for(int i=0;i<size;i++){
                String htmlPath=htmlTemp.get(i);
                System.out.print(htmlTemp.get(i)+"  "+hostURL.substring(hostURL.lastIndexOf('/')+1));
                //如果在對的層數
                if(htmlPath.substring(htmlPath.indexOf('-')+1).equals(String.valueOf(layerNum-1))&&!htmlTemp.get(i).substring(0,htmlTemp.get(i).indexOf('-')).equals(hostURL.substring(hostURL.lastIndexOf('/')+1))){
                	for(j=0;j<i;j++){
                		//避免下載重複的檔案
                		if(htmlTemp.get(i).substring(0,htmlTemp.get(i).indexOf('-')).equals(htmlTemp.get(j).substring(0,htmlTemp.get(j).indexOf('-'))))
                			break;
                	}
                	//前面沒有重複檔案
                	if(i==j){
                		//getHTML檔案
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
        //圖片檔
        for(int i=0;i<imgTemp.size();i++){
        	//如果沒有重複
        	for(j=0;j<i;j++){
        		if(imgTemp.get(i).equals(imgTemp.get(j)))
        			break;
        	}
        	//判斷是否是使用者設定的僅下載檔案並下載圖片檔
        	if(i==j&&(!isOnlyType||imgTemp.get(i).substring(imgTemp.get(i).lastIndexOf('/')).contains(onlyType)))
        		getImg(imgTemp.get(i));
            
        }
        textArea.append("\n*****下載完成*****\n");
        imgTemp.clear();
        htmlTemp.clear();

    }
    //取得HTML檔案，address為路徑
    public static void getHTML(String address){
        try {
            try {
            	//放入URL
                URL=new URL(address);
                URL.getHost();
                textArea.append(" "+address.substring(address.lastIndexOf('/')+1)+"  ===");
            } catch (Exception e) {
                textArea.append("*****URL不正確*****"+"\n");textArea.paintImmediately(textArea.getBounds());   
                return ;
            }
            if (!(URL.getProtocol().equalsIgnoreCase("http"))) {
                textArea.append("請輸入http網址"+"\n");textArea.paintImmediately(textArea.getBounds());   
                return ;
            }
            //建立socket
            Socket socket = new Socket(URL.getHost(),80);
            //寄出要求
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
            out.println(createRequest(URL.getHost(),URL.getFile()));
            out.println();
            out.flush();
            //取得回復
            InputStream input = URL.openStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input, "UTF-8"));
            //輸出參數
            File allPath=null;
            FileOutputStream fileOutputStream=null;
            OutputStreamWriter writer = null;
            
            try{//檢查本機路徑及資料是否存在或需要創建
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
                //輸出工具
                fileOutputStream = new FileOutputStream(allPath, false);
                writer = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);

            }catch (Exception e){
                e.printStackTrace();
                textArea.append("*****位置輸入有誤*****");
            }
            textArea.append("==");
            String inputLine;
            long startTime = System.currentTimeMillis();
            //讀寫輸出
            while ((inputLine = in.readLine()) != null) {
                if(inputLine.contains("img")){//如果是img
                    String imgAddr=inputLine.substring(inputLine.indexOf("\"")+1,inputLine.indexOf("\"",inputLine.indexOf("\"")+1));
                    imgTemp.add(imgAddr);
                    inputLine=inputLine.replace(imgAddr,imgAddr.substring(imgAddr.lastIndexOf("/")+1));
                }
                if(inputLine.contains("href")){//如果是html
                    String htmlAddr=inputLine.substring(inputLine.indexOf("\"")+1,inputLine.indexOf("\"",inputLine.indexOf("\"")+1));
                    htmlTemp.add(htmlAddr+'-'+layerNum);
                    
                }
                inputLine+='\n';
                writer.write(inputLine);//寫入
                writer.flush();
            }
            in.close();
            writer.close();
            long endTime = System.currentTimeMillis();
            //寫出的檔案
            File file = new File(outputDir +URL.getFile());
            //格式
            DecimalFormat dq = new DecimalFormat("0.0000");
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
            //時間
            Date date = new Date();
            String strDate = sdFormat.format(date);
            //顯示資訊
            textArea.append("=====>  "+calcuFileSize(file.length())+"B     \n\t\t----------in:" + dq.format((double)(endTime - startTime)/1000) + "s"+"   speed:"+calcuFileSize(file.length()/((double)(endTime - startTime)/1000)));textArea.append("bps   at:"+strDate+"----------\n");textArea.paintImmediately(textArea.getBounds());     
            fileNum++;fileSize+=(double) file.length();time+=(double)(endTime - startTime)/1000;
            txtFileNum.setText("目前下載檔案數："+fileNum+" 個     ");
            txtFileSize.setText("目前下載檔案大小："+calcuFileSize(fileSize)+"B     ");
            txtTime.setText("目前下載時間："+calcuTime(time));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //取得img檔案，srcPath為路徑
    public static void getImg(String srcPath){
        try {
            try {
            	//建立URL
                URL=new URL(rootUrl.getProtocol()+"://"+rootUrl.getHost()+srcPath);
                textArea.append(" "+srcPath.substring(srcPath.lastIndexOf('/')+1)+"  ===");
                //URL.getHost();
            } catch (Exception e) {
                textArea.append("*****URL不正確*****"+"\n");textArea.paintImmediately(textArea.getBounds());   
                return ;
            }
            //建立socket
            Socket socket = new Socket(URL.getHost(),80);
            //寄出HTTP要求
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
            out.println(createRequest(URL.getHost(),URL.getFile()));
            //textArea.append(URL.getHost()+" "+URL.getFile()+"\n");textArea.paintImmediately(textArea.getBounds()); 
            out.println();
            out.flush();
            //取得回復
            InputStream input = URL.openStream();
            File allPath=null;
            FileOutputStream fileOutputStream=null;
            try{//檢查本機路徑及資料是否存在或需要創建
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
                textArea.append("*****位置輸入有誤*****");
            }
            
            textArea.append("==");
            long startTime = System.currentTimeMillis();
            //以int方法一個一個讀取
            int inputInt;
            while ((inputInt=input.read()) != -1) {
                fileOutputStream.write(inputInt);
                fileOutputStream.flush();
            }
            //in.close();
            fileOutputStream.close();
            long endTime = System.currentTimeMillis();
            //輸出的檔案
            File file = new File(outputDir +URL.getFile().substring(URL.getFile().lastIndexOf('/')));
            //格式
            DecimalFormat dq = new DecimalFormat("0.0000");
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
            //日期時間
            Date date = new Date();
            String strDate = sdFormat.format(date);
            //顯示資訊
            textArea.append("=====>  "+calcuFileSize(file.length())+"B     \n\t\t----------in:" + dq.format((double)(endTime - startTime)/1000) + "s"+"   speed:"+calcuFileSize(file.length()/((double)(endTime - startTime)/1000)));textArea.append("bps   at:"+strDate+"----------\n");textArea.paintImmediately(textArea.getBounds());   
            fileNum++;fileSize+=(double) file.length();time+=(double)(endTime - startTime)/1000;
            txtFileNum.setText("目前下載檔案數："+fileNum+" 個     ");
            txtFileSize.setText("目前下載檔案大小："+calcuFileSize(fileSize)+"B     ");
            txtTime.setText("目前下載時間："+calcuTime(time));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //製造request
    // host：主機，file：路徑
    public static String createRequest(String host,String file){
        String request="";
        request+="GET /"+file+" HTTP/1.1\r\n" +
                "Host: "+host+"\r\n" +
                "Connection: keep-alive\r\n" +
                "\r\n";
        return request;

    }
    //計算檔案大小，fileSize是以BYTES為單位，回傳值會是String為已經轉成各個單位
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
    //計算時間，Time是以秒為單位的，回傳值String是已經轉換完的
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