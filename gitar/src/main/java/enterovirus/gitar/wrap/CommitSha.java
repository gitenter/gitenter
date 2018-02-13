package enterovirus.gitar.wrap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class CommitSha {
	
	private String shaChecksumHash;
	
	public CommitSha (String shaChecksumHash) {
		this.shaChecksumHash = shaChecksumHash;
	}
	
	@Override
	public boolean equals(Object anotherCommitSha) {
		
		if (this.shaChecksumHash.equals(((CommitSha)anotherCommitSha).shaChecksumHash)) {
			return true;
		}
		return false;
	}
	
	/*
	 * This constructor is mainly used for writing tests.
	 * It gets the commit SHA value of a pre-defined txt file. 
	 */
	public CommitSha (File commitFilepath, Integer lineNumber) throws IOException {
		try (Stream<String> stream = Files.lines(Paths.get(commitFilepath.getAbsolutePath()))) {

			Integer currentLineNumber = 1;
			for (String line : stream.toArray(String[]::new)) {
				if (lineNumber.equals(currentLineNumber)) {
					this.shaChecksumHash = line;
					return;
				}
				++currentLineNumber;
			}
			
			throw new IOException ("commit SHA list file "+commitFilepath+" does not exist!");
		}
	}
	
	public String getShaChecksumHash () {
		return shaChecksumHash;
	}
	
	/*
	 * This is the value of the old commit SHA git "update"/"post-receive" hooks
	 * gives, when it is the first push with no previous commit.
	 */
	public boolean isNull () {
		if (shaChecksumHash.equals("0000000000000000000000000000000000000000")) {
			return true;
		}
		return false;
	}
}
