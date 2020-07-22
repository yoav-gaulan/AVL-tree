/**
 *
 * AVLTree - Yoav Gaulan 
 *
 * An implementation of a AVL Tree with
 * distinct integer keys and info
 *
 */

public class AVLTree {

    private IAVLNode minNode;
    private IAVLNode maxNode;
    private IAVLNode root;
    private AVLNode NIL;

    public AVLTree() {
        AVLNode NIL = new AVLNode();
        this.maxNode = null;
        this.minNode = null;
        this.NIL = NIL;
        this.root = NIL;

    }

                               //////// Public functions ////////


    public IAVLNode minNode(){
        return minNode;
    }
    /**
     * set root for empty tree
     */


    public void setRoot(int k, String s) {
        IAVLNode x = new AVLNode(k, s, NIL);
        this.root = x;
        this.maxNode = this.minNode = x;
    }


    /**
     * public boolean empty()
     * <p>
     * returns true if and only if the tree is empty
     */

    public boolean empty() {
        return this.root == null | this.root == NIL;
    }

    /**
     * public String search(int k)
     * <p>
     * returns the info of an item with key k if it exists in the tree
     * otherwise, returns null
     */
    public String search(int k) {
        return (recSearch(this.root, k)).getValue();
    }

    /**
     * public int insert(int k, String i)
     * <p>
     * inserts an item with key k and info i to the AVL tree.
     * the tree must remain valid (keep its invariants).
     * returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
     * returns -1 if an item with key k already exists in the tree.
     */
    public int insert(int k, String i) {
        if (empty()) {
            this.setRoot(k, i);
            return 0;
        } else {
            int cnt = 0;
            IAVLNode y = insertRec(root, NIL, k, i);
            if (y == null) {
                return -1;
            } else {
                boolean changed = true;
                while ((y != NIL ) & changed) {
                    int oldHeight = y.getHeight();
                    updateHeight(y);
                    changed = (oldHeight != y.getHeight());
                    int bf = BF(y);
                    if (bf > 1 | bf < -1) {
                        return BalanceAfterInsert(bf, y);

                    } else {
                        y = y.getParent();
                    }
                }
                return cnt;
            }
        }
    }

    /**
     * public int delete(int k)
     * <p>
     * deletes an item with key k from the binary tree, if it is there;
     * the tree must remain valid (keep its invariants).
     * returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
     * returns -1 if an item with key k was not found in the tree.
     */
    public int delete(int k) {
        if (empty() | search(k) == null) {
            return -1;
        }
        int num_of_ops = 0 ;
        IAVLNode to_del = deleteRecSearch(this.root, k);
        //Case: node isn't in the tree or the tree is already empty
        actuallDelete(to_del);
        IAVLNode y = to_del.getParent();
        boolean changed = true;
        while (y != NIL) {
            int oldHeight = y.getHeight();
            updateHeight(y);
            int bf_y = BF(y);
            changed = (oldHeight != y.getHeight());

            if ((bf_y == -1 | bf_y == 0 | bf_y == 1) && (!changed)) {
                return num_of_ops ;
            }
            else if ((bf_y == -1 | bf_y == 0 | bf_y == 1) && (changed)) {
                y = y.getParent() ;
            }
            else if ((bf_y == 2 || bf_y == -2 ) ) {
                //Now we deleted the node, but remained with unbalanced tree, let's fix it:
                num_of_ops += deleteCase(y, bf_y) ;
                y= y.getParent().getParent() ;
            }

        }


        return num_of_ops ;
    }


    /**
     * public String min()
     * <p>
     * Returns the info of the item with the smallest key in the tree,
     * or null if the tree is empty
     */
    public String min() {
        if (this.empty()) {
            return null;
        } else {
            return this.minNode.getValue();
        }

    }

    /**
     * public String max()
     * <p>
     * Returns the info of the item with the largest key in the tree,
     * or null if the tree is empty
     */
    public String max() {
        if (this.empty()) {
            return null;
        } else {
            return this.maxNode.getValue();
        }
    }


