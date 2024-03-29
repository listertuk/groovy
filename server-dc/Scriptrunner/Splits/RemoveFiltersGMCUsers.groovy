// use groups of users to remove to remove any dahsboards they own
// see GetAllSplitGroupNames, PutRoleUsersAndGroupsIntoSplitHolders
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
import com.atlassian.jira.bc.filter.*
import com.atlassian.jira.issue.search.SearchRequest
import com.atlassian.jira.bc.JiraServiceContextImpl
import com.atlassian.jira.sharing.SharedEntity.SharePermissions
import com.atlassian.jira.sharing.SharePermission
import com.atlassian.jira.sharing.SharePermissionImpl
import com.atlassian.jira.sharing.type.ShareType

def logit = Logger.getLogger("com.domain1.eu.logging")

ProjectRoleService projectRoleService = ComponentAccessor.getComponent(ProjectRoleService)                        
//SimpleErrorCollection errorCollection
ProjectRoleManager projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager)
UserUtil userUtil = ComponentAccessor.userUtil
def groupManager = ComponentAccessor.groupManager

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

def license = groupManager.getGroup("jira-software-users")
SearchRequestService srs = ComponentAccessor.getComponent(SearchRequestService)
def luser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def ctx// = new JiraServiceContextImpl(luser)

String[] userGroups
groups.each() {
	Group groupIn = groupManager.getGroup(it)
    String originalGroup = groupIn.getName().replaceFirst("split_","")

	if (groupIn) {
	//logit.info("source : " + groupIn.name)
        ComponentAccessor.getGroupManager().getUsersInGroup(groupIn).each { ApplicationUser user ->
            //errorCollection = new SimpleErrorCollection()
           // logit.info( " " + user +" " +  " "+ userGroups)
            if (!groupManager.isUserInGroup(user, license)) {
            Collection<SearchRequest> filters = srs.getOwnedFilters(user)
            filters.each() { SearchRequest filter ->
 
                //filter.getPermissions() = false
                ctx = new JiraServiceContextImpl(luser)
                Set<SharePermission> permissionsSet = new HashSet<SharePermission>(filter.getPermissions().getPermissionSet());
                permissionsSet.clear()
 				permissionsSet.add(new SharePermissionImpl(ShareType.Name.GLOBAL, null, null));
				filter.setPermissions(new SharePermissions(permissionsSet));
    			/*srs.validateFilterForUpdate(ctx, filter)
                 if (!ctx.getErrorCollection().hasAnyErrors()) {
                    srs.updateFilter(ctx, filter)
                } else {
                    logit.info("perm "+ctx.getErrorCollection().getErrorMessages())
                }   */         	
                              
                
                logit.info(filter.getName())
                ctx = new JiraServiceContextImpl(luser)
           	    srs.validateFilterForChangeOwner(ctx, filter)
                if (!ctx.getErrorCollection().hasAnyErrors()) {
                    srs.updateFilterOwner(ctx, luser, filter)
                } else {
                    logit.info("owner "+ctx.getErrorCollection().getErrorMessages())
                }
    
            	
                ctx = new JiraServiceContextImpl(luser)
                
                srs.validateForDelete(ctx, filter.getId())
                if (!ctx.getErrorCollection().hasAnyErrors()) {
                    srs.deleteFilter(ctx, filter.getId())
                    logit.info(filter.getName() +  "deleted")
                } else {
                    logit.info("delete "+ctx.getErrorCollection().getErrorMessages())
                }
                
            }
            }
 
    	}
        
	}
    
}
