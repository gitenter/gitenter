package enterovirus.protease.database;

import java.io.IOException;

import enterovirus.protease.domain.*;

public interface SshKeyRepository {

	public SshKeyBean saveAndFlush(SshKeyBean sshKey) throws IOException;
}
