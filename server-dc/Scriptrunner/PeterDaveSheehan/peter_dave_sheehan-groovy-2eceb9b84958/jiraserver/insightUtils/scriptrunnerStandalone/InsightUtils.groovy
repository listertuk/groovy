package jiraserver.insightUtils.scriptrunnerStandalone

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.exception.CreateException
import com.atlassian.jira.project.Project
import com.atlassian.jira.user.ApplicationUser
import com.onresolve.scriptrunner.runner.customisers.PluginModule
import com.onresolve.scriptrunner.runner.customisers.WithPlugin
import com.riadalabs.jira.plugins.insight.channel.external.api.facade.*
import com.riadalabs.jira.plugins.insight.common.exception.InsightException
import com.riadalabs.jira.plugins.insight.services.events.EventDispatchOption
import com.riadalabs.jira.plugins.insight.services.model.*
import com.riadalabs.jira.plugins.insight.services.model.factory.ObjectAttributeBeanFactory
import com.riadalabs.jira.plugins.insight.services.model.move.MoveAttributeMapping
import com.riadalabs.jira.plugins.insight.services.model.move.MoveObjectBean
import com.riadalabs.jira.plugins.insight.services.model.move.MoveObjectMapping
import groovy.util.logging.Log4j

@Log4j
final class InsightUtils {
    @WithPlugin('com.riadalabs.jira.plugins.insight') insightPlugin
    @PluginModule
    static ObjectSchemaFacade objectSchemaFacade
    @PluginModule
    static ObjectFacade objectFacade
    @PluginModule
    static ObjectTypeFacade objectTypeFacade
    @PluginModule
    static ObjectTypeAttributeFacade objectTypeAttributeFacade
    @PluginModule
    static ObjectAttributeBeanFactory objectAttributeBeanFactory
    @PluginModule
    static IQLFacade iqlFacade
    @PluginModule
    static ConfigureFacade configureFacade

    private InsightUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    static private ApplicationUser setUser(ApplicationUser asUser = null) {
        def currentUser = ComponentAccessor.jiraAuthenticationContext.loggedInUser
        if (asUser && currentUser != asUser) {
            ComponentAccessor.jiraAuthenticationContext.loggedInUser = asUser
        }
        return currentUser
    }

    static private void resetUser(ApplicationUser currentUser) {
        ComponentAccessor.jiraAuthenticationContext.loggedInUser = currentUser
    }

    static EventDispatchOption getDispatchOption(Boolean doDispatch) {
        def dispatchOption = EventDispatchOption.DISPATCH
        if (!doDispatch) {
            dispatchOption = EventDispatchOption.DO_NOT_DISPATCH
        }
        return dispatchOption
    }

    static ObjectSchemaBean getObjectSchemaByKey(String schemaKey, ApplicationUser asUser = null) {
        def currentUser = setUser(asUser)
        def schema = objectSchemaFacade.findObjectSchemaBeans().find { it.objectSchemaKey == schemaKey }
        resetUser(currentUser)
        return schema
    }

    static ObjectSchemaBean getObjectSchemaFromObject(ObjectBean object, ApplicationUser asUser = null) {
        def currentUser = setUser(asUser)
        def objectType = getObjectTypeFromObject(object)
        def schema = objectSchemaFacade.loadObjectSchema(objectType.objectSchemaId)
        resetUser(currentUser)
        return schema
    }

    static ObjectTypeBean getObjectTypeByName(String schemaKey, String objectTypeName, ApplicationUser asUser = null) {
        def currentUser = setUser(asUser)
        ObjectSchemaBean schema = getObjectSchemaByKey(schemaKey)
        if (!schema) {
            log.error "No schema found for $schemaKey"
            return null
        }
        def objectType = objectTypeFacade.findObjectTypeBeansFlat(schema.id).find { it.name == objectTypeName }
        resetUser(currentUser)
        return objectType
    }

    static ObjectTypeBean getObjectTypeByName(ObjectSchemaBean schema, String objectTypeName, ApplicationUser asUser = null) {
        def currentUser = setUser(asUser)
        def objectType = objectTypeFacade.findObjectTypeBeansFlat(schema.id).find { it.name == objectTypeName }
        resetUser(currentUser)
        return objectType
    }

    static ObjectTypeBean getObjectTypeFromObject(ObjectBean object, ApplicationUser asUser = null) {
        def currentUser = setUser(asUser)
        def objectType = objectTypeFacade.loadObjectType(object.objectTypeId)
        resetUser(currentUser)
        objectType
    }

    static ArrayList<ObjectTypeAttributeBean> getAttributesForObjectType(ObjectTypeBean objectType, ApplicationUser asUser = null) {
        def currentUser = setUser(asUser)
        def objectTypeAttributes = objectTypeAttributeFacade.findObjectTypeAttributeBeans(objectType.id)
        resetUser(currentUser)
        return objectTypeAttributes
    }

