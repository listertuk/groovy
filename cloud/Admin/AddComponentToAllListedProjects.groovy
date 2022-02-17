/*
 bulk add a list of components to a list of projects where all share same functionality
*/
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.bc.project.component.ProjectComponent
import com.atlassian.jira.bc.project.component.ProjectComponentManager

long projectId = 0

ProjectComponentManager pComMgr = ComponentAccessor.getProjectComponentManager()
ProjectComponent component
def projectManager = ComponentAccessor.getProjectManager()

String[] projects = ["PROJ1",
"PROJ2",
"PROJ3"
]
					 
String[] components = ["Issue & Inquiry Only",
"Registration",
"Modification",
"Update",
"Static Page",
"Promotion",
"Home Page",
"Footer"
]
					 

projects.each() {String projectName ->
    projectId = projectManager.getProjectByCurrentKey(projectName).getId()
    component.each() {String componentName ->
    component = pComMgr.create(componentName, null, null , 0, projectId)
    }
}

