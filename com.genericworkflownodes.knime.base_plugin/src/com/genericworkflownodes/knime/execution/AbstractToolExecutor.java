/**
 * Copyright (c) 2012, Stephan Aiche.
 *
 * This file is part of GenericKnimeNodes.
 * 
 * GenericKnimeNodes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.genericworkflownodes.knime.execution;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.ballproject.knime.GenericNodesPlugin;
import org.ballproject.knime.base.config.INodeConfiguration;
import org.ballproject.knime.base.config.NodeConfigurationStore;
import org.ballproject.knime.base.external.ExtToolDB;
import org.ballproject.knime.base.external.ExtToolDB.ExternalTool;
import org.ballproject.knime.base.preferences.GKNPreferenceInitializer;
import org.ballproject.knime.base.util.Helper;
import org.eclipse.jface.preference.IPreferenceStore;
import org.knime.core.node.NodeLogger;

import com.genericworkflownodes.knime.config.IPluginConfiguration;

/**
 * The AbstractToolExecutor handles the basic tasks associated with the
 * execution of a tool on the command line.
 * 
 * @author aiche
 */
public abstract class AbstractToolExecutor implements IToolExecutor {
	protected static final NodeLogger logger = NodeLogger
			.getLogger(AbstractToolExecutor.class);

	private File workingDirectory;
	private Map<String, String> environmentVariables;
	private int returnCode;
	private String toolOutput;
	private Process process;

	private File executable;

	private static String PATHSEP = System.getProperty("path.separator");

	public AbstractToolExecutor() {
		environmentVariables = new TreeMap<String, String>();
		returnCode = -1;
		executable = null;
		workingDirectory = null;
	}

	/**
	 * Sets the working directory of the process. If the directory does not
	 * exist or the @p path does not point to a directory (but a file), an
	 * exception will be thrown.
	 * 
	 * @param directory
	 *            The new working directory.
	 * @throws Exception
	 *             If the path does not exist or points to a file (and not a
	 *             directory).
	 */
	public void setWorkingDirectory(File directory) throws Exception {
		workingDirectory = directory;
		if (!workingDirectory.isDirectory() || !workingDirectory.exists()) {
			throw new Exception(directory + " is not a directory!");
		}
	}

	/**
	 * Adds the environment variables included in @p newEnvironmentVariables to
	 * the environment variables of the tool.
	 * 
	 * @note If the environment variable is a path (e.g., PATH or
	 *       LD_LIBRARY_PATH) the environment variable will be extended and not
	 *       overwritten (i.e.,
	 *       LD_LIBRARY_PATH=<specified-value>:$LD_LIBRARY_PATH).
	 * 
	 * @note Existing values with equal keys will be overwritten.
	 * 
	 * @param newEnvironmentVariables
	 *            The environment variables that will be added.
	 */
	private void addEnvironmentVariables(
			Map<String, String> newEnvironmentVariables) {
		environmentVariables.putAll(newEnvironmentVariables);
	}

	/**
	 * Returns the return value of the process. If the tool didn't not run or is
	 * not finished it is set to -1.
	 * 
	 * @return
	 */
	public int getReturnCode() {
		return returnCode;
	}

	/**
	 * Returns the output generated by the tool as single string.
	 * 
	 * @return The ouput of the tool.
	 */
	public String getToolOutput() {
		return toolOutput;
	}

	/**
	 * Kills the running process.
	 */
	public void kill() {
		process.destroy();
	}

	/**
	 * Returns the working directory.
	 * 
	 * @return
	 */
	public File getWorkingDirectory() {
		return workingDirectory;
	}

	/**
	 * 
	 * @return
	 */
	public File getExecutable() {
		return this.executable;
	}

	/**
	 * Prepare the specific command line call.
	 * 
	 * @return
	 */
	protected abstract List<String> prepareCall() throws Exception;

