package up.visulog.gitrawdata;

public class CommitBuilder {
    private final String id;
    private String author;
    private String date;
    private String description;
    private String mergedFrom;
    private int nbLines;
	private int nbLinesS;

    public CommitBuilder(final String id) {
	this.id = id;
    }

    public CommitBuilder setAuthor(final String author) {
	this.author = author;
	return this;
    }

    public CommitBuilder setDate(final String date) {
	this.date = date;
	return this;
    }

    public CommitBuilder setDescription(final String description) {
	this.description = description;
	return this;
    }

    public CommitBuilder setMergedFrom(final String mergedFrom) {
	this.mergedFrom = mergedFrom;
	return this;
    }
    public CommitBuilder setLine(int lines){
		this.nbLines=lines;
    	return this;
	}

	public CommitBuilder setLineS(int lines)
	{
		this.nbLinesS = lines;
		return this;
	}
    public Commit createCommit() {
        return new Commit(id, author, date, description, mergedFrom, nbLines, nbLinesS);
    }
}