    static ObjectBean getObjectByKey(String objectKey, ApplicationUser asUser = null) {
        def currentUser = setUser(asUser)
        def object = objectFacade.loadObjectBean(objectKey)
        resetUser(currentUser)
        object
    }

    static ArrayList<ObjectBean> findObjects(String iql, ApplicationUser asUser = null, Integer limit = null, Integer start = 0) {
        def currentUser = setUser(asUser)
        def objectList = []
        if (limit) {
            objectList = iqlFacade.findObjects(iql, start, limit).objects
        } else {
            objectList = iqlFacade.findObjects(iql)
        }
        resetUser(currentUser)
        objectList
    }

    static ArrayList<ObjectBean> findObjects(Map attributeValueMap, ApplicationUser asUser = null, Integer limit = null, Integer start = 0) {
        def currentUser = setUser(asUser)
        def iql = attributeValueMap.collect { attributeName, value ->
            /"$attributeName" = "$value"/
        }.join(' AND ')
        def objectList = []
        objectList = findObjects(iql, asUser, limit, start)
        resetUser(currentUser)
        objectList
    }

    static ArrayList<ObjectBean> getObjectsByLabel(String objectTypeName, String label, ApplicationUser asUser = null) {
        def currentUser = setUser(asUser)
        def objectList = iqlFacade.findObjects("""objectType = "$objectTypeName" and label ="$label" """)
        resetUser(currentUser)
        objectList

    }

    static ArrayList<ObjectBean> getObjectsByLabel(Integer objectTypeId, String label, ApplicationUser asUser = null) {
        def currentUser = setUser(asUser)
        def objectList = iqlFacade.findObjects("""objectTypeId = $objectTypeId and label ="$label" """)
        resetUser(currentUser)
        objectList
    }

    static Map getAllAttributesAsKeyValuePairs(ObjectBean object, ApplicationUser asUser = null) {
        def currentUser = setUser(asUser)
        StringBuffer output = new StringBuffer()
        def returnMap = object.objectAttributeBeans.collectEntries { objAttr ->
            def attribute = objectTypeAttributeFacade.loadObjectTypeAttribute(objAttr.objectTypeAttributeId)
            def name = attribute.name
            def isMulti = attribute.maximumCardinality != 1
            def val = ''
            ObjectTypeAttributeBean.Type.DEFAULT
            log.info attribute.typeValue
            switch (attribute.type) {
                case ObjectTypeAttributeBean.Type.REFERENCED_OBJECT:
                    val = objAttr.objectAttributeValueBeans.collect { objectFacade.loadObjectBean(it.referencedObjectBeanId).label }
                    break
                case ObjectTypeAttributeBean.Type.STATUS:
                    val = objAttr.objectAttributeValueBeans.collect { configureFacade.loadStatusTypeBean(it.value as Integer).name }
                    break
                case ObjectTypeAttributeBean.Type.USER:
                    val = objAttr.objectAttributeValueBeans.value.collect { ComponentAccessor.userManager.getUserByKey(it as String) }
                    break
                case ObjectTypeAttributeBean.Type.PROJECT:
                    val = objAttr.objectAttributeValueBeans.value.collect { ComponentAccessor.projectManager.getProjectObj(it as Long) }
                    break
                default: //DEFAULT/GROUP
                    val = objAttr.objectAttributeValueBeans.value
                    break
            }
            if (!isMulti && val instanceof List) {
                val = val[0]
            }
            [(name): val]
        }
        resetUser(currentUser)
        returnMap
    }
    /**
     * This recursive method is ugly and will give you a headache if you try to follow it. But it works
     *
     * @param object
     * @param dotNotation
     * @param returnObject
     * @param asUser
     * @return Object Dynamically typed groovy object representation of the attribute
     */
    static def getAttributeValueFromDotNotation(ObjectBean object, String dotNotation = '', Boolean returnObject = false, ApplicationUser asUser = null) {
        def currentUser = setUser(asUser)
        object = objectFacade.loadObjectBean(object.id)
        if (object) {
            log.debug("getAttributeValueFromDotNotation: Getting '${dotNotation ?: 'label'}' from '$object'")
            def objectType = objectTypeFacade.loadObjectType(object.objectTypeId)
            def returnValue
            if (dotNotation == '') {
                if (returnObject) {
                    log.trace "getAttributeValueFromDotNotation: returning object $object"
                    resetUser(currentUser)
                    return object
                } else {
                    log.trace "getAttributeValueFromDotNotation: returning label $object.label"
                    resetUser(currentUser)
                    return object.label
                }
            }
            def parts = dotNotation.split(/\./)
            def previousPartIsNull = false
            parts.each { part ->
                //todo: check the return value when previous part is null
                log.trace "Evaluating part $part. Current returnValue is $returnValue and previousPartIsNull is $previousPartIsNull"
                if (returnValue || previousPartIsNull) {
                    log.debug "Cancel processing of part $part"
                    return
                }
                def attributeBean = objectTypeAttributeFacade.findObjectTypeAttributeBeans(objectType.id).find {
                    it.name == part
                }
                if (attributeBean) {
                    log.trace "getAttributeValueFromDotNotation: $attributeBean.name is a valid attribute of $objectType.name"
                    if (attributeBean.isObjectReference()) {
                        log.trace "getAttributeValueFromDotNotation: $attributeBean.name is a reference object"
                        def referencedObjectBeans = object.objectAttributeBeans.find {
                            it.objectTypeAttributeId == attributeBean.id
                        }?.objectAttributeValueBeans
                        if (referencedObjectBeans) {
                            log.trace "getAttributeValueFromDotNotation: found a value for $attributeBean.name"

                            returnValue = referencedObjectBeans.collect { referencedObjectBean ->
                                def refObject = objectFacade.loadObjectBean(referencedObjectBean.value)
                                getAttributeValueFromDotNotation(refObject, parts.drop(1).join('.'), returnObject, asUser)
                            }.findAll { it }
                            log.trace "Setting previousPartIsNull to ${(!returnValue)} returnValue =$returnValue"
                            previousPartIsNull = (!returnValue)
                        } else {
                            log.debug "getAttributeValueFromDotNotation: $attributeBean.name has no value"
                            returnValue = null
                            previousPartIsNull = true
                        }
                    } else {
                        def objectAttributeBeans = object.objectAttributeBeans
                        log.trace "getAttributeValueFromDotNotation: $object has ${objectAttributeBeans.size()} attributes"
                        def attributeBean2 = objectAttributeBeans.find {
                            it.objectTypeAttributeId == attributeBean.id
                        }
                        log.debug "getAttributeValueFromDotNotation: attributeBean for attributeId $attributeBean.id = $attributeBean2"
                        if (attributeBean2?.objectAttributeValueBeans) {
                            log.trace "getAttributeValueFromDotNotation: attempting to get the value: ${attributeBean2?.objectAttributeValueBeans*.value}"
                            returnValue = attributeBean2?.objectAttributeValueBeans*.value
                        }
                        if (attributeBean.isStatus() && returnValue instanceof List && returnValue.size() > 0) {
                            returnValue = configureFacade.loadStatusTypeBean(returnValue[0] as Integer).name
                        }
                    }
                } else {
                    log.error "$part is not a valid attribute of $objectType.name\n ${Thread.dumpStack()}"
                }
            }
            if (returnValue instanceof ArrayList && returnValue.size() == 1) {
                returnValue = returnValue.first()
            }
            log.debug "getAttributeValueFromDotNotation: \n\tobject: $object \n\tattribute: $dotNotation \n\treturning $returnValue"
            resetUser(currentUser)
            return returnValue
        }
        resetUser(currentUser)
        return null
    }