	/**
	 * The execute method used by derived classes to execute their command.
	 * 
	 * @param command
	 *            The command line of the tool as list of strings.
	 * @return The return value of the executed process.
	 * @throws Exception
	 */
	public int execute() throws Exception {

		List<String> command = prepareCall();

		try {
			// build process
			ProcessBuilder builder = new ProcessBuilder(command);

			setupProcessEnvironment(builder);

			if (workingDirectory != null) {
				builder.directory(workingDirectory);
			}

			builder.redirectErrorStream(true);

			// execute
			process = builder.start();

			// fetch output data (stdio+stderr)
			InputStreamReader isr = new InputStreamReader(
					process.getInputStream());
			BufferedReader br = new BufferedReader(isr);

			String line = null;
			StringBuffer out = new StringBuffer();

			while ((line = br.readLine()) != null) {
				out.append(line + System.getProperty("line.separator"));
			}

			toolOutput = out.toString();

			// fetch return code
			returnCode = process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return returnCode;
	}

	/**
	 * @param builder
	 */
	private void setupProcessEnvironment(ProcessBuilder builder) {
		for (String key : environmentVariables.keySet()) {

			String value = environmentVariables.get(key);

			// extend paths instead of replacing them, if it was already set by
			// the system and was requested by the value, e.g.,
			// PATH=/usr/bin:$PATH .. will be extended by the system variable
			// PATH=/usr/bin .. not
			if (value.contains("$" + key) && System.getenv(key) != null) {
				value = value + PATHSEP + System.getenv(key);
			}

			builder.environment().put(key, value);
		}
	}

	/**
	 * Internal initialization method called after global initialization.
	 * 
	 * @param nodeConfiguration
	 * @param pluginConfiguration
	 */
	protected abstract void localPrepareExecution(
			INodeConfiguration nodeConfiguration,
			NodeConfigurationStore configStore,
			IPluginConfiguration pluginConfiguration) throws Exception;

	/**
	 * Initialization method of the executor.
	 * 
	 * @param nodeConfiguration
	 * @param pluginConfiguration
	 */
	public void prepareExecution(INodeConfiguration nodeConfiguration,
			NodeConfigurationStore configStore,
			IPluginConfiguration pluginConfiguration) throws Exception {
		findExecutable(nodeConfiguration, pluginConfiguration);
		addEnvironmentVariables(pluginConfiguration.getEnvironmentVariables());
		updatePATH();
		localPrepareExecution(nodeConfiguration, configStore,
				pluginConfiguration);
	}

	/**
	 * TODO: Re-think this concept. AbstractToolExecutor shouldn't need to know
	 * something about this.
	 */
	private void updatePATH() {
		IPreferenceStore store = GenericNodesPlugin.getDefault()
				.getPreferenceStore();
		String PATH_extension = store
				.getString(GKNPreferenceInitializer.PREF_PATHES);
		if (environmentVariables.containsKey("PATH")) {
			PATH_extension = PATH_extension + PATHSEP
					+ environmentVariables.get("PATH");
		}
		environmentVariables.put("PATH", PATH_extension);
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	private void findExecutable(INodeConfiguration nodeConfiguration,
			IPluginConfiguration pluginConfiguration) throws Exception {
		executable = ExtToolDB.getInstance().getToolPath(
				new ExternalTool(pluginConfiguration.getPluginName(),
						nodeConfiguration.getName()));

		boolean useShipped = (executable == null || !executable.exists());

		if (useShipped) {
			logger.info("The path of the binary to invoke \"" + executable
					+ "\" could not be found.\n"
					+ "Using shipped binaries instead.");
			executable = Helper.getExecutableName(
					new File(pluginConfiguration.getBinariesPath(), "bin"),
					nodeConfiguration.getName());
		}

		if (executable == null) {
			throw new Exception("Neither externally configured nor shipped "
					+ "binaries exist for this node. Aborting execution.");
		}
	}
}
