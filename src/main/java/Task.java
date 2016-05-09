import java.util.List;
import org.sql2o.*;
import java.util.ArrayList;
import java.text.ParseException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Task {
  private int id;
  private String description;
  private boolean completed;
  private String duedate;

  public Task(String description, String duedate) {
    this.description = description;
    this.completed = false;
    this.duedate = duedate;
  }

  public String getDescription() {
    return description;
  }

  public boolean isCompleted() {
    return completed;
  }

  public String getDuedate() {
    return duedate;
  }

  public int getId() {
    return id;
  }

  public String formatDuedate(){
    SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat myFormat = new SimpleDateFormat("MMMM dd yyyy");
    try {
      String reformattedStr = myFormat.format(fromUser.parse(duedate));
      return reformattedStr;
    } catch (ParseException e) {
      return "broken";
    }

  }

  public static List<Task> all() {
    String sql = "SELECT * FROM tasks ORDER BY duedate ASC";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(Task.class);
    }
  }

  @Override
  public boolean equals(Object otherTask){
    if (!(otherTask instanceof Task)) {
      return false;
    } else {
      Task newTask = (Task) otherTask;
      return this.getDescription().equals(newTask.getDescription()) &&
      this.getId() == newTask.getId();
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO tasks(description, completed, duedate) VALUES (:description, :completed, :duedate)";
      this.id = (int) con.createQuery(sql, true)
      .addParameter("description", this.description)
      .addParameter("completed", this.completed)
      .addParameter("duedate", this.duedate)
      .executeUpdate()
      .getKey();
    }
  }

  public static Task find(int id) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM tasks where id=:id";
      Task task = con.createQuery(sql)
      .addParameter("id", id)
      .executeAndFetchFirst(Task.class);
      return task;
    }
  }

  public void update(String newDescription, String newDuedate) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE tasks SET description = :description, duedate = :duedate WHERE id = :id";
      con.createQuery(sql)
      .addParameter("description", newDescription)
      .addParameter("duedate", newDuedate)
      .addParameter("id", this.id)
      .executeUpdate();
    }
  }

  public void complete() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE tasks SET completed = true WHERE id = :id";
      con.createQuery(sql)
      .addParameter("id", this.id)
      .executeUpdate();
    }
  }

  public void delete() {
    try(Connection con = DB.sql2o.open()) {
      String deleteQuery = "DELETE FROM tasks WHERE id = :id;";
        con.createQuery(deleteQuery)
          .addParameter("id", this.getId())
          .executeUpdate();

      String joinDeleteQuery = "DELETE FROM categories_tasks WHERE task_id = :taskId";
        con.createQuery(joinDeleteQuery)
          .addParameter("taskId", this.getId())
          .executeUpdate();
    }
  }

  public void addCategory(Category category) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO categories_tasks (category_id, task_id) VALUES (:category_id, :task_id)";
      con.createQuery(sql)
      .addParameter("category_id", category.getId())
      .addParameter("task_id", this.getId())
      .executeUpdate();
    }
  }

  public List<Category> getCategories() {
    try(Connection con = DB.sql2o.open()){
      String joinQuery = "SELECT category_id FROM categories_tasks WHERE task_id = :task_id";
      List<Integer> categoryIds = con.createQuery(joinQuery)
      .addParameter("task_id", this.getId())
      .executeAndFetch(Integer.class);

      List<Category> categories = new ArrayList<Category>();

      for (Integer categoryId : categoryIds) {
        String taskQuery = "Select * From categories WHERE id = :categoryId";
        Category category = con.createQuery(taskQuery)
        .addParameter("categoryId", categoryId)
        .executeAndFetchFirst(Category.class);
        categories.add(category);
      }
      return categories;
    }
  }

}