    /**
     *
     * @param objects ArrayList<Object> the object to get the attribute from. Object can either be instance of ObjectBean or String objectKey
     * @param dotNotation String DotNotation for the attribute to retreive
     * @param returnObject Boolean. If true and the dotNotation is a an Insight Object reference, the return value will be an ObjectBean. Otherwise, it will be the objectKey
     * @param asUser
     * @return Object ArrayList of dynamically typed groovy object representations of the attribute
     */
    static def getAttributeValueFromDotNotation(ArrayList<Object> objects, String dotNotation = '', Boolean returnObject = false, ApplicationUser asUser = null) {
        objects.collect { object ->
            if (object instanceof String) {
                object = getObjectByKey(object)
            }
            getAttributeValueFromDotNotation(object, dotNotation, returnObject, asUser)
        }
    }
    /**
     *
     * @param objectKey String
     * @param dotNotation
     * @param returnObject
     * @param asUser
     * @return
     */
    static def getAttributeValueFromDotNotation(String objectKey, String dotNotation = '', Boolean returnObject = false, ApplicationUser asUser = null) {
        def object = getObjectByKey(objectKey)
        getAttributeValueFromDotNotation(object, dotNotation, returnObject, asUser)
    }
    /**
     *
     * @param object
     * @param dotNotation
     * @param asUser
     * @return
     */
    static ObjectBean getObjectReferenceFromDotNotation(ObjectBean object, String dotNotation = '', ApplicationUser asUser = null) {
        getAttributeValueFromDotNotation(object, dotNotation, true, asUser) as ObjectBean
    }

    /**
     *
     * @param objectKey
     * @param dotNotation
     * @param asUser
     * @return
     */
    static ObjectBean getObjectReferenceFromDotNotation(String objectKey, String dotNotation = '', ApplicationUser asUser = null) {
        def object = getObjectByKey(objectKey)
        getAttributeValueFromDotNotation(object, dotNotation, true, asUser) as ObjectBean
    }

    /**
     *  This method assumes the cloned targetObjectType has all the same attributes
     * @param object The source object to be cloned
     * @param targetObjectType The target object type to clone the object to
     * @param excludeAttributes ArrayList of attributes names to exclude from cloning
     * @param overrideAttributes Map of targetAttributeName:value to allow for putting
     *                              specific value/object into the target attribute
     * @return theClonedObject
     */
    static ObjectBean getSimpleObjectClone(
            ObjectBean object,
            ObjectTypeBean targetObjectType,
            ArrayList excludeAttributes = [],
            Map overrideAttributes = [:],
            ApplicationUser asUser = null
    ) {
        def currentUser = setUser(asUser)
        //log.setLevel(Level.DEBUG)
        ArrayList defaultExcludeAttributes = ['Key', 'Created', 'Updated']
        excludeAttributes = (defaultExcludeAttributes + excludeAttributes + overrideAttributes.keySet()).unique()
        log.debug "getSimpleObjectClone: excludeAttributes=$excludeAttributes"
        //create the new object
        def cloneObject = targetObjectType.createMutableObjectBean()

        //transfer all the attributes not in exclude list and not being overridden
        //def newAttributes =[]
        def newAttributes = object.objectAttributeBeans.findAll { objectAttributeBean ->
            def typeAttBean = objectTypeAttributeFacade.loadObjectTypeAttribute(objectAttributeBean.objectTypeAttributeId)
            !(typeAttBean.name in excludeAttributes)
        }.collect { it.createMutable() }

        log.debug "getSimpleObjectClone: overrideAttributes=$overrideAttributes"
        overrideAttributes.each { attributeName, value ->
            log.debug "getSimpleObjectClone: processing overrideAttribute ($attributeName) with overrideValue ($value)"
            def objectTypeAttributeBean = objectTypeAttributeFacade.loadObjectTypeAttribute(targetObjectType.id, attributeName)
            log.debug "getSimpleObjectClone: Identified ota '$objectTypeAttributeBean' from name='$attributeName'"
            log.debug "getSimpleObjectClone: Attempting to create oa using $value (${value.getClass()})"
            newAttributes << objectAttributeBeanFactory.createObjectAttributeBeanForObject(cloneObject, objectTypeAttributeBean, value)
        }
        cloneObject.setObjectAttributeBeans(newAttributes)
        def tmpDisplayAttr = cloneObject.objectAttributeBeans.find { it.objectTypeAttributeId == 217 }

        //log.debug cloneObject.objectAttributeBeans.find{it.objectTypeAttributeId == 217}.objectAttributeValueBeans.first().value
        resetUser(currentUser)
        try {
            //can't validate for some reason unique fields are always failing
            //objectFacade.validateObjectBean(cloneObject)
            return cloneObject
        } catch (exception) {
            log.error "ObjectBean validationfailed", exception
            return null
        }
    }

    static boolean moveObjectAndWait(ObjectBean object, String targetObjectType, Integer maxWaitMillis = 10000, ApplicationUser asUser = null) {
        def currentUser = setUser(asUser)
        def sourceObjectType = objectTypeFacade.loadObjectType(object.objectTypeId)
        def targetObjecType = objectTypeFacade.findObjectTypeBeansFlat(sourceObjectType.objectSchemaId).find { it.name == targetObjectType }
        resetUser(currentUser)
        moveObjectAndWait(object, targetObjecType, maxWaitMillis, asUser)
    }

    static boolean moveObjectAndWait(ObjectBean object, ObjectTypeBean targetObjectType, Integer maxWaitMillis = 10000, ApplicationUser asUser = null) {
        def currentUser = setUser(asUser)
        MoveObjectBean moveObjectBean = new MoveObjectBean()
        moveObjectBean.setObjectSchemaId(targetObjectType.objectSchemaId)
        moveObjectBean.setFromObjectTypeId(object.objectTypeId)
        moveObjectBean.setToObjectTypeId(targetObjectType.id)
        moveObjectBean.setReferences(MoveObjectBean.References.REMOVE_REFERENCES_TO_OBJECT)
        MoveObjectMapping moveMap = new MoveObjectMapping()
        objectTypeAttributeFacade.findObjectTypeAttributeBeans(object.objectTypeId).each { att ->
            def attMap = MoveAttributeMapping.create(att, att)
            log.info "Preparing to move $att (${att.getClass()})"
            moveMap.map(att.id, attMap)
        }

        moveObjectBean.setMapping(moveMap)
        moveObjectBean.setIql("Key = $object.objectKey")

        def moveSuccess = false

        try {
            def moveProgress = objectFacade.moveObjects(moveObjectBean)
            //wait for move to finish
            Integer curWait = 0
            log.debug "moveObjectAndWait: Waiting for move to finish: $curWait ($moveProgress.status)"
            while (moveProgress.isFinished() == false && curWait < maxWaitMillis) {
                Thread.sleep(100)
                curWait = curWait + 100
                log.debug "moveObjectAndWait: Waiting for move to finish: $curWait ($moveProgress.status)"
            }
            if (moveProgress.isFinished()) {
                log.info "moveObjectAndWait: Move complete"
                moveSuccess = true
            } else if (moveProgress.isError()) {
                log.error "moveObjectAndWait: error during move - "
            } else {
                log.warn "moveObjectAndWait: Gave up waiting for move to finish."
            }
        } catch (moveEx) {
            log.error "moveObjectAndWait: Could not move ${object?.objectKey} to ${targetObjectType?.name}: $moveEx.message"
        }
        resetUser(currentUser)
        moveSuccess
    }

