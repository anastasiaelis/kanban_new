package manager;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    public InMemoryHistoryManager() {
    }

    private static class Node{
        Node prev;
        Node next;
        Task task;

        public Node(Node prev, Node next, Task task){
            this.prev=prev;
            this.next=next;
            this.task=task;
        }
    }
    private Node getTasks;
    private Node linkLast;

    Map<Integer,Node> nodes=new HashMap<>();

    @Override
    public void add (Task task) {

        if (task!=null) {
             remove(task.getId());
             Node node = new Node(linkLast,null,task);
             if (getTasks==null) {
                 getTasks=node;
             }else{
                 linkLast.next=node;
             }
            linkLast=node;
             nodes.put(task.getId(),node);
       }
    }
    @Override

    public List<Task> getHistory() {
        List<Task> listHistory=new ArrayList<>() ;
        Node current =getTasks;
        while (current!=null){
            listHistory.add(current.task);
            current=current.next;
        }
        return listHistory;
    }
    @Override
    public void remove(int id){
        Node remove =nodes.remove(id);
        if (remove==null)
            return;
        if (remove.prev==null && remove.next==null ){
            getTasks=null;
            linkLast=null;
        }else if (remove.prev==null) {
            getTasks = remove.next;
            remove.next.prev = null;
        }else if(remove.prev==null){
            linkLast=remove.prev;
            remove.prev.next=null;
        }else{
            remove.prev.next=remove.next;
            remove.next.prev=remove.next;
        }
}

}
