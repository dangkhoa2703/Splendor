package entity

/**
 * Class to maintain a tree
 */
class TreeNode<T>(var data: T) {

    private var children: MutableList<TreeNode<T>> = mutableListOf()
    private var parent: TreeNode<T>? = null

    /**
     * Adds a children to the tree
     */
    fun addChildren(child: T): TreeNode<T> {
        val childNode = TreeNode(child)
        childNode.parent = this
        children.add(childNode)
        return childNode
    }

    /**
     * Returns the children of the tree
     */
    fun getChildren() : List<TreeNode<T>> {
        return children
    }

    /**
     * Returns the parent of the tree
     */
    fun getParent() : TreeNode<T>? {
        return parent
    }

}