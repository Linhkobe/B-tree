package fr.miage.btree;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;


public class Btree <TKey extends Comparable<TKey>, TValue> implements Serializable {

    @JsonView(Views.Public.class)
    private Node<TKey> root;


    public Btree() {
        this.root = new LeafNode<TKey, TValue>();
    }


    /**
     * Insert a new key and its associated value into the B+ tree.
     */
    public void insert(TKey key, TValue value) {
        LeafNode<TKey, TValue> leaf = this.findLeafNodeShouldContainKey(key);
        leaf.insertKey(key, value);

        if (leaf.isOverflow()) {
            Node<TKey> n = leaf.dealOverflow();
            if (n != null)
                this.root = n;
        }
    }

    /**
     * Search a key value on the tree and return its associated value.
     */
    public TValue search(TKey key) {
        LeafNode<TKey, TValue> leaf = this.findLeafNodeShouldContainKey(key);

        int index = leaf.search(key);
        return (index == -1) ? null : leaf.getValue(index);
    }

    /**
     * Delete a key and its associated value from the tree.
     */
    public void delete(TKey key) {
        LeafNode<TKey, TValue> leaf = this.findLeafNodeShouldContainKey(key);

        if (leaf.delete(key) && leaf.isUnderflow()) {
            Node<TKey> n = leaf.dealUnderflow();
            if (n != null)
                this.root = n;
        }
    }

    /**
     * Search the leaf node which should contain the specified key
     */
    @SuppressWarnings("unchecked")
    private LeafNode<TKey, TValue> findLeafNodeShouldContainKey(TKey key) {
        Node<TKey> node = this.root;
        while (node.getNodeType() == NodeType.InternalNode) {
            node = ((InternalNode<TKey>)node).getChild( node.search(key) );
        }

        return (LeafNode<TKey, TValue>)node;
    }
    // Method to collect all keys
    public List<TKey> getAllKeys() {
        List<TKey> keys = new ArrayList<>();
        collectAllKeys(this.root, keys);
        return keys;
    }

    // Recursive helper method to traverse and collect keys
    private void collectAllKeys(Node<TKey> node, List<TKey> keys) {
        if (node instanceof LeafNode) {
            LeafNode<TKey, TValue> leaf = (LeafNode<TKey, TValue>) node;
            keys.addAll(leaf.keys);
        } else if (node instanceof InternalNode) {
            InternalNode<TKey> internal = (InternalNode<TKey>) node;
            for (Node<TKey> child : internal.children) {
                collectAllKeys(child, keys);
            }
        }
    }

    @Override
    public String toString() {
        return this.root.toString();
    }
}
