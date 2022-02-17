//import groovy.xml.MarkupBuilder

String[] projects = ["FA1"]
String[] components = [
    "Bug splatter",
    "Cleaner"]

projects.each() {projectKey ->
    def result = get("rest/api/3/project/" + projectKey)
    .header('Content-Type', 'application/json')
    .asJson()
    
    if (result.status == 200 ) {
        //logger.info( result)
         components.each() {compName ->
    
        def componentResponse = post('/rest/api/3/component')
            .header('Content-Type', 'application/json')
            .body([
              name : compName,
              project: projectKey
               
             ])
        .asObject(Map)
    
    }


}}

