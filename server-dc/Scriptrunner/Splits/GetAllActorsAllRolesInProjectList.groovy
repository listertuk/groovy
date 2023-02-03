import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.security.roles.ProjectRoleManager
import com.atlassian.jira.security.roles.ProjectRole
import com.atlassian.jira.security.roles.ProjectRoleActor
import com.atlassian.jira.security.roles.RoleActor
import com.atlassian.jira.security.roles.ProjectRoleActors
import com.atlassian.jira.project.Project
import com.atlassian.jira.project.ProjectManager
import org.apache.log4j.Logger
import com.atlassian.jira.bc.projectroles.ProjectRoleService  
import com.atlassian.jira.util.SimpleErrorCollection
import com.atlassian.jira.security.groups.GroupManager
import com.atlassian.crowd.embedded.api.Group
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.crowd.embedded.api.User

 
def logit = Logger.getRootLogger()
logit.setLevel(org.apache.log4j.Level.DEBUG)
String [] projectKeys = ['KAN1', 'JSM1', 'NR2']

def projectRoleService = ComponentAccessor.getComponent(ProjectRoleService)                        
Map projectUsers
def errorCollection = new SimpleErrorCollection()
ProjectRoleManager projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager)
 
ProjectManager pMgr = ComponentAccessor.getProjectManager()
def grpMgr = ComponentAccessor.getGroupManager()
logit.info('Project,User,Project Name, Project Role, Group/direct')
	projectKeys.each() { pKey ->
        Project p = pMgr.getProjectObjByKey(pKey)
        // map projectUsers to report users only once
        projectUsers = [:]
       Collection roles = projectRoleService.getProjectRoles(errorCollection)
        if (errorCollection.hasAnyErrors()) {
            logit.info(errorCollection.getErrors())
        }
        roles.each() {ProjectRole r ->
            //logit.info(r.getName())

           ProjectRoleActors actors =   projectRoleService.getProjectRoleActors(r, p, errorCollection)
           //log.info(actors)
           if (actors) {
            if (!actors.getRoleActors().empty)
            {
               String actorType = null
                String region = null
                actors.getRoleActors().collect().each() {RoleActor ra ->
                    //logit.info(ra.getType().compareTo("atlassian-group-role-actor"))
                    if (ra.getType().compareTo("atlassian-group-role-actor") == 0) {
                        actorType = "group"
                       
                        def users = grpMgr.getUsersInGroup(ra.getDescriptor())
                        users.each() {ApplicationUser it ->
                            if (uniqueUsers(projectUsers, it.name)) {
                           		logit.info(p.key + "," + it.name + "," +  p.getName() + "," + r.getName() + "," + ra.getDescriptor()  )
                            }
                         }
                    } else {
                        actorType = "user"
                            region = "external"
                            if (uniqueUsers(projectUsers, ra.getDescriptor())) {
 									logit.info(pKey + "," + ra.getDescriptor() + "," + p.getName() + "," +  r.getName() + "," + "direct" )
                                                   }
                    }
                 }
            }
            }
        }
    }

def boolean  uniqueUsers(Map map,String actor) {
   boolean retval = true
    if (map.containsKey(actor)) {
        retval= false
    } else {
        map.put(actor, actor)
        retval = true
    }
}
