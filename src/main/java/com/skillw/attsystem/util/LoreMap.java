package com.skillw.attsystem.util;

import java.util.concurrent.ConcurrentHashMap;


public class LoreMap<T> {

    private final TrieNode root = new TrieNode();

    private final boolean ignorePrefix;
    private final boolean ignoreSpace;
    private final boolean ignoreColor;


    public LoreMap(boolean ignoreSpace, boolean ignoreColor, boolean ignorePrefix) {
        this.ignoreSpace = ignoreSpace;
        this.ignoreColor = ignoreColor;
        this.ignorePrefix = ignorePrefix;
    }


    public LoreMap() {
        this(true, true, false);
    }


    public void put(String lore, T value) {
        lore = lore.replaceAll("&", "§");
        if (this.ignoreSpace) {
            lore = lore.replaceAll("\\s", "");
        }
        if (this.ignoreColor) {
            lore = lore.replaceAll("§.", "");
        }
        int depth = 0;
        TrieNode current = this.root;
        while (depth < lore.length()) {
            LoreChar c = new LoreChar(lore.charAt(depth));
            if (current.child.containsKey(c)) {
                current = current.child.get(c);
            } else {
                TrieNode node = new TrieNode();
                node.depth++;
                node.pre = current;
                current.child.put(c, node);
                current = node;
            }
            if (depth == lore.length() - 1) {
                current.obj = value;
            }
            depth++;
        }
    }

    public T get(String lore) {
        int depth = 0;
        if (this.ignoreSpace) {
            lore = lore.replaceAll("\\s", "");
        }
        if (this.ignoreColor) {
            lore = lore.replaceAll("§.", "");
        }
        TrieNode current = this.root;
        if (this.ignorePrefix) {
            while (depth < lore.length()) {
                if (this.root.child.containsKey(new LoreChar(lore.charAt(depth)))) {
                    break;
                }
                depth++;
            }
        }
        while (depth < lore.length()) {
            LoreChar c = new LoreChar(lore.charAt(depth));
            TrieNode node = current.child.get(c);
            if (node == null) {
                return null;
            }
            if (node.obj != null) {
                if (lore.length() >= depth + 2 && node.child.containsKey(new LoreChar(lore.charAt(depth + 1)))) {
                    current = node;
                    depth++;
                    continue;
                }
                return node.obj;
            }
            current = node;
            depth++;
        }
        return null;
    }

    public void clear() {
        this.root.child.clear();
    }


    public static class LoreChar {

        private final char c;

        public LoreChar(char c) {
            this.c = c;
        }

        public char get() {
            return this.c;
        }

        @Override
        public int hashCode() {
            return this.c;
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof LoreChar) && ((LoreChar) o).c == this.c;
        }
    }

    public class TrieNode {
        final ConcurrentHashMap<LoreChar, TrieNode> child = new ConcurrentHashMap<>();
        TrieNode pre = null;
        T obj = null;
        int depth = 0;
    }
}
