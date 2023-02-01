package com.domain1.jira
/*
retail client brief sub-task script
depends on transition properties being set
"shoptect.flag.field" = single select field to indicate step sub-tasks have been processed
"shoptect.subtask.label" = label to identify sub-tasks belongint to this transition
"shopitect.sub-tasks" = pipe | separated list of sub-task summaries
*/
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.project.Project
import com.atlassian.jira.project.ProjectManager
import com.atlassian.jira.security.roles.ProjectRoleManager
import com.atlassian.jira.security.roles.ProjectRole
import com.atlassian.jira.security.roles.ProjectRoleActor
import com.atlassian.jira.security.roles.RoleActor
import com.atlassian.jira.security.roles.ProjectRoleActors
import com.atlassian.jira.bc.projectroles.ProjectRoleService  
import com.atlassian.jira.util.SimpleErrorCollection
import org.apache.log4j.Logger
import com.atlassian.jira.user.ApplicationUser

class RemoveProjectRoles {
    
    Project project
    ProjectRoleService projectRoleService
    Logger logit = Logger.getLogger("com.domain1.eu.logging")
    Collection<String> actorNames

     public RemoveProjectRoles(Project project) {
        this.project = project
        projectRoleService = ComponentAccessor.getComponent(ProjectRoleService)
         

    }
    
    public void removeRoles() {
        def errorCollection = new SimpleErrorCollection()

        Collection roles = projectRoleService.getProjectRoles(errorCollection)
        if (errorCollection.hasAnyErrors()) {
            logit.info(errorCollection.getErrors())
        } else {
            roles.each() { ProjectRole r ->
                   ProjectRoleActors actors=   projectRoleService.getProjectRoleActors(r, project, errorCollection)
                if (!actors.getRoleActors().empty)
                {
                    actors.getRoleActors().collect().each() { RoleActor ra ->
                    actorNames = new ArrayList<String>()
                    if (ra.getType().contentEquals("atlassian-user-role-actor")) {
                        Set<ApplicationUser> users = ra.getUsers()
                        users.each() {
                            actorNames.add(it.name)
                        }

                    } else {
                        actorNames.add(ra.getDescriptor())
                    }
                       projectRoleService.removeActorsFromProjectRole(actorNames, r, project, ra.getType().toString(), errorCollection)
                    if (errorCollection.hasAnyErrors()) {
                        logit.info(errorCollection.getErrors())
                        }
                       }
                }
            }
        } 
    }
    public void addGroupToRole(String group, String role) {
        def errorCollection = new SimpleErrorCollection()
        ProjectRole r = projectRoleService.getProjectRoleByName(role, errorCollection)
        actorNames = new ArrayList<String>()
        actorNames.add(group)
        projectRoleService.addActorsToProjectRole(actorNames, r, project, "atlassian-group-role-actor", errorCollection)
        if (errorCollection.hasAnyErrors()) {
            logit.info(errorCollection.getErrors())
        }
        
    }
}
