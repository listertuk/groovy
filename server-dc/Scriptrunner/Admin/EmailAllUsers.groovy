import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.security.groups.GroupManager
import com.atlassian.jira.user.ApplicationUser
import groovy.xml.MarkupBuilder
import com.atlassian.jira.mail.Email
import com.atlassian.jira.mail.settings.MailSettings
import com.atlassian.mail.MailException
 
import org.apache.log4j.Logger
 
// this logger needs defining in log4j.properties 
Logger logit = Logger.getLogger("com.blah.logging")
 

def userManager = ComponentAccessor.getUserManager()
String greeting = "Hello "
 
GroupManager groupManager = ComponentAccessor.getGroupManager()
Collection<String> grpUsers =   groupManager.getUserNamesInGroup("jira-software-users")
logit.info("grpUsers " + grpUsers.size())
Collection<String> grpUsers2 =   groupManager.getUserNamesInGroup("confluence-users")
// merge to unique user list
grpUsers2.each() {
    if (!grpUsers.contains(it)) {
        grpUsers.add(it)
    }
}
 
Integer count = 0
 
grpUsers.each() {String u ->
    ApplicationUser it = userManager.getUserByName(u)
    if (it && it.active) {
 
         logit.info (it.emailAddress  + " " + it.active)
        count++
        def title = "[${it.getDisplayName()}] Jira & Confluence cloud migration."
        String bodytext =  buildHTMLBody( it, title)
        sendEmail(it.getEmailAddress(), title, bodytext, logit)
    }
   
    }
    logit.info( "Sent "  + count )
 

String buildHTMLBody( ApplicationUser user,String title) {
    def writer = new StringWriter()
    def html = new MarkupBuilder(writer)
    def greeting =  "Hello " +  user.getDisplayName()
    html.html {
        head {
            title: title
        }
        body(id: "main") {
        p {
           mkp.yield greeting
        }
        p {
            mkp.yield "This email is to confirm we starting the migration of "
            strong "our.jira.com"
            mkp.yield " and "
            strong "our.confluence.com"
            mkp.yield " sites to Atlassian Cloud."
            br{}
            mkp.yield "Data transfers will begin sometime after 5pm. Please do not use Jira/Confluence after 4pm while we prepare extracts."
        }
     
        p {
            
            mkp.yield "The link to the new system login page on Monday will be"
        }
            a href: "https://ourjira.atlassian.net", "https://ourjira.atlassian.net"
           
        p {
           mkp.yield  "Kind Regards"
           br{}
           mkp.yield  "Tom Lister"
           br{}
           mkp.yield  "HQ IT"
        }
       
    }
}
    return (writer.toString())
}
   



String sendEmail(String emailAddr, String subject, String body, Logger logit) {
    // Stop emails being sent if the outgoing mail server gets disabled (useful if you start a script sending emails and need to stop it)
    def mailSettings = ComponentAccessor.getComponent(MailSettings)
    if (mailSettings?.send()?.disabled) {
        logit.error "Your outgoing mail server has been disabled"
        return "Your outgoing mail server has been disabled"
    }
 
    def mailServer = ComponentAccessor.mailServerManager.defaultSMTPMailServer
    if (!mailServer) {
        logit.error("Your mail server Object is Null, make sure to set the SMTP Mail Server Settings Correctly on your Server")
        return "Failed to Send Mail. No SMTP Mail Server Defined"
    }
 
    def email = new Email(emailAddr)
    email.setMimeType("text/html")
    email.setSubject(subject)
    email.setBody(body)
    try {
        mailServer.send(email)
        logit.info("Mail sent to " + emailAddr)
    } catch (MailException e) {
        logit.error("Send mail failed with error: ${e.message}")
    }
}
