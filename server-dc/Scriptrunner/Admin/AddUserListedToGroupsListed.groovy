/*
add listed users to each of listed groups
*/
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.util.UserManager
import com.atlassian.jira.security.groups.GroupManager
import com.atlassian.crowd.embedded.api.Group
import com.atlassian.jira.user.ApplicationUser
import org.apache.log4j.Logger
 
def log = Logger.getLogger("com.domain1.logging")

// **********************************************
String[] userList = [
"m.bartos@domain1.com",
"m.binkie@domain1.com",
"o.bow@domain1.com",
"m.brat@domain1.com",
"m.cel@domain1.com"

]
// **********************************************

String[] groups = ["My PMs"]
ApplicationUser user
def userUtil = ComponentAccessor.getUserUtil() 
UserManager userManager = ComponentAccessor.getUserManager()
GroupManager groupManager = ComponentAccessor.getGroupManager()


// for each user to process
userList.each() {
    user = userManager.getUserByName((String)it)
    
    log.info("" + user + "/" + (user.active?"active":"inactive"))
    // String[] groups = userUtil.getGroupNamesForUser(user.getName())
    groups.each() {
        log.info(it)
        if (user.active) {
            Group targetGroup = groupManager.getGroup((String)it)
            userUtil.AddUserToGroup(targetGroup, user)
            log.info("Added " + user.displayName + " to  " + targetGroup.getName())
        }
    }
}
