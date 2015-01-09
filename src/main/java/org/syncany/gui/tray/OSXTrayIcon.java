package org.syncany.gui.tray;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import com.google.common.collect.Sets;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

/**
 * In contrast to see {@link org.syncany.gui.tray.DefaultTrayIcon}, this tray icon uses the mavericks+ notification
 * center to notify of changes. This is a more convienent way on OS X. The notifier's source code is avaiable at:
 *
 *
 * <p>Only supported by Mac OS X 10.9+</p>
 *
 * @author Christian Roth <christian.roth@port17.de>
 */

public class OSXTrayIcon extends DefaultTrayIcon {
	private static final Logger logger = Logger.getLogger(OSXTrayIcon.class.getSimpleName());

	private final static String TERMINAL_NOTIFIER_PACKAGE = "/org/syncany/gui/helper/osx-notifier.app.zip";
	private final static String TERMINAL_NOTIFIER_BINARY = "/Syncany.app/Contents/MacOS/Syncany";

	private File terminalNotifierExtractedBinary;
	private boolean useFallbackNotificationSystem;

	public OSXTrayIcon(Shell shell, TrayIconTheme theme) {
		super(shell, theme);

		extractHelperUtility();
		testHelperUtility();
	}

	@Override
	protected void displayNotification(final String subject, final String message) {
		if (useFallbackNotificationSystem) {
			super.displayNotification(subject, message);
			return;
		}

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				List<String> command = new ArrayList<>();
				command.add(terminalNotifierExtractedBinary.getAbsolutePath());
				command.add("-subject");
				command.add(subject);
				command.add("-message");
				command.add(message);
				command.add("-folder");
				command.add("");

				try {
					Runtime.getRuntime().exec(command.toArray(new String[command.size()]));
				} catch (IOException e) {
					throw new RuntimeException("Unable to notify using " + terminalNotifierExtractedBinary, e);
				}
			}
		});
	}

	private void extractHelperUtility() {
		try {
			logger.log(Level.INFO, "Extracting required helper tools...");

			// create a target for the app
			final File terminalNotifierExtracted = Files.createTempDirectory("syncany-osx-notifier").toFile();
			terminalNotifierExtracted.deleteOnExit();

			if (logger.isLoggable(Level.FINE)) {
				logger.log(Level.FINE, "Extracting syncany-notifier to {0}", new Object[]{terminalNotifierExtracted});
			}

			// extract the compressed notifier
			File temporaryZipFile = File.createTempFile("syncany-notifier", ".zip");
			FileUtils.copyInputStreamToFile(getClass().getResourceAsStream(TERMINAL_NOTIFIER_PACKAGE), temporaryZipFile);
			new ZipFile(temporaryZipFile).extractAll(terminalNotifierExtracted.toString());

			// make it executable
			terminalNotifierExtractedBinary = new File(terminalNotifierExtracted, TERMINAL_NOTIFIER_BINARY);
			Set<PosixFilePermission> perms = Sets.newHashSet(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE);
			Files.setPosixFilePermissions(terminalNotifierExtractedBinary.toPath(), perms);
			logger.log(Level.INFO, "Using {0} to deliver notifications", new Object[]{terminalNotifierExtractedBinary});

			temporaryZipFile.delete();

			logger.log(Level.INFO, "Done Extracting helper tools");
		}
		catch(NullPointerException | IOException | ZipException e) {
			logger.log(Level.SEVERE, "Unable to extract required helpers", e);
		}
	}

	private void testHelperUtility() {
		useFallbackNotificationSystem = !terminalNotifierExtractedBinary.exists() || !terminalNotifierExtractedBinary.canExecute();

		if (useFallbackNotificationSystem) {
			logger.log(Level.INFO, "Unable to notify using the native helper utility ({0}), using generic swt fallback", new Object[]{terminalNotifierExtractedBinary});
		}
	}

}
