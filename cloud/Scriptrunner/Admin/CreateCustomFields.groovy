String TEXT_CF = "Trace Text";
String CHECKBOX = "com.atlassian.jira.plugin.system.customfieldtypes:multicheckboxes"
String CHECKBOX_SEARCHER = "com.atlassian.jira.plugin.system.customfieldtypes:multiselectsearcher"
String SELECT ="com.atlassian.jira.plugin.system.customfieldtypes:multiselect"
String SELECT_SEARCHER = "com.atlassian.jira.plugin.system.customfieldtypes:multiselectsearcher"
String TEXT = "com.atlassian.jira.plugin.system.customfieldtypes:textfield"
String TEXT_SEARCHER = "com.atlassian.jira.plugin.system.customfieldtypes:textsearcher"
String SINGLE_SELECT = "com.atlassian.jira.plugin.system.customfieldtypes:select"
String SINGLE_SELECT_SEARCHER = "com.atlassian.jira.plugin.system.customfieldtypes:multiselectsearcher"

  def componentResponse = post('/rest/api/3/component')
            .header('Content-Type', 'application/json')
            .body([
              name : compName,
              project: projectKey
             ])
            .asObject(Map)