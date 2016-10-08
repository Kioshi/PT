public class Main {

    public static void main(String[] args)
    {
	// write your code here
        Trie trie = new Trie();
        trie.print();
        trie.insert("a");
        trie.insert("aa",1);
        trie.insert("ab",2);
        trie.insert("ab",3);
        trie.insert("aba",3);
        trie.insert("b");
        trie.print();
    }
}
