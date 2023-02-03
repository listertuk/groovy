import com.atlassian.jira.component.ComponentAccessor
import org.apache.log4j.Category
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenScheme
import com.atlassian.jira.project.ProjectManager
import com.atlassian.jira.issue.issuetype.IssueType
import com.atlassian.jira.project.Project
import com.atlassian.jira.issue.fields.screen.FieldScreenScheme
import com.atlassian.jira.issue.fields.screen.FieldScreenSchemeItem
import com.atlassian.jira.issue.fields.screen.FieldScreen
import com.atlassian.jira.issue.fields.screen.FieldScreenTab
import com.atlassian.jira.issue.fields.screen.FieldScreenLayoutItem
import com.atlassian.jira.issue.fields.FieldManager
import com.atlassian.jira.issue.fields.CustomField

def issueTypeScreenSchemeManager=ComponentAccessor.getIssueTypeScreenSchemeManager()

def Category log = Category.getInstance("com.onresolve.jira.groovy.PostFunction")
log.setLevel(org.apache.log4j.Level.DEBUG)


String [] projectKeys = ['KAN1', 'JSM1', 'NR3']
ProjectManager pMgr = ComponentAccessor.projectManager 
def issueTypeScreenScheme = ComponentAccessor.getIssueTypeScreenSchemeManager()
def wrkflwScheme = ComponentAccessor.getWorkflowSchemeManager()
def fieldScheme = ComponentAccessor.getFieldLayoutManager()
FieldManager fieldMgr = ComponentAccessor.fieldManager

log.info("Project Name,Field Id, Field Name ")
projectKeys.each() { pKey ->
    Project project = pMgr.getProjectObjByKey(pKey) //.getProjectObjectsFromProjectCategory(cat.getId())
    if (project) {
      
      Collection<IssueType> issueTypes = project.getIssueTypes()
      issueTypes.each() {IssueType iType ->
          IssueTypeScreenScheme itss = issueTypeScreenScheme.getIssueTypeScreenScheme(project)
          FieldScreenScheme fss = itss.getEffectiveFieldScreenScheme(iType)
          Collection<FieldScreenSchemeItem> its = fss.getFieldScreenSchemeItems()
          its.each() {FieldScreenSchemeItem item ->

          FieldScreen fs = item.getFieldScreen()
          List<FieldScreenTab> tabs = fs.getTabs()
          tabs.each() {FieldScreenTab fst ->

            List<FieldScreenLayoutItem> fsli = fst.getFieldScreenLayoutItems()
            fsli.each() {it ->
                  //log.info(it.getFieldId())
                  if (it.getFieldId().contains('customfield_')) {
                        CustomField cf = fieldMgr.getCustomField(it.getFieldId())
                        log.info(project.getKey() + "," + cf.fieldName + "," + cf.customFieldType.descriptor )

                  } else {
                         def field = fieldMgr.getField(it.getFieldId())
                         log.info(project.getKey() + "," + field.id + "," + field.name)
                         
                  }
            }
          }

 
          }
      }
} else {
      log.info(pKey +  " not found")
}
      
}