    static def deleteObjectByKey(String objectKey, Boolean dispatchEvent = true, ApplicationUser asUser = null) {
        def currentUser = setUser(asUser)
        def object = getObjectByKey(objectKey)
        if (object) {
            objectFacade.deleteObjectBean(object.id, getDispatchOption(dispatchEvent))
        } else {
            log.warn "There are no objects matching $objectKey to delete"
        }
        resetUser(currentUser)
    }

    static ObjectBean setObjectAttribute(ObjectBean object, String attributeName, ObjectBean value, Boolean dispatchEvent = true, ApplicationUser asUser = null) {
        setObjectAttribute(object, attributeName, value.id, dispatchEvent, asUser)
    }

    static ObjectBean setObjectAttribute(ObjectBean object, String attributeName, value, Boolean dispatchEvent = true, ApplicationUser asUser = null) {
        def currentUser = setUser(asUser)
        def objectTypeAttributeBean = objectTypeAttributeFacade.loadObjectTypeAttribute(object.objectTypeId, attributeName)
        def objectAttributeBean = object.createObjectAttributeBean(objectTypeAttributeBean)
        def objectAttributeValueBean = objectAttributeBean.createObjectAttributeValueBean()
        if (value instanceof GString) value = value.toString()
        if (objectTypeAttributeBean.isStatus() && value instanceof String) {
            def status = getStatusByName(getObjectSchemaFromObject(object), value as String)
            value = status.id
        }
        log.debug "Attempting to set value for $attributeName on $object.objectKey using value=$value (${value.getClass()})"
        objectAttributeValueBean.setValue(objectTypeAttributeBean, value)
        objectAttributeBean.setObjectAttributeValueBeans([objectAttributeValueBean])
        try {
            log.debug "Attempting to update $attributeName to $value on $object.objectKey"
            objectAttributeBean = objectFacade.storeObjectAttributeBean(objectAttributeBean, getDispatchOption(dispatchEvent))
            object = objectFacade.loadObjectBean(object.id)
        } catch (e) {
            log.error "Could not set the value($value) for attribute $attributeName): $e.message"
        }
        resetUser(currentUser)
        return object
    }

    static ObjectBean setObjectAttribute(ObjectBean object, String attributeName, ArrayList<Object> values, Boolean dispatchEvent = true, ApplicationUser asUser = null) {
        def currentUser = setUser(asUser)
        def objectTypeAttributeBean = objectTypeAttributeFacade.loadObjectTypeAttribute(object.objectTypeId, attributeName)
        def objectAttributeBean = object.createObjectAttributeBean(objectTypeAttributeBean)
        def arrayOfObjectAttValueBeans = values.collect { value ->
            def objectAttributeValueBean = objectAttributeBean.createObjectAttributeValueBean()
            if (value instanceof ObjectBean) {
                objectAttributeValueBean.setValue(objectTypeAttributeBean, value.id)
            } else if (objectTypeAttributeBean.isStatus() && value instanceof String) {
                def status = getStatusByName(getObjectSchemaFromObject(object), value as String)
                objectAttributeValueBean.setValue(objectTypeAttributeBean, status.id)
            } else {
                objectAttributeValueBean.setValue(objectTypeAttributeBean, value)
            }
            objectAttributeValueBean
        }
        objectAttributeBean.setObjectAttributeValueBeans(arrayOfObjectAttValueBeans)
        try {
            log.debug "Attempting to update $attributeName to $values on $object.objectKey"
            objectAttributeBean = objectFacade.storeObjectAttributeBean(objectAttributeBean, getDispatchOption(dispatchEvent))
            object = objectFacade.loadObjectBean(object.id)
        } catch (e) {
            log.error "Could not set the values($values) for attribute $attributeName on $object.objectKey: $e.message"
        }
        resetUser(currentUser)
        return object
    }

    static ObjectBean setObjectAttributes(ObjectBean object, Map attributeUpdateMap, Boolean dispatchEvent = true, ApplicationUser asUser = null) {
        def currentUser = setUser(asUser)
        MutableObjectBean mutableObject = object.createMutable()
        ObjectBean savedObjectBean

        //get all the attributes in the object currently and replace the value (if applicable) with values submitted in the attributeUpdateMap
        def objectAttributeBeans = object.objectAttributeBeans.collect { objectAttribute ->
            def objectTypeAttributeBean = objectTypeAttributeFacade.loadObjectTypeAttribute(objectAttribute.objectTypeAttributeId)
            log.debug "Check if $objectTypeAttributeBean.name exists in ${attributeUpdateMap.keySet()}"
            if (attributeUpdateMap.containsKey(objectTypeAttributeBean.name)) {
                def mObjectAttribute = objectAttribute.createMutable()
                def valueBeans
                def currentValue = objectAttribute.objectAttributeValueBeans*.value
                def newValue = attributeUpdateMap[objectTypeAttributeBean.name]
                if (newValue instanceof GString) newValue = newValue.toString()
                if (objectTypeAttributeBean.isStatus() && newValue instanceof String) {
                    def status = getStatusByName(getObjectSchemaFromObject(object), newValue)
                    newValue = status.id

                }
                if (currentValue == newValue) {
                    log.info "The new value supplied ($newValue) was the same as the current value ($currentValue)"
                    return objectAttribute
                } else {
                    log.info "Replacing $objectTypeAttributeBean.name with new value $newValue"
                    if (newValue instanceof List) {
                        valueBeans = newValue.collect { singleValue ->
                            if (singleValue instanceof GString) singleValue = singleValue.toString()
                            def objectAttributeValueBean = mObjectAttribute.createObjectAttributeValueBean()
                            if (singleValue instanceof ObjectBean) {
                                objectAttributeValueBean.setReferencedObjectBeanId(singleValue.id)
                            } else {
                                objectAttributeValueBean.setValue(objectTypeAttributeBean, singleValue)
                            }
                            objectAttributeValueBean
                        }
                    } else {
                        def objectAttributeValueBean = mObjectAttribute.createObjectAttributeValueBean()
                        if (newValue instanceof ObjectBean) {
                            objectAttributeValueBean.setReferencedObjectBeanId(newValue.id)
                        } else {
                            objectAttributeValueBean.setValue(objectTypeAttributeBean, newValue)
                        }
                        valueBeans = [objectAttributeValueBean]
                    }
                    mObjectAttribute.setObjectAttributeValueBeans(valueBeans)
                    //remove the current attribute from the attributeUpdateMap
                    attributeUpdateMap.remove(objectTypeAttributeBean.name)
                    return mObjectAttribute
                }
            } else {
                return objectAttribute
            }
        }
        log.debug "Looping through remaining attributeUpdateMap with keys ${attributeUpdateMap.keySet()}"
        attributeUpdateMap.each { attributeName, value ->
            log.debug "Adding $value to $attributeName"
            def objectTypeAttributeBean = objectTypeAttributeFacade.loadObjectTypeAttribute(object.objectTypeId, attributeName)
            def objectAttributeBean = object.createObjectAttributeBean(objectTypeAttributeBean)
            if (value instanceof GString) value = value.toString()
            if (objectTypeAttributeBean.isStatus() && value instanceof String) {
                def status = getStatusByName(getObjectSchemaFromObject(object), value)
                value = status.id
            }
            if (value instanceof List) {
                def valueBeans = value.collect {
                    log.debug "Creating objectAttributeValueBean from $it"
                    def objectAttributeValueBean = objectAttributeBean.createObjectAttributeValueBean()
                    if (it instanceof ObjectBean) {
                        objectAttributeValueBean.setReferencedObjectBeanId(it.id)
                    } else {
                        objectAttributeValueBean.setValue(objectTypeAttributeBean, it)
                    }
                    objectAttributeValueBean
                }
                objectAttributeBean.setObjectAttributeValueBeans(valueBeans)
            } else {
                def objectAttributeValueBean = objectAttributeBean.createObjectAttributeValueBean()
                if (value instanceof ObjectBean) {
                    objectAttributeValueBean.setReferencedObjectBeanId(value.id)
                } else {
                    objectAttributeValueBean.setValue(objectTypeAttributeBean, value)
                }
                objectAttributeBean.setObjectAttributeValueBeans([objectAttributeValueBean])
            }

            objectAttributeBeans << objectAttributeBean
        }
        log.debug "Attempting to store $objectAttributeBeans in $object"
        mutableObject.setObjectAttributeBeans(objectAttributeBeans)
        try {
            /**
             * I used to validate before storing,
             * but validating was somehow triggering the object to be indexed
             * such that non-unique errors would be raised when storing.
             * So I wrapped the operation in a try/catch block instead.
             */
            //objectFacade.validateObjectBean(mutableObject)
            savedObjectBean = objectFacade.storeObjectBean(mutableObject, getDispatchOption(dispatchEvent))
        } catch (e) {
            log.error "Could not update $object.objectKey with $attributeUpdateMap" //$e.message"
        }
        resetUser(currentUser)
        return savedObjectBean
    }

    static ObjectBean clearObjectAttribute(ObjectBean object, String attributeName, Boolean dispatchEvent = true, ApplicationUser asUser = null) {
        def dispatchOption = EventDispatchOption.DISPATCH
        if (!dispatchEvent) {
            dispatchOption = EventDispatchOption.DO_NOT_DISPATCH
        }
        def currentUser = setUser(asUser)
        def objectTypeAttributeBean = objectTypeAttributeFacade.loadObjectTypeAttribute(object.objectTypeId, attributeName)
        def objectAttributeBean = object.objectAttributeBeans.find { it.objectTypeAttributeId == objectTypeAttributeBean.id }
        if (objectAttributeBean) {
            try {
                objectFacade.deleteObjectAttributeBean(objectAttributeBean.id as Long, getDispatchOption(dispatchEvent))
                object = objectFacade.loadObjectBean(object.id)
            } catch (e) {
                log.error "Could not clear attribute $attributeName from $object.objectKey: $e.message"
            }
        }
        resetUser(currentUser)
        return object
    }

