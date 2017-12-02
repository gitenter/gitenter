package enterovirus.capsid.database;

import java.io.IOException;
import java.util.List;

import enterovirus.capsid.domain.*;

public interface DocumentRepository {

	public List<DocumentBean> findById(Integer id) throws IOException;
}
