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

public class CountLinesPerAuthorPlugin implements AnalyzerPlugin {
	private final Configuration configuration;
	private Result result;
	private static boolean strict;

	public CountLinesPerAuthorPlugin(final Configuration generalConfiguration) {
		this.configuration = generalConfiguration;
		this.strict = false;
	}

	public CountLinesPerAuthorPlugin(final Configuration generalConfiguration, boolean strict) {
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
				result.linesPerAuthor.put(mail, nb + commit.nbLinesStrict);
			else
				result.linesPerAuthor.put(mail, nb + commit.nbLines);
		}
		result.linesPerAuthor.values().removeIf(val -> val <= 0);
		result.sortByValue();
		
		return result;
	}

	@Override
	public void run() {
		this.result = CountLinesPerAuthorPlugin.processLog(Commit.parseLogFromCommand(this.configuration.getGitPath()));
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

			String aff="Lines per author :\n";
			Set<String> listKeys=linesPerAuthor.keySet();  
			Iterator<String> iterateur=listKeys.iterator();

			while(iterateur.hasNext()){
				Object key= iterateur.next();

				aff=aff+"\n"+key+"=>"+linesPerAuthor.get(key);


			}
			return aff;
		}

		@Override
		public String getResultAsHtmlDiv() {
			PieChart camembert=new PieChart(linesPerAuthor);
			final StringBuilder html = new StringBuilder("<div id=\"lines\"><h2>Total lines per author: </h2><ul>");
			for (final var item : this.linesPerAuthor.entrySet()) {
				html.append("<li>").append(item.getKey()).append(": ").append(item.getValue()).append("</li>");
			}
			html.append("</ul>");
			html.append(camembert.drawChart());
			html.append("</div>");
			return html.toString();
		}
	}
}
