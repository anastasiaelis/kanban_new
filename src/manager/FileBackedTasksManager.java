package manager;
import task.Task;
import task.Epic;
import task.SubTask;

import java.io.BufferedWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileBackedTasksManager  extends InMemoryTaskManager{
    protected static final HistoryManager historyManager=Managers.getDefaultHistory();

    private final File file;
    private int numbMain;
//    private static final String HEADER_CSV_FILE = "id,type,name,status,description,startTime,duration,epicId\n";


    public FileBackedTasksManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }
    public static FileBackedTasksManager loadFromFile(File file){
        final FileBackedTasksManager taskManager=new FileBackedTasksManager(historyManager,file);
        int maxId=-1;
        try{
            final String csv= Files.readString(file.toPath());
            final String[] lines = csv.split(System.lineSeparator());

            for(int i=1;i<lines.length-2;i++) {
                if (lines[i]==""){
                    break;
                }else{
            Task task=CSVtaskFormat.taskFromString(lines[i]);
                     String[] values=lines[i].split(",");
                     TaskType type= TaskType.valueOf(values[1]);

            final int id=task.getId();
                    if (id>maxId){
                       maxId=id;
            }

                    switch(type){
                case TASK:
                    taskManager.tableTask.put(id,task);

                    break;
                case SUBTASK:
                    taskManager.tableSubTask.put(id,(SubTask)task);
                    Epic epUp=taskManager.getOneEp(Integer.parseInt(values[5]));
                    List<Integer>zb=epUp.getNumberSub();
                    zb.add(id);
                    epUp.setNumberSub(zb);
                    taskManager.updateEp(epUp);

                    break;
                case EPIC:
                    taskManager.tableEpic.put(id,(Epic)task);

                    break;

            }
            }
                String historyLine=lines[lines.length-1];

               // System.out.println(historyLine);

                for (Integer historyId: CSVtaskFormat.historyFromString(historyLine)){
                    Task task=taskManager.findTask(historyId);
                    taskManager.historyManager.add(task);

                }

                for (SubTask subtask: taskManager.getSubs()){
                    Epic epic2=taskManager.getOneEp(subtask.getNumberEpic());

                }

                taskManager.numbMain=maxId;

            }
        }catch (IOException e){
            throw new ManagerSaveException("Нельзя прочитать файл: "+file.getName());
        }
        return taskManager;
    }


     public void save(){

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
            final String csv= Files.readString(file.toPath());
            final String[] lines = csv.split(System.lineSeparator());

           writer.write(CSVtaskFormat.getHeader());
           writer.newLine();

        for (Map.Entry<Integer,Task> entry:tableTask.entrySet()){
                final Task task=entry.getValue();
                writer.write(CSVtaskFormat.toString(task));
                writer.newLine();
        }

        for (Map.Entry<Integer,Epic> entry:tableEpic.entrySet()){
                final Task task=entry.getValue();
                writer.write(CSVtaskFormat.toString(task));
                writer.newLine();
            }

        for (Map.Entry<Integer,SubTask> entry:tableSubTask.entrySet()){
                final Task task=entry.getValue();
                writer.write(CSVtaskFormat.toString(task));
                writer.newLine();
        }
            writer.newLine();
            writer.write(CSVtaskFormat.historyToString(historyManager));
            writer.newLine();


        }catch (IOException e){
            throw new ManagerSaveException("Нельзя записать в файл: "+file.getName());
        }

    }
   protected Task findTask(Integer id){
        final Task task=tableTask.get(id);
        if(task!=null){
            return  task;
        }
       final SubTask subtask=tableSubTask.get(id);
       if(subtask!=null){
           return  subtask;
       }
       return tableSubTask.get(id);
   }



    @Override
    public void addNew(Task task) {
        super.addNew(task);
        save();
    }

    @Override
    public void addNewEp(Epic epic) {
        super.addNewEp(epic);
        save();
    }

    @Override
    public void addNewSub(SubTask task) {
        super.addNewSub(task);
        save();
    }

    @Override
    public  void updateEp(Epic epic) {
        super.updateEp(epic);
        save();
    }

    @Override
    public void updateSub(SubTask task) {
        super.updateSub(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }



    @Override
    public Task getOneTask(int taskId) {
        Task task = super.getOneTask(taskId);
        save();
        return task;
    }

    @Override
    public Epic getOneEp(int epicId) {
        Epic epic = super.getOneEp(epicId);
        save();
        return epic;
    }

    @Override
    public SubTask getOneSub(int subTaskId) {
        SubTask task = super.getOneSub(subTaskId);
        save();
        return task;
    }

    @Override
    public void delete() {
        super.delete();
        save();
    }

    @Override
    public void deleteEp() {
        super.deleteEp();
        save();
    }

    @Override
    public void deleteSub() {
        super.deleteSub();
        save();
    }

    @Override
    public void deleteOne(int taskId) {
        super.deleteOne(taskId);
        save();
    }

    @Override
    public void deleteOneEp(int epicId) {
        super.deleteOneEp(epicId);
        save();
    }

    @Override
    public void deleteOneSub(int subTaskId) {
        super.deleteOneSub(subTaskId);
        save();
    }

    @Override
    public List<Integer> getSubList(int epicId) {
        List<Integer> listSub=super.getSubList(epicId);
        save();
        return listSub;
    }
    @Override
    public ArrayList<Task> getTasks() {
        ArrayList<Task> allTasks = super.getTasks();
        save();
        return allTasks;
    }

    @Override
    public List<SubTask> getSubs() {
        List<SubTask> allSubTasks = super.getSubs();
        save();
        return allSubTasks;
    }

    @Override
    public List<Epic> getEps() {
       List<Epic> allEpics = super.getEps();
        save();
        return allEpics;
    }
    @Override


    public List<Task> getHistory() {
        //save();
        return super.getHistory();
    }
    public static void main(String[] args) {
     // FileBackedTasksManager manager = new FileBackedTasksManager(new File("resources/file.csv"));
        loadFromFile(new File("resources/file.csv"));
       // System.out.println("manager.getHistory()");
        //Task task1 = new Task(1, "nhtnmz", "задача", TaskStatus.DONE);
        //manager.addNew(task1);
    }

}