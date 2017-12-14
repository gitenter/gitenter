package enterovirus.gitar.tree;

import java.util.List;
import javax.swing.tree.TreeNode;

public interface ListableTreeNode extends TreeNode {

	List<ListableTreeNode> childrenList();
}
