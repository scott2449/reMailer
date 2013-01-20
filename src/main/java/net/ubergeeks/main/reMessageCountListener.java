package net.ubergeeks.main;

import java.util.Properties;
import java.util.ResourceBundle;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessageRemovedException;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import javax.mail.internet.InternetAddress;

import net.ubergeeks.gui.ConsolePlus;

/**
 * Custom MessageCountListner that will iterate add messages and will redirect
 * them to the desired account via SMTP
 * 
 * @author Scott Rahner
 * @see MessageCountListner
 * 
 */

public class reMessageCountListener implements MessageCountListener {

	private static final ResourceBundle rb = ResourceBundle.getBundle("mail");
	private static final String smtpServer = rb.getString("smtp.server");
	private static final String smtpTLS = rb.getString("smtp.isTLS");
	private static final String smtpUser = rb.getString("smtp.user");
	private static final String smtpPass = rb.getString("smtp.pass");
	private static final String smtpPort = rb.getString("smtp.port");
	private static final String smtpRCPT = rb.getString("smtp.rcptto");
	private static final String smtpAuth = rb.getString("smtp.auth");
	private static Properties propsOut;

	public reMessageCountListener() {
		propsOut = new Properties();
		propsOut.put("mail.transport.protocol", "smtp");
		propsOut.put("mail.smtp.starttls.enable", smtpTLS);
		propsOut.put("mail.smtp.host", smtpServer);
		propsOut.put("mail.smtp.port", smtpPort);
		propsOut.put("mail.smtp.auth", smtpAuth);
		propsOut.put("mail.smtp.connectiontimeout", "5000");
		propsOut.put("mail.smtp.timeout", "5000");
	}

	public void messagesAdded(MessageCountEvent event){

		try {
			Daemon.pollingSemaphore.acquire();
		} catch (InterruptedException e1) {
			ConsolePlus.getInstance().write("Semaphore Exception");
		}
		try {
			Message[] messages = event.getMessages();

			Session mailSession = Session.getInstance(propsOut);
			mailSession.setDebug(true);
			Transport transport = mailSession.getTransport();
			transport.connect(smtpServer, Integer.parseInt(smtpPort), smtpUser, smtpPass);

			Address[] meList = new Address[1];
			meList[0] = new InternetAddress(smtpRCPT);

			ConsolePlus.getInstance().write("Redirecting " + messages.length + " messages...");
			for (int i = messages.length - 1; i >= 0; i--) {
				try {
					transport.sendMessage(messages[i], meList);
				} catch (MessageRemovedException mre) {
					//messages have been removed, this is OK.
				}
			}
			transport.close();

		} catch (MessagingException e) {
			if (e.getNextException() != null)
				ConsolePlus.getInstance().write(e.getNextException().toString());
			else
				ConsolePlus.getInstance().write(e.toString());
			ConsolePlus.getInstance().write("\nPlease update the properties file (SMTP) with the correct information and restart.\n" + "If all information appears correct, one of your email servers may be unavailable.\n");
		}

		Daemon.pollingSemaphore.release();

	}

	public void messagesRemoved(javax.mail.event.MessageCountEvent event) {
	}

}
