package up.visulog.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.ArrayList;
public class Configuration {
    private final Path gitPath;
    private final Map<String, PluginConfig> plugins;
	private ArrayList<String> options = new ArrayList<>();

    public Configuration(final Path gitPath, final Map<String, PluginConfig> plugins, final ArrayList<String> options) {
	this.gitPath = gitPath;
	this.plugins = Map.copyOf(plugins);
	this.options = options;
    }



    public Configuration(final Path gitPath, final Map<String, PluginConfig> plugins) {
	this.gitPath = gitPath;
	this.plugins = Map.copyOf(plugins);
    }

	public ArrayList<String> getOptions() {
		return options;
	}

    public final void writeToDisk() {
	final File configFile = new File("../save-" + new SimpleDateFormat("dd-MM-YYYY").format(new Date()) + ".cfg");
	System.out.println(configFile.getAbsolutePath());
	try {
	    configFile.createNewFile();
	} catch (final IOException ioException) {
	    ioException.printStackTrace();
	    // TODO: find a way to nicely handle exceptions - system wide or at least branch
	    // wide
	}
	try {
	    final FileWriter fileWriter = new FileWriter(configFile);
	    final BufferedWriter writer = new BufferedWriter(fileWriter);
	    for (final String plugin : this.plugins.keySet()) {
		writer.write("Plugin: " + plugin + "\n");
		final PluginConfig config = this.plugins.get(plugin);
		//writer.write(config.getKey() + " -> " + config.getKey());
	    }
		for (String option : options)
		{
			var cut	= option.split(":");
			if (cut.length > 1 && cut[0].equals("log option")) {
				writer.write("logOption: " + option.substring(11) + "\n");
			}
			else
				writer.write("Option: " + option + "\n");
		}
	    writer.flush();
	    writer.close();
	} catch (final IOException ioException) {
	    ioException.printStackTrace();
	}

    }

    public final Path getGitPath() {
	return this.gitPath;
    }

    public final Map<String, PluginConfig> getPluginConfigs() {
	return this.plugins;
    }
}
