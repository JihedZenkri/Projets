package up.visulog.analyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

public class PercentageLinePerAuthorPlugin implements AnalyzerPlugin {

	private final Configuration configuration;
	private Result result;
	private static boolean strict;

	public PercentageLinePerAuthorPlugin(final Configuration generalConfiguration) {
		this.configuration = generalConfiguration;
		this.strict = false;
	}

	public PercentageLinePerAuthorPlugin(final Configuration generalConfiguration, boolean strict) {
		this.configuration = generalConfiguration;
		this.strict = strict;
	}

	static Result processLog(final List<Commit> gitLog) {
		final var result = new Result();
		for (final var commit : gitLog) {
			String mail = commit.email;
			mail = mail.substring(0, mail.indexOf('@') == -1 ? mail.length() : mail.indexOf('@'));

			final var nb = result.linesPerAuthor.getOrDefault(mail, 0);
			if (strict)
				result.linesPerAuthor.put(mail, nb + Integer.valueOf(commit.nbLinesStrict));
			else
				result.linesPerAuthor.put(mail, nb + Integer.valueOf(commit.nbLines));
		}
		result.sortByValue();
		result.linesPerAuthor.values().removeIf(val -> val <= 0);
		result.createNewMap();
		return result;
	}

	@Override
	public void run() {
		this.result = PercentageLinePerAuthorPlugin
				.processLog(Commit.parseLogFromCommand(this.configuration.getGitPath()));
	}

	@Override
	public Result getResult() {
		if (this.result == null) {
			this.run();
		}
		return this.result;
	}

	static class Result implements AnalyzerPlugin.Result {
		private Map<String, Integer> linesPerAuthor = new HashMap<>();
		private final Map<String, Double> percentageLinesPerAuthor = new LinkedHashMap<>();

		Map<String, Integer> getLinesPerAuthor() {
			return this.linesPerAuthor;
		}

		private void sortByValue()
		{
			List<Entry<String, Integer>> list = new LinkedList<>(linesPerAuthor.entrySet());

			// Sorting the list based on values
			list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()) == 0
					? o2.getKey().compareTo(o1.getKey())
							: o2.getValue().compareTo(o1.getValue()));
			linesPerAuthor = list.stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> b, LinkedHashMap::new));
		}

		@Override
		public String getResultAsString() {


			String aff="Percentage Lines per author :\n";
			Set<String> listKeys=percentageLinesPerAuthor.keySet();  
			Iterator<String> iterateur=listKeys.iterator();

			while(iterateur.hasNext())
			{
				Object key= iterateur.next();		 	

				aff=aff+"\n"+key+"=>"+percentageLinesPerAuthor.get(key);
			}
			return aff;

		}
		@Override
		public String getResultAsHtmlDiv() {
			PieChart camembert=new PieChart(linesPerAuthor);
			final StringBuilder html = new StringBuilder("<div><h2>Commit percentage line per author: </h2><ul>");
			for (final var item : this.percentageLinesPerAuthor.entrySet()) {
				String pretty = String.valueOf(item.getValue());
				int length = (pretty.length() < 5 ? pretty.length() : 5);
				html.append("<li>").append(item.getKey()).append(": ")
				.append(pretty.substring(0, length)).append("%</li>");
			}

			html.append("</ul></div>");
			return html.toString()+camembert.drawChart();
		}

		public int totallines() {
			int total = 0;
			for (final String i : this.linesPerAuthor.keySet()) {
				total += this.linesPerAuthor.get(i);
			}
			return total;

		}

		public void createNewMap() {

			for (final String i : this.linesPerAuthor.keySet()) {
				this.percentageLinesPerAuthor.put(i,
						((double) this.linesPerAuthor.get(i) / (double) this.totallines()) * 100);
			}
		}
	}
}
