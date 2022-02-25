
//import com.atlassian.jira.issue.comments.MutableComment
//import com.atlassian.jira.issue.comments.Comment
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.util.UserManager

import com.atlassian.jira.user.ApplicationUser
import org.apache.log4j.Logger
import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.bc.issue.IssueService
import com.atlassian.jira.bc.issue.IssueService.IssueValidationResult
import com.atlassian.jira.bc.issue.IssueService.UpdateValidationResult
import com.atlassian.jira.bc.issue.IssueService.IssueResult

def logit = Logger.getLogger('com.domain1.logging')

// **********************************************
// user replacements
// **********************************************
// key : value
def users = [
'yasin.t@domain1eu.com':'yasin.t@domain1.com',
'wenni.chong@domain1eu.com':'wenni.chong@domain1.com',
'filipiak@domain1eu.com':'filipiak@domain1.com'
]
String oldUser
String newUser
String assigneeclause1 = 'assignee in ( "'
String reporterclause1 = 'reporter in ( "'
String clause2 = '")'

// set true to add key (lefthand value), set false to add value (righthand value)
boolean adduser = true
def jqlSearch
//List<Issue> issues = null
SearchService searchService = ComponentAccessor.getComponent(SearchService.class)
//UserManager userMgr = ComponentAccessor.getUserManager()
ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
//IssueManager issueManager = ComponentAccessor.getIssueManager()

IssueService issueService = ComponentAccessor.issueService

def searchResult

users.each() {
    if (adduser) {
        oldUser = it.key
        newUser = it.value
    } else {
        oldUser = it.value
        newUser = it.key
    }
    // ASSIGNEE
    jqlSearch = assigneeclause1 + oldUser + clause2
    logit.info(String.valueOf(jqlSearch))
    SearchService.ParseResult parseResult =  searchService.parseQuery(user, jqlSearch)
    if (parseResult.isValid()) {
        searchResult = searchService.search(user, parseResult.getQuery(), PagerFilter.getUnlimitedFilter())
        StringBuffer sBuf1 = new StringBuffer()
        searchResult.getResults().each { issue ->
            sBuf1.append(issue.getKey() + ', ')
            def issueInputParameters = issueService.newIssueInputParameters()
            issueInputParameters.setSkipScreenCheck(true)
            issueInputParameters.setAssigneeId(newUser)
            /* groovylint-disable-next-line LineLength */
            UpdateValidationResult updateValidationResult = issueService.validateUpdate(user, issue.id, issueInputParameters)
            if (updateValidationResult.isValid()) {
                IssueResult updateResult = issueService.update(user, updateValidationResult)
                if (!updateResult.isValid()) {
                    sBuf1.append('Y, ')
                }
            }
        }
        logit.info(sBuf1.toString())
        } else {
        logit.error('Invalid JQL: ' + jqlSearch)
    }

    // REPORTER
    jqlSearch = reporterclause1 + oldUser + clause2
    logit.info(String.valueOf(jqlSearch))
    SearchService.ParseResult parseResult2 =  searchService.parseQuery(user, jqlSearch)
    /* groovylint-disable-next-line UnnecessaryGetter */
    if (parseResult.isValid()) {
        /* groovylint-disable-next-line UnnecessaryGetter */
        searchResult = searchService.search(user, parseResult2.getQuery(), PagerFilter.getUnlimitedFilter())
        //logit.info(searchResult.getResults().size())
        StringBuffer sBuf2 = new StringBuffer()
        /* groovylint-disable-next-line UnnecessaryGetter */
        searchResult.getResults().each { issue ->
            sBuf2.append(issue.getKey() + ', ')
            def issueInputParameters = issueService.newIssueInputParameters()
            issueInputParameters.setSkipScreenCheck(true)
            issueInputParameters.setReporterId(newUser)
            /* groovylint-disable-next-line LineLength */
            UpdateValidationResult updateValidationResult = issueService.validateUpdate(user, issue.id, issueInputParameters)
            if (updateValidationResult.isValid()) {
                IssueResult updateResult = issueService.update(user, updateValidationResult)
                if (!updateResult.isValid()) {
                    sBuf2.append('Y, ')
                }
            }
        }
        logit.info(sBuf2.toString())
        } else {
        log.error('Invalid JQL: ' + jqlSearch)
    }
}

