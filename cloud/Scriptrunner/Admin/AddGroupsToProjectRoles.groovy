
/*
**************
set your role name and project here
****************
*/
String[] roleNames = ['Clients', 'Project Managers', 'Project Members', 'Administrators']
String[] projectNames  = ['FA1',
'SCRUM1'
]
String group = 'Bodgers'

projectNames.each() { projectKey ->
    def roles = get('/rest/api/3/project/' + projectKey + '/role')
        .asObject(Map).body

    roleNames.each() { roleName ->
        String developersUrl = roles[roleName]
        logger.info('url: ' + roleName + ' = ' + developersUrl)
        if (developersUrl) {
            def result2 = post(developersUrl)
            .header('Content-Type', 'application/json')
            .body([
                group: [group]
            ])
            .asString()

            assert result2.status == 200
        }
    }
}
