package com.dbs.config;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;
import com.dbs.commons.CryptString;
import com.dbs.commons.POMCreater;


public class Config {

	private static final Logger logger = Logger.getLogger(Config.class);
	public static final String EXECUTION_ID = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(Calendar.getInstance().getTime());
	public static HashMap<String,String> appConfig = new HashMap<String,String>();
	public static HashMap<String,String> serenityConfig = new HashMap<String,String>();
	public static final String fp = File.separator;
	public static final String APP_CONFIG_PATH =  "src"+fp+"test"+fp+"resources" +fp+ "config" + fp + "Config_v3.6.3.xlsx";
	public static final String APP_CONFIG_CMD_PATH =  "resources" +fp+ "config" + fp + "Config.xlsx";
	public static final String APP_LOG4J_PATH =  "src"+fp+"test"+fp+"resources" +fp+ "config" + fp + "log4j2.xml";
	public static final String CURRENT_DIR = System.getProperty("user.dir");
	public static final String MOBILE_DEVICES_SHEET = "MobileDevicesData";
	public static String sql_appConfig="SELECT config_Category,config_Name,config_Value from appConfig where configName is not null";
	private static HashMap<String, String> driverData = new HashMap<String,String>();
	public static List<HashMap<String, String>> deviceList= new ArrayList<HashMap<String,String>>();
	public static int inUseDeviceCnt= 0;

