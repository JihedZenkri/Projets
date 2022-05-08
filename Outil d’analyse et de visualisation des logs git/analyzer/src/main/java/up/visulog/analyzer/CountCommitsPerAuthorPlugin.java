package up.visulog.analyzer;


import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

public class CountCommitsPerAuthorPlugin implements AnalyzerPlugin {
	private final Configuration configuration;
	private Result result;

	public CountCommitsPerAuthorPlugin(final Configuration generalConfiguration) {
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
		return result;
	}

	@Override
	public void run() {
		this.result = CountCommitsPerAuthorPlugin
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
		private Map<String, Integer> commitsPerAuthor = new HashMap<>();

		Map<String, Integer> getCommitsPerAuthor() {
			return this.commitsPerAuthor;
		}

		private String countAll() {
			int n=0;
			for(var i:commitsPerAuthor.entrySet()) n+=(int)i.getValue();
			return Integer.toString(n);
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


			String aff="Commits per author :\n";
			Set<String> listKeys=commitsPerAuthor.keySet();  
			Iterator<String> iterateur=listKeys.iterator();

			while(iterateur.hasNext())
			{
				Object key= iterateur.next();
				aff=aff+"\n"+key+"=>"+commitsPerAuthor.get(key);
			}
			return aff;

		}



		@Override
		public String getResultAsHtmlDiv() {
			PieChart camembert=new PieChart(commitsPerAuthor);
			final StringBuilder html = new StringBuilder("<div id=\"commit\"><h2>Commits per author: </h2><ul>");
			for (final var item : this.commitsPerAuthor.entrySet()) {
				html.append("<li>").append(item.getKey()).append(": ").append(item.getValue()).append("</li>");
			}
			html.append("</ul>").append("Number of commits:").append(this.countAll());
			html.append(camembert.drawChart());
			html.append("</div>");
			return html.toString();
		}
	}
}
