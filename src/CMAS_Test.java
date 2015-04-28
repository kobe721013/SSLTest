import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;



import Comm.Socket.SSL;


public class CMAS_Test {
	static Logger logger = Logger.getLogger(CMAS_Test.class);
	 
	
	
	public static void main(String args[])
	{
		
		
		logger.info("AP Start");
		
		SSL ssl = null;
		Properties logp = new Properties();	
		//logp.load(EasycardAPI.class.getResourceAsStream("../config/log4j.properties"));
		try {
			logp.load(CMAS_Test.class.getClassLoader().getResourceAsStream("log4j.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		try {
			String req = "<TransXML><Trans><T0100>0800</T0100><T0300>881999</T0300><T1100>165625</T1100><T1101>165625</T1101><T1200>115016</T1200><T1201>115016</T1201><T1300>20150325</T1300><T1301>20150325</T1301><T3700>20150325165625</T3700><T4100>0A100D112700</T4100><T4101>4ED4FE00</T4101><T4102>192.168.1.102</T4102><T4103>KOBE-PC-24</T4103><T4104>B0808C59</T4104><T4200>00010001</T4200><T4210>00000</T4210><T4802>00</T4802><T4820>01</T4820><T4823>11</T4823><T4824>00</T4824><T5301>01</T5301><T5307>45F34FC1D89E0D7C</T5307><T5308>EE63B2D99F654899</T5308><T5361>5430363931333432</T5361><T5362>00005E54</T5362><T5363>2233D81FDFA75D73</T5363><T5364>014DD9822020DF01DF02DF03DF04DF05DF06DF07DF08DF09DF10DF11DF12000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000</T5364><T5365>007102F04909000000000000</T5365><T5366>102700</T5366><T5368>00001644</T5368><T5369>11</T5369><T5370>0A100D1127000000164386A0125500900000000000000000000000000000000000</T5370><T5371>5039303030373234</T5371><T5501>15032525</T5501><T5503>0000100011</T5503><T5504>00</T5504><T5510>1234</T5510><T5588><T558801>01</T558801><T558803>5566</T558803></T5588><T5588><T558801>02</T558801><T558803>00000</T558803></T5588><T5588><T558801>03</T558801><T558802>EasyCardApi</T558802><T558803>00001</T558803></T5588><T5596><T559601>00000000</T559601><T559602>00000000</T559602><T559603>00000000</T559603><T559604>00000000</T559604></T5596><T6000>261D0323D000</T6000><T6002>11B80B01E8030001A0860101F40100   000010000000000</T6002><T6003>A086010000000000000000000000000000000000000000000000000000000000</T6003><T6004>00000</T6004><T6400>38056D399ED7BECF</T6400><T6408>4746F0702E0B356E2818341AB587EC1E</T6408></Trans></TransXML>\n";
			String resp = null;
		    ssl = new SSL("cmas-gateway.easycard.com.tw", 
					7000, 
					null,
					null);
			
			if(ssl.connect()) logger.info("connect OK");
			else logger.info("connect fail");
			
			resp = ssl.sendRequest(req);
			logger.info("1st resp:"+resp);
			Thread.sleep(2000);
			
			
			resp = ssl.sendRequest(req);
			logger.info("2nd resp:"+resp);
			
			Thread.sleep(2000);
			
			resp = ssl.sendRequest(req);
			logger.info("3th resp:"+resp);
			
			
		} catch (Exception e)
		{
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
		finally{
			ssl.disconnect();			
		}
	}
}
