package D2O_Spliter2;

abstract public class Heap {


    abstract public int getSize();

    abstract public void insert(Graph g, Params params);

    abstract public boolean isEmpty();
 
    abstract public int parent(int pos);
 
    abstract public int leftChild(int pos);
 
    abstract public int rightChild(int pos);
 
    abstract public boolean isLeaf(int pos);
 
    abstract public void swap(int fpos,int spos);
 
    abstract public void Heapify(int pos);
    
    abstract public void insert(Node element);
 
    abstract public void print();
    abstract public void build();

    abstract public Node remove();
 

}
