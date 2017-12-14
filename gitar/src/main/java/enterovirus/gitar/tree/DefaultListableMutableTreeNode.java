package enterovirus.gitar.tree;

import java.util.Collections;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

public class DefaultListableMutableTreeNode extends DefaultMutableTreeNode implements ListableMutableTreeNode {

	public DefaultListableMutableTreeNode() {
		// TODO Auto-generated constructor stub
	}

	public DefaultListableMutableTreeNode(Object arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public DefaultListableMutableTreeNode(Object arg0, boolean arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<ListableTreeNode> childrenList() {
		return Collections.list(this.children());
	}

}
