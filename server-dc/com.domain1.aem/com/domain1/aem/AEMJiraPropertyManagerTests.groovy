import com.domain1.aem.AEMJiraPropertyManager
import org.apache.log4j.Logger

Logger logit = Logger.getLogger("com.domain1.eu.logging")
//AEMJiraPropertyManager propMgr = new AEMJiraPropertyManager()

logit.info(AEMJiraPropertyManager)

logit.info("aemSysAcc : " + AEMJiraPropertyManager.getAemSysAcc())
logit.info("aemSysPwd : " + AEMJiraPropertyManager.getAemSysPwd())
logit.info("jiraSysAcc : " + AEMJiraPropertyManager.getJiraSysAcc())
logit.info("jiraSysPwd : " + AEMJiraPropertyManager.getJiraSysPwd())

logit.info("US url : " + AEMJiraPropertyManager.getSiteURL("US"))

logit.info("US AEM url : " + AEMJiraPropertyManager.getAemURL("US"))

logit.info("UK url : " + AEMJiraPropertyManager.getSiteURL("UK"))

logit.info("UK AEM url : " + AEMJiraPropertyManager.getAemURL("UK"))

logit.info("UA url : " + AEMJiraPropertyManager.getSiteURL("UA"))

logit.info("UA AEM url : " + AEMJiraPropertyManager.getAemURL("UA"))
