/*
for each project in a category add a group as role actor to each of listed roles
Repeated for two categories - TODO replace with array loop
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

String[] roleNames = [ "Project Managers"] 

Collection<String> actorCollection = new ArrayList<>();
actorCollection.add("My PMs")

def projectRoleService = ComponentAccessor.getComponent(ProjectRoleService)                         
def errorCollection = new SimpleErrorCollection()
ProjectRoleManager projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager)
ProjectRole role
ProjectManager pMgr = ComponentAccessor.getProjectManager()

def cat = pMgr.getProjectCategoryObjectByName("DEVS")
def catProj = pMgr.getProjectObjectsFromProjectCategory(cat.getId())

catProj.each() { project ->
	//project = pMgr.getProjectByCurrentKey((String)projectName)
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

def cat2 = pMgr.getProjectCategoryObjectByName("PMS")
def cat2Proj = pMgr.getProjectObjectsFromProjectCategory(cat2.getId())

cat2Proj.each() { project ->
	//project = pMgr.getProjectByCurrentKey((String)projectName)
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
 
 
