package Comm.Socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.log4j.Logger;

import Comm.Socket.MyX509TrustManager;


public class SSL extends Thread {
//public class SSL {
	
	static Logger logger = Logger.getLogger(SSL.class);
	
	//private String scCertFilePath = "../config/CMAS-FTP51T.jks";
	private String CAFilePath=null ;//= "../config/CMAS-FTP51T.jks";
	private String clentCertPath=null;
	//private final String scPassWord = "Cmas@999";
	
	private final int scBufferLen = 65535;
	
	private SSLSocket sslSocket = null;
	private String mUrl = null;
	private int mPort = 0; 
	private boolean isSocketOK = false;
	
    //private ICMASResponse mClient;
    
    private byte[] mBuffer = new byte[scBufferLen];
    private OutputStream out;
    private InputStream in;
    
	public SSL(String url, int port, String CAFilePath, String clientCertPath) {
		mUrl = url;
		mPort = port;
		this.CAFilePath = CAFilePath;
		this.clentCertPath = clientCertPath;
	}
	
	
	
	/**
	 * @return the isSocketOK
	 */
	public boolean isSocketOK() {
		return isSocketOK;
	}



	/**
	 * @param isSocketOK the isSocketOK to set
	 */
	public void setSocketOK(boolean isSocketOK) {
		this.isSocketOK = isSocketOK;
	}



	//protected boolean disconnect() {
	public boolean disconnect() {
		
		logger.info("Start");
		if (sslSocket == null) {
			logger.error("sslSocket Obj 'NULL'");
			return false;
		}
		
		try {
			logger.info("SSL Close");
			sslSocket.close();
		} catch (IOException e) {
			
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
		return true;
	}
	
	/*
	private KeyStore getKeyStore() throws KeyStoreException,
	  									  NoSuchAlgorithmException,
	  									  CertificateException,
	  									  IOException {
		
		logger.debug("SSL CertFileName:"+scCertFilePath);
		//FileInputStream fKeyStore = new FileInputStream("C:\\eclipse\\Workspace\\EasycardAPI\\config\\CMAS-FTP51T.jks");

		//InputStream fKeyStore = SSL.class.getClass().getResourceAsStream(scCertFilePath);
		
		
		InputStream fKeyStore = SSL.class.getClassLoader().getResourceAsStream(scCertFilePath);
		logger.debug("load SSL Cert OK");
		
		KeyStore keystore = KeyStore.getInstance("JKS");
		keystore.load(fKeyStore, scPassWord.toCharArray()); 

		fKeyStore.close();

		return keystore;
	}*/
	
	
	
	public boolean connect() {
		logger.info("Start");
		TrustManager[] trustManager = null;
		SSLContext context = null;
		try {
			trustManager = new TrustManager [] {
					(TrustManager) new MyX509TrustManager(CAFilePath,clentCertPath)				
			};
			context = SSLContext.getInstance("TLS");
			context.init(null, trustManager, null);
			
			//SocketFactory sf = context.getSocketFactory();
			//sslSocket = (SSLSocket) sf.createSocket(mUrl, mPort);
			
			
			SSLSocketFactory  sslf = context.getSocketFactory();
			sslSocket = (SSLSocket) sslf.createSocket(mUrl, mPort);
			sslSocket.setSoTimeout(10*1000);
			
			
			
			out= sslSocket.getOutputStream();
			in= sslSocket.getInputStream();
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			logger.error(e1.getMessage());
		}
		
		
		if(sslSocket==null) 
			return false;
		
		logger.info("sslSocket OK");
		logger.info("end");
		return true;
	}
	
	/*
	public boolean connect(){
		 // Generating the default SSLSocketFactory
        SSLSocketFactory sf = (SSLSocketFactory) SSLSocketFactory.getDefault();
        
        

        // Genearting SSLSocket
        SSLSocket s;
		try {
			s = (SSLSocket) sf.createSocket(mUrl, mPort);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      
		
		
		return true;
	}
	*/
	//protected void sendRequest() throws IOException {
	//protected String sendRequest(String req){
	public String sendRequest(String req){
		
		try {
			
		
			if (sslSocket == null) {
				//throw new IOException(ICMASResponse.scErr_NoConnection);
				logger.error("SSL Socket NoConnection");
				return null;
				//throw new IOException("SSL Socket NoConnection");
			}
			
			logger.info(sslSocket.getSession().isValid());
			/*
			OutputStream out;
			out = sslSocket.getOutputStream();
			
	   	 	InputStream in;
	   	 	in = sslSocket.getInputStream();
			*/
			//logger.debug("session id:"+new String(sslSocket.getSession().getId()));
	   	 	
			
	   	 	out.write(req.getBytes("UTF-8"));
	   	 	out.flush();
	
	   	 	String resp = null;
	   	 	int len = in.read(mBuffer);
	   	 	if (len > 0) {
	   	 		resp = new String(Arrays.copyOf(mBuffer, len), "UTF-8");
	   	 	}
	   	 
	   	 	logger.info("resp:"+resp);
	   	 	
	   	 
	   	
	   	 	//out.close(); //!!Note: close() means closeSocket
	   	 	//in.close(); //!!Note: close() means closeSocket
	   	 		   	 
	   	 	return resp;
		} catch(IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	
	public void run() {
		int retry= 2;
		int cnt=0;
		
		logger.debug("SSL Thread Start");
		
		synchronized (this) {
			while (cnt < retry) {
				
				logger.debug("cnt:"+cnt+", retry:"+retry);
				synchronized (this) {					
					if (!connect()) {
						cnt++;
						continue;
					}
					break;
				}
			}
			if(cnt == retry)
				this.setSocketOK(false);
			else
				this.setSocketOK(true);
		}
	}
	
	/*
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				synchronized (this) {
					this.wait();
				}
				if (!connect()) {
					continue;
				}
				sendRequest(); 
				disconnect();
			} catch (InterruptedException e) {
				if (mClient != null) {
					mClient.onError(e.getMessage());
				}
        		break;
			} catch (IOException e) {
				if (mClient != null) {
					mClient.onError(e.getMessage());
				}
			}
		}
	}*/
}
