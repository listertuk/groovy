/*
add listed components to listed projects
Cloud equivalent:
https://github.com/listertuk/groovy/blob/b8116dff8c72e1e2001b3f7249b235ebbb5cb3b0/cloud/Scriptrunner/Admin/AddComponentToAllListedProjects.groovy
 

*/
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.bc.project.component.ProjectComponent
import com.atlassian.jira.bc.project.component.ProjectComponentManager
import org.apache.log4j.Logger

def logit = Logger.getLogger("com.domain1.logging")

long projectId = 0

ProjectComponentManager pComMgr = ComponentAccessor.getProjectComponentManager()
ProjectComponent component
def projectManager = ComponentAccessor.getProjectManager()

String[] projects = ["PROJ1",
"PROJ2"
]
                     
String[] components = ["Home Page",
"Footer",
"Static Page"
]
                     

projects.each() {String projectName ->
     projectId = projectManager.getProjectByCurrentKey(projectName).getId()
    components.each() {String componentName ->
    component = pComMgr.create(componentName, null, null , 0, projectId)
         logit.info(projectId + "/" + componentName)
    }
    
}

