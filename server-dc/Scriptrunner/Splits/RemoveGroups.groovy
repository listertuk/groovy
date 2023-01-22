// remove temp group used in migration
// see GetAllSplitGroupNames, PutRoleUsersAndGroupsIntoSplitHolders

import com.atlassian.jira.component.ComponentAccessor
import org.apache.log4j.Logger

import com.atlassian.crowd.embedded.api.Group
import com.atlassian.jira.bc.group.GroupService
import com.atlassian.jira.bc.JiraServiceContextImpl

//def logit = Logger.getLogger("com.domain1.eu.logging")

GroupService groupService = ComponentAccessor.getComponent(GroupService)
//def groupManager = ComponentAccessor.groupManager

def groups = [
"split_WSC Team",
"split_WPC SEOUL",
"split_WPC PL Managers",
"split_WPC PL",
"split_WPC CN",
"split_WPC BR",
"split_WMC PMO",
"split_Wisewire QA",
"split_UserRoles",
"split_tse-lpm"
]
 
//Group inactive = groupManager.getGroup("jira-inactive-users")
//Group license = groupManager.getGroup("jira-software-users")
def luser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def ctx = new JiraServiceContextImpl(luser)


//String[] userGroups
groups.each() {
    //Group groupIn = groupManager.getGroup(it)
    
    //String originalGroup = it.replaceFirst("split_","")
    //Group original = groupManager.getGroup(originalGroup)
    //logit.info("original " + originalGroup  + " " + original)

    if (groupService.validateDelete(ctx,it,"jira-administrators")) {
       
        groupService.delete(ctx,it,"jira-administrators")   
    }
    //if (groupService.validateDelete(ctx,originalGroup,"jira-administrators")) {
    //    groupService.delete(ctx,originalGroup,"jira-administrators")   
   // }
   

    
}
