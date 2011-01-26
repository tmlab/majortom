/**
 * 
 */
package de.topicmapslab.majortom.importer.file;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Sven Krosse
 * 
 */
public class ImportToDatabaseTask {

	public static final String COMMAND = "{0}psql -q {1} {2} < {3}";

	// public static final String COMMAND = "{0}psql";

	public static final void importSql(final String taskPath, final String filename, final String database,
			final String user, final String password) throws Exception {
		String taskPath_ = taskPath;
		if (!taskPath_.isEmpty() && !taskPath_.endsWith("/")) {
			taskPath_ += "/";
		}

		String osName = System.getProperty("os.name");

		ProcessBuilder pb = null;
		if (osName.toLowerCase().startsWith("windows")) {
			pb = new ProcessBuilder("cmd.exe", taskPath_, "-q", database, user, "< " + filename);
		} else {
			pb = new ProcessBuilder(taskPath_, "-q", database, user, "< " + filename);
		}

		pb.environment().put("PGPASSWORD", password);
		Process p = pb.start();
		p.waitFor();

		// final String cmd = MessageFormat.format(COMMAND, taskPath_, database, user, filename);
		// // ProcessBuilder builder = new ProcessBuilder(cmd, "-q","-w", database, user, "< " + filename);
		// // builder.environment().put("PGPASSWORD", password);
		// // Process p = builder.start();
		// // System.setProperty("PGWASSWORD", password);
		// // Runtime r = Runtime.getRuntime();
		// // Process process = r.exec("cmd.exe", null);// , new String[] { "PGPASSWORD=" + password });
		// ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe").redirectErrorStream(true);
		// Process process = processBuilder.start();
		// createAndStartProcessOutputHandlerThread(process);
		// Thread processInputHandlerThread = createAndStartProcessInputHandlerThread(cmd, password, process);
		// process.waitFor();
		// processInputHandlerThread.interrupt();
		// // OutputStream os = p.getOutputStream();
		// // InputStream is = p.getInputStream();
		// // InputStream es = p.getErrorStream();
		//
		// // os.write(password.getBytes("UTF-8"));
		// // os.flush();
		// // os.close();
		// System.out.println("---------------");

		// String line;
		// // clean up if any output in stdout
		// BufferedReader brCleanUp = new BufferedReader(new InputStreamReader(is));
		// while ((line = brCleanUp.readLine()) != null) {
		// System.out.println("[Stdout] " + line);
		// }
		// brCleanUp.close();
		// System.out.println("---------------");
		//
		// // clean up if any output in stderr
		// brCleanUp = new BufferedReader(new InputStreamReader(es));
		// while ((line = brCleanUp.readLine()) != null) {
		// System.err.println("[Stderr] " + line);
		// }
		// brCleanUp.close();
		// System.out.println("---------------");
	}

	private static Thread createAndStartProcessInputHandlerThread(final String cmd, final String password,
			final Process process) {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				PrintWriter printWriter = new PrintWriter(process.getOutputStream());
				try {
					printWriter.println("set PGPASSWORD=" + password);
					printWriter.println(cmd);
					printWriter.println(password);
					printWriter.flush();
					Thread.sleep(50);
				} catch (InterruptedException interruptedException) {
					// ignore Process shutdown
				}
			}
		});
		thread.start();
		return thread;
	}

	private static Thread createAndStartProcessOutputHandlerThread(final Process process) {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					char c = Character.MAX_VALUE;
					while ((c = (char) process.getInputStream().read()) != Character.MAX_VALUE) {
						System.out.print(c);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
		return thread;
	}
}
