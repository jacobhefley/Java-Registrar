import java.util.HashMap;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;
import static spark.Spark.*;

public class App {
  public static void main(String[] args) {
    staticFileLocation("/public");
    String layout = "templates/layout.vtl";

    get("/", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      model.put("template", "templates/index.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    get("/students", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      model.put("students", Student.all());
      model.put("template", "templates/students.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    get("/courses", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      model.put("courses", Course.all());
      model.put("template", "templates/courses.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/students", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      String firstname = request.queryParams("firstname");
      String lastname = request.queryParams("lastname");
      Student newStudent = new Student(firstname, lastname);
      newStudent.save();
      response.redirect("/students");
      return null;
    });

    post("/courses", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      String course = request.queryParams("course");
      String coursenum = request.queryParams("coursenum");
      Course newCourse = new Course(course, coursenum);
      newCourse.save();
      response.redirect("/courses");
      return null;
    });

    // get("/tasks", (request, response) -> {
    //   HashMap<String, Object> model = new HashMap<String, Object>();
    //   model.put("tasks", Task.all());
    //   model.put("template", "templates/tasks.vtl");
    //   return new ModelAndView(model, layout);
    // }, new VelocityTemplateEngine());
    //
    get("/students/:id", (request,response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      Student student = Student.find(Integer.parseInt(request.params("id")));
      model.put("student", student);
      model.put("courses", Course.all());
      model.put("template", "templates/student.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());


    get("/courses/:id", (request,response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      Course course = Course.find(Integer.parseInt(request.params("id")));
      model.put("course", course);
      model.put("courses", Course.all());
      model.put("template", "templates/course.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());
    //
    // get("/tasks/edit/:id", (request,response) -> {
    //   HashMap<String, Object> model = new HashMap<String, Object>();
    //   Task task = Task.find(Integer.parseInt(request.params("id")));
    //   model.put("task", task);
    //   model.put("template", "templates/edit-task.vtl");
    //   return new ModelAndView(model, layout);
    // }, new VelocityTemplateEngine());
    //
    // get("/categories/:id", (request,response) ->{
    //   HashMap<String, Object> model = new HashMap<String, Object>();
    //   Category category = Category.find(Integer.parseInt(request.params("id")));
    //   model.put("category", category);
    //   model.put("allTasks", Task.all());
    //   model.put("template", "templates/category.vtl");
    //   return new ModelAndView(model, layout);
    // }, new VelocityTemplateEngine());
    //
    // get("/categories/edit/:id", (request,response) -> {
    //   HashMap<String, Object> model = new HashMap<String, Object>();
    //   Task task = Task.find(Integer.parseInt(request.params("id")));
    //   model.put("task", task);
    //   model.put("template", "templates/edit-category.vtl");
    //   return new ModelAndView(model, layout);
    // }, new VelocityTemplateEngine());
    //
    // post("/tasks", (request, response) -> {
    //   HashMap<String, Object> model = new HashMap<String, Object>();
    //   String description = request.queryParams("description");
    //   String duedate = request.queryParams("duedate");
    //   Task newTask = new Task(description, duedate);
    //   newTask.save();
    //   response.redirect("/tasks");
    //   return null;
    // });
    //
    // post("/categories", (request, response) -> {
    //   HashMap<String, Object> model = new HashMap<String, Object>();
    //   String name = request.queryParams("name");
    //   Category newCategory = new Category(name);
    //   newCategory.save();
    //   response.redirect("/categories");
    //   return null;
    // });
    //
    // post("delete/:id", (request, response) -> {
    //   HashMap<String, Object> model = new HashMap<String, Object>();
    //   Task task;
    //   int n = Integer.parseInt(request.queryParams("type"));
    //   String id = request.queryParams("id");
    //   switch(n){
    //     case 1:
    //       task = Task.find(Integer.parseInt(request.params("id")));
    //       task.delete();
    //       response.redirect("/tasks");
    //       break;
    //     case 2:
    //       task = Task.find(Integer.parseInt(request.params("id")));
    //       task.delete();
    //       response.redirect("/categories/" + id);
    //       break;
    //     case 3:
    //       Category category = Category.find(Integer.parseInt(request.params("id")));
    //       category.delete();
    //       response.redirect("/categories");
    //       break;
    //     default:
    //       response.redirect("/");
    //       break;
    //   }
    //
    //   return null;
    // });
    //
    // post("complete/:id", (request, response) -> {
    //   HashMap<String, Object> model = new HashMap<String, Object>();
    //   int n = Integer.parseInt(request.queryParams("type"));
    //   String id = request.queryParams("id");
    //   Task task = Task.find(Integer.parseInt(request.params("id")));
    //   task.complete();
    //   switch(n){
    //     case 1:
    //       response.redirect("/tasks");
    //       break;
    //     case 2:
    //       response.redirect("/categories/" + id);
    //       break;
    //     case 3:
    //       response.redirect("/tasks/" + request.params("id"));
    //     default:
    //       response.redirect("/");
    //       break;
    //   }
    //   return null;
    // });
    //
    post("/add_course", (request, response) -> {
      int studentId = Integer.parseInt(request.queryParams("student_id"));
      int courseId = Integer.parseInt(request.queryParams("course_id"));
      Course course = Course.find(courseId);
      Student student = Student.find(studentId);
      student.addCourse(course);
      response.redirect("/students/" + studentId);
      return null;
    });
    //
    // post("/students-update/:id", (request, response) -> {
    //   String description = request.queryParams("description");
    //   String duedate = request.queryParams("duedate");
    //   Task task = Task.find(Integer.parseInt(request.params("id")));
    //   task.update(description, duedate);
    //   response.redirect("/tasks");
    //   return null;
    // });
    //
    // post("/add_categories", (request, response) -> {
    //   int taskId = Integer.parseInt(request.queryParams("task_id"));
    //   int categoryId = Integer.parseInt(request.queryParams("category_id"));
    //   Category category = Category.find(categoryId);
    //   Task task = Task.find(taskId);
    //   task.addCategory(category);
    //   response.redirect("/tasks/" + taskId);
    //   return null;
    // });

  }
}