    /**
     * public int[] keysToArray()
     * <p>
     * Returns a sorted array which contains all keys in the tree,
     * or an empty array if the tree is empty.
     */
    public int[] keysToArray() {

        int[] arr = new int[size()];
        if (size() > 0) {
            Inorder(root, 0, arr);
        }

        return arr;
    }

    /**
     * public String[] infoToArray()
     *
     * Returns an array which contains all info in the tree,
     * sorted by their respective keys,
     * or an empty array if the tree is empty.
     */
    public String[] infoToArray ()
    {
        String[] arr = new String[size()];
        if(size() > 0) {
            InorderStrings(root, 0, arr);
        }
        return arr;
    }

    /**
     * public int size()
     *
     * Returns the number of nodes in the tree.
     *
     * precondition: none
     * postcondition: none
     */
    public int size ()
    {
        if(empty()){
            return 0;
        }
        else{
            return root.getSubtreeSize(); //
        }
    }

    /**
     * public int getRoot()
     *
     * Returns the root AVL node, or null if the tree is empty
     *
     * precondition: none
     * postcondition: none
     */
    public IAVLNode getRoot ()
    {
        if(this.empty()){
            return null;
        }
        else{
            return this.root;
        }
    }
    /**
     * public string select(int i)
     *
     * Returns the value of the i'th smallest key (return null if tree is empty)
     * Example 1: select(1) returns the value of the node with minimal key
     * Example 2: select(size()) returns the value of the node with maximal key
     * Example 3: select(2) returns the value 2nd smallest minimal node, i.e the value of the node minimal node's successor
     *
     * precondition: size() >= i > 0
     * postcondition: none
     */
    public String select(int i)
    {
        IAVLNode subRoot = subTreeRootSelect(i);
        return NaiveSelect(subRoot, i);
    }

    /**
     * public int less(int i)
     *
     * Returns the sum of all keys which are less or equal to i
     * i is not neccessarily a key in the tree
     *
     * precondition: none
     * postcondition: none
     */
    public int less ( int i)
    {
        return iterativeLess(root, i);
    }

                        /////////// Private functions ///////////


    /**
     * set the min node of the tree
     */
    private void setMin(IAVLNode x) {
        this.minNode = x;
    }

    /**
     * set the max node of the tree
     */
    private void setMax(IAVLNode x) {
        this.maxNode = x;
    }

    /**
     * update min/max if needed after insertion
     */
    private void updateEdges(IAVLNode x) {
        if (x.getKey() < this.minNode.getKey()) {
            this.setMin(x);
        }
        if (x.getKey() > this.maxNode.getKey()) {
            this.setMax(x);
        }
    }


    /**
     * calculate and set the new  size of node x
     */
    private void updateSize(IAVLNode x) {
        x.setSubtreeSize(1 + x.getLeft().getSubtreeSize() + x.getRight().getSubtreeSize());
    }


    /**
     * helper function for balance subtree after insertion and returns number of rotations
     */

    private int BalanceAfterInsert(int bf, IAVLNode node) {
        if (bf == 2) {
            int childBF = BF(node.getLeft());
            if (childBF == 1) {
                rotateLL(node);
                return 1;
            } else if (childBF == -1) {
                rotateLR(node);
                return 2;
            }

        } else if (bf == -2) {
            int childBF = BF(node.getRight());
            if (childBF == 1) {
                rotateRL(node);
                return 2;
            } else if (childBF == -1) {
                rotateRR(node);
                return 1;
            }

        }


        return 0;

    }



    /** update size and height to node */
    private void updateFields(IAVLNode node){
        updateSize(node);
        updateHeight(node);
        updateSum(node);
    }



