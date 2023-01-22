// use groups of users to remove to remove any dahsboards they own
// see GetAllSplitGroupNames, PutRoleUsersAndGroupsIntoSplitHolders
import com.atlassian.jira.component.ComponentAccessor
import org.apache.log4j.Logger
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.crowd.embedded.api.Group
import com.atlassian.jira.issue.search.SearchRequest
import com.atlassian.jira.bc.JiraServiceContextImpl
import com.atlassian.jira.bc.JiraServiceContext
import com.atlassian.jira.bc.portal.PortalPageService
import com.atlassian.jira.portal.PortalPage
import com.atlassian.jira.portal.PortalPage.Builder

def logit = Logger.getLogger("com.domain1.eu.logging")

def groups = [
"split_africa-dmm",
"split_africa-lpm",
"split_Artience",
"split_CH HQ",
"split_global-dmm",
"split_global-wb",
"split_global-wpl",
"split_GMC",
"split_HQ Support Team",
"split_iran-dmm",
"split_iran-lpm"
]
def groupManager = ComponentAccessor.groupManager
def license = groupManager.getGroup("jira-software-users")

PortalPageService portalPageService = ComponentAccessor.getComponent(PortalPageService)

def luser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
JiraServiceContext sourceUserServiceCtx = new JiraServiceContextImpl(luser)

groups.each() {
	Group groupIn = groupManager.getGroup(it)
    String originalGroup = groupIn.getName().replaceFirst("split_","")

	if (groupIn) {
	//logit.info("source : " + groupIn.name)
        ComponentAccessor.getGroupManager().getUsersInGroup(groupIn).each { ApplicationUser user ->
            
        	Collection<PortalPage> dashboards = portalPageService.getOwnedPortalPages(user)
        	if (!groupManager.isUserInGroup(user, license)) {
        		dashboards.each() {
            		logit.info("" + user + " " + it.getName())
            		def builder = new PortalPage.Builder().portalPage(it).owner(luser).build()

                    // take ownership by force
            		portalPageService.updatePortalPageUnconditionally(sourceUserServiceCtx, luser, builder)

            		def result = portalPageService.validateForDelete(sourceUserServiceCtx, it.id)
            		if (result) {
            			portalPageService.deletePortalPage(sourceUserServiceCtx, it.id)
            		} else {
                		logit.info("can't delete " + it.getName())
            		}
        		}
     		}
    	}
   	}
}
    
