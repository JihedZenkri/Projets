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

public class CountMergesAcceptedPerAuthorPlugin implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;

    public CountMergesAcceptedPerAuthorPlugin(final Configuration generalConfiguration) {
	this.configuration = generalConfiguration;
    }

    static Result processLog(final List<Commit> gitLog) {
	final var result = new Result();
	for (final var commit : gitLog) {
		String mail = commit.email;
		mail = mail.substring(0, mail.indexOf('@') == -1 ? mail.length() : mail.indexOf('@'));
		final int nb = result.mergesPerAuthor.getOrDefault(mail, 0);
		result.mergesPerAuthor.put(mail, nb + 1);
	}
	result.sortByValue();
	return result;
    }

    @Override
    public void run() {
	this.result = CountMergesAcceptedPerAuthorPlugin
		.processLog(Commit.parseLogFromCommand(this.configuration.getGitPath(), "--merges"));
    }

    @Override
    public Result getResult() {
	if (this.result == null) {
	    this.run();
	}
	return this.result;
    }

    static class Result implements AnalyzerPlugin.Result {
	private Map<String, Integer> mergesPerAuthor = new HashMap<>();

	Map<String, Integer> getMergesPerAuthor() {
	    return this.mergesPerAuthor;
	}
	
	private String countAll() {
		int n=0;
		for(var i:mergesPerAuthor.entrySet()) n+=(int)i.getValue();
		return Integer.toString(n);
	}
	
	private void sortByValue()
	{
		List<Entry<String, Integer>> list = new LinkedList<>(mergesPerAuthor.entrySet());

		// Sorting the list based on values
		list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()) == 0
				? o2.getKey().compareTo(o1.getKey())
						: o2.getValue().compareTo(o1.getValue()));
		mergesPerAuthor = list.stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> b, LinkedHashMap::new));
	}


	@Override
	public String getResultAsString() {
	 
	   String aff="Merges per author :\n";
	   Set<String> listKeys=mergesPerAuthor.keySet();  
    		Iterator<String> iterateur=listKeys.iterator();
    		
    		while(iterateur.hasNext())
    		{	
    			Object key= iterateur.next();

    			aff=aff+"\n"+key+"=>"+mergesPerAuthor.get(key);
    		}
    		return aff;
        }


	@Override
	public String getResultAsHtmlDiv() {
		PieChart camembert=new PieChart(mergesPerAuthor);
	    final StringBuilder html = new StringBuilder("<div id=\"merge\"><h2>Merges reviewed per Author : </h2><ul>");
	    for (final var item : this.mergesPerAuthor.entrySet()) {
		html.append("<li>").append(item.getKey()).append(": ").append(item.getValue()).append("</li>");
	    }
	    html.append("</ul>").append("Number of reviewed merges:").append(this.countAll())
	    ;
	    html.append(camembert.drawChart());
	    html.append("</div>");
	    return html.toString();
	}
    }
}