    /**
     * Rotates the given subtree to the right.
     *
     * @return the right rotated subtree
     */
    private IAVLNode rotateLL(IAVLNode x) {
        IAVLNode y = x.getLeft();
        IAVLNode z = x.getParent();
        x.setLeft(y.getRight());
        if (z != NIL) {
            if(z.getLeft() == x){
                z.setLeft(y);
            }
            else{
                z.setRight(y);
            }
        }
        y.setRight(x);
        updateFields(x);
        updateFields(y);
        if(z==NIL){
            this.root = y;
            y.setParent(NIL);
        }
        return y;
    }

    /**
     * Rotates the given subtree to the left.
     *
     * @return left rotated subtree
     */
    private IAVLNode rotateRR(IAVLNode x) {
        IAVLNode y = x.getRight();
        IAVLNode z = x.getParent();
        x.setRight(y.getLeft());
        if (z != NIL) {
            if(z.getLeft() == x){
                z.setLeft(y);
            }
            else{
                z.setRight(y);
            }
        }
        y.setLeft(x);
        updateFields(x);
        updateFields(y);
        if(z==NIL){
            this.root = y;
            y.setParent(NIL);
        }
        return y;

    }

    /**
     * double rotate LR
     */
    private IAVLNode rotateLR(IAVLNode x) {
        x.setLeft(rotateRR(x.getLeft()));
        x = rotateLL(x);
        return x;
    }

    /**
     * double rotate RL
     */
    private IAVLNode rotateRL(IAVLNode x) {
        x.setRight(rotateLL(x.getRight()));
        x = rotateRR(x);
        return x;
    }




    /**
     * update the new height gor node x
     */

    private void updateHeight(IAVLNode x) {
        x.setHeight(Math.max(x.getLeft().getHeight(), x.getRight().getHeight()) + 1);
    }

    /**
     * add one to node.size (+1)
     */

    private void SizePlus(IAVLNode x) {
        x.setSubtreeSize(x.getSubtreeSize() + 1);
    }


    /**
     * Recursive search with subtree and key
     * return the key's node or null if the key not in the subtree
     */

    private IAVLNode recSearch(IAVLNode x, int k) {
        if (x == null | x == NIL) {
            return NIL;
        }
        if (x.getKey() == k) {
            return x;
        } else if (x.getKey() < k) {
            return recSearch(x.getRight(), k);
        } else {
            return recSearch(x.getLeft(), k);
        }
    }


    /** update size after deletion (size = size - 1 );
     *
     */
    private void sizeMinus(IAVLNode x){
        int newSize = x.getSubtreeSize() - 1;
        x.setSubtreeSize(newSize);
    }


    /** update subTree keys sum */

    private void updateSum(IAVLNode x){
        int newSum = x.getKey() + x.getLeft().getSum() + x.getRight().getSum();
        x.setSum(newSum);
    }

    /** update the subTree keys some after insertion */

    private void updateSumAfterInsert(IAVLNode x, int newKey){
        int newSum = x.getSum() + newKey;
        x.setSum(newSum);
    }

    /** update the subtree sum after deletion */
    private void updateSumAfterDeletion(IAVLNode x, int key){
        int newSum = x.getSum() - key;
        x.setSum(newSum);
    }



    /**
     * helper function for insertion, returns the parent of the new node
     */


    private IAVLNode insertRec(IAVLNode x, IAVLNode prev, int k, String s) {

        if (x == NIL) {
            IAVLNode newchild = new AVLNode(k, s, prev);
            updateEdges(newchild);
            if (prev.getKey() > k) {
                prev.setLeft(newchild);

            } else {
                prev.setRight(newchild);
            }
            return prev;
        }

        if (x.getKey() == k) {
            return null;
        } else if (x.getKey() < k) {
            IAVLNode toReturn = insertRec(x.getRight(), x, k, s);
            if (toReturn != null) {
                SizePlus(x);
                updateSumAfterInsert(x, k);
            }
            return toReturn;

        } else {
            IAVLNode toReturn = insertRec(x.getLeft(), x, k, s);
            if (toReturn != null) {
                SizePlus(x);
                updateSumAfterInsert(x,k);
            }

            return toReturn;
        }
    }

