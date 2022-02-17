package com.domain1.aem

import com.atlassian.jira.component.ComponentAccessor

class AEMJiraPropertyManager {
    

static Map<String,String> baseurl = [
    "https://log.domain1eu.com":"PROD",
    "https://staging-log.domain1eu.com":"STG",
    "https://dev-log.domain1eu.com":"DEV"
]
    
static Map<String,String> aemURL = [
    "PRODAemApUrl":"https://org-p6-ap-author.domain2.com",
	"PRODAemUsUrl":"https://org-p6-us-author.domain2.com",
	"PRODAemEuUrl":"https://org-p6-eu-author.domain2.com",
	"STGAemApUrl":"https://s6-author.domain2.com",
	"STGAemUsUrl":"https://s6-author.domain2.com",
	"STGAemEuUrl":"https://s6-author.domain2.com",
    "DEVAemApUrl":"http://d6-aem.domain2.com:4201",
	"DEVAemUsUrl":"http://d6-aem.domain2.com:4201",
	"DEVAemEuUrl":"http://d6-aem.domain2.com:4201"
    ]

    
static Map<String,String> accountInfo = [
	"aemSysAcc":"jirauser",
	"aemSysPwd":"jirauser@",
	"jiraSysAcc":"aemconnect@domain1eu.com",
	"jiraSysPwd":"coviD20!"
    ]

   
static Map<String,String> sites = [
	"AU":"AemApUrl",
	"CN":"AemApUrl",
	"ID":"AemApUrl",
	"IN":"AemApUrl",
	"MY":"AemApUrl",
	"NZ":"AemApUrl",
	"PH":"AemApUrl",
	"RU":"AemApUrl",
	"SG":"AemApUrl",
	"TH":"AemApUrl",
	"TW":"AemApUrl",
	"VN":"AemApUrl",
	"MM":"AemApUrl",
	"HK":"AemApUrl",
	"HK_EN":"AemApUrl",
	"AFRICA_EN":"AemApUrl",
	"AFRICA_FR":"AemApUrl",
	"AFRICA_PT":"AemApUrl",
	"ZA":"AemApUrl",
	"UA":"AemApUrl",
	"KZ_RU":"AemApUrl",
	"KZ_KZ":"AemApUrl",
	"UZ_UZ":"AemApUrl",
	"UZ_RU":"AemApUrl",
	"AT":"AemEuUrl",
	"BE":"AemEuUrl",
	"BE_FR":"AemEuUrl",
	"CZ":"AemEuUrl",
	"DE":"AemEuUrl",
	"DK":"AemEuUrl",
	"EE":"AemEuUrl",
	"ES":"AemEuUrl",
	"FI":"AemEuUrl",
	"FR":"AemEuUrl",
	"IE":"AemEuUrl",
	"IT":"AemEuUrl",
	"LT":"AemEuUrl",
	"LV":"AemEuUrl",
	"NL":"AemEuUrl",
	"NO":"AemEuUrl",
	"PL":"AemEuUrl",
	"PT":"AemEuUrl",
	"RO":"AemEuUrl",
	"SE":"AemEuUrl",
	"SK":"AemEuUrl",
	"UK":"AemEuUrl",
	"HU":"AemEuUrl",
	"GR":"AemEuUrl",
	"BG":"AemEuUrl",
	"CH":"AemEuUrl",
	"CH_FR":"AemEuUrl",
	"RS":"AemEuUrl",
	"HR":"AemEuUrl",
	"SI":"AemEuUrl",
	"AL":"AemEuUrl",
	"MK":"AemEuUrl",
	"BA":"AemEuUrl",
	"AR":"AemUsUrl",
	"BR":"AemUsUrl",
	"CA":"AemUsUrl",
	"CA_FR":"AemUsUrl",
	"CL":"AemUsUrl",
	"CO":"AemUsUrl",
	"LATIN":"AemUsUrl",
	"MX":"AemUsUrl",
	"PE":"AemUsUrl",
	"PY":"AemUsUrl",
	"US":"AemUsUrl",
	"LATIN_EN":"AemUsUrl",
	"UY":"AemUsUrl"
    ]    
    
    //String baseURL
    //String SYSTEM

    
    //public AEMJiraPropertyManager() {
        //baseURL = ComponentAccessor.getApplicationProperties().getString("jira.baseurl")
        //SYSTEM = baseurl.get(baseURL)
    //}
    
    public static getSystem(String baseURL) {
        return baseurl.get(baseURL)
    }
    
    public static String getAemSysAcc() {
        return accountInfo.get("aemSysAcc")
    }

    public static String getAemSysPwd() {
        return accountInfo.get("aemSysPwd")
    }
    
    public static String getJiraSysAcc() {
        return accountInfo.get("jiraSysAcc")
    }

    public static String getJiraSysPwd() {
        return accountInfo.get("jiraSysPwd")
    }
    
    public static String getSiteURL(String site) {
        return sites.get(site)
    }
    
    public static String getAemURL(String site) {
        return aemURL.get(getSystem(ComponentAccessor.getApplicationProperties().getString("jira.baseurl")).toString() + getSiteURL(site))
    }
    
    public String toString() {
        return "baseURL: " + ComponentAccessor.getApplicationProperties().getString("jira.baseurl") + "\n" -
            "SYSTEM: " + getSystem(ComponentAccessor.getApplicationProperties().getString("jira.baseurl")) + "\n"
    }


}
