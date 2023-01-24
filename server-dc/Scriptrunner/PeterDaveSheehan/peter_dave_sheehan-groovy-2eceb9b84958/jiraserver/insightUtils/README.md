
## Ownership
Everything in this directory is the work of
https://bitbucket.org/peter_dave_sheehan/groovy/src/master/jiraserver/insightUtils/
copied for convenience 23.1.2023


## Insight Utils
Functions to abstract many of the common interactions with the Insight APIs from Adaptavist ScriptRunner scripts

### Compatibility
This was developed with Insight 8.6.8 for Jira Server. You may need to make some adjustments for other versions/deployments.

### Common Features
* Class and Methods are all static
* All functions have a "runAs" parameter to supply a user other than the current user when you want user elevated permissions.
* Some methods are overloaded to make calling in different contexts simpler

### Methods
* getObjectSchemaByKey
* getObjectTypeByName
* getObjectTypeFromObject
* getAttributesForObjectType
* findObjects
* getObjectByKey
* getObjectsByLabel
* getAllAttributesAsKeyValuePairs
* getAttributeValueFromDotNotation
* getObjectReferenceFromDotNotation
* getSimpleObjectClone
* moveObjectAndWait
* deleteObjectByKey
* setObjectAttribute
* clearObjectAttribute
* storeObject
* createObject
### Usage
There are 3 implementation of the class.

| Class Path | Purpose |
| ---------- | ------- |
| scriptrunnerStandalone.InsightUtils | Use this if you only use the class from ScriptRunner scripts.|
| insight.InsightUtils | Use this anytime you want to use this class from Insight Groovy script files. You will have to import the class file into you scripts (see below) |
| scriptrunner.InsightUtils | Requires insight.InsightUtils. Use *instead* of scriptrunnerStandalone when you also use insightInsightUtils. You'll only care about this if you want autocomplete and static type checking in your ScriptRunner scripts. Otherwise, just import and call the insight.InsightUtils class. |

#### Using from Insight Scripts
Insight groovy script don't seem to allow importing custom classes using import statements. 
Instead, we have to import the file into each script. This means the class has to recompile with each usage.

This is a lot less efficient than with ScriptRunner because ScriptRunner caches the compiled classes and only recompiles when the file changes or you specifically clear the Groovy cache.

For large scripts with large execution volume, you might want to just steal some code from the class and embed it into each of your insight groovy scripts.
```
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.config.util.JiraHome
def jiraHome = ComponentAccessor.getComponentOfType(JiraHome.class).home
Class InsightUtils = new GroovyClassLoader(getClass().getClassLoader()).parseClass(new File("$jiraHome/path/to/scripts/InsightUtils.groovy"))
``` 
#### Using within scriptrunner

Add package declaration (first line) to you scriptrunner version of the InsightUtils corresponding to the path where you saved the class in your scriptrunner script root.

For example:

```package myClasses.utils```

Add to any script with an import:

```import myClasses.utils.InsightUtils```

Call the desired method (autocomplete should even work):

```def obj = InsightUtils.getObjectByKey('KEY-123')```