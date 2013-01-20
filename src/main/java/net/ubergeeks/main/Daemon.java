package net.ubergeeks.main;

import java.awt.SplashScreen;
import java.awt.SystemTray;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import javax.mail.MessagingException;

import net.ubergeeks.gui.ConsolePlus;
import net.ubergeeks.gui.MailTrayIcon;

/**
 * Java service with GUI tray icon that runs in the background and redirects
 * mail from one email account (via IMAP currently) to any other. The email
 * arrive untouched they are exactly like the originals, senders, CC,
 * attachments, everything!
 * 
 * @author Scott Rahner
 */
public class Daemon {

	public static final Timer pollingTimer = new Timer();

	public static final Semaphore pollingSemaphore = new Semaphore(1);

	public static void main(String args[]) {
	try{
			ResourceBundle rb = ResourceBundle.getBundle("mail");
			int pollFreq = Integer.parseInt(rb.getString("pollFreq"));
			
			if (SystemTray.isSupported()) {
				createSystemTrayIcon();
			}
			//Setup polling
			pollingTimer.schedule(new TimerTask() {
				public void run() {
					try {
						IMAPFolder.getInstance().getMessageCount();
						ConsolePlus.getInstance().write("Polling Inbox ...");
					} catch (MessagingException e) {
						ConsolePlus.getInstance().error("Polling failed: ");
						if (e.getNextException() != null)
							ConsolePlus.getInstance().error(e.getNextException().toString());
						else
							ConsolePlus.getInstance().error(e.toString());
					}
				}
			}, 0, pollFreq * 60000);
	}catch(Exception e){
		if (SplashScreen.getSplashScreen() != null)
			SplashScreen.getSplashScreen().close();
		System.err.println(e.toString());
	}	
}

	public static void createSystemTrayIcon() {
		SystemTray tray = SystemTray.getSystemTray();
		try {
			MailTrayIcon trayIcon = new MailTrayIcon();
			tray.add(trayIcon);
		} catch (Exception e) {
			System.err.println("Tray Image could not be added.");
			System.exit(1);
		}
	}
}
