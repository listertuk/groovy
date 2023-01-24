package jiraserver.insightUtils.scriptrunner
/**
 *  This class does nothing. It only passes the call to the MainInsightUtils (which must also be installed)
 *  This is done to share the same code between insight groovy and scriptrunner groovy.
 *  Having a separate class helps with autocomplete and static type checking in the ScriptRunner script editor.
 */
import com.atlassian.jira.user.ApplicationUser
import com.onresolve.scriptrunner.runner.customisers.WithPlugin
import groovy.util.logging.Log4j
import com.riadalabs.jira.plugins.insight.services.model.*
import jiraserver.insightUtils.insight.InsightUtils as MainInsightUtils

@Log4j
final class InsightUtils {
    @WithPlugin('com.riadalabs.jira.plugins.insight') insightPlugin
    private InsightUtils() {
        throw new UnsupportedOperationException("This is a utility class with static methods only and cannot be instantiated")
    }

    static ObjectSchemaBean getObjectSchemaByKey(String schemaKey, ApplicationUser asUser = null) {
        MainInsightUtils.getObjectSchemaByKey(schemaKey, asUser)
    }
    static ObjectSchemaBean getObjectSchemaFromObject(ObjectBean object, ApplicationUser asUser = null){
        MainInsightUtils.getObjectSchemaFromObject(object, asUser)
    }
    static ObjectTypeBean getObjectTypeByName(String schemaKey, String objectTypeName, ApplicationUser asUser = null) {
        MainInsightUtils.getObjectTypeByName(schemaKey, objectTypeName, asUser)
    }

    static ObjectTypeBean getObjectTypeByName(ObjectSchemaBean schema, String objectTypeName, ApplicationUser asUser = null) {
        MainInsightUtils.getObjectTypeByName(schema, objectTypeName, asUser)
    }

    static ObjectTypeBean getObjectTypeFromObject(ObjectBean object, ApplicationUser asUser = null) {
        MainInsightUtils.getObjectTypeFromObject(object, asUser)
    }

    static ArrayList<ObjectTypeAttributeBean> getAttributesForObjectType(ObjectTypeBean objectType, ApplicationUser asUser = null) {
        MainInsightUtils.getAttributesForObjectType(objectType, asUser)
    }

    static ObjectBean getObjectByKey(String objectKey, ApplicationUser asUser = null) {
        MainInsightUtils.getObjectByKey(objectKey, asUser)
    }

    static ArrayList<ObjectBean> findObjects(String iql, ApplicationUser asUser = null, Integer limit = null, Integer start = 0) {
        MainInsightUtils.findObjects(iql, asUser, limit, start)
    }

    static ArrayList<ObjectBean> findObjects(Map attributeValueMap, ApplicationUser asUser = null, Integer limit = null, Integer start = 0) {
        MainInsightUtils.findObjects(attributeValueMap, asUser, limit, start)
    }

    static ArrayList<ObjectBean> getObjectsByLabel(String objectTypeName, String label, ApplicationUser asUser = null) {
        MainInsightUtils.getObjectsByLabel(objectTypeName, label, asUser)
    }

    static ArrayList<ObjectBean> getObjectsByLabel(Integer objectTypeId, String label, ApplicationUser asUser = null) {
        MainInsightUtils.getObjectsByLabel(objectTypeId, label, asUser)
    }

    static Map getAllAttributesAsKeyValuePairs(ObjectBean object, ApplicationUser asUser = null) {
        MainInsightUtils.getAllAttributesAsKeyValuePairs(object, asUser)
    }

    static def getAttributeValueFromDotNotation(ArrayList<Object> objects, String dotNotation = '', Boolean returnObject = false, ApplicationUser asUser = null) {
        MainInsightUtils.getAttributeValueFromDotNotation(objects, dotNotation, returnObject, asUser)
    }

    static def getAttributeValueFromDotNotation(String objectKey, String dotNotation = '', Boolean returnObject = false, ApplicationUser asUser = null) {
        MainInsightUtils.getAttributeValueFromDotNotation(objectKey, dotNotation, returnObject, asUser)
    }

    static def getAttributeValueFromDotNotation(ObjectBean object, String dotNotation = '', Boolean returnObject = false, ApplicationUser asUser = null) {
        MainInsightUtils.getAttributeValueFromDotNotation(object, dotNotation, returnObject, asUser)
    }

