package com.dbs.setup;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner;
import com.anarsoft.vmlens.concurrent.junit.ThreadCount;
import com.dbs.commons.ConnectDB;
import com.dbs.commons.POMCreater;
import com.dbs.commons.RunDetails;
import com.dbs.config.Config;
import com.dbs.controller.TestConfiguration;
import com.dbs.util.EmailProcessor;
import com.dbs.util.ZipManager;
import com.jbehaveforjira.javaclient.JiraStoryPathsFinder;

@RunWith(ConcurrentTestRunner.class)
public class DigiBddTest{
	public static final Logger logger = Logger.getLogger(DigiBddTest.class);
	public static final String fp = File.separator;
	public static final int THREAD_COUNT = 1;
	//For normal Execution
	public static String metaFilter = "+Test";
	public static String key ="DNID-10752";//"DNID-7842";//"DNID-4351,DNID-7";//
	public static String[] packageNames = {"com.dbs.id.ib.steps","com.dbs.id.ib.steps"};
	public static String customJiraLabel = ""; //"";UXRegression;
	public static boolean jiraReportPublish = false;
	public static boolean saveScreenshotFile = false;//not used - refer from config
	public static String cycleName="Ovo Biller";
	//public static String[] folderName = {};       // If no folder, pass this way
	public static boolean  zypher = false; // need to update or not (true will update)
	public static String  iDorSummary = "ID"; // use test Id or summary to identify the zypher id test case
	public static String[] folderName = {"Android", "iOS"};  //if folder are there, pass this way and should match the config device name
	public static boolean  dryRun = false; 
	
	/*For JiraTabName, device description from config is used by default, however custom name can be specified in below variable
	 *If any value OTHER THAN "" or null is specified then it will be used.Use this ONLY when you're running with single thread, 
	 *otherwise all thread report will be overwritten in single jira tab */
	public static String jiraTabName = "Automation";	
	
	@ThreadCount(THREAD_COUNT) @Test
	public void run() throws Throwable{

		String jira_label=System.getProperty("jira.filter.label");
			logger.info("JIRA LABEL from system props"+jira_label);
		if(jira_label == null)
			jira_label = customJiraLabel;
		String metaTag = System.getProperty("jbehave.metatag");
			logger.info("JIRA METATAG from system props"+metaTag);
		if(metaTag == null)
			metaTag = metaFilter;
		String jiraKey = System.getProperty("jira.filter.id");
			logger.info("JIRA KEY from system props"+jiraKey);
		if(jiraKey == null)
			jiraKey = key;
		String pckgName = System.getProperty("step.library.package");
		if(pckgName == null)
			packageNames[0] = "com.dbs.id.mb.steps";
		else
			packageNames[0]=pckgName;
		String cycleName=System.getProperty("cycleName");
		if(cycleName==null)
			cycleName=DigiBddTest.cycleName;
				
		
		if(THREAD_COUNT == Config.deviceList.size()) {
			/* Use this constructor to download story using JIRA key instead of label */
			TestConfiguration testConfig;
			if(customJiraLabel!=null && !customJiraLabel.equalsIgnoreCase(""))
				testConfig = new TestConfiguration(metaTag,packageNames,JiraStoryPathsFinder.IssueFilterParam.LABELS,jira_label,jiraTabName,dryRun,cycleName,folderName,iDorSummary,zypher);
			else
				testConfig = new TestConfiguration(metaTag,packageNames,JiraStoryPathsFinder.IssueFilterParam.KEY,jiraKey,jiraTabName,dryRun,cycleName,folderName,iDorSummary,zypher);
					
			//For Template Creation
			//TestConfiguration testConfig = new TestConfiguration(metaTag,packageNames,JiraStoryPathsFinder.IssueFilterParam.LABELS,jiraKey,jiraTabName,true);
			
			// use below initialization for label based execution
			//TestConfiguration testConfig = new TestConfiguration(metaFilter,packageNames,jira_label,jiraTabName);
			
			/* Use this to perform dry run - to check if steps have wrong templates */
			//testConfig.setDryRun(true);
			
			/* Use this if you do not want to publish the report to JIRA - helpful during script development */
			String jiraPublish = System.getProperty("jira.report.publish");
			if(jiraPublish != null)
				jiraReportPublish = Boolean.valueOf(jiraPublish);
			testConfig.setJiraReportPublish(jiraReportPublish);
			
			/* Use this if screen shots need to be saved separately instead of embedding in report - necessary during regression run */
			String saveToFile = System.getProperty("save.screenshot.tofile");
			if(saveToFile != null)
				saveScreenshotFile = Boolean.valueOf(saveToFile);
			testConfig.saveScreenshotToFile(saveScreenshotFile);
			
			/*use this to set different tags for different devices, e.g., below*/
			testConfig.setTagForParallel(metaTag);
			
			/*use this to set different labels for different devices, e.g., below*/
			testConfig.setLabelForParallel(jira_label);
			
			/*use this to set different stories for different devices, e.g., below*/
			testConfig.setStoryForParallel(jiraKey);

			Thread executor = new Thread(testConfig);
			executor.start();
			try {  
				executor.join();
			} catch (InterruptedException ex) {  
				Thread.currentThread().interrupt();  
			}			
		}
		else {
			System.out.println("EXECUTION WILL NOT BE PERFORMED, NO.OF DEVICES IN THE CONFIG.XLSX IS NOT MATCHING WITH THREAD_COUNT");
		}
	}	
	