    /** search the node we want to delete, if the node is not inner node, updates the nodes size & sum on the path */

    private IAVLNode deleteRecSearch(IAVLNode x, int k) {

        if (x == NIL) {
            return null;
        }

        if (x.getKey() == k) {
            return x;
        } else if (x.getKey() < k) {
            IAVLNode toReturn = deleteRecSearch(x.getRight(),  k);
            if (toReturn != null & NotInnerNode(toReturn)) {
                sizeMinus(x);
                updateSumAfterDeletion(x,k);
            }
            return toReturn;

        } else {
            IAVLNode toReturn = deleteRecSearch(x.getLeft(), k);
            if (toReturn != null & NotInnerNode(toReturn) ) {
                sizeMinus(x);
                updateSumAfterDeletion(x,k);
            }

            return toReturn;
        }
    }




    /**returns True only if node x has less then 2 real children*/
    private boolean NotInnerNode(IAVLNode x){
        return (x.getLeft()== NIL | x.getRight()== NIL);
    }


    /** returns the balance factor of node x */
    private int BF(IAVLNode x) {
        if(x == NIL){
            return 0;
        }
        int bf = x.getLeft().getHeight() - x.getRight().getHeight();
        return bf;
    }


    private IAVLNode successor(IAVLNode node) {

        if (node.getKey() == maxNode.getKey() ){
            return NIL ;
        }

        else if  (node.getRight() != NIL) {
            return nextMin(node.getRight());
        }
        else {
            IAVLNode parent_node = node.getParent() ;
            while ((parent_node != NIL) && (parent_node.getRight() == node)) {
                node = parent_node;
                parent_node = node.getParent();
            }
            return parent_node ;


        }

    }

    //helper function to find the successor//
    private IAVLNode nextMin(IAVLNode node) {
        IAVLNode current = node;
        while(current.getLeft() != NIL){
            current = current.getLeft();
        }
        return current;
    }


    private IAVLNode predecessor(IAVLNode node) {

        if (node.getKey() == minNode.getKey() ){
            return NIL ;
        }

        else if ( (node.getLeft() != NIL)) {
            return prevMax(node.getLeft());
        }
        else {
            IAVLNode parent_node = node.getParent() ;
            while ((parent_node != NIL) && (parent_node.getLeft() == node)) {
                node = parent_node;
                parent_node = node.getParent();
            }
            return parent_node ;


        }

    }

    //helper function to find predecessor//
    private IAVLNode prevMax(IAVLNode node) {

        IAVLNode next_node = node ;
        while (next_node.getRight() != NIL) {
            next_node = next_node.getRight() ;
        }
        return next_node ;
    }






    /** balance after deletion, returns the numbers of the rotations **/
    private int deleteCase(IAVLNode y, int bf ) {

        IAVLNode y_right = y.getRight();
        IAVLNode y_left = y.getLeft();
        if (bf == 2) {
            if ((BF(y_left) == 0) || (BF(y_left) == 1)) {
                rotateLL(y);
                return 1;
            } else if (BF(y_left) == -1) {
                rotateLR(y);
                return 2;
            }
        }
        else { //here bf == -2
            if (BF(y_right) == 1) {
                rotateRL(y);
                return 2;
            } else {
                rotateRR(y);
                return 1;
            }

        }
        return 0;
    }





    /** min and max update after deletion **/
    private void updateEdgesForDelete(IAVLNode x) {
        if (this.size() <= 1) {
            this.minNode = this.maxNode = null ;
        } else {
            if (x == minNode) {
                this.minNode = successor(x);
            }

            if (x == maxNode) {
                this.maxNode = predecessor(x);
            }
        }
    }

