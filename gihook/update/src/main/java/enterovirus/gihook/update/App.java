package enterovirus.gihook.update;

public class App {
	
	public static void main (String[] args) {
		
		String branchName = args[0];
		String oldCommitSha = args[1];
		String newCommitSha = args[2];
		
		System.out.println("branchName: "+branchName);
		System.out.println("oldCommitSha: "+oldCommitSha);
		System.out.println("newCommitSha: "+newCommitSha);
	}
}
