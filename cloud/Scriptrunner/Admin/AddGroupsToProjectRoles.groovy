
/*
**************
set your role name and project here
****************
*/
String[] roleNames = ["Clients", "Project Managers", "Project Members", "Administrators"]
String[] projectNames  = ["FA1",
"SCRUM1"
]
String group = "Bodgers"
def roleIds = [:]

projectNames.each() {projectKey ->
    def result = get("rest/api/3/project/" + projectKey)
    .header('Content-Type', 'application/json')
    .asJson()
    
    if (result.status == 200 ) {
          def roles = get("/rest/api/3/project/" + projectKey + "/role")
        .asObject(Map).body
    
    roleNames.each() {roleName ->
        String developersUrl = roles[roleName]
        logger.info("url: " + roleName + " = " + developersUrl)
        if (developersUrl) {
        def result2 = post(developersUrl)
            .header('Content-Type', 'application/json')
            .body([
                group: [group]
            ])
            .asString()

            assert result.status == 200
            }
            
        }

    }


}