    /**execution of delete**/
    private void actuallDelete(IAVLNode ToDelNode) {
        updateEdgesForDelete(ToDelNode);
        //Case: the node we want to delete is a leaf.
        if (!(ToDelNode.getRight().isRealNode()) && !(ToDelNode.getLeft().isRealNode())) {
            if (ToDelNode.getParent() == NIL) {
                this.root = NIL;
            }
            else{
                if(ToDelNode.getParent().getLeft() == ToDelNode){
                    ToDelNode.getParent().setLeft(NIL);
                }
                else {
                    ToDelNode.getParent().setRight(NIL);
                }
            }

        }
        //Case: the node we want do delte has only 1 real child , and we bypass (2 options)
        else if (ToDelNode.getRight().isRealNode() && ToDelNode.getLeft()== NIL ) {
            if(ToDelNode == root){
                this.root = ToDelNode.getRight();
                ToDelNode.getRight().setParent(NIL);
            }
            if (ToDelNode.getParent().getRight() == ToDelNode) {
                ToDelNode.getParent().setRight(ToDelNode.getRight());
            } else {
                ToDelNode.getParent().setLeft(ToDelNode.getRight());

            }
        }
        else if (ToDelNode.getRight() == NIL  && ToDelNode.getLeft().isRealNode() ) {
            if(ToDelNode == root){
                this.root = ToDelNode.getLeft();
                ToDelNode.getLeft().setParent(NIL);
            }
            if (ToDelNode.getParent().getRight() == ToDelNode) {
                ToDelNode.getParent().setRight(ToDelNode.getLeft());
            } else {
                ToDelNode.getParent().setLeft(ToDelNode.getLeft());
            }
            //Case: regular node with 2 child's
        }
        else{
            IAVLNode to_del_successor = successor(ToDelNode);
            replaceNodes(ToDelNode, to_del_successor);
            updateSizeAfterReplaceDEL(ToDelNode);
            actuallDelete(ToDelNode);



        }

    }



/** if we deleted inner node we didnt updated nodes size & sum on the way, so we do it now**/
    private void updateSizeAfterReplaceDEL(IAVLNode x){
        while(x != root){
            sizeMinus(x.getParent());
            updateSum(x.getParent());
            x = x.getParent();

        }
    }


    /** switch  nodes parents between father(nodeA) and child(nodeB) */

    private void switchFatherSonParents( IAVLNode nodeA, IAVLNode nodeB){
        IAVLNode parA = nodeA.getParent();
        if(parA == NIL){
            nodeB.setParent(NIL);
            root = nodeB;

        }
        else if(nodeA.getParent().getLeft() == nodeA) {
            parA.setLeft(nodeB);
        }
        else{
            parA.setRight(nodeB);
        }
        if(nodeA.getKey() > nodeB.getKey()){
            nodeB.setRight(nodeA);
        }
        else{
            nodeB.setLeft(nodeA);
        }
    }




    /** switch between two nodes parents */

    private void switchParets(IAVLNode nodeA, IAVLNode nodeB){
        IAVLNode parentA = nodeA.getParent();
        IAVLNode parentB = nodeB.getParent();
        boolean parentA_NIL = false;
        boolean parentB_NIL = false;

        if (parentA == NIL) {
            nodeB.setParent(NIL);
            root = nodeB;
            parentA_NIL = true;
        } else if (parentB == NIL) {
            nodeA.setParent(NIL);
            root = nodeA;
            parentB_NIL = true;
        }

        if (!parentA_NIL) {
            if (parentA.getKey() > nodeA.getKey()) {
                parentA.setLeft(nodeB);
            } else {
                parentA.setRight(nodeB);
            }
        }
        if (!parentB_NIL) {
            if (parentB.getKey() > nodeB.getKey()) {
                parentB.setLeft(nodeA);
            } else {
                parentB.setRight(nodeA);
            }
        }
    }



    /** switch between two nodes  */

    private void replaceNodes(IAVLNode nodeA, IAVLNode nodeB){
        if(nodeA == nodeB.getParent()){
            replaceParentAndChild(nodeA,nodeB);
        }
        else if( nodeB == nodeA.getParent()){
            replaceParentAndChild(nodeB,nodeA);
        }
        else {

            IAVLNode nodeA_left = nodeA.getLeft();
            IAVLNode nodeA_right = nodeA.getRight();
            int sizeA = nodeA.getSubtreeSize();
            int heightA = nodeA.getHeight();
            switchParets(nodeA, nodeB);
            //change A's data to B's data//
            nodeA.setSubtreeSize(nodeB.getSubtreeSize());
            nodeA.setHeight(nodeB.getHeight());
            nodeA.setLeft(nodeB.getLeft());
            nodeA.setRight(nodeB.getRight());
            //change B's data to A's data//
            nodeB.setSubtreeSize(sizeA);
            nodeB.setHeight(heightA);
            nodeB.setLeft(nodeA_left);
            nodeB.setRight(nodeA_right);
            updateSum(nodeA);
            updateSum(nodeB);
        }
    }


    /** switch between two nodes when nodeA is the parent nodeB one of his children**/
    private void replaceParentAndChild(IAVLNode nodeA, IAVLNode nodeB){
        IAVLNode leftB = nodeB.getLeft();
        IAVLNode rightB = nodeB.getRight();
        int sizeB = nodeB.getSubtreeSize();
        int heightB = nodeB.getHeight();
        switchFatherSonParents(nodeA,nodeB);
        nodeB.setHeight(nodeA.getHeight());
        nodeB.setSubtreeSize(nodeA.getSubtreeSize());
        if(nodeA.getLeft() == nodeB){
            nodeB.setRight(nodeA.getRight());
            nodeB.setLeft(nodeA);
        }
        else{
            nodeB.setLeft(nodeA.getLeft());
            nodeB.setRight(nodeA);
        }
        nodeA.setRight(rightB);
        nodeA.setLeft(leftB);
        nodeA.setSubtreeSize(sizeB);
        nodeA.setHeight(heightB);
        updateSum(nodeA);
        updateSum(nodeB);



    }






    /** helper function for keysToArray, inorder walk  */
    private int Inorder(IAVLNode node, int index, int[] arr){
        if(node != NIL){
            index = Inorder(node.getLeft(), index, arr);
            arr[index] = node.getKey();
            index++;
            index = Inorder(node.getRight(), index, arr);
        }
        return index;
    }



    /**  helper function  for infoToArray , inorder walk */
    private int InorderStrings (IAVLNode node, int index, String[] arr){
        if(node != NIL){
            index = InorderStrings(node.getLeft(), index, arr);
            arr[index] = node.getValue();
            index++;
            index = InorderStrings(node.getRight(),index, arr);
        }
        return index;
    }








/** finds the subtree root for execution select in O(i) */
    private IAVLNode subTreeRootSelect(int i){
        IAVLNode newSelectRoot = minNode;
        if(i > this.size() || i < 1){
            return NIL;
        }
        while (newSelectRoot.getSubtreeSize() < i && (newSelectRoot != NIL || newSelectRoot != null)){
            newSelectRoot = newSelectRoot.getParent();
        }
        return newSelectRoot;
    }


/** naive select function , helper function to Select */

    private String NaiveSelect (IAVLNode subtreeRoot ,int i) {
        if (subtreeRoot == NIL || subtreeRoot == null || i < 1 || subtreeRoot.getSubtreeSize() < i ) {
            return null ;

        }
        else {
            return selectRec(subtreeRoot, i);

        }

    }


/** helper recursive function for select **/
    private String selectRec(IAVLNode node,int i) {
        int left_size = node.getLeft().getSubtreeSize() + 1  ;
        if (left_size == i ) {
            return node.getValue() ;
        }
        else if (i < left_size  ) {
            return selectRec(node.getLeft(),i   ) ;

        }
        else {

            return selectRec(node.getRight(), i - left_size) ;
        }
    }






/** helper function for Less: iterative search for the key k , and sums all the keys then less then k */
private int iterativeLess(IAVLNode curr, int k) {
    int summ = 0;
    IAVLNode prev = curr.getParent();
    while(curr != NIL && curr.getKey() != k){
        prev = curr;
        if(k < curr.getKey()){
            curr = curr.getLeft();

        }
        if(k > curr.getKey()){
            summ += curr.getKey();
            summ += curr.getLeft().getSum();
            curr = curr.getRight();
        }
    }
    if(curr.getKey() == k){
        summ += curr.getKey();
        summ += curr.getLeft().getSum();
        return summ;
    }

    return summ;

}


/** returns the sum of all keys in the tree **/
private int SubtreeSum(IAVLNode SubRoot ){
    int keySum = 0;
    if(SubRoot != NIL){
        keySum += SubRoot.getKey();
        keySum += SubtreeSum(SubRoot.getLeft());
        keySum += SubtreeSum(SubRoot.getRight());



    }
    return keySum;

}



