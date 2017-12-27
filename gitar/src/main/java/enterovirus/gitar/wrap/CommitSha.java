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
	
	/*
	 * This constructor is mainly used for writing tests.
	 * It gets the commit SHA value of a pre-defined txt file. 
	 */
	public CommitSha (File commitFilepath, Integer commitOrder) throws IOException {
		try (Stream<String> stream = Files.lines(Paths.get(commitFilepath.getAbsolutePath()))) {

			Integer lineNumber = 1;
			for (String line : stream.toArray(String[]::new)) {
				if (lineNumber.equals(commitOrder)) {
					this.shaChecksumHash = line;
					return;
				}
				++lineNumber;
			}
			
			throw new IOException ("commitOrder does not exist!");
		}
	}
	
	public String getShaChecksumHash () {
		return shaChecksumHash;
	}
}
