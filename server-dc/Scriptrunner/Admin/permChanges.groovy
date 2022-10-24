import com.atlassian.jira.sharing.type.ShareType
import com.atlassian.jira.sharing.SharedEntity
import com.atlassian.jira.issue.search.SearchRequestManager
import com.atlassian.jira.sharing.SharePermissionImpl
import com.atlassian.jira.sharing.SharePermission
import com.atlassian.jira.bc.filter.SearchRequestService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.util.UserManager
import com.atlassian.jira.issue.search.SearchRequest
import com.atlassian.jira.bc.JiraServiceContextImpl
import com.atlassian.jira.sharing.rights.ShareRights

def userManager = ComponentAccessor.getUserManager()

try {
    def ownerUser = userManager.getUserByName('listertuk-admin')
    log.warn('LOGGER decomUser ' + ownerUser)
    def user2 = userManager.getUserByName('listertuk-user2')
    log.warn('LOGGER decomUser ' + user2)
    def loggedInUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

    String groupName = 'Group1'

    SearchRequestService searchRequestService = ComponentAccessor.getComponent(SearchRequestService.class)
    SearchRequestManager searchRequestManager = ComponentAccessor.getComponent(SearchRequestManager.class)
    def contextImpl = new JiraServiceContextImpl(ownerUser)
    Collection<SearchRequest> searchRequests = searchRequestManager.getAllOwnedSearchRequests(ownerUser)

    if (searchRequests.size() > 0) {
        searchRequests.each { s ->
            log.warn s

     //set owner
     //s.setOwner(user2)
            Set<SharePermission> permissionsSet = new HashSet<SharePermission>(
              s.getPermissions().getPermissionSet()
            )

            log.warn('start ' + permissionsSet)
            try {
                def newPerm = new SharePermissionImpl(ShareType.Name.GROUP, groupName, '', ShareRights.VIEW_EDIT)
                //log.warn permissionsSet.contains(newPerm)
                permissionsSet.add(newPerm)
                permissionsSet.forEach() { perm ->
                    if (perm.type.toString() == 'loggedin') {
                        //log.warn('loggedin ' + perm.type.toString() == 'loggedin')
                        //log.warn('before remove ' + permissionsSet.contains(perm))
                        permissionsSet.remove(perm)
                        //log.warn('after remove ' + ' ' + perm + permissionsSet)
                    }
                }
            } catch (Exception e) {
                log.warn('ERROR1 ' + e.getMessage())
            }
            //log.warn('*(*(' + permissionsSet)

            s.setPermissions(new SharedEntity.SharePermissions(permissionsSet))

            searchRequestManager.update(loggedInUser, s)
        }
 } else {
        log.warn "LOGGER ${ownerUser.getName()} no filters found."
    }
} catch (Exception e) {
    log.warn('ERROR2 '  + e.getMessage())
}
