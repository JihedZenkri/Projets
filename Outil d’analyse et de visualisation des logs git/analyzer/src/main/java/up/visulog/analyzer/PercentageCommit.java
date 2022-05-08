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

public class PercentageCommit implements AnalyzerPlugin {

	private final Configuration configuration;
	private Result result;

	public PercentageCommit(final Configuration generalConfiguration) {
		this.configuration = generalConfiguration;
	}

	static Result processLog(final List<Commit> gitLog) {
		final var result = new Result();
		for (final var commit : gitLog) {
			String mail = commit.email;
			mail = mail.substring(0, mail.indexOf('@') == -1 ? mail.length() : mail.indexOf('@'));
			final int nb = result.commitsPerAuthor.getOrDefault(mail, 0);
			result.commitsPerAuthor.put(mail, nb + 1);
		}
		result.sortByValue();
		result.createNewMap();
		return result;
	}

	@Override
	public void run() {
		this.result = PercentageCommit
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
		private  Map<String, Integer> commitsPerAuthor = new HashMap<>();
		private final Map<String, Double> percentageCommitsPerAuthor= new LinkedHashMap<>();

		Map<String, Integer> getCommitsPerAuthor() {
			return this.commitsPerAuthor;
		}


		private void sortByValue()
		{
			List<Entry<String, Integer>> list = new LinkedList<>(commitsPerAuthor.entrySet());

			// Sorting the list based on values
			list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()) == 0
					? o2.getKey().compareTo(o1.getKey())
							: o2.getValue().compareTo(o1.getValue()));
			commitsPerAuthor = list.stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> b, LinkedHashMap::new));
		}


		@Override
		public String getResultAsString() {

			String aff=" Commit Percentage  per author :\n";
			Set<String> listKeys=percentageCommitsPerAuthor.keySet();  
			Iterator<String> iterateur=listKeys.iterator();

			while(iterateur.hasNext())
			{
				Object key= iterateur.next();

				aff=aff+"\n"+key+"=>"+percentageCommitsPerAuthor.get(key);
			}
			return aff;

		}

		@Override
		public String getResultAsHtmlDiv() {
			PieChart camembert=new PieChart(commitsPerAuthor);
			final StringBuilder html = new StringBuilder("<div><h2>Commit percentage  per author: </h2><ul>");
			for (final var item : this.percentageCommitsPerAuthor.entrySet()) {
				html.append("<li>").append(item.getKey()).append(": ")
				.append(String.valueOf(item.getValue()).substring(0, 4)).append("%</li>");
			}
			html.append(camembert.drawChart());
			html.append("</ul></div>");
			return html.toString();
		}

		public int totalCommit() {
			int total = 0;
			for (final String i : this.commitsPerAuthor.keySet()) {
				total += this.commitsPerAuthor.get(i);
			}
			return total;

		}

		public void createNewMap() {

			for (final String i : this.commitsPerAuthor.keySet()) {
				this.percentageCommitsPerAuthor.put(i,
						((double) this.commitsPerAuthor.get(i) / (double) this.totalCommit()) * 100);
			}
		}
	}
}
