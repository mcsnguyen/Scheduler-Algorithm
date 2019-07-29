import java.util.Iterator;

public class CircularLinkedList<T> implements Iterable<T>{
    private Node head;
    private int size;

    public CircularLinkedList(){
        head = null;
    }

    class Node{
        private Node next;
        private T data;

        Node(T data){
            this(data, head);
        }

        Node(T data, Node next){
            this.data = data;
            this.next = next;
        }

        public T getProcess(){
            return data;
        }
    }

    public void add(T data) {
        if(isEmpty())
            head = new Node(data);
        else {
            if(size() == 1){
                head.next = new Node(data, head);
            }
            else {
                Node temp = head;
                while (temp.next != head)
                    temp = temp.next;
                temp.next = new Node(data);
            }
        }
        size++;
    }

    public void remove(int position){
        if(isEmpty()){
            return;
        }
        else if(size == 1){
            head = null;
        }
        else{
                Node previous = getNodeAt(position - 1);
                previous.next = previous.next.next;
        }
        size--;
    }

    public T getData(int position){
        Node temp = head;
        while(position-- > 0){
            temp = temp.next;
        }
        return temp.data;
    }

    private Node getNodeAt(int position){
        Node traverse = head;
        if(position < 0) {
            traverse = getNodeAt(size - 1);
            traverse.next = traverse.next.next;

        }
        else {
            while (position-- > 0) {
                traverse = traverse.next;
            }
        }

        return traverse;
    }

    public boolean isEmpty() {
        return (size == 0);
    }

    public int size(){return size;}

    public Iterator<T> iterator() {
        return new ProcessIterator();
    }

    private class ProcessIterator implements Iterator<T> {
        private Node nextNode;

        public ProcessIterator() {
            nextNode = head;
        }

        @Override
        public boolean hasNext() {
            return (size != 0);
        }

        @Override
        public T next() {
            T next = nextNode.data;
            nextNode = nextNode.next;
            return next;
        }
    }


}
