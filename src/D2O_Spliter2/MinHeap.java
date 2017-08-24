package D2O_Spliter2;

import java.util.Map.Entry;

public class MinHeap extends Heap
{
    private Node[] Heap;
    private int size;
    private int maxsize;
 
    private static final int FRONT = 1;
 
    public MinHeap(Graph g) 
    {
        this.maxsize = g.getContainer().size();
        this.size = 0;
        Heap = new Node[this.maxsize + 1];
        Heap[0] = Node.getInstanceForMinHeap();
    }
    
    public int getSize(){
    	return size;
    }
    
    public void insert(Graph g, Params params){
    	for(Entry<Integer, Node> t : g.getContainer().entrySet()){
    		if(t.getValue().isVisited()==true){
    			continue;
    		}
    		if(params.checkCandidate(t.getValue().getPurity())){
    			this.insert(t.getValue());
	    		if(Main.printOn==true)
	    			System.out.println("Inserted!!"+t.getKey() + " purity="+ t.getValue().getPurity() + "candi param = "+params.getCandidatePruningParams());    	    		
    		}
    	}
		if(Main.printOn==true)
			System.out.println("Inserting all the nodes are finished!");
    }
    
    public boolean isEmpty(){
    	if(size==0){
    		return true;
    	}
    	else{
    		return false;
    	}
    }
 
    public int parent(int pos)
    {
        return pos / 2;
    }
 
    public int leftChild(int pos)
    {
        return (2 * pos);
    }
 
    public int rightChild(int pos)
    {
        return (2 * pos) + 1;
    }
 
    public boolean isLeaf(int pos)
    {
        if (pos >=  (size / 2)  &&  pos <= size)
        { 
            return true;
        }
        return false;
    }
 
    public void swap(int fpos, int spos)
    {
        Node tmp;
        tmp = Heap[fpos];
        Heap[fpos] = Heap[spos];
        Heap[spos] = tmp;
    }
 
    public void Heapify(int pos)
    {
        if (!isLeaf(pos))
        { 
            if ( Heap[pos].getPurity() > Heap[leftChild(pos)].getPurity()  || 
            		Heap[pos].getPurity() > Heap[rightChild(pos)].getPurity())
            {
                if (Heap[leftChild(pos)].getPurity() < Heap[rightChild(pos)].getPurity())
                {
                    swap(pos, leftChild(pos));
                    Heapify(leftChild(pos));
                }else
                {
                    swap(pos, rightChild(pos));
                    Heapify(rightChild(pos));
                }
            }
        }
    }
 
    public void insert(Node element)
    {
    	element.visited();// when it visited, check. Otherwise, checkout()
    	
        Heap[++size] = element;
        int current = size;
 
        while (Heap[current].getPurity() < Heap[parent(current)].getPurity())
        {
            swap(current,parent(current));
            current = parent(current);
        }	
        
    }
 
    public void print()
    {
        for (int i = 1; i <= size / 2; i++ )
        {
            System.out.print(" PARENT : " + Heap[i] + " LEFT CHILD : " + Heap[2*i] 
                + " RIGHT CHILD :" + Heap[2 * i  + 1]);
            System.out.println();
        } 
    }
 
    public void build()
    {
        for (int pos = (size / 2); pos >= 1 ; pos--)
        {
            Heapify(pos);
        }
    }
 
    public Node remove()
    {
    	if(size==0){
    		return null;
    	}
        Node popped = Heap[FRONT];
        Heap[FRONT] = Heap[size--]; 
       if(size!=0){
           Heapify(FRONT);
       }
       return popped;
    }

 
//    public static void main(String...arg)
//    {
//        System.out.println("The Min Heap is ");
//        MinHeap minHeap = new MinHeap(15);
//        minHeap.insert(5);
//        minHeap.insert(3);
//        minHeap.insert(17);
//        minHeap.insert(10);
//        minHeap.insert(84);
//        minHeap.insert(19);
//        minHeap.insert(6);
//        minHeap.insert(22);
//        minHeap.insert(9);
//        minHeap.minHeap();
// 
//        minHeap.print();
//        System.out.println("The Min val is " + minHeap.remove());
//    }
}