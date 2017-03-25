public void remove(K key) 
    {
        // DONE: Implement me(EC beyond full score)
        
        // Implementation inspired by the example approach given by prof 
        // Vigfusson in class,  taken entirely from memory not copied.  

        if (root == null)
            return;

        int cmp = root.kv.key.compareTo(key);

        //if root is the element to remove
        if (cmp == 0)
            root = removeRecur(key, root);
        

        // root is larger than item to remove, so it must be in left subtree 
        // (if it exists)
        else if (cmp > 0)
            root.left = removeRecur(key, root.left);
        

        // root is smaller than item to remove, so it must be in right  
        // subtree (if it exists)
        else
            root.right = removeRecur(key, root.right);
    }

    // this recursive method either returns the node it was given 
    // (potentially with a subtree modified by another recursive call to 
    // removeRecur) or it returns the new node which replaces the node we 
    // removed. this solves the issue of making sure that, when we remove 
    // a node, we have its parent point to the node which replaced it.  
    // -
    // @param key - key for node to remove (if it exists)
    // @param node - position in the tree. 
    // @returns - either @param node or the node which replaces it
    private Node removeRecur(K key, Node node)
    {
        if (node == null) // base case 1 -- we did not find element to remove
            return null;

        int cmp = node.kv.key.compareTo(key);

        if (cmp == 0) // base case 2 - we found the element to remove
        {
            // if theres no right subtree replace self with left subtree. 
            // this properly handles case where left and right are both null
            if (node.right == null) 
                return node.left;

            // if right subtree has no left child, it can become (sub)root 
            // simply by making its left child the current (sub)root's left 
            // child and returning it.  
            if (node.right.left == null)
            {
                node.right.left = node.left;
                return node.right;
            }


            // traverses left until we find the first node whose left child
            // has no left child (means tmp.left is the smallest node
            // in the right subtree. then make tmp.left the new root.
            Node tmp = node.right;
            while (tmp.left.left != null)
                tmp = tmp.left;

            Node becomesRoot = tmp.left;
            
            // handles case where becomesRoot has a right subtree, 
            // and case when it's a leaf.
            tmp.left = becomesRoot.right; 

            // gives appopriate left and right subtrees
            becomesRoot.right = node.right; 
            becomesRoot.left = node.left;
            
            // return assigns this to its parent right or left pointer 
            return becomesRoot;
        }

        // node < key, so if key exists it must be on the right, recur right
        else if (cmp < 0)
        {
            node.right = removeRecur(key, node.right);
            return node;
        }

        else //cmp > 0 so node larger than key so go left
        {
            node.left = removeRecur(key, node.left);
            return node;
        }
    }