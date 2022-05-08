package up.visulog.config;

import java.util.HashMap;
import java.util.Map;

public class TestConfiguration {
    /*
     * Do not forget writing tests as soon as class Configuration contains more than
     * just getters (or if they or the constructor start doing something smart!).
     */

    public static void main(final String[] args) {
	final Map<String, PluginConfig> plugins = new HashMap<>();
	plugins.put("Printer", new PluginConfig() {
	    @Override
	    public String getValue() {
		return "true";
	    }

	    @Override
	    public String getKey() {
		return "print-details";
	    }
	});
	final Configuration configuration = new Configuration(null, plugins);
	configuration.writeToDisk();
    }
}
