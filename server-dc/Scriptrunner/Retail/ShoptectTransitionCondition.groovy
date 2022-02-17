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
  
def logit = Logger.getLogger("com.domain1.eu.logging")
String  LABEL = getLabelProperty(logit)
//inbuilt return variable
passesCondition = true
def subTasks = issue.getSubTaskObjects()
//String  LABEL = 'RetailClientBrief'
logit.debug("LABEL: " + LABEL)
subTasks.each {
    def containsLabel = LABEL in it.getLabels()*.label
    log.debug("LABEL: " + LABEL + " found " + containsLabel)
    if (containsLabel && it.resolution == null)  
    {
        passesCondition  = false
        log.info(it.key)
    }
}
return passesCondition


/* ============Functions=========== */
def getLabelProperty(Logger logit) {
    def retVal = ""
    def workflow = ComponentAccessor.getWorkflowManager().getWorkflow(issue) 
	StepDescriptor sourceStepDescriptor = workflow.getLinkedStep(issue.getStatus())
	def currentAttributes = sourceStepDescriptor.getMetaAttributes()
	logit.debug("shoptect.flag.field : " + currentAttributes["shoptect.flag.field"])
	logit.debug("shoptect.subtask.label : " + currentAttributes["shoptect.subtask.label"])
	retVal = currentAttributes["shoptect.subtask.label"]    
	return retVal
}
