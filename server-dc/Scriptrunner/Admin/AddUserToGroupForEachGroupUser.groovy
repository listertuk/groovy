/*
  Used where users had been entered with wrong domain name
  This only fixes the user groups memberships as a time saving operation
  Take the users in source groups
  For each member, change domain and add to source group and license group.
*/
import com.atlassian.jira.component.ComponentAccessor
import org.apache.log4j.Logger
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.crowd.embedded.api.Group

def logit = Logger.getLogger("com.domain1.logging")
// the name of the  group
// format :
// "Source group":"license group"
def groups = [
    "Accounts Team":"jira-software-users",
    "Dev Team":"jira-software-users",
	"QA Team":"jira-software-users",
]
 
def groupManager = ComponentAccessor.groupManager
def userManager = ComponentAccessor.getUserManager()

groups.each(){
Group groupIn = groupManager.getGroup(it.key)
if (groupIn) {
	logit.info("source : " + groupIn.name)
    def license = groupManager.getGroup(it.value)
		Collection<ApplicationUser> inUsers = groupManager.getUsersInGroup(groupIn)
    	inUsers.each(){ ApplicationUser u ->
            if (u.username.contains("domain1")) {
                def newName = u.username.replaceFirst("domain1.com", "domain2.com")
                def u2 = userManager.getUserByName(newName)
                if(u2) {
        			logit.info("change " + u2.username + " to " + newName)
       				groupManager.addUserToGroup(u2, groupIn)
       				groupManager.addUserToGroup(u2, license)
                }
    		}	
		}
	}
}
    
