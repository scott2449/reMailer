package net.ubergeeks.gui;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import net.ubergeeks.main.Daemon;

/**
 * 
 * Custom TrayIcon that assign handlers and pop-up specific to application
 * 
 * @see TrayIcon
 * @author Scott
 */
public class MailTrayIcon extends TrayIcon {

	public MailTrayIcon() throws IOException {
		super(ImageIO.read(MailTrayIcon.class.getResource("/net/ubergeeks/images/tray.gif")));

		MenuItem show = new MenuItem("Show Console");
		MenuItem exit = new MenuItem("Exit");

		PopupMenu popup = new PopupMenu();
		popup.add(show);
		popup.add(exit);

		this.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConsolePlus.getInstance().setState(JFrame.NORMAL);
				ConsolePlus.getInstance().setVisible(true);
			}
		});

		show.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConsolePlus.getInstance().setVisible(true);
			}
		});

		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try { // killing timer waiting for current poll to finish
					Daemon.pollingTimer.cancel();
					Daemon.pollingSemaphore.acquire();
				} catch (Exception ex) {}
				System.exit(0);
			}
		});

		this.setPopupMenu(popup);
		this.setToolTip("reMailer v1.09.1001 daemon is running...");
	}
}