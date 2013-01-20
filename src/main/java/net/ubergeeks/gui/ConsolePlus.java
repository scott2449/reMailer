package net.ubergeeks.gui;

import java.awt.BorderLayout;
import java.awt.SplashScreen;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.vng.gui.OutputPane;
import org.vng.gui.OutputPane.ToolBarPosition;

public class ConsolePlus extends JFrame {

	private static final long serialVersionUID = -4007320832435919698L;
	private static ConsolePlus console;
	private static OutputPane pane;

	private ConsolePlus() {
		super("Mail Daemon Console");
		this.getContentPane().setLayout(new BorderLayout());

		pane = new OutputPane(this, 500, ToolBarPosition.SOUTH, new OutputPane.TimePrompt("hh:mm:ss"), "bold");
		pane.getOutputTextPane().setEditable(false);
		try {
			this.setIconImage(ImageIO.read(ConsolePlus.class.getResource("/net/ubergeeks/images/tray.gif")));
		} catch (IOException e) {
			ConsolePlus.getInstance().write(e.toString());
		}
		this.getContentPane().add(pane, BorderLayout.CENTER);
		this.setSize(600, 400);
		
		if (SplashScreen.getSplashScreen() != null)
			SplashScreen.getSplashScreen().close();
	}

	public void write(final String text) {
		pane.print(text);
	}

	public void error(final String text) {
		pane.printError(text);
	}

	public static ConsolePlus getInstance() {
		if (console == null){
			console = new ConsolePlus();
		}

		return console;
	}
}
