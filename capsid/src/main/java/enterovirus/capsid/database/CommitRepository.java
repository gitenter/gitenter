package enterovirus.capsid.database;

import java.io.IOException;

import enterovirus.capsid.domain.*;

public interface CommitRepository {

	public CommitBean findById(Integer id) throws IOException;
	public CommitBean findByShaChecksumHash(String shaChecksumHash) throws IOException;
}
