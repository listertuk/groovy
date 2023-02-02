import com.atlassian.jira.component.ComponentAccessor
import org.apache.log4j.Category

import com.atlassian.jira.project.ProjectManager

import com.atlassian.jira.project.Project

def issueTypeScreenSchemeManager=ComponentAccessor.getIssueTypeScreenSchemeManager()

def Category log = Category.getInstance("com.onresolve.jira.groovy.PostFunction")
log.setLevel(org.apache.log4j.Level.DEBUG)


String [] projectKeys = ['KAN1', 'NR2', 'NR3']
ProjectManager pMgr = ComponentAccessor.projectManager 
def issueTypeScreenScheme = ComponentAccessor.getIssueTypeScreenSchemeManager()
def wrkflwScheme = ComponentAccessor.getWorkflowSchemeManager()
def fieldScheme = ComponentAccessor.getFieldLayoutManager()
log.info("Project Name,Project Id,Issue type Screen scheme,Issue types,Workflow Scheme,Screen SchemeField Config Scheme ")
projectKeys.each() {pKey ->
    Project project = pMgr.getProjectObjByKey(pKey) //.getProjectObjectsFromProjectCategory(cat.getId())
    if (project) {
      
    log.info(""+project.getName() +","+ project.getId() +","+ issueTypeScreenSchemeManager.getIssueTypeScreenScheme(project).getName() +","+ project.getIssueTypes()*.name +","+ 
        wrkflwScheme.getWorkflowSchemeObj(project).getName() +","+ issueTypeScreenScheme.getIssueTypeScreenScheme(project).getName() +","+ fieldScheme.getFieldConfigurationScheme(project)?.getName())
//log.info("Project Name is: " +project.getName())
//log.info("Project Id of Long type is: " +project.getId()) 
//log.info ("Issue type Screen scheme is :"+issueTypeScreenSchemeManager.getIssueTypeScreenScheme(project).getName())
//log.info("Issue types associated with project is: " +project.getIssueTypes()*.name)
//log.info("Workflow Scheme for Project: " +project.getName().toString() +" is: "+wrkflwScheme.getWorkflowSchemeObj(project).getName())
//log.info("Screen Scheme is :" +issueTypeScreenScheme.getIssueTypeScreenScheme(project).getName()) 
//log.info("Field Config Scheme is :" +fieldScheme.getFieldConfigurationScheme(project)?.getName())
//log.info('---------------------------------------------------------------------') 
} else {
      log.info(pKey +  " not found")
}
      
}