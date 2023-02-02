import com.atlassian.jira.component.ComponentAccessor
import org.apache.log4j.Category
import com.atlassian.jira.project.Project

def issueTypeScreenSchemeManager=ComponentAccessor.getIssueTypeScreenSchemeManager()

//def loggedInUser = ComponentAccessor.jiraAuthenticationContext.loggedInUser
def Category log = Category.getInstance("com.onresolve.jira.groovy.PostFunction")
log.setLevel(org.apache.log4j.Level.DEBUG)

String [] categories = ["MIGRATE"] 

//gives info about projects
def pMgr = ComponentAccessor.getProjectManager() //.getProjectObjects()
//Project prj = ComponentAccessor.getProjectManager().getProjectObjByKey("HCI")
//def issueTypeSchemeManager = ComponentAccessor.getIssueTypeSchemeManager()
def issueTypeScreenScheme = ComponentAccessor.getIssueTypeScreenSchemeManager()
def wrkflwScheme = ComponentAccessor.getWorkflowSchemeManager()
def fieldScheme = ComponentAccessor.getFieldLayoutManager()
log.info("Project Name,Project Id,Issue type Screen scheme,Issue types,Workflow Scheme,Screen SchemeField Config Scheme ")
categories.each() {category ->
      def cat = pMgr.getProjectCategoryObjectByName(category)
    // get all projects for category
    def allProjects = pMgr.getProjectObjectsFromProjectCategory(cat.getId())
   
      allProjects.each{it->

      Project p =ComponentAccessor.getProjectManager().getProjectObj(it.getId())

      log.info(""+it.getName() +","+ it.getId() +","+ issueTypeScreenSchemeManager.getIssueTypeScreenScheme(p).getName() +","+ it.getIssueTypes()*.name +","+ 
      wrkflwScheme.getWorkflowSchemeObj(p).getName() +","+ issueTypeScreenScheme.getIssueTypeScreenScheme(p).getName() +","+ fieldScheme.getFieldConfigurationScheme(p)?.getName())
//log.info("Project Name is: " +it.getName())
//log.info("Project Id of Long type is: " +it.getId()) 
//log.info ("Issue type Screen scheme is :"+issueTypeScreenSchemeManager.getIssueTypeScreenScheme(p).getName())
//log.info("Issue types associated with project is: " +it.getIssueTypes()*.name)
//log.info("Workflow Scheme for Project: " +it.getName().toString() +" is: "+wrkflwScheme.getWorkflowSchemeObj(p).getName())
//log.info("Screen Scheme is :" +issueTypeScreenScheme.getIssueTypeScreenScheme(p).getName()) 
//log.info("Field Config Scheme is :" +fieldScheme.getFieldConfigurationScheme(p)?.getName())
//log.info('---------------------------------------------------------------------') 
      }
}