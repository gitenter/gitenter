package enterovirus.capsid.database;

import java.io.IOException;

import enterovirus.capsid.domain.*;

public interface DocumentRepository {

	public DocumentBean findById(Integer id) throws IOException;
	public DocumentBean findByCommitIdAndRelativeFilepath(Integer commitId, String relativeFilepath) throws IOException;
}
