package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.util.Map; 
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class DatePerAuthor implements AnalyzerPlugin {
	private final Configuration configuration;
	private Result result;

	public DatePerAuthor(Configuration generalConfiguration) {
		this.configuration = generalConfiguration;
	}




	static Result processLog(List<Commit> gitLog) {
		var result = new Result();

		for (var commit : gitLog) {
			String mail = commit.email;
			mail = mail.substring(0, mail.indexOf('@') == -1 ? mail.length() : mail.indexOf('@'));
			LinkedList<String> dates = result.datePerAuthor.getOrDefault(mail,new LinkedList<String>());
			int nb = result.commitsPerAuthor.getOrDefault(mail, 0);
			dates.add(commit.date);
			result.commitsPerAuthor.put(mail, nb + 1);
			result.datePerAuthor.put(mail,dates);
		}
		result.sortByValue();
		return result;
	}




	@Override
	public void run() {
		result = processLog(Commit.parseLogFromCommand(configuration.getGitPath()));
	}

	@Override
	public Result getResult() {
		if (result == null) run();
		return result;
	}

	static class Result implements AnalyzerPlugin.Result {

		private final Map<String,LinkedList<String>> datePerAuthor=new HashMap<>();

		private Map<String, Integer> commitsPerAuthor = new LinkedHashMap<>();


		Map<String,LinkedList<String>> getdateperAuthor() {
			return datePerAuthor;
		}

		Map<String, Integer> getCommitsPerAuthor() {
			return commitsPerAuthor;
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


			String aff="Date per author :\n";
			Set<String> listKeys=datePerAuthor.keySet();  
			Iterator<String> iterateur=listKeys.iterator();

			while(iterateur.hasNext())
			{
				Object key= iterateur.next();
				aff=aff+"\n"+key+"=>"+datePerAuthor.get(key) + "\n";
			}
			return aff;
		}

		@Override
		public String getResultAsHtmlDiv() {
			StringBuilder html = new StringBuilder("<div><h2>Commits per author through time : </h2><ul>");
			String s="";
			for (var item : commitsPerAuthor.entrySet()) {
				DateChart lc=new DateChart( datePerAuthor.get(item.getKey()));
				html.append("<li><h3>").append(item.getKey()).append(": ").append(item.getValue()).append("</h3><br>").append(datePerAuthor.get(item.getKey()).toString()).append("</li>");
				html.append(lc.drawChart());
				// s=s+"Commits per date of: "+item.getKey()+lc.drawChart();
			}

			html.append("</ul></div>");
			return html.toString();
		}
	}
}
