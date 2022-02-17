package com.domain1.retail
/*
retail client brief sub-task script
check sub-tasks complete using status properties
depends on status properties being set
"shoptect.flag.field" = single select field to indicate step sub-tasks have been processed
"shoptect.subtask.label" = label to identify sub-tasks belongint to this transition
"shopitect.sub-tasks" = pipe | separated list of sub-task summaries
*/
import org.apache.log4j.Logger
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.status.Status
import com.opensymphony.workflow.loader.StepDescriptor
import com.atlassian.jira.workflow.JiraWorkflow
import com.atlassian.jira.issue.Issue


class ShoptectSubTaskChecker {

Logger logit = Logger.getLogger("com.domain1.eu.logging")
Issue issue
    
    public ShoptectSubTaskChecker(Issue issue) {
        this.issue = issue
    }
    
    public boolean checkSubTaskCompletion() {
        boolean passesCondition = true
		def subTasks = issue.getSubTaskObjects()
		//String  LABEL = 'RetailClientBrief'
		String  LABEL = getLabelProperty()
		logit.debug("LABEL: " + LABEL)
        
		subTasks.each {
    		def containsLabel = LABEL in it.getLabels()*.label
    		logit.debug("LABEL: " + LABEL + " found " + containsLabel)
    		if (containsLabel && it.resolution == null)  
    		{
        		passesCondition  = false
        		logit.info(it.key)
    		}
		}
		return passesCondition
	}

	String getLabelProperty() {
    	String retVal = ""
   		JiraWorkflow workflow = ComponentAccessor.getWorkflowManager().getWorkflow(issue) 
		StepDescriptor sourceStepDescriptor = workflow.getLinkedStep(issue.getStatus())
		def currentAttributes = sourceStepDescriptor.getMetaAttributes()
		logit.debug(ShoptectConstants.LABEL + " " + currentAttributes[ShoptectConstants.LABEL])
		retVal = currentAttributes[ShoptectConstants.LABEL]    
		return retVal
	}
}
