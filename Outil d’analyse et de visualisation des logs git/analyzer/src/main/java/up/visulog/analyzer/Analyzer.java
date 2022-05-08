package up.visulog.analyzer;

import up.visulog.gitrawdata.Branch;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import up.visulog.gitrawdata.Commit;

import up.visulog.config.Configuration;
import up.visulog.config.PluginConfig;

public class Analyzer  {
	private final Configuration config;
	private AnalyzerResult result;
	
	public Analyzer(final Configuration config) {
		this.config = config;
	}

	public AnalyzerResult computeResults() {
		final List<AnalyzerPlugin> plugins = new ArrayList<>();
		boolean mergesNeeded = false;
		for (final var pluginConfigEntry : this.config.getPluginConfigs().entrySet()) {
			final var pluginName = pluginConfigEntry.getKey();
			final var pluginConfig = pluginConfigEntry.getValue();
			final var plugin = this.makePlugin(pluginName, pluginConfig);
			plugin.ifPresent(plugins::add);
			if (pluginName.equals("countMergesAccepted"))
				mergesNeeded = true;
		}
		ArrayList<String> logOptions = new ArrayList<String>();
		for (String s : this.config.getOptions())
		{
			String[] cut = s.split(":");
			if (cut.length > 1 && cut[0].equals("log option"))
				switch (cut[1]) {
				case "--no-merges" :

				    if (!mergesNeeded)
						logOptions.add(cut[1]);
					break;
				default :
					System.out.println("Option non reconue!");
					break;
					
					
				case "--max-count":
					
					logOptions.add(cut[1]+"="+cut[2]);
					break;

				case "--since":
				case "--after":
					if(bonFormat(cut[2]))
						logOptions.add(cut[1]+"="+cut[2]);
					break;

				case "--until":
				case "--before":
					if(bonFormat(cut[2]))
						logOptions.add(cut[1]+"="+cut[2]);
					break;
				case"--author":
				    logOptions.add(cut[1]+"="+cut[2]);
				break;
			 	case "--branche" :			
				  var a=Branch.parseLog2FromCommand(config.getGitPath());
			 		boolean b=true;
				  for(var branch : a) {
			 			if(cut[2].equals(branch.name)) {
			 				logOptions.add(cut[2]);
			 				b=false;
			 				break;
			 			}
			 			
				  }
			 		if(b) {
					  System.out.println("Branche inexistante, option ignorée");
			 		}
				    break;
				}


		}
		Commit.setOptions(logOptions);
		// run all the plugins
		// TODO: try running them in parallel
		for (final var plugin : plugins) {
			plugin.run();
		}

		// store the results together in an AnalyzerResult instance and return it
		return new AnalyzerResult(plugins.stream().map(AnalyzerPlugin::getResult).collect(Collectors.toList()));
	}
	
	
	public static boolean bonFormat(String t) {
		String[] cutd = t.split("/");
		if(cutd.length==3) 
			return true;
		return false;
	}

	// TODO: find a way so that the list of plugins is not hardcoded in this factory
	private Optional<AnalyzerPlugin> makePlugin(final String pluginName, final PluginConfig pluginConfig) {
		boolean strict = false;
		if (config.getOptions().contains("strict")) strict = true;
		switch (pluginName) {
		case "countCommits":
			return Optional.of(new CountCommitsPerAuthorPlugin(this.config));
		case "percentageLine":	return Optional.of(new PercentageLinePerAuthorPlugin(this.config, strict));
		case "countMergesAccepted" : return Optional.of(new CountMergesAcceptedPerAuthorPlugin(config));
		case "countLines" : return Optional.of(new CountLinesPerAuthorPlugin(config, strict));
		case "DatePerAuthor" : return Optional.of(new DatePerAuthor(this.config));
		case "percentageCommits" :	return Optional.of(new PercentageCommit(this.config));

		case "StatsOf" : return Optional.of(new StatsOfAuthorPlugin(this.config, pluginConfig.getValue()));

        case "countBranches" :return Optional.of(new CountBranchesPerAuthorPlugin(config));
        case "allCommitsPerDate" : return Optional.of(new AllCommitsPerDate(this.config));

		default : return Optional.empty(); 
	}

}
}
