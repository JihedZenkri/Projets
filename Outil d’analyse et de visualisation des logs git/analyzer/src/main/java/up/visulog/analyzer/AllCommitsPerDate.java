package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.util.Map; 
import java.util.Set; 
import java.util.Iterator;

public class AllCommitsPerDate implements AnalyzerPlugin {

	private final Configuration configuration;
	private Result result;

	public AllCommitsPerDate(final Configuration generalConfiguration) {
		this.configuration = generalConfiguration;
	}

	static Result processLog(final List<Commit> gitLog) {
		final var result = new Result();

		for (final var commit : gitLog) {
			final int nb = result.allCommitsPerDate.getOrDefault(commit.date, 0);
			result.allCommitsPerDate.put(commit.date, nb + 1);
		}
		return result;
	}

	@Override
	public void run() {
		this.result = AllCommitsPerDate
				.processLog(Commit.parseLogFromCommand(this.configuration.getGitPath()));
	}

	@Override
	public Result getResult() {
		if (this.result == null) {
			this.run();
		}
		return this.result;
	}

	static class Result implements AnalyzerPlugin.Result{

		private Map<String,Integer> allCommitsPerDate=new HashMap<>();

		Map<String,Integer> getAllCommitsPerDate(){
			return allCommitsPerDate;
		}

		private String countAll() {
			int n=0;
			for(var i:allCommitsPerDate.entrySet()) n+=(int)i.getValue();
			return Integer.toString(n);
		}

		@Override
		public String getResultAsString() {
			String aff="Commits per date :\n";

			Set<String> listKeys=allCommitsPerDate.keySet();  
			Iterator<String> iterateur=listKeys.iterator();

			while(iterateur.hasNext()){
				Object key= iterateur.next();
				aff=aff+"\n"+key+"=>"+allCommitsPerDate.get(key);
			}

			return aff;
		}

		@Override
		public String getResultAsHtmlDiv() {
			DateChart dc=new DateChart(allCommitsPerDate);
			final StringBuilder html = new StringBuilder("<div id=\"commit\"><h2>Commits per date: </h2>");
			html.append(dc.drawChart());
			html.append("<h4>Number of commits: ").append(this.countAll()).append("<br></h4>");
			html.append("</div>");
			return html.toString();
		}
	}
}

