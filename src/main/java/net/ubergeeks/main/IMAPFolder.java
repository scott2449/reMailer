package net.ubergeeks.main;

import java.util.Properties;
import java.util.ResourceBundle;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import net.ubergeeks.gui.ConsolePlus;

/**
 *
 * Self contain IMAP folder creation. Pulled out mainly for logical separation and getInstance will recover
 * closed folders. In the future will need to write an interface to encapsulate pop3 as well and maybe a factory to
 * return the appropriate folder as per prop file. Count Listener can read from generic folder and redirect SMTP
 *
 * @author Scott
 */
public class IMAPFolder {

    private static Session session;
    private static Store store;
    private static Folder folder = null;
    private static int TTL = 10; //Integer time to live for the IMAP folder before regenerating.
    private static int generation = 1;
    
    private static final ResourceBundle rb = ResourceBundle.getBundle("mail");

    private static final String imapFolderName = rb.getString("imap.folder");
    private static final String imapUser = rb.getString("imap.user");
    private static final String imapPass = rb.getString("imap.pass");
    private static final String imapServer = rb.getString("imap.server");
    private static final String imapPort = rb.getString("imap.port");
    private static final boolean imapIsSSL = Boolean.parseBoolean(rb.getString("imap.isSSL"));

    private IMAPFolder(){

		try {
			// open IMAP connection		
			Properties props = System.getProperties();
			props.put("mail.store.protocol", "imap");
			props.put("mail.imap.host", imapServer);
			props.put("mail.imap.port", imapPort);
			props.put("mail.imap.connectiontimeout", "5000");
			props.put("mail.imap.timeout", "5000");

			if (imapIsSSL) {
				java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
				props.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
				props.put("mail.imap.socketFactory.fallback", "true");
			}

			ConsolePlus.getInstance().write("IMAP Store/Folder is building...");
			
			session = Session.getInstance(props, null);
			store = session.getStore("imap");

			store.connect(imapServer, imapUser, imapPass);

			try{
				folder.getMessageCount();
			}catch (Exception e){}
			
			generation = 1;
			folder = store.getFolder(imapFolderName);
			folder.open(Folder.READ_ONLY);
			// reMessageCountListener will gather all new messages and SMTP them
			// to declared mailbox
			folder.addMessageCountListener(new reMessageCountListener());
        } catch (MessagingException e) {
			if (e.getNextException() != null)
				ConsolePlus.getInstance().error(e.getNextException().toString());
			else
				ConsolePlus.getInstance().error(e.toString());
			ConsolePlus.getInstance().error("\nPlease update the properties file (IMAP) with the correct information and restart.\n" + "If all information appears correct, one of your email servers may be unavailable.");
		}
    }

    public static Folder getInstance(){
        if( generation > TTL || folder == null || !folder.isOpen() ){
        	new IMAPFolder();
         }
        generation++;
        return IMAPFolder.folder;
    }

}