    static ObjectBean storeObject(MutableObjectBean object, Boolean dispatchEvent = true, ApplicationUser asUser = null) {
        def currentUser = setUser(asUser)
        def storedObject = objectFacade.storeObjectBean(object, getDispatchOption(dispatchEvent))
        resetUser(currentUser)
        storedObject
    }

    /**
     * Create an object my passing a map of AttributeName:Attribute value. This will automatically convert ObjectBean,
     * objectKey, ApplicationUser and project Project objects to the correct data type.
     * Can be used recursively in attributes that are reference objects
     *     eg. map=[Name='testname',RefAttr:[Name='refObjName']
     *          If refObjName exists in target object type for RefAttr, it will be linked,
     *          if it doesn't exist, it will be created first, then linked
     * @param objectType
     * @param attributeValueMap
     * @param asUser
     * @return the new object
     */
    static ObjectBean createObject(String schemaKey, String objectTypeName, Map attributeValueMap, Boolean allowObjectReferenceCreate = false, Boolean dispatchEvent = true, ApplicationUser asUser = null) {
        def currentUser = setUser(asUser)
        def schema = getObjectSchemaByKey(schemaKey)
        if (!schema) {
            log.error "No schema found for $schemaKey"
            return null
        }
        def objectTypeBean = getObjectTypeByName(schema, objectTypeName)
        if (!objectTypeBean) {
            log.error "No ObjectType found for $objectTypeName in schema $schemaKey"
        }
        def object = createObject(objectTypeBean, attributeValueMap, allowObjectReferenceCreate, dispatchEvent)
        resetUser(currentUser)
        return object
    }

    static ObjectBean createObject(ObjectSchemaBean schema, String objectTypeName, Map attributeValueMap, Boolean allowObjectReferenceCreate = false, Boolean dispatchEvent = true, ApplicationUser asUser = null) {
        def objectTypeBean = getObjectTypeByName(schema, objectTypeName)
        createObject(objectTypeBean, attributeValueMap, allowObjectReferenceCreate, dispatchEvent, asUser)
    }

    static ObjectBean createObject(ObjectTypeBean objectType, Map attributeValueMap, Boolean allowObjectReferenceCreate = false, Boolean dispatchEvent = true, ApplicationUser asUser = null) {
        def currentUser = setUser(asUser)
        MutableObjectBean object = objectType.createMutableObjectBean()
        def attributeValidationResult = validateAttributes(objectType, attributeValueMap.keySet().asList())
        if (!attributeValidationResult.isValid) {
            throw new CreateException(attributeValidationResult.messages as String)
        }
        def objectTypeAttributes = objectTypeAttributeFacade.findObjectTypeAttributeBeans(objectType.id)
        def oaBeans = attributeValueMap.collect { attributeName, value ->
            def attributeParts = attributeName.tokenize('.')
            def refAttributeName = attributeParts[0]
            def otaBean = objectTypeAttributes.find { it.name == refAttributeName }
            if (otaBean.objectReference && attributeParts.size() > 1) {
                def refObjectType = objectTypeFacade.loadObjectType(otaBean.referenceObjectTypeId)
                // the reference object needs to be associated based on the provided attributes in a dot notation
                // we have to assume that this notation is intended to return a single value, so we'll search for it using IQL
                def refAttributeRemainder = attributeParts.drop(1).join('.')
                def refObjIql = /objectType = "$otaBean.name" and "$refAttributeRemainder" = "$value"/
                def refObjects = iqlFacade.findObjects(refObjIql)
                if (!refObjects) {
                    throw new CreateException(/Unable to find a reference $refObjectType.name object matching '"$refAttributeRemainder" = "$value"' for attribute $otaBean.name/)
                }
                if (refObjects.size() > 1) {
                    value = refObjects
                } else {
                    value = refObjects[0]
                }
            }
            def oaBean = object.createObjectAttributeBean(otaBean)
            log.debug "InsightUtils.createObject: $attributeName = $value"
            def oavBeans = getObjectValueBeans(oaBean, otaBean, value, allowObjectReferenceCreate, dispatchEvent)
            oaBean.setObjectAttributeValueBeans(oavBeans)
            oaBean
        }
        object.objectAttributeBeans = oaBeans
        def createdObject = objectFacade.storeObjectBean(object, getDispatchOption(dispatchEvent))
        resetUser(currentUser)
        return createdObject
    }

    static StatusTypeBean getStatusByName(ObjectSchemaBean schema, String statusName, ApplicationUser asUser = null) {
        def currentUser = setUser(asUser)
        StatusTypeBean status
        try {
            status = configureFacade.loadStatusTypeBean(schema.id, statusName)
            return status
        } catch (InsightException exception) {
            log.error "No status matching $statusName found in Insight Schema $schema.name"
            status = null
        }
        resetUser(currentUser)
        return status
    }

