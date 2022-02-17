import com.atlassian.jira.component.ComponentAccessor
import org.apache.log4j.Logger
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.crowd.embedded.api.Group

def logit = Logger.getLogger("com.domain1.eu.logging")

def groups = [
"Corporate Support Team",
"jira-administrators",
"Cheil Ukraine Campaign - Team",
"Cheil Ukraine Campaign - Admin",
"Test And Learn Team",
"Steer Group",
"DMM Cheil",
"POR Team",
"Optimisation Solution Team",
"Operations Team",
"Operations Admins",
"Merchandising Solution Team",
"IM Solution Team",
"DataDrivenUX Team",
"CIP Samsung",
"CIP Cheil",
"CIP Jarmany",
"CIP BBH",
"CIP Administrators",
"Cheil Retainer UK",
"EO Samsung",
"Cheil IT UK",
"DMM Cheil Agents",
"CE & Support Solution Team",
"BAU Solution Team",
"SSO2 All Access",
"SSO2 SEUK Cheil",
"SSO2 SESA Cheil",
"SSO2 SEROM Cheil",
"SSO2 SENA Cheil",
"SSO2 SEI Cheil",
"SSO2 SEG Cheil",
"SSO2 SEFR Cheil",
"SSO2 SECZ Cheil",
"SSO2 SEBN Cheil",
"SSO2 EO Cheil",
"WSC Team",
"SEUK Cheil Retainer",
"SEUK Cheil",
"EU Cheil PMO",
"SEUK Samsung",
"CSSO Team",
"ITC SDSE",
"ITC Analytics",
"EO Cheil Studio",
"EO Cheil Central",
"SESG Cheil",
"SESG Samsung",
"seas-dmm",
"SESA Cheil",
"SESA Samsung",
"SEROM Cheil",
"SEROM Samsung",
"SEPOL Cheil",
"SEPOL Samsung",
"SEP Cheil",
"SEP Samsung",
"SENA Cheil",
"SENA Samsung",
"SEI Cheil",
"SEI Samsung",
"SEH Cheil",
"SEH Samsung",
"SEGR Cheil",
"SEGR Samsung",
"SEG Cheil",
"SEG Samsung",
"SEF Cheil",
"SEF Samsung",
"SECZ Cheil",
"SECZ Samsung",
"SEBN Cheil",
"SEBN Samsung",
"SEB Cheil",
"SEB Samsung",
"SEAG Cheil",
"SEAG Samsung",
"sead-dmm",
"SEAD Cheil",
"SEAD Samsung",
"EO Cheil Production Centre",
"EO Samsung Production Centre",
"Samsung Members",
"Shopitect Team",
"RXPPOL Cheil",
"RXPCZ Cheil",
"RXPROM Cheil",
"RXPES Cheil",
"RXPFR Cheil",
"RXPIT Cheil",
"RXPBN Cheil",
"RXPDE Cheil",
"RXPUK Cheil",
"RXPNA Cheil",
"BMP Cheil Poland",
"BMP Team",
"Tech Centre Scrum Poland PM",
"Tech Centre Scrum Poland UX",
"Tech Centre Scrum Poland DEV",
"TX1 Team",
"Cheil PL Digital",
"SMP Team",
"SEPOL WG",
"SMP Clients",
"SEPOL WG Administrators",
"SEO Poland Team",
"Athena Team",
"POS Team",
"Samsung SMNA",
"Cheil SMNA",
"SEO Team",
"CRM Team",
"HQ Support Team",
"Welkin & Meraki Team",
"Cheil FR SAMFY",
"SFOT Cheil",
"Sales Force",
"LH Team",
"LH Administrators",
"SEF Marketing Retail",
"DIA Team",
"Cheil DE MFF",
"FEIDE Team",
"COMP_YDB1",
"Cheil IT DE",
"Cheil eCommerce Support - Admin",
"Cheil Canada Optimisation Team",
"CCAT General Team",
"CCAT Accounts Team"
    ]

def groupManager = ComponentAccessor.groupManager
groups.each(){
Group groupIn = groupManager.getGroup(it)
if (groupIn) {
	//logit.info("source : " + groupIn.name)
		Collection<ApplicationUser> inUsers = groupManager.getUsersInGroup(groupIn)
    	inUsers.each(){
            if (it.getDirectoryId() == 1 || it.isActive() == false) {
               // ignore
            } else {
                def license = groupManager.isUserInGroup(it, "jira-software-users")
                if (license) {
        			logit.info( groupIn.getName()  + "," +it.displayName)
            	}
            }
    	}
	}
}