    static ObjectBean getObjectReferenceFromDotNotation(ObjectBean object, String dotNotation = '', ApplicationUser asUser = null) {
        MainInsightUtils.getAttributeValueFromDotNotation(object, dotNotation, true, asUser) as ObjectBean
    }

    static ObjectBean getObjectReferenceFromDotNotation(String objectKey, String dotNotation = '', ApplicationUser asUser = null) {
        MainInsightUtils.getAttributeValueFromDotNotation(objectKey, dotNotation, true, asUser) as ObjectBean
    }

    static ObjectBean getSimpleObjectClone(
            ObjectBean object,
            ObjectTypeBean targetObjectType,
            ArrayList excludeAttributes = [],
            Map overrideAttributes = [:],
            ApplicationUser asUser = null
    ) {
        MainInsightUtils.getSimpleObjectClone(object, targetObjectType, excludeAttributes, overrideAttributes, asUser)
    }

    static boolean moveObjectAndWait(ObjectBean object, String targetObjectTypeName, Integer maxWaitMillis = 10000, ApplicationUser asUser = null) {
        MainInsightUtils.moveObjectAndWait(object, targetObjectTypeName, maxWaitMillis, asUser)
    }

    static boolean moveObjectAndWait(ObjectBean object, ObjectTypeBean targetObjectType, Integer maxWaitMillis = 10000, ApplicationUser asUser = null) {
        MainInsightUtils.moveObjectAndWait(object, targetObjectType, maxWaitMillis, asUser)
    }

    static def deleteObjectByKey(String objectKey, Boolean dispatchEvent = true, ApplicationUser asUser = null) {
        MainInsightUtils.deleteObjectByKey(objectKey, dispatchEvent, asUser)
    }

    static ObjectBean setObjectAttribute(ObjectBean object, String attributeName, value, Boolean dispatchEvent = true, ApplicationUser asUser = null) {
        MainInsightUtils.setObjectAttribute(object, attributeName, value, dispatchEvent, asUser)
    }

    static ObjectBean setObjectAttribute(ObjectBean object, String attributeName, ArrayList<Object> values, Boolean dispatchEvent = true, ApplicationUser asUser = null) {
        MainInsightUtils.setObjectAttribute(object, attributeName, values, dispatchEvent, asUser)
    }

    static ObjectBean setObjectAttributes(ObjectBean object, Map attributeValueMap, Boolean dispatchEvent = true, ApplicationUser asUser = null) {
        MainInsightUtils.setObjectAttributes(object, attributeValueMap, dispatchEvent, asUser)
    }

    static ObjectBean clearObjectAttribute(ObjectBean object, String attributeName, Boolean dispatchEvent = true, ApplicationUser asUser = null) {
        MainInsightUtils.clearObjectAttribute(object, attributeName, dispatchEvent, asUser)
    }

    static ObjectBean storeObject(MutableObjectBean object, Boolean dispatchEvent = true, ApplicationUser asUser = null) {
        MainInsightUtils.storeObject(object, dispatchEvent, asUser)
    }

    static ObjectBean createObject(String schemaKey, String objectTypeName, Map attributeValueMap, Boolean allowObjectReferenceCreate = false, Boolean dispatchEvent = true, ApplicationUser asUser = null) {
        MainInsightUtils.createObject(schemaKey, objectTypeName, attributeValueMap, allowObjectReferenceCreate, dispatchEvent, asUser)
    }

    static ObjectBean createObject(ObjectSchemaBean schema, String objectTypeName, Map attributeValueMap, Boolean allowObjectReferenceCreate = false, Boolean dispatchEvent = true, ApplicationUser asUser = null) {
        MainInsightUtils.createObject(schema, objectTypeName, attributeValueMap, allowObjectReferenceCreate, dispatchEvent, asUser)
    }

    static ObjectBean createObject(ObjectTypeBean objectType, Map attributeValueMap, Boolean allowObjectReferenceCreate = false, Boolean dispatchEvent = true, ApplicationUser asUser = null) {
        MainInsightUtils.createObject(objectType, attributeValueMap, allowObjectReferenceCreate, dispatchEvent, asUser)
    }
    static StatusTypeBean getStatusByName(ObjectSchemaBean schema, String statusName, ApplicationUser asUser=null){
        MainInsightUtils.getStatusByName(schema, statusName, asUser)
    }

}
