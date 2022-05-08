package up.visulog.analyzer;

import java.util.List;

public class AnalyzerResult{
    public List<AnalyzerPlugin.Result> getSubResults() {
	return this.subResults;
    }

    private final List<AnalyzerPlugin.Result> subResults;

    public AnalyzerResult(final List<AnalyzerPlugin.Result> subResults) {
	this.subResults = subResults;
    }

    @Override
    public String toString() {
	return this.subResults.stream().map(AnalyzerPlugin.Result::getResultAsString).reduce("",
		(acc, cur) -> acc + "\n" + cur);
    }

    public String toHTML() {
	return "<html><head><title>Visulog Resultat</title><script src=\"https://canvasjs.com/assets/script/canvasjs.min.js\"></script></head><body><div id=\"content\">" + this.subResults.stream().map(AnalyzerPlugin.Result::getResultAsHtmlDiv).reduce("",
		(acc, cur) -> acc + cur) + "</div></body></html>";
    }
}