	static {
		try {

			File certFile = new File(CURRENT_DIR+fp+"resources"+fp+"cacerts");
			if(certFile.exists()) {
				System.setProperty("javax.net.ssl.trustStore", "resources"+fp+"cacerts"); 
				System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
			}
			else {
				logger.error("cacerts file is NOT present under resources folder");
			}
			
			File confFile = null;
			if(System.getProperty("ConfigFilePath")!=null && !System.getProperty("ConfigFilePath").equalsIgnoreCase("")) 
				confFile = new File(CURRENT_DIR+fp+"digi-nextgenID"+fp+APP_CONFIG_PATH);
			else 
				confFile = new File(CURRENT_DIR+fp+APP_CONFIG_PATH);
			
			File confCustomFile = new File(APP_CONFIG_CMD_PATH);
			System.setProperty("javax.net.ssl.trustStore", "resources"+fp+"cacerts"); 
			System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
			String filename;
				if(!(System.getProperty("jira.story.id")==null)) 
					filename="resources"+fp+ "Config.xlsx";
				else 
					filename=APP_CONFIG_PATH;
			
			//File confFile = new File(filename);	
				if(confFile.exists() && System.getProperty("ConfigFilePath")!=null && !System.getProperty("ConfigFilePath").equalsIgnoreCase("")) {
					System.out.println("run from jenkins...as "+System.getProperty("ConfigFilePath"));
					System.out.println("Config file path is +"+CURRENT_DIR+fp+"digi-nextgenID"+fp+Config.APP_CONFIG_PATH);
					appConfig=read_Xl_prop(CURRENT_DIR+fp+"digi-nextgenID"+fp+Config.APP_CONFIG_PATH,Config.sql_appConfig);
					//PropertyConfigurator.configure(APP_LOG4J_PATH);
					File logFile = new File(APP_LOG4J_PATH);
					if(logFile.exists())
						DOMConfigurator.configure(APP_LOG4J_PATH);
				}else if(confFile.exists() && (System.getProperty("ConfigFilePath")==null || System.getProperty("ConfigFilePath").equalsIgnoreCase(""))) {
					appConfig=read_Xl_prop(CURRENT_DIR+fp+Config.APP_CONFIG_PATH,Config.sql_appConfig);
					//PropertyConfigurator.configure(APP_LOG4J_PATH);
					File logFile = new File(APP_LOG4J_PATH);
					if(logFile.exists())
						DOMConfigurator.configure(APP_LOG4J_PATH);
				}
				else if(confCustomFile.exists()){
					appConfig=read_Xl_prop(CURRENT_DIR+fp+Config.APP_CONFIG_CMD_PATH,Config.sql_appConfig);
				}else {
					String homeDir = System.getProperty("user.home");
					appConfig=read_Xl_prop(homeDir+fp+ "config" + fp + "Config.xlsx",Config.sql_appConfig);
				}

			String query = "select * from " + Config.MOBILE_DEVICES_SHEET;
			Fillo fillo = new Fillo();
			Connection conn;
			if(System.getProperty("ConfigFilePath")!=null && !System.getProperty("ConfigFilePath").equalsIgnoreCase(""))
				conn = fillo.getConnection(CURRENT_DIR+fp+"digi-nextgenID"+fp+Config.APP_CONFIG_PATH);
			else 
				conn = fillo.getConnection(Config.APP_CONFIG_PATH);
				
				
			Recordset rs = conn.executeQuery(query);
			while(rs.next() && rs.getField("Device Name") != "") {
				HashMap<String,String> tempHolder = new HashMap<String,String>();
				for (int i = 0; i < rs.getFieldNames().size(); i++) {
					tempHolder.put(rs.getFieldNames().get(i), rs.getField(rs.getFieldNames().get(i)));
				}
				
				tempHolder.put("Status", "notinuse");
				deviceList.add(tempHolder);
				logger.info("Device: "+tempHolder.get("Device Name")+" has been added to available device list");
			}
			logger.info("Total available Devices count: "+ deviceList.size());
			rs.close();
			conn.close();
		} catch (FilloException e) {
			System.err.println(e.getLocalizedMessage());
			System.err.println(
					"All the browsers and devices are 'inuse' status in config.xlsx. \nPlease change the status to 'notinuse'.\nOr they are in use already.\nExiting execution...\nStackTrace is below");
		}catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static final boolean LOCAL_RUN = Boolean.valueOf(appConfig.get("LocalRun"));
	public static final String COUNTRY = appConfig.get("Country");
	public static final String ENVIRONMENT = appConfig.get("Environment");
	public static final String BROWSER = appConfig.get("Browser");
	public static final String IB_URL = appConfig.get(COUNTRY + "_" + ENVIRONMENT + "_URL");
	public static final String DRIVER_PATH = appConfig.get("DriverPath");
	public static final String FILEUPLOAD_PATH = appConfig.get("FileUploadPath");
	public static final long LONG_WAIT = (long) Double
			.parseDouble(appConfig.get("LongWait").replaceAll("\\D+", ""));
	public static final long SHORT_WAIT = (long) Double
			.parseDouble(appConfig.get("ShortWait").replaceAll("\\D+", ""));
	public static final int WAIT_FOR_ELEMENT_COUNTER = (int) Double 
			.parseDouble(appConfig.get("WaitForElementCounter").replaceAll("\\D+", ""));
	public static final int SWIPE_COUNTER = Integer.parseInt(appConfig.get("SwipeCounter"));
	public static final String CHROME_DRIVER_PATH = appConfig.get("ChromeDriverPath");
	public static final String FIREFOX_DRIVER_PATH = appConfig.get("FirefoxDriverPath");
	
	public static final String FIREFOX_DRIVER = appConfig.get("FirefoxDriver");
	public static final String IE_DRIVER = appConfig.get("IEDriver");
	public static final String IE_DRIVER_NAME = appConfig.get("IEDriverName");
	public static final String CHROME_DRIVER = appConfig.get("ChromeDriver");
	public static final String EDGE_DRIVER = appConfig.get("EdgeDriver");
	public static final String ALM_URL = appConfig.get("ALM_url");
	public static final String ALM_USER = appConfig.get("ALM_user");
//	public static final String ALM_PASSWORD = CryptString.decrypt(appConfig.getProperty("ALM_password"));
	public static final String ALM_DOMAIN = appConfig.get("ALM_domain");
	public static final String ALM_PROJECT = appConfig.get("ALM_project");
	public static final boolean ERRORS_CHECK = Boolean.valueOf(appConfig.get("ErrorCheck"));
	public static final String DB_IB_REPO_TABLE = appConfig.get("DataBase_IB_Repo");
	public static final String DB_MB_REPO_TABLE = appConfig.get("DataBase_MB_Repo");
	public static final String DB_SRVC_REPO_TABLE = appConfig.get("DataBase_Service_Repo");
	public static final String DB_IB_IMAGE_REPO_TABLE = appConfig.get("DataBase_IB_Image_Repo");
	public static final String DB_MB_IMAGE_REPO_TABLE = appConfig.get("DataBase_MB_Image_Repo");	
	public static final String DB_AUTOMATION_DRIVER = appConfig.get("DataBase_Automation_JDBCdriver");
	public static final String DB_AUTOMATION_CONNECTION = appConfig.get("DataBase_Automation_Connection");
	public static final String DB_AUTOMATION_USER = appConfig.get("DataBase_Automation_User");
	public static final String DB_AUTOMATION_PASSWORD = CryptString.decrypt(appConfig.get("DataBase_Automation_Password"));
	public static final String MB_POM_DIRECTORY = appConfig.get("MB_POM_Directory");
	public static final String IB_POM_DIRECTORY = appConfig.get("IB_POM_Directory");
	public static final String WEBSERVER_PATH = appConfig.get("WebServerPath");
	public static final String LOG4J_PATH = appConfig.get("Log4J_FilePath");
	public static final String CHROMEDRIVER_LOGS_PATH = appConfig.get("ChomeDriverLogs");
	public static final String FIREFOXDRIVER_LOGS_PATH = appConfig.get("FirefoxDriverLogs");
	public static final String IEDRIVER_LOGS_PATH = appConfig.get("IEDriverlogs");
	public static final boolean ENABLEDRIVERLOGS = Boolean.valueOf(appConfig.get("EnableDriverLogging"));
	public static final String WEB_ELEMENT_VAR_TEXT = appConfig.get("VariableText");
	public static final String SECRET_KEY = appConfig.get("SecretKey");
	public static final boolean OTP_ENABLED = Boolean.valueOf(appConfig.get("OTP_Enabled"));
	public static final boolean OTP_STUBBED = Boolean.valueOf(appConfig.get("OTP_Stubbed"));
	public static final String KONY_MDLWRE_HOST = appConfig
			.get(COUNTRY + "_" + ENVIRONMENT + "_Kony_Mddlwr_Host");
	public static final String KONY_MDLWRE_USER_NAME = appConfig
			.get(COUNTRY + "_" + ENVIRONMENT + "_Kony_Mddlwr_UserName");
	//public static final String KONY_MDLWRE_PASSWORD = CryptString
		//	.decrypt(appConfig.getProperty(COUNTRY + "_" + ENVIRONMENT + "_Kony_Mddlwr_Password"));
	public static final String KONY_MDLWRE_LOG_PATH = appConfig
			.get(COUNTRY + "_" + ENVIRONMENT + "_Kony_Mddlwr_logPath");
	public static final String KONY_MDLWRE_LOG_NAME = appConfig.get(COUNTRY + "_Kony_Mddlwr_log_name");
	public static final String EXECUTION_MODE = appConfig.get("ExecutionMode");
	public static final String DB_REPORTING_CONNECTION = appConfig.get("DataBase_Reporting_Connection");
	public static final String DB_REPORTING_USER = appConfig.get("DataBase_Reporting_User");
	public static final String DB_REPORTING_PASSWORD = CryptString.decrypt(appConfig.get("DataBase_Reporting_Password"));
	public static final String DB_REPORT_RUN_NUMBER = getRunNum();
	public static final String EXTENT_REPORTS_PATH = appConfig.get("ExtentReportsPath").replace("$timestamp.html", Config.EXECUTION_ID + Config.fp);
	public static final String ZYPHER_EXTENT_REPORTS_PATH = appConfig.get("ZypherExtentReportsPath").replace("$timestamp.html", EXECUTION_ID + Config.fp);
	public static final String REPORTS_USERNAME = appConfig.get("ReportsUserName");
	public static final String REPORTS_DOCUMENT_NAME = appConfig.get("ReportsDocumentName");
	public static final String REPORTS_NAME = appConfig.get("ReportsName");
	public static final boolean LOCAL_REPORTS = Boolean.valueOf(appConfig.get("LocalReports"));
	public static final String SCREENSHOTS_PATH = EXTENT_REPORTS_PATH;
	public static final String JIRA_URL = appConfig.get("JIRA_URL");
	public static final String JIRA_USER = appConfig.get("JIRA_user");
	public static final String JIRA_PASSWORD = CryptString.decrypt(appConfig.get("JIRA_password"));
	public static final String JIRA_PROJECT = appConfig.get("JIRA_Project");
	public static final String RELEASE = appConfig.get("Release");
	public static final String SPRINT_NO = appConfig.get("Sprint_no");
	public static final String image_Driver_Usage = appConfig.get("Use_Image_Recognition_Driver") ;
	public static boolean USE_IMAGE_DRIVER = (image_Driver_Usage == null?false:Boolean.valueOf(image_Driver_Usage));
	public static final boolean DOWNLOAD_MANUAL_SCENARIOS = Boolean.valueOf(appConfig.get("DownloadManualScenarios"));
	public static final boolean GENERATE_DATATABLE_FILES = Boolean.valueOf(appConfig.get("GenerateDataTableFiles"));
	public static final String SCENARIO_SUMMARY_PATH = appConfig.get("ScenSummaryFilePath");
	public static final String TESTDATA_FILE_PATH = appConfig.get("TestDataFilePath");
	private static Boolean screenShotValFromConfig = Boolean.valueOf(appConfig.get("TakeScreenshotForEachAction"));
	public static final boolean TAKESCREENSHOT_FOREACH_ACTION = (screenShotValFromConfig == null?true:screenShotValFromConfig);
	public static String JBEHAVE_KEYWORDS = appConfig.get("jBehaveKeywords");
	public static String language;
	public static String locale;
	public static String previousLanguage;
	public static final boolean COMPRESS_IMAGES = Boolean.valueOf(appConfig.get("CompressImages"));
	public static final String IMAGE_MATCH_LEVEL = appConfig.get("Image_Match_Level");
	public static final String DB_CONFIG1 = appConfig.get("DbConfig1");
	public static final String DB_CONFIG2 = appConfig.get("DbConfig2");
	public static final String DB_CONFIG3 = appConfig.get("DbConfig3");
	public static final String DB_CONFIG4 = appConfig.get("DbConfig4");
	public static final String DB_CONFIG5 = appConfig.get("DbConfig5");
	private static Boolean emailValueFromConfig = Boolean.valueOf(appConfig.get("SendMail"));
	public static final boolean SEND_MAIL = (emailValueFromConfig == null?false:emailValueFromConfig);
	private static Boolean serviceResultFlag = Boolean.valueOf(appConfig.get("CaptureServicePayload"));
	public static final boolean CAPTURE_SERVICE_PAYLOAD = (serviceResultFlag == null?false:serviceResultFlag);
	public static final String TO_MAIL_LIST = appConfig.get("ToMailList");
	public static final String CC_MAIL_LIST = appConfig.get("CcMailList");
	public static final String SUBJECT = appConfig.get("Subject");
	public static final String EMAIL_BODY_PATH = appConfig.get("EmailBodyPath");
	public static final String CONTACT_MAIL = appConfig.get("ContactMailId");
	private static Boolean runLocalStoriesFromConfig = Boolean.valueOf(appConfig.get("RunLocalStories"));
	public static final boolean RUN_LOCAL_STORIES = (runLocalStoriesFromConfig == null?false:runLocalStoriesFromConfig);
	public static final String REPORT_FILES_PATH = CURRENT_DIR+fp+StringUtils.chop(EXTENT_REPORTS_PATH)+".zip";
	public static final String ZIP_FILE_PATH = CURRENT_DIR+fp+EXECUTION_ID+".zip";
	public static final String PROXY_SERVER_HOST = appConfig.get("ProxyHost");
	
	public static final String RUN_DETAILS_INSERT_SQL = "INSERT INTO `ib_reports`.`run_details` (`Run_id`, `Country`, `Environment`, `Exec_mode`, `Release_name`, `Sprint_no`, `scenarios_total`, `examples_total`, `tests_total`, `tests_passed`, `tests_failed`, `tests_skipped`, `tests_Pending`, `run_start`, `run_end`) VALUES ('?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?'); ";
	public static final String SCENARIO_DETAILS_INSERT_SQL = "INSERT INTO `reportsdb`.`t_scenario_details` (`Scenario_seq`, `run_id`, `scenario_id`, `example_id`, `Scenario_name`, `execution_device`, `status`, `scenarios_tags`) VALUES ('?', '?', '?', '?', '?', '?', '?', '?');";
	public static final String STEP_DETAILS_INSERT_SQL = "INSERT INTO `reportsdb`.`t_step_details` (`step_seq`, `scenario_seq`, `step_id`, `scenario_id`, `step_name`) VALUES ('?', '?', '?', '?', '?');";
	public static final String ACTIONS_INSERT_SQL = "INSERT INTO `reportsdb`.`t_actions` (`action_seq`, `step_seq`, `action_name`, `action_input`, `action_output`, `resource_1`, `resource_2`, `resource_3`) VALUES ('?', '?', '?', '?', '?', ?, ?, ?);";
	public static final String RUN_DETAILS_UPDATE_SQL = "UPDATE `reportsdb`.`run_details` SET `scenarios_total`='?', `examples_total`='?', `tests_total`='?', `tests_passed`='?', `tests_failed`='?', `tests_skipped`='?', `tests_Pending`='?' WHERE  `Run_id`='?';";
	public static final String SCENARIO_DETAILS_UPDATE_SQL = "UPDATE `reportsdb`.`t_scenario_details` SET `example_id`='?', `status`='?', `scenarios_tags`='?' WHERE  `run_id`='?' AND `jira_no`='?' AND `scenario_id`='?';";
	public static final String STEP_DETAILS_UPDATE_SQL="UPDATE `reportsdb`.`t_step_details` SET `step_name`='?', `status`='?' WHERE  `step_id`='?' AND `scenario_id`='?';";
	public static final String GET_SCENARIO_SEQ_ID_SELECT_SQL = "select scenario_seq from t_scenario_details where run_id = '?' and jira_no = '?' and scenario_id ='?';";
	public static final String GET_STEP_SEQ_ID_SELECT_SQL = "select step_seq from t_step_details where scenario_seq = '?' and scenario_id = '?';";
	
	public static final String ELEMENT_FINDER_IMAGE_SQL="select Image from {replace4} "
			+ "where ObjectId ='{replace1}' "
			+ "and {replace5} ='{replace2}' and Resolution ='{replace3}'{replace7}";
	public static final String ELEMENT_FINDER_IMAGES_SQL="select count(*) from {replace} "+ "where ObjectId =";
	public static final String projectId = appConfig.get("ProjectId");
	public static final String versionName = appConfig.get("VersionName");
	
	
	public static synchronized HashMap<String, String> getDeviceData() {
		HashMap<String, String> holderMap = new HashMap<String, String>(); 

		//To get the device NOT INUSE
		for( HashMap<String,String> device:deviceList ) {
			if(device.get("Status").equalsIgnoreCase("notinuse")) {
				driverData = device;
				holderMap = device;
				device.put("Status", "inuse");
				inUseDeviceCnt++;
				logger.info("Device: "+device.get("Device Name")+" will be used for current test execution");
				break;
			}
		}
		
		//To get the CURRENT INUSE Device count
		logger.info("Currently Available Devices count :" + (deviceList.size()-inUseDeviceCnt));
		
		if((deviceList.size()-inUseDeviceCnt)==0)
			logger.info("All devices are In Use");
		return holderMap;
	}	
	
	public static String getRunNum() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSSS");
		return sdf.format(new Date());
	}
	public static HashMap<String,String> read_Xl_prop(String filePath, String sqlQuery) {
		Fillo fillo = new Fillo();
		HashMap<String,String> appConfig= new HashMap<>();
		try {
			Connection conn = fillo.getConnection(filePath);
			Recordset rs = conn.executeQuery(sql_appConfig);
			while(rs.next())
				appConfig.put(rs.getField("config_Name"),rs.getField("config_Value"));
			rs.close();
			conn.close();

		} catch (FilloException e) {
			e.printStackTrace();
		}
		return appConfig;
	}	
	
	public static void Logger() {
		if(LOG4J_PATH.endsWith(".xml"))
			DOMConfigurator.configure(LOG4J_PATH);
		else if(LOG4J_PATH.endsWith(".properties"))
			PropertyConfigurator.configure(LOG4J_PATH);
	}
	public static void print_prop(HashMap<String,String> appConfig) {
		for(Entry<String, String> conf: appConfig.entrySet())
			logger.info("Property =>" + conf.getKey() + ":" +conf.getValue());
	}
	
	public static void gracefulEnd(Throwable t, Logger log) {
		log.error("Exception occured :- " + t);
		StackTraceElement[] s = t.getStackTrace();
		for (StackTraceElement e : s) {
			log.error("\tat " + e);

		}
	}
	
}
