import com.atlassian.crowd.embedded.api.CrowdService
import com.atlassian.crowd.embedded.api.UserWithAttributes
import com.atlassian.crowd.embedded.impl.ImmutableUser
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.bc.user.search.UserSearchParams
import com.atlassian.jira.bc.user.search.UserSearchService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.security.groups.GroupManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.ApplicationUsers
import com.atlassian.jira.user.util.UserUtil
import org.apache.log4j.Logger
def logit = Logger.getLogger("com.domain1.eu.logging")

int numOfDays = 90 // Number of days the user was not logged in
Date dateLimit = (new Date())- numOfDays

UserUtil userUtil = ComponentAccessor.userUtil
CrowdService crowdService = ComponentAccessor.crowdService
UserService userService = ComponentAccessor.getComponent(UserService)
UserSearchService userSearchService = ComponentAccessor.getComponent(UserSearchService.class);
UserSearchParams userSearchParams = new UserSearchParams(true, true, false);
List<ApplicationUser> userList = userSearchService.findUsers("", userSearchParams);
ApplicationUser updateUser
UserService.UpdateUserValidationResult updateUserValidationResult
def userManager = ComponentAccessor.getUserManager()


long count = 0

GroupManager groupManager = ComponentAccessor.getGroupManager();

userList.findAll{it.isActive()}.each {

    if(groupManager.isUserInGroup(it.getName(),"jira-administrators"))
        return;
    
    
	def u = userManager.getUser(it.getName())
    UserWithAttributes user = crowdService.getUserWithAttributes(it.getName())
    
    def inactive = groupManager.getGroup("jira-inactive-users")
    def active = groupManager.getGroup("jira-software-users")

    String lastLoginMillis = user.getValue('login.lastLoginMillis')
    if (lastLoginMillis?.isNumber()) {
        Date d = new Date(Long.parseLong(lastLoginMillis))
        if (d.before(dateLimit) &&  it.isActive() && groupManager.isUserInGroup(it.getName(),"jira-software-users")) {
            groupManager.addUserToGroup(u, inactive)
            userUtil.removeUserFromGroup(active, u)
           logit.info "Deactivate ${user.name} ${d} ${groupManager.getGroupNamesForUser(it.getName())}"
                count++
        }
    }
}

"${count} users deactivated.\n"