	@Before
	public void before() throws Exception {
		//Files.copy(new File(System.getProperty("user.home")+fp+ "config" + fp + "Config.xlsx"),
				//new File(System.getProperty("user.dir")+fp+"src"+fp+"test"+fp+"resources" +fp+ "config" + fp + "Config.xlsx"));
		Config.Logger();
		logger.info("@Before method of JUNIT");
		logger.info("Generating RunID....");
		RunDetails.runStartTime = RunDetails.getCurrentTime();
		Date today = Calendar.getInstance().getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		RunDetails.runID = formatter.format(today);
		logger.info("RunID ::" + RunDetails.runID);	
		boolean createPages=Boolean.parseBoolean(System.getProperty("run.createpages"));
		if(createPages) {
			POMCreater pom = new POMCreater();
			pom.createPOMforMB();
			pom.createPOMforIB();
			pom.createPOMforService();
			pom.createPOMforIB();
		}
		
		try {
			ConnectDB.connectToDB();
			if(!Config.LOCAL_RUN)
				RunDetails.insertRunDetailsIntoDB(RunDetails.runID, Config.COUNTRY, Config.ENVIRONMENT, Config.EXECUTION_MODE, Config.RELEASE, Config.SPRINT_NO, RunDetails.runStartTime);
		}catch(Exception e) {
			logger.info("Error in ConnectDB.connectToDB");
		}
		logger.info("End of @Before method of JUNIT");
	}	

	@After 
	public void after() throws Exception {
		logger.info("@After method of JUNIT");
		ZipManager zipper = new ZipManager();
		if(Config.SEND_MAIL) {			
			zipper.createZip(Config.REPORT_FILES_PATH);
			new EmailProcessor().sendMail();	
			Thread.sleep(10000);
			zipper.deleteZip(Config.ZIP_FILE_PATH);
		}		
		ConnectDB.close();
		RunDetails.runEndTime = RunDetails.getCurrentTime();
		//RunDetails.insertRunDetailsIntoDB(RunDetails.runID, Config.COUNTRY, Config.ENVIRONMENT, Config.EXECUTION_MODE, Config.RELEASE, Config.SPRINT_NO, RunDetails.totalScenarios, RunDetails.totalExamples, RunDetails.getTotalExamplesPassed(), RunDetails.getTotalExamplesFailed(), RunDetails.getTotalExamplesSkipped(), RunDetails.getTotalExamplesPending(), RunDetails.runStartTime, RunDetails.runEndTime);
		logger.info("End of @After method of JUNIT");
		System.out.println("Total Scenarios: "+RunDetails.totalScenarios+" Passed: "+RunDetails.totalPassedScenarios+" Failed: "+RunDetails.totalFailedScenarios+" Skipped: "+RunDetails.totalSkippedScenarios);		//RunDetails.insertRunDetailsIntoDB(RunDetails.runID, Config.COUNTRY, Config.ENVIRONMENT, Config.EXECUTION_MODE, Config.RELEASE, Config.SPRINT_NO, RunDetails.totalScenarios, RunDetails.totalExamples, RunDetails.getTotalExamplesPassed(), RunDetails.getTotalExamplesFailed(), RunDetails.getTotalExamplesSkipped(), RunDetails.getTotalExamplesPending(), RunDetails.runStartTime, RunDetails.runEndTime);		
		logger.info("Total Scenarios: "+RunDetails.totalScenarios+" Passed: "+RunDetails.totalPassedScenarios+" Failed: "+RunDetails.totalFailedScenarios+" Skipped: "+RunDetails.totalSkippedScenarios);
		logger.info("End of @After method of JUNIT");		
		
		if(RunDetails.totalFailedScenarios > 0) {		
			System.out.println("------------------------------------------------------------");
			System.out.println("        FAILED SCENARIOS");
			logger.error("        FAILED SCENARIOS");
			System.out.println("------------------------------------------------------------");
			System.out.println("ScenarioId  |    Device    |  StepName");
			System.out.println("------------------------------------------------------------");
			logger.error("ScenarioId  |  StepName");
			for(String scen: RunDetails.failedScenarios) {
				String holder = " ";
				int totalSize = 12;
				String[] splitString = scen.split("\\|");
				int scenSize = splitString[0].length();
				String inserter = IntStream.range(0, totalSize-scenSize).mapToObj(i -> holder).collect(Collectors.joining(""));
				String formattedString = splitString[0]+inserter+"| "+splitString[1]+"| "+splitString[2];
				logger.error(formattedString);
				System.out.println(formattedString);
			}
			System.out.println("------------------------------------------------------------");
			assertFalse(true);
		}		
		FileUtils.deleteDirectory(new File("target/zypherreport/"));
	}

}