    private static List<ObjectAttributeValueBean> getObjectValueBeans(ObjectAttributeBean oaBean, ObjectTypeAttributeBean otaBean, Object value, Boolean allowObjectReferenceCreate, Boolean dispatchOption) {
        if (value instanceof List) {
            log.info "List detected: $value (${value.getClass()}"
            return value.collect { getObjectValueBean(oaBean, otaBean, it, allowObjectReferenceCreate, dispatchOption) }
        }
        return [getObjectValueBean(oaBean, otaBean, value, allowObjectReferenceCreate, dispatchOption)]
    }

    private static ObjectAttributeValueBean getObjectValueBean(ObjectAttributeBean oaBean, ObjectTypeAttributeBean otaBean, Object value, Boolean allowObjectReferenceCreate, Boolean dispatchOption) {
        log.info "Start: $value (${value.getClass()}"
        if (value instanceof List) {
            log.info "List detected: $value (${value.getClass()}"
            return value.collect { getObjectValueBeans(oaBean, otaBean, it, allowObjectReferenceCreate, dispatchOption) }

        }
        log.debug "checking other types: $value (${value.getClass()})"
        def oavBean = oaBean.createObjectAttributeValueBean()
        if (value instanceof ObjectBean) {
            log.debug "ObjectBean detected for: $value (${value.getClass()})"
            oavBean.setReferencedObjectBeanId(value.id)
        } else if (value instanceof ApplicationUser) {
            log.debug "ApplicationUser detected for: $value (${value.getClass()})"
            oavBean.setValue(otaBean, value.key)
        } else if (value instanceof Project) {
            log.debug "Project detected for: $value (${value.getClass()})"
            oavBean.setValue(otaBean, value.id.toInteger())
        } else if (value instanceof Map && otaBean.objectReference) {
            log.debug "Map detected for object reference: $value (${value.getClass()})"
            //in this case the value represents either an existing object or we might need to create a new one
            def findResults = findObjects(value)
            if (findResults) {
                return findResults.collect { getObjectValueBeans(oaBean, otaBean, it, allowObjectReferenceCreate, dispatchOption) }
                //oavBean.setReferencedObjectBeanId(findResults.first().id)
            } else {
                if (!allowObjectReferenceCreate) {
                    throw new CreateException("An attributeValueMap was specified as the value for $otaBean.name but allowObjectReferenceCreate==false. Specifiy the objectBean or the objectKey as the value or ensure the allowObjectReferenceCreate is set to true")
                }
                def objectType = objectTypeFacade.loadObjectType(otaBean.objectTypeId)
                def newRefObject = createObject(objectType, value, dispatchOption)
                if (newRefObject) {
                    oavBean.setReferencedObjectBeanId(newRefObject.id)
                }
            }
        } else if (value instanceof String && otaBean.objectReference) {
            log.debug "String detected for object reference: $value (${value.getClass()})"
            def refObject = getObjectByKey(value)
            if (refObject) {
                oavBean.setReferencedObjectBeanId(refObject.id)
            }
        } else {
            def ot = objectTypeFacade.loadObjectType(otaBean.objectTypeId)
            log.debug "Nothing detected for $otaBean.name:  $value (${value.getClass()}) AttributeType: $otaBean.type-$otaBean.defaultType ($ot)"
            oavBean.setValue(otaBean, value)
        }
        oavBean
    }

    private static Map<String, Object> validateAttributes(ObjectTypeBean objectType, List attributeList, attributeValidationResults = [:]) {
        def objectTypeAttributes = objectTypeAttributeFacade.findObjectTypeAttributeBeans(objectType.id)
        if (!attributeValidationResults) {
            attributeValidationResults = [isValid: true, messages: [], invalidAttributeNames: []]
        }
        attributeList.each { String attributeName ->
            def attributeParts = attributeName.tokenize('.')
            if (!objectTypeAttributes*.name.contains(attributeParts[0])) {
                attributeValidationResults.isValid = false
                attributeValidationResults.invalidAttributeNames << attributeName
                attributeValidationResults.messages << "$attributeName is not valid for $objectType.name"
            }
            if (attributeParts.size() > 1) {
                def refAttr = objectTypeAttributes.find { it.name == attributeParts[0] }
                if (!refAttr.objectReference) {
                    attributeValidationResults.isValid = false
                    attributeValidationResults.invalidAttributeNames << attributeParts[0]
                    attributeValidationResults.messages << "${attributeParts[0]} is not valid refernce attribute and can't specify sub-attributes: ${attributeParts.drop(1)}"
                }
                def refObjectType = objectTypeFacade.loadObjectType(refAttr.referenceObjectTypeId)
                attributeValidationResults = validateAttributes(refObjectType, attributeParts.drop(1), attributeValidationResults)
            }
        }
        attributeValidationResults
    }
}
