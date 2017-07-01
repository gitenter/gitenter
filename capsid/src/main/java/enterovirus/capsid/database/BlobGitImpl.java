package enterovirus.capsid.database;

import org.springframework.stereotype.Repository;

import enterovirus.capsid.domain.*;
import enterovirus.gitar.GitSource;

@Repository
public class BlobGitImpl implements BlobRepository {

	private GitSource gitSource;
	
	public BlobBean findBlob () {
		return new BlobBean ();
	}
}
