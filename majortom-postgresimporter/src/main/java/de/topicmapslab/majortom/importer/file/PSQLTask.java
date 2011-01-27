/**
 * 
 */
package de.topicmapslab.majortom.importer.file;

/**
 * Utility class containing a method to start the PSQL Task imports large SQL files to a database.
 * 
 * @author Sven Krosse
 * 
 */
public class PSQLTask {

	/**
	 * the slash token
	 */
	private static final String SLASH = "/";
	/**
	 * the PSQL command
	 */
	private static final String PSQL = "psql";
	/**
	 * constant for name of operation system
	 */
	private static final String OS_NAME = "os.name";
	/**
	 * constant for windows
	 */
	private static final String WINDOWS = "windows";
	/**
	 * constant for pipe in argument
	 */
	private static final String ARG_PIPE = "<";
	/**
	 * constant for quiet argument
	 */
	private static final String ARG_QUIET = "-q";
	/**
	 * constant for run command argument
	 */
	private static final String COMMAND = "/C";
	/**
	 * constant for windows command
	 */
	private static final String CMD_EXE = "cmd.exe";
	/**
	 * constant for PostGreSQL password variable
	 */
	private static final String PGPASSWORD = "PGPASSWORD";

	/**
	 * Method starts the psql task of the PostGreSql database manager to import the given large SQL file.
	 * 
	 * @param taskPath
	 *            the path to psql task or <code>null</code> if the path is defined in search path
	 * @param filename
	 *            the filename to import ( absolute path )
	 * @param database
	 *            the database name to import to
	 * @param user
	 *            the user
	 * @param password
	 *            the password
	 * @throws Exception
	 *             thrown if anything fails or the task terminates before finishing
	 */
	public static final void runPSQL(final String taskPath, final String filename, final String database,
			final String user, final String password) throws Exception {
		String taskPath_ = "";
		if (taskPath != null && !taskPath.isEmpty()) {
			taskPath_ = taskPath;
			if (!taskPath_.endsWith(SLASH)) {
				taskPath_ += SLASH;
			}
		}
		taskPath_ += PSQL;

		String osName = System.getProperty(OS_NAME);

		ProcessBuilder pb = null;
		if (osName.toLowerCase().startsWith(WINDOWS)) {
			pb = new ProcessBuilder(CMD_EXE, COMMAND, taskPath_, ARG_QUIET, database, user, ARG_PIPE, filename);
		} else {
			pb = new ProcessBuilder(COMMAND, taskPath_, ARG_QUIET, database, user, ARG_PIPE, filename);
		}

		pb.environment().put(PGPASSWORD, password);
		Process p = pb.start();
		p.waitFor();
	}
}
