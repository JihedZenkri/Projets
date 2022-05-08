package up.visulog.analyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

public class StatsOfAuthorPlugin implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;
	private String author;

    public StatsOfAuthorPlugin(final Configuration generalConfiguration, String aut) {
	this.configuration = generalConfiguration;
	this.author = aut;
    }

    static Result processLog(final List<Commit> gitLog, final List<Commit> allLog) {
	final var result = new Result();
	for (final var commit : gitLog) {
	    int commits = result.StatsOfAuthor.getOrDefault("Nombres de commits", 0);
	    result.StatsOfAuthor.put("Nombres de commits", commits + 1);
		int dayCommits = result.CommitsPerDate.getOrDefault(commit.date, 0);
		result.CommitsPerDate.put(commit.date, dayCommits + 1);
	    if (commit.mergedFrom != null) {
			int merges = result.StatsOfAuthor.getOrDefault("Dont merges commits", 0);
	    	result.StatsOfAuthor.put("Dont merges commits", merges + 1);
		}
	    int lignes = result.StatsOfAuthor.getOrDefault("Nombres de lignes", 0);
	    result.StatsOfAuthor.put("Nombres de lignes", lignes + commit.nbLines);
		int dayLines = result.LinesPerDate.getOrDefault(commit.date, 0);
		result.LinesPerDate.put(commit.date, dayLines + commit.nbLines);
	}
	for (var commit : allLog) {
		result.totalCommits++;
		result.totalLines += commit.nbLines;
	}
	result.sortDate();
	return result;
    }

    @Override
    public void run() {
	this.result = StatsOfAuthorPlugin.processLog(Commit.parseLogFromCommand(this.configuration.getGitPath(), "--author=" + this.author),Commit.parseLogFromCommand(this.configuration.getGitPath()));
	this.result.author = this.author;
    }

    @Override
    public Result getResult() {
	if (this.result == null) {
	    this.run();
	}
	return this.result;
    }

    static class Result implements AnalyzerPlugin.Result {
	private Map<String, Integer> StatsOfAuthor = new HashMap<>();
	private Map<String, Integer> LinesPerDate = new HashMap<>();
	private Map<String, Integer> CommitsPerDate = new HashMap<>();
	private Map<String, Integer> percentageCommits = new HashMap<>();
	private Map<String, Integer> percentageLines = new HashMap<>();
	private int totalLines = 0;
	private int totalCommits = 0;
	private String author = "";

	Map<String, Integer> getStatsOfAuthor() {
	    return this.StatsOfAuthor;
	}



	public void sortDate() {
		this.LinesPerDate.values().removeIf(val -> val <= 0);
		this.LinesPerDate =new TreeMap<String,Integer>(LinesPerDate);
		this.CommitsPerDate =new TreeMap<String,Integer>(CommitsPerDate);
	}

	public void fillPercentageMap() {
		int commits = this.StatsOfAuthor.get("Nombres de commits");
		this.percentageCommits.put(this.author, commits);
		this.percentageCommits.put("Others", totalCommits - commits);
		int lines = this.StatsOfAuthor.get("Nombres de lignes");
		this.percentageLines.put(this.author, lines);
		this.percentageLines.put("Others", totalLines - lines);
	}

	@Override
	public String getResultAsString() {
		if (StatsOfAuthor.isEmpty())
			return "Stats of " + this.author + " does not exists.";
		String s = "Stats of " + this.author + " :\n";
		s += "Number of commits : " + StatsOfAuthor.get("Nombres de commits") + "/" + totalCommits + "\n";
		if (StatsOfAuthor.containsKey("Dont merges commits"))
			s += "Including merges commits : " + StatsOfAuthor.get("Dont merges commits") + "/" + StatsOfAuthor.get("Nombres de commits") + "\n";
		s += "Number of lines : " + StatsOfAuthor.get("Nombres de lignes") + "/" + totalLines + "\n";
		s += "Commits on each day :\n";
		for (var item : this.CommitsPerDate.entrySet()) {
			s += "  " + item.getKey() + " > " + item.getValue() + "\n";
		}
		
		s += "Lines on each day :\n";
		for (var item : this.LinesPerDate.entrySet()) {
			s += "  " + item.getKey() + " > " + item.getValue() + "\n";
		}
	    return s;
	}

	@Override
	public String getResultAsHtmlDiv() {
		if (StatsOfAuthor.isEmpty())
			return "<div><h2>Stats of " + this.author + " does not exists.</h2><div>";
		fillPercentageMap();
		PieChart camembert = new PieChart(percentageCommits);
	    final StringBuilder html = new StringBuilder("<div><h2>Stats of "+author+" : </h2><ul>");
		html.append("<li><h4>Number of commits : </h4>" + Integer.toString(StatsOfAuthor.get("Nombres de commits")));
		html.append("/" + Integer.toString(totalCommits) + "</li>");
		if (StatsOfAuthor.containsKey("Dont merges commits"))
			html.append("<li>Including merges commits : " + StatsOfAuthor.get("Dont merges commits") + "/" + StatsOfAuthor.get("Nombres de commits") + "</li>");
		html.append(camembert.drawChart());
		camembert = new PieChart(percentageLines);
		html.append("<li><h4>Number of lines : </h4>" + StatsOfAuthor.get("Nombres de lignes") + "/" + totalLines + "</li>");
		html.append(camembert.drawChart());
		html.append("<li><h4>Commits Per Date : </h4>").append("</li>");
		DateChart dates = new DateChart(this.CommitsPerDate);
		html.append(dates.drawChart());
		html.append("<li><h4>Lines Per Date : </h4>").append("</li>");
		dates = new DateChart(this.LinesPerDate);
		html.append(dates.drawChart());
	    html.append("</ul></div>");
	    return html.toString();
	}
    }
}
