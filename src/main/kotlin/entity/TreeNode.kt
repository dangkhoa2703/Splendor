package entity

import java.lang.IllegalArgumentException

/**
 * Class to maintain a tree
 */
class TreeNode<T>(var data: T?) {

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

    companion object {
        /**
         * Create tree with defined [depth] and [childrenCount]
         */
        fun <T> createEmptyTree(depth: Int, childrenCount: Int): TreeNode<T>  {
            if(depth == 0) {
                throw IllegalArgumentException()
            }
            val root: TreeNode<T> = TreeNode(null)
            if(depth == 1) {
                return root
            }
            for(i in 0 until childrenCount) {
                val children: TreeNode<T> = createEmptyTree(depth - 1, childrenCount)
                children.parent = root
                root.children.add(children)
            }
            return root
        }
    }

}