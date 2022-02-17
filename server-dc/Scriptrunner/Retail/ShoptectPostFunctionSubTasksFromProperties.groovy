/*
retail client brief sub-task script
depends on transition properties being set
"shoptect.flag.field" = single select field to indicate step sub-tasks have been processed
"shoptect.subtask.label" = label to identify sub-tasks belongint to this transition
"shopitect.sub-tasks" = pipe | separated list of sub-task summaries
*/
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.fields.CustomField
import org.apache.log4j.Logger
import java.util.StringTokenizer
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.issuetype.IssueType
import com.atlassian.jira.util.json.JSONObject
import com.atlassian.jira.bc.issue.comment.CommentService
import java.sql.Timestamp
import com.atlassian.jira.issue.label.Label
import com.atlassian.jira.issue.status.Status
import com.opensymphony.workflow.loader.StepDescriptor

def log = Logger.getLogger("com.domain1.eu.logging")
def customFieldManager = ComponentAccessor.getCustomFieldManager()
def userUtil = ComponentAccessor.getUserUtil()
  
def constantsManager = ComponentAccessor.constantsManager
def issueManager = ComponentAccessor.issueManager
//def projectManager = ComponentAccessor.projectManager
def issueFactory = ComponentAccessor.issueFactory
def subTaskManager = ComponentAccessor.subTaskManager
//LabelManager labelManager = ComponentAccessor.getComponent(LabelManager)
 
//Get the logged in user, this is the user that will be creating the new subtask.
def authenticationContext = ComponentAccessor.jiraAuthenticationContext
def cwdUser =  authenticationContext.getLoggedInUser()
Status status1 = ComponentAccessor.getConstantsManager().getStatus(issue.getStatus().name)
StepDescriptor stepDescriptor = workflow.getLinkedStep(issue.getStatus())

def currentAttributes = stepDescriptor.getMetaAttributes()
logit.info("stepDescriptor :" + stepDescriptor.getName())
logit.info(" currentAttributes: " + currentAttributes)
logit.info("property2" + currentAttributes["shoptect.flag.field"])
logit.info("sub-tasks" + currentAttributes["shoptect.subtask.label"])
def subTaskList = currentAttributes["shopitect.sub-tasks"].toString().tokenize("|")
logit.info(subTaskList)
//tokens.each() {
//    logit.info(it)
//}


String  FIELD = currentAttributes["shoptect.flag.field"]
String  LABEL = currentAttributes["shoptect.subtask.label"]

Collection<CustomField> done = customFieldManager.getCustomFieldObjectsByName(FIELD)
CustomField field = done.first()
def doneVal = issue.getCustomFieldValue(field)
log.info('done: ' + doneVal)
if(doneVal == null || !doneVal == 'Y')

{
//You need a target project
def targetProject = issue.getProjectObject()
IssueType[] type = new IssueType[1]
IssueType[] issueTypes = targetProject.getIssueTypes().toArray(type)
def issueType
issueTypes.each() {
    IssueType iss = (IssueType)it
    if (iss.name == 'Sub-task') {
        issueType = iss
    }
}
log.info('issueType: ' + issueType)

    subTaskList.each()
	{
      			//You need a summary
                String issueSummary = it
                
                //This creates an empty subtask
                def subtask = issueFactory.issue
                //Set the summary and the target project
                def errorText = ""
                subtask.setSummary(issueSummary)
                subtask.setDescription(issueSummary)
                subtask.setPriority(issue.getPriority())
                subtask.setParentObject(issue)
    			subtask.setReporter(issue.reporter)
                subtask.setProjectObject(targetProject)
                subtask.assignee = issue.getAssignee()
                if (issue.dueDate) {
                    subtask.setDueDate(new Timestamp(issue.dueDate.getTime()))
                }
             	subtask.setLabels([new Label(null,null, LABEL)] as Set)
                // def issueType = constantsManager.getIssueType("Sub-task")
                if (!issueType)
                {
                    log.info( "Your subtask type does not exist, you need a subtask type to create a subtask!")
                    return "Your subtask type does not exist, you need a subtask type to create a subtask!"
                }
                subtask.setIssueTypeId(issueType.getId())
 
                //You need to parse all of this as a subtask params
                Map<String, Object> newIssueParams = ["issue": subtask] as Map<String, Object>
                issueManager.createIssueObject(cwdUser, newIssueParams)
 
                //After the issue is created, you need to create the link.
                subTaskManager.createSubTaskIssueLink(issue, subtask, cwdUser)
        		//labelManager.addLabel(cwdUser, subtask.getId(), FIELD, false)

		}
    
	def fieldConfig = field.getRelevantConfig(issue)
	def value = ComponentAccessor.optionsManager.getOptions(fieldConfig)?.find { it.toString() == 'Y' }	
	issue.setCustomFieldValue(field, value)
	}




