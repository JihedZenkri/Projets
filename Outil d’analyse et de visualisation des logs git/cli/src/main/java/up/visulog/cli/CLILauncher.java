package up.visulog.cli;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.Scanner;
import java.util.ArrayList;

import up.visulog.analyzer.Analyzer;
import up.visulog.config.Configuration;
import up.visulog.config.PluginConfig;

public class CLILauncher {
	static boolean saved = false;

	static void openInBrowser(final String html, String fileName) {
		File htmlFile;
		if (fileName == null)
			htmlFile = new File("web-" + new SimpleDateFormat("dd-MM-YYYY").format(new Date()) + ".html");
		else
			htmlFile = new File(fileName + ".html");
		try {
			final BufferedWriter writer = new BufferedWriter(new FileWriter(htmlFile));
			writer.write(html);
			writer.flush();
			writer.close();
			final Desktop desktop = Desktop.getDesktop();
			desktop.browse(htmlFile.toURI());
		} catch (final IOException ioException) {
			System.err.println("Could not display results in browser!");
		}
	}

	static void openInFile(String contenu, String fileName) {
		File rslt = new File(fileName + ".txt");
		try {
			final BufferedWriter writer = new BufferedWriter(new FileWriter(rslt));
			writer.write(contenu);
			writer.flush();
			writer.close();
		} catch (final IOException ioException) {
			System.err.println("Could not save results in file!");
		}

	}

	public static void main(String[] args) {
		var config = makeConfigFromCommandLineArgs(args);
		String fileName = null;
		if (config.isPresent()) {
			var analyzer = new Analyzer(config.get());
			var results = analyzer.computeResults();
			boolean toHtml = false;
			boolean toFile = false;
			for (final String key : config.get().getOptions()) {
				if (key.equalsIgnoreCase("htmlToNav")) {
					toHtml = true;
				}
				if (key.startsWith("outputToFile")) {
					toFile = true;
					var cut = key.split(":");
					fileName = cut[1];
				}
			}
			if (toHtml) {
				System.out.println("Displaying results in default system browser...");
				CLILauncher.openInBrowser(results.toHTML(), fileName);
			} else if (toFile) {
				CLILauncher.openInFile(results.toString(), fileName);
			} else {
				System.out.println(results.toString());
			}
		} else if (!saved)
			displayHelpAndExit();
	}

	static Optional<Configuration> makeConfigFromCommandLineArgs(String[] args) {
		Path gitPath = null;
		var plugins = new HashMap<String, PluginConfig>();
		var options = new ArrayList<String>();
		boolean saveAndExit = false, loadConfig = false, fullPack = false, evolution = false;
		String possConfigPath = "";
		for (var arg : args) {
			if (arg.startsWith("--")) {
				String[] parts = arg.split("=");
				if (parts.length != 2)
					return Optional.empty();
				else {
					String pName = parts[0];
					String pValue = parts[1];
					switch (pName) {
						case "--addPlugin":
							// TODO: parse argument and make an instance of PluginConfig

							// Let's just trivially do this, before the TODO is fixed:
							if (pValue.equals("countLines"))
								plugins.put("countLines", new PluginConfig() {
									public String getValue() {
										return "countLines";
									}

									public String getKey() {
										return "addPlugin";
									}
								});

							if (pValue.equals("countBranches"))
								plugins.put("countBranches", new PluginConfig() {
									public String getValue() {
										return "countBranches";
									}

									public String getKey() {
										return "addPlugin";
									}

								});
							if (pValue.equals("countCommits"))
								plugins.put("countCommits", new PluginConfig() {
									public String getValue() {
										return "countCommits";
									}

									public String getKey() {
										return "addPlugin";
									}
								});
							if (pValue.equals("percentageLine"))
								plugins.put("percentageLine", new PluginConfig() {
									public String getValue() {
										return "percentageLine";
									}

									public String getKey() {
										return "addPlugin";
									}
								});

							if (pValue.equals("allCommitsPerDate"))
								plugins.put("allCommitsPerDate", new PluginConfig() {
									public String getValue() {
										return "allCommitsPerDate";
									}

									public String getKey() {
										return "addPlugin";
									}
								});
							final var coupe = pValue.split(":");
							if (coupe.length == 2)
								if (coupe[0].equals("StatsOf"))
									plugins.put("StatsOf", new PluginConfig() {
										public String getValue() {
											return coupe[1];
										}

										public String getKey() {
											return "addPlugin";
										}
									});

							if (pValue.equals("countMergesAccepted"))
								plugins.put("countMergesAccepted", new PluginConfig() {
									public String getValue() {
										return "countMergesAccepted";
									}

									public String getKey() {
										return "addPlugin";
									}
								});

							if (pValue.equals("DatePerAuthor"))
								plugins.put("DatePerAuthor", new PluginConfig() {
									public String getValue() {
										return "DatePerAuthor";
									}

									public String getKey() {
										return "addPlugin";
									}
								});
							if (pValue.equals("percentageCommits"))
								plugins.put("percentageCommits", new PluginConfig() {
									public String getValue() {
										return "percentageCommits";
									}

									public String getKey() {
										return "addPlugin";
									}
								});

							break;
						case "--addOption":

							if (pValue.equals("strict") || pValue.equals("htmlToNav"))
								options.add(pValue);
							else {
								var cut = pValue.split(":");
								if (cut.length == 2 && cut[0].equals("outputToFile")) {
									options.add(pValue);
								} else
									return Optional.empty();
							}
							break;
						case "--logOption":
							options.add("log option:" + pValue);
							break;
						case "--loadConfigFile":
							possConfigPath = pValue;
							loadConfig = true;
							break;
						case "--fullPack":
							fullPack = true;
							break;
						case "--evolutionInfo":
							evolution = true;
							break;
						case "--justSaveConfigFile":
							// TODO (save command line options to a file instead of running the analysis)
							saveAndExit = true;
							break;
						case "--man":
							try {
								File myObj = new File("../Documentation/man.txt");
								Scanner myReader = new Scanner(myObj);
								while (myReader.hasNextLine()) {
									String data = myReader.nextLine();
									System.out.println(data);
								}
								myReader.close();
							} catch (FileNotFoundException e) {
								System.out.println("An error occurred.");
								e.printStackTrace();
							}
							break;
						default:
							return Optional.empty();
					}
				}
			} else {
				File theoGitPath = new File(arg);
				if (theoGitPath.exists() && theoGitPath.isDirectory()) {
					gitPath = FileSystems.getDefault().getPath(theoGitPath.getAbsolutePath());
				} else {
					System.err.println("Could not find directory, defaulting to ..");
					gitPath = FileSystems.getDefault().getPath("..");
				}
			}
		}
		if (gitPath == null) {
			gitPath = FileSystems.getDefault().getPath("..");
		}
		if (loadConfig) {
			return makeConfigFromCommandLineArgs(makeCommandLineFromConfigFile(possConfigPath, gitPath));
		} else if (fullPack) {
			return makeConfigFromCommandLineArgs(makeCommandLineFromConfigFile("../package/ourWorld.cfg", gitPath));
		} else if (evolution) {
			return makeConfigFromCommandLineArgs(makeCommandLineFromConfigFile("../package/evolution.cfg", gitPath));
		}
		if (plugins.isEmpty())
			return Optional.empty();
		final Configuration config = new Configuration(gitPath, plugins, options);
		if (saveAndExit) {
			config.writeToDisk();
			saved = true;
			return Optional.empty();
		}
		return Optional.of(config);
	}

	private static String[] addString(final String[] array, final String elem) {
		final String newarray[] = new String[array.length + 1];
		for (int i = 0; i < array.length; i++) {
			newarray[i] = array[i];
		}
		newarray[array.length] = elem;
		return newarray;
	}

	private static String[] makeCommandLineFromConfigFile(final String fichier, final Path gitPath) {
		Scanner sc = null;
		String PluginName;
		String[] args = { gitPath.toString() };
		try {
			sc = new Scanner(new File(fichier));
		} catch (final Exception e) {
			System.out.println("Erreur lors d’ouverture du fichier:");
			e.printStackTrace();
			System.exit(1);
		}
		while (sc.hasNext()) {
			switch (sc.next()) {
				case "Plugin:":
					if (sc.hasNext()) {
						PluginName = sc.next();
						args = CLILauncher.addString(args, "--addPlugin=" + PluginName);
					}
					break;
				case "Option:":
					if (sc.hasNext()) {
						String OptionName = sc.next();
						args = addString(args, "--addOption=" + OptionName);
					}
					break;
				case "logOption:":
					if (sc.hasNext())
						args = addString(args, "--logOption=" + sc.next());
					break;
				default:
					break;
			}
		}
		return args;
	}

	private static void displayHelpAndExit() {
		System.out.println("Wrong command...");
		try {
			File myObj = new File("../Documentation/man.md");
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
			String data = myReader.nextLine();
			System.out.println(data);
						}
				myReader.close();
		} 
		catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		System.exit(0);
	}

}
