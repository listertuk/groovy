package com.domain1.retail
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
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.issuetype.IssueType
import com.atlassian.jira.bc.issue.comment.CommentService
import java.sql.Timestamp
import com.atlassian.jira.issue.label.Label
import com.atlassian.jira.issue.status.Status
import com.opensymphony.workflow.loader.StepDescriptor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.workflow.JiraWorkflow
import com.atlassian.jira.security.JiraAuthenticationContext
import com.atlassian.jira.project.Project
import com.atlassian.jira.issue.IssueFactory
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.config.SubTaskManager
import com.atlassian.jira.config.ConstantsManager
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.issue.fields.config.FieldConfig
import com.atlassian.jira.issue.customfields.option.Option

import com.domain1.retail.ShoptectConstants

class ShoptectSubTaskHandler {

Logger logit = Logger.getLogger("com.domain1.eu.logging")

CustomFieldManager customFieldManager //= ComponentAccessor.getCustomFieldManager()
UserUtil userUtil //= ComponentAccessor.getUserUtil()
ConstantsManager constantsManager //= ComponentAccessor.constantsManager
IssueManager issueManager //= ComponentAccessor.issueManager
IssueFactory issueFactory //= ComponentAccessor.issueFactory
SubTaskManager subTaskManager //= ComponentAccessor.subTaskManager
JiraAuthenticationContext authenticationContext //= ComponentAccessor.jiraAuthenticationContext
ApplicationUser cwdUser //=  authenticationContext.getLoggedInUser()
JiraWorkflow workflow //= ComponentAccessor.getWorkflowManager().getWorkflow(issue) 
MutableIssue issue


    public ShoptectSubTaskHandler(MutableIssue issue) {
        this.issue = issue
        logit = Logger.getLogger("com.domain1.eu.logging")
		customFieldManager = ComponentAccessor.getCustomFieldManager()
		userUtil = ComponentAccessor.getUserUtil()
		constantsManager = ComponentAccessor.constantsManager
		issueManager = ComponentAccessor.issueManager
		issueFactory = ComponentAccessor.issueFactory
		subTaskManager = ComponentAccessor.subTaskManager
		authenticationContext = ComponentAccessor.jiraAuthenticationContext
        cwdUser =  authenticationContext.getLoggedInUser()
    	workflow = ComponentAccessor.getWorkflowManager().getWorkflow(issue) 
        logit.info("ShoptectSubTaskHandler")
    }
    
    public String toString() { return "ShoptectSubTaskHandler " + cwdUser}
    
    public void createSubTasks() {
        logit.info("ShoptectSubTaskHandler.createSubTasks")

		StepDescriptor destStepDescriptor = workflow.getLinkedStep(issue.getStatus())
		def currentAttributes = destStepDescriptor.getMetaAttributes()
		String  FIELD = currentAttributes[ShoptectConstants.FIELD] // get the flag field name
		String  LABEL = currentAttributes[ShoptectConstants.LABEL] // get the sub-task label value

		logit.debug("" + ShoptectConstants.FIELD + " : " + FIELD)
		logit.debug("" + ShoptectConstants.LABEL + " : " + LABEL)
		def subTaskList = currentAttributes[ShoptectConstants.SUBTASKS].toString().tokenize("|") // get the sub-task summary list
		logit.debug("" + ShoptectConstants.SUBTASKS + " : " + subTaskList)


		Collection<CustomField> done = customFieldManager.getCustomFieldObjectsByName(FIELD) // get current flag field value
		CustomField flagField = done.first()
		def doneVal = issue.getCustomFieldValue(flagField)
		logit.info('done: ' + doneVal)
        // check is the step is not flagged as already processed (no value or not set to 'Y')
		if(doneVal == null || !doneVal == 'Y') {
			//You need a target project
			Project targetProject = issue.getProjectObject()
			IssueType[] type = new IssueType[1]
			IssueType[] issueTypes = targetProject.getIssueTypes().toArray(type)
			IssueType issueType
			issueTypes.each() {
    			IssueType iss = (IssueType)it
    			if (iss.name == 'Sub-task') {
    	    		issueType = iss
    			}
			}
			logit.debug('sub-task issueType: ' + issueType)

    		subTaskList.each()
			{
        		//You need a summary
        		String issueSummary = it
        		logit.debug("sub : " + it)
        		//This creates an empty subtask
        		Issue subtask = issueFactory.issue
        		//Set the summary and the target project
        		def errorText = ""
        		subtask.setSummary(issueSummary)
        		subtask.setDescription(issueSummary)
        		subtask.setPriority(issue.getPriority())
        		subtask.setParentObject(issue)
    			subtask.setReporter(issue.reporter)
        		subtask.setProjectObject(targetProject)
        		//subtask.assignee = issue.getAssignee()
        		if (issue.dueDate) {
            		subtask.setDueDate(new Timestamp(issue.dueDate.getTime()))
        		}
        		subtask.setLabels([new Label(null,null, LABEL)] as Set)
        		// def issueType = constantsManager.getIssueType("Sub-task")
        		if (!issueType) {
            		logit.info( "Your subtask type does not exist, you need a subtask type to create a subtask!")
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
            
   			// when sub-tasks completed update issue flag field for statua
			FieldConfig fieldConfig = flagField.getRelevantConfig(issue)
			Option value = ComponentAccessor.optionsManager.getOptions(fieldConfig)?.find { it.toString() == 'Y' }	
			issue.setCustomFieldValue(flagField, value)
		}

	}
}





