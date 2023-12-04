package manager;
import task.SubTask;
import task.Task;
import task.TaskStatus;
import task.Epic;
import java.time.Instant;

import java.util.List;
import java.lang.String;

import java.util.ArrayList;

public class CSVtaskFormat {
    public static Task taskFromString(String value){
        final String[] values=value.split(",");
        final int id=Integer.parseInt(values[0]);
        final TaskType type= TaskType.valueOf(values[1]);
        final String name=values[2];
        final TaskStatus status=TaskStatus.valueOf(values[3]);
        final String description=values[4];
        Instant startTime = Instant.parse(values[5]);
        long duration = Long.parseLong(values[6]);

        if (type.equals(TaskType.TASK)){
            return new Task(id,name,description,status,startTime,duration);
        }
        if(type.equals(TaskType.SUBTASK)){
            final int epicId=Integer.parseInt((values[7]));

            return new SubTask(id,epicId,name,description,status,startTime,duration);
        }
        if (type.equals(TaskType.EPIC)){
            return new Epic(id,name,description,status,new ArrayList<>(),startTime,duration);
        }
        else{
            throw new ManagerSaveException("Такой тип задач отсутствует");
        }
    }

    public static String historyToString(HistoryManager historyManager){
        final List<Task> history=historyManager.getHistory();
        if (history.isEmpty()){
            return "";
        }
        StringBuilder sb=new StringBuilder();
        sb.append(history.get(0).getId());
        for (int i=1;i<history.size();i++){
            Task task=history.get(i);
            sb.append(",");
            sb.append(task.getId());
        }
        return sb.toString();
    }

    public static String toString(Task task) {
        TaskType taskType;
        String epicId = "";
        StringBuilder sb = new StringBuilder();
        if (task instanceof Epic) {
            taskType = TaskType.EPIC;
            for (Integer id : ((Epic) task).getNumberSub()) {
                sb.append(id + "/");
            }
            epicId = sb.toString();
        } else if (task instanceof SubTask) {
            taskType = TaskType.SUBTASK;
            epicId += ((SubTask) task).getNumberEpic();
        } else {
            taskType = TaskType.TASK;
        }

        return String.format("%d,%s,%s,%s,%s,%s",task.getId(), taskType, task.getName(), task.getStatus(),
               task.getDescription(), epicId);
    }


    public static List<Integer> historyFromString(String value){
        final List<Integer> ids=new ArrayList<>();
        if (value!=null){
        final String[] values=value.split(",");
        for (String id:values){
            ids.add(Integer.parseInt(id));
        }
        }
        return ids;
    }

    public static String getHeader(){
        return "id,type,name,status,description,startTime, duration, epic";
    }
}
