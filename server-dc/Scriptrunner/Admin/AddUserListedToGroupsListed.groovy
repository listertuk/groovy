String[] userList = [
    //"557058%3Aebde908e-e998-4fcb-8c8d-7d0076732fa4",
    "listertuk@gmail.com"//,
    //"Tom%20Lister"
    /*
    "bartos@domain1.com",
    "binkie@domain1.com",
    "bow@domain1.com",
    "brat@domain1.com",
    "cel@domain1.com"
    */

]
String[] groups = ["My%20PMs"]

userList.each() {user ->
    def result2 = get('/rest/api/3/user/search?query=' + user)
    .header('Content-Type', 'application/json')
    .asObject(List)
    
    assert result2.status == 200
    
    logger.info(user + " result2: " + result2.body)
    result2.body.each() { userId ->
        String accountId = userId.accountId
        logger.info("accountId " + accountId)
        groups.each() {groupname ->
        String groupsUrl = "/rest/api/3/group/user?groupname=${groupname}"
        logger.info('url: ' + groupname + ' = ' + groupsUrl)
       
            def result3 = post(groupsUrl)
            .header('Content-Type', 'application/json')
            .body([
                accountId: accountId
            ])
            .asString()

            assert result2.status == 200
        
        }
    }
}
