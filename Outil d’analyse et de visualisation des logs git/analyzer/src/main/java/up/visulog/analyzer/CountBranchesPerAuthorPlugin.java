package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Branch;
import up.visulog.gitrawdata.Commit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountBranchesPerAuthorPlugin implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;

    public CountBranchesPerAuthorPlugin(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }

    static Result processLog(List<Branch> gitLog) {
        var result = new Result();
        for (var branch : gitLog) {
            int nb = result.branchesPerAuthor.getOrDefault(branch.author, 0);
            result.branchesPerAuthor.put(branch.author, nb + 1);
        }
        return result;
    }

    @Override
    public void run() {
        result = processLog(Branch.parseLog2FromCommand(configuration.getGitPath()));
    }

    @Override
    public Result getResult() {
        if (result == null) run();
        return result;
    }

    static class Result implements AnalyzerPlugin.Result {
        private final Map<String, Integer> branchesPerAuthor = new HashMap<>();

        Map<String, Integer> getBranchesPerAuthor() {
            return branchesPerAuthor;
        }

        @Override
        public String getResultAsString() {
            return branchesPerAuthor.toString();
        }

        @Override
        public String getResultAsHtmlDiv() {
            StringBuilder html = new StringBuilder("<div>Branches per author: <ul>");
            for (var item : branchesPerAuthor.entrySet()) {
                html.append("<li>").append(item.getKey()).append(": ").append(item.getValue()).append("</li>");
            }
            html.append("</ul></div>");
            return html.toString();
        }
    }
}

