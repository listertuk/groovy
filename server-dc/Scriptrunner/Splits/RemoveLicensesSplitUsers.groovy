// looks like a variation of EURemoveLicensesSplitUsers
import com.atlassian.jira.component.ComponentAccessor
import org.apache.log4j.Logger
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.crowd.embedded.api.Group
import com.atlassian.jira.security.roles.ProjectRoleManager
import com.atlassian.jira.security.roles.ProjectRole
import com.atlassian.jira.security.roles.ProjectRoleActor
import com.atlassian.jira.security.roles.RoleActor
import com.atlassian.jira.security.roles.ProjectRoleActors
import com.atlassian.jira.bc.projectroles.ProjectRoleService  
import com.atlassian.jira.util.SimpleErrorCollection
import com.atlassian.jira.user.util.UserUtil
UserUtil userUtil = ComponentAccessor.userUtil
def logit = Logger.getLogger("com.domain1.eu.logging")

//ProjectRoleService projectRoleService = ComponentAccessor.getComponent(ProjectRoleService)                        
//SimpleErrorCollection errorCollection
//ProjectRoleManager projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager)

def groupManager = ComponentAccessor.groupManager

def groups = [
"split_WSC Team",
"split_WPC SEOUL",
"split_WPC PL Managers",
"split_WPC PL",
"split_WPC CN",
"split_WPC BR",
"split_WMC PMO"
]
 
Group inactive = groupManager.getGroup("jira-inactive-users")
Group license = groupManager.getGroup("jira-software-users")

//String[] userGroups
groups.each() {
    Group groupIn = groupManager.getGroup(it)
    String originalGroup = groupIn.getName().replaceFirst("split_","")
    Group original = groupManager.getGroup(originalGroup)
    logit.info("original " + originalGroup  + " " + original)

    if (groupIn) {
    //logit.info("source : " + groupIn.name)
        Collection<ApplicationUser> inUsers = groupManager.getUsersInGroup(groupIn)
        inUsers.each(){ApplicationUser user ->
            errorCollection = new SimpleErrorCollection()
            //projectRoleService.
            Collection<String> userGrps = groupManager.getGroupNamesForUser(user)
            Collection<String> userGroups = userGrps.findAll()//new ArrayList(userGrps)
            userGrps.each() {
                if (it != groupIn.name && it.contains("split_")) {
                    String originalG = it.replaceFirst("split_","")
                    userGroups.remove(originalG)
                }
            }
            
            boolean deactivate = true
            for (String grpName : userGroups) {
           // userGroups.each() {String grpName ->
                if (!grpName.contains(originalGroup) && !grpName.contains("COMP_") && !grpName.contains("ORG_") && !grpName.contains("FnSec_")  && !grpName.contains("split") && !grpName.contains("jira-software-users") && !grpName.contains("jira-inactive-users")) {
                    //logit.info("ignore user " + user +" " + grpName + " "+ userGroups)
                    deactivate = false
                } else {
                    //logit.info("deactivate "  + user +" " + grpName + " "+ userGroups)
                    deactivate = true
                }
                if (!deactivate) {
                   // logit.info("break")
                    break 
                }
                
            }
            if (deactivate) {
                groupManager.addUserToGroup(user, inactive)
                userUtil.removeUserFromGroup(license, user)
                logit.info("**deactivate," + deactivate + "," + user + "," + userGroups)
                if (original) {
                    logit.info("remove " + user + " from original " + original.name)
                    //userUtil.removeUserFromGroup(original, user)
                }
            }
 
        }
    }
}
