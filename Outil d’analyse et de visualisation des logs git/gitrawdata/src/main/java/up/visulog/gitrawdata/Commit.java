package up.visulog.gitrawdata;
import java.util.Scanner;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class Commit {
	// FIXME: (some of) these fields could have more specialized types than String
	public final String id;
	public final String date;
	public final String author, email;
	public final String description;
	public final String mergedFrom;
	public  int nbLines;
	public final int nbLinesStrict;
	private static ArrayList<String> logOptions;

	public Commit(String id, String author, String date, String description, String mergedFrom ,int nbLines, int nbLS) {
		this.id = id;
		this.author = author.substring(0, author.indexOf("<") - 1);
		this.email = author.substring(author.indexOf("<") + 1, author.lastIndexOf(">"));
		this.date = date;
		this.description = description;
		this.nbLines=nbLines;
		this.nbLinesStrict = nbLS;
		this.mergedFrom = mergedFrom;
	}

	public static void setOptions(ArrayList<String> list) {
		logOptions = list;
	}

	// TODO: factor this out (similar code will have to be used for all git commands)
	public static List<Commit> parseLogFromCommand(Path gitPath, String... args) {
		ArrayList<String> commande = new ArrayList<String>();
		commande.add("git");
		commande.add("log");
		commande.add("--date=short");
		commande.add("--shortstat");
		commande.add("-w");
		for (String s : args)
			commande.add(s);
		for (String str : logOptions)
			commande.add(str);
		ProcessBuilder builder =
				new ProcessBuilder(commande).directory(gitPath.toFile());
		Process process;
		try {
			process = builder.start();
		} catch (IOException e) {
			throw new RuntimeException("Error running \"git log\".", e);
		}
		InputStream is = process.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		return parseLog(reader, gitPath);
	}

	public static List<Commit> parseLog(BufferedReader reader, Path gitPath ) {
		var result = new ArrayList<Commit>();
		Optional<Commit> commit = parseCommit(reader);
		while (commit.isPresent()) {
			result.add(commit.get());
			commit = parseCommit(reader);
		}
		return result;
	}

	/**
	 * Parses a log item and outputs a commit object. Exceptions will be thrown in case the input does not have the proper format.
	 * Returns an empty optional if there is nothing to parse anymore.
	 */
	public static Optional<Commit> parseCommit(BufferedReader input ) {
		try {

			var line = input.readLine();
			if (line == null) return Optional.empty(); // if no line can be read, we are done reading the buffer
			var idChunks = line.split(" ");
			if (!idChunks[0].equals("commit")) parseError();
			var builder = new CommitBuilder(idChunks[1]);
			boolean mergeCommit = false;
			builder.setLine(0);
			line = input.readLine();
			while (!line.isEmpty()) {
				var colonPos = line.indexOf(":");
				var fieldName = line.substring(0, colonPos);
				var fieldContent = line.substring(colonPos + 1).trim();
				switch (fieldName) {
				case "Author":
					builder.setAuthor(fieldContent);
					break;
				case "Merge":
					mergeCommit = true;
					builder.setMergedFrom(fieldContent);
					break;
				case "Date":
					builder.setDate(fieldContent);
					break;
				default: // TODO: warn the user that some field was ignored
				}
				line = input.readLine(); //prepare next iteration
				if (line == null) parseError(); // end of stream is not supposed to happen now (commit data incomplete)
			}

			// now read the commit message per se
			var description = input
					.lines() // get a stream of lines to work with
					.takeWhile(currentLine -> !currentLine.isEmpty()) // take all lines until the first empty one (commits are separated by empty lines). Remark: commit messages are indented with spaces, so any blank line in the message contains at least a couple of spaces.
					.map(String::trim) // remove indentation
					.reduce("", (accumulator, currentLine) -> accumulator + currentLine); // concatenate everything
		
			builder.setDescription(description);
			if (!mergeCommit)
			{
				line = input.readLine();
				while (line != null && !line.isEmpty())
				{
					if (line.contains("insertion") || line.contains("deletion"))
					{
						builder.setLine(parseInsertions(line, true));
						builder.setLineS(parseInsertions(line, false));
					}
					line = input.readLine();
				}
			}		
			return Optional.of(builder.createCommit());
		} catch (IOException e) {
			parseError();
		}
		return Optional.empty(); // this is supposed to be unreachable, as parseError should never return
	}

	// Helper function for generating parsing exceptions. This function *always* quits on an exception. It *never* returns.
	private static void parseError() {
		throw new RuntimeException("Wrong commit format.");
	}






	@Override
	public String toString() {
		return "Commit{" +
				"id='" + id + '\'' +
				(mergedFrom != null ? ("mergedFrom...='" + mergedFrom + '\'') : "") + //TODO: find out if this is the only optional field
				", date='" + date + '\'' +
				", author='" + author + '\'' +
				", description='" + description + '\'' +
				", email='" + email  + "'" + 
				'}';
	}

	 

	public static  int  diffFromCommand(String id,String prev, Path gitPath) {
		ProcessBuilder builder =
				new ProcessBuilder("git", "diff", "--numstat", id, prev).directory(gitPath.toFile());
		Process process;
		try {
			process = builder.start();
		} catch (IOException e) {
			throw new RuntimeException("Error running \"git diff --numstat\".", e);
		}
		InputStream is = process.getInputStream();
		BufferedReader read = new BufferedReader(new InputStreamReader(is));
		return lineTotal(read);
	}

	public static int lineTotal(BufferedReader reader) {
		Scanner input=new Scanner(reader);
		input.useDelimiter("\n");
		int sum=0;
		while(input.hasNext()) {
			String line=input.next();
			sum=sum+recupNbLines(line);
		}
		return sum;
	}

	public static int parseInsertions(String s, boolean del) {
		var cut = s.split(",");
		int i = 0;
		for (String a : cut)
		{
			a = a.trim();
			var cutd = a.split(" ");
			if (a.charAt(a.length() - 2) == '+')
				i += Integer.parseInt(cutd[0]);
			else if (del && a.charAt(a.length() - 2) == '-')
				i -= Integer.valueOf(cutd[0]);
		}
		return i;
	}


	public  static int recupNbLines(String line) {
		 
		Scanner s=new Scanner(line);
		String st=s.next();	
		if(!st.equals("-") ){
			int a=Integer.parseInt(st);
			//st=s.next();
			//int b=Integer.parseInt(st);
			return a;}
		else {
				return 0;
			}

	}

}