    /**
     * public interface IAVLNode
     * ! Do not delete or modify this - otherwise all tests will fail !
     */
    public interface IAVLNode {
        public int getKey(); //returns node's key (for virtuval node return -1)

        public String getValue(); //returns node's value [info] (for virtuval node return null)

        public void setLeft(IAVLNode node); //sets left child

        public IAVLNode getLeft(); //returns left child (if there is no left child return null)

        public void setRight(IAVLNode node); //sets right child

        public IAVLNode getRight(); //returns right child (if there is no right child return null)

        public void setParent(IAVLNode node); //sets parent

        public IAVLNode getParent(); //returns the parent (if there is no parent return null)

        public boolean isRealNode(); // Returns True if this is a non-virtual AVL node

        public void setSubtreeSize(int size); // sets the number of real nodes in this node's subtree

        public int getSubtreeSize(); // Returns the number of real nodes in this node's subtree (Should be implemented in O(1))

        public void setHeight(int height); // sets the height of the node

        public int getHeight(); // Returns the height of the node (-1 for virtual nodes)

        public int getSum(); // Returns the key's sum of the node subtree

        public void setSum(int newSum); // sets new sum of the node
    }


    /**
     * public class AVLNode
     *
     * If you wish to implement classes other than AVLTree
     * (for example AVLNode), do it in this file, not in
     * another file.
     * This class can and must be modified.
     * (It must implement IAVLNode)
     */
    public class AVLNode implements IAVLNode{

        private int key;
        private String info;
        private IAVLNode parent;
        private IAVLNode left;
        private IAVLNode right;
        private int size;
        private int height;
        private int sum;


        public AVLNode(int key,String info, IAVLNode parent) {
            this.key = key;
            this.info = info;
            this.parent = parent;
            this.size = 1;
            this.height = 0;
            this.left = NIL;
            this.right = NIL;
            this.sum = key;
        }

        private AVLNode() {
            this(-1, null, null);
            this.size = 0;
            this.height = -1;
            this.left = null;
            this.right = null;
            this.sum = 0;
        }


        public int getKey()
        {
            return this.key;
        }
        public String getValue()
        {
            return this.info;
        }
        public void setLeft(IAVLNode node) {
            this.left = node;
            if(node!= NIL & node != null){
                node.setParent(this);
            }
        }

        public IAVLNode getLeft()
        {
            return this.left;
        }
        public void setRight(IAVLNode node) {
            this.right = node;
            if(node!= NIL & node != null){
                node.setParent(this);
            }
        }
        public IAVLNode getRight()
        {
            return this.right;
        }
        public void setParent(IAVLNode node)
        {
            this.parent = node;
        }
        public IAVLNode getParent()
        {
            return this.parent;
        }
        // Returns True if this is a non-virtual AVL node
        public boolean isRealNode()
        {
            if(this.key == -1) {
                return false;
            }
            else {
                return true;
            }
        }

        public void setSubtreeSize(int size)
        {
            this.size = size;
        }
        public int getSubtreeSize()
        {
            return this.size;
        }
        public void setHeight(int height)
        {
            this.height = height;
        }
        public int getHeight()
        {
            return this.height;
        }
        public int getSum(){
            return this.sum;
        }
        public void setSum(int newSum){
            this.sum = newSum;
        }









    }
}
