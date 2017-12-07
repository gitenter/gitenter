package enterovirus.gitar.wrap;

import java.util.ArrayList;
import java.util.List;

public class Folderpath extends FolderAndFilepath{

	private List<FolderAndFilepath> subFolderAndFilepaths = new ArrayList<FolderAndFilepath>();
	
	public Folderpath(String relativePath) {
		super(relativePath);
	}
	
	public boolean addFolderOrFile (FolderAndFilepath subFolderAndFilepath) {
		return subFolderAndFilepaths.add(subFolderAndFilepath);
	}

	public List<FolderAndFilepath> getSubFolderAndFilepaths() {
		return subFolderAndFilepaths;
	}
}
