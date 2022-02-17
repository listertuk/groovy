/*
add a group to all listed roles in all listed projects
*/

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.util.UserManager
import com.atlassian.jira.security.roles.ProjectRoleManager
import com.atlassian.jira.security.roles.ProjectRole
import com.atlassian.jira.security.roles.ProjectRoleActor
import com.atlassian.jira.security.roles.ProjectRoleActors
import com.atlassian.jira.project.Project
import com.atlassian.jira.project.ProjectManager
import org.apache.log4j.Logger
import com.atlassian.jira.bc.projectroles.ProjectRoleService   
import com.atlassian.jira.util.SimpleErrorCollection

def logit = Logger.getLogger("com.domain1.logging")
/*
**************
set your role name and project here
****************
*/
String[] roleNames = ["Clients", "Project Managers", "Project Members", "Administrators"]
String[] projectNames  = ["PROJ1",
"PROJ2",
"PROJ3"]
String group = "Bodgers"


def projectRoleService = ComponentAccessor.getComponent(ProjectRoleService)                         
Project project
def errorCollection = new SimpleErrorCollection()
ProjectRoleManager projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager)
ProjectRole role
ProjectManager pMgr = ComponentAccessor.getProjectManager()
Collection<String> actorCollection = new ArrayList<>();
actorCollection.add(group)

projectNames.each() { projectName ->
	project = pMgr.getProjectByCurrentKey((String)projectName)
  	roleNames.each() { roleName ->             
	role = projectRoleManager.getProjectRole((String)roleName)
	ProjectRoleActors actors = projectRoleManager.getProjectRoleActors(role, project)
	projectRoleService.addActorsToProjectRole(actorCollection,
    	role,
        project,
        ProjectRoleActor.GROUP_ROLE_ACTOR_TYPE ,
        errorCollection)
	}
    logit.info("add " + actorCollection + " to " + role + "/" + project)
}
 
