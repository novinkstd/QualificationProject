package ru.ProjectFile;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Theme implements TestComponent<Theme>{
    final private String name;

    final private List<Question> questionList;

    public Theme(String name, List<Question> questionList){
        this.name = name;
        this.questionList = questionList;
    }

    public String getName() {
        return name;
    }

    public List<Question> getQuestionList() {
        return questionList;
    }

    @Override
    public void print() {
        IOInterface.transmitMess(name);
        for (var el: questionList) {
            el.print();
        }
    }

    @Override
    public void executeComponent() {
        IOInterface.transmitMess("Для старта тестирования по выбранной теме введите 'начать', " +
                "а если хотите вернуться, то 'назад'.\n" +
                "После начала тетсирования нельзя будет вернуться, до того пока вы не ответите на все вопросы.");
        var userAns = IOInterface.receiveMess ();
        if (userAns.compareTo("назад") == 0) {
            return;
        }
        int i = 1;
        int score = 0;
        for (var el :questionList) {
            IOInterface.transmitMess(String.format("Вопрос номер %d", i++));
            el.executeComponent();
            if (el.isAnswerCorrect()){
                score++;
            }
        }
        IOInterface.transmitMess(String.format(
                "Ваш итоговый результат составил %d / %d", score, questionList.size()));
    }

    public static boolean create(Theme obj) {
        String sql = "insert into themes (themeName) values (?)";
        var conn = ConnectionBD.getConnection();

        try(PreparedStatement statement = conn.prepareStatement(sql)){
            statement.setString(1, obj.name);
            int result = statement.executeUpdate();
            if (result == 0){
                return false;
            }
        }
        catch (SQLException e) {
            System.err.println("Ошибка при работе с БД: " + e.getMessage());
            return false;
        }
        for (var el: obj.questionList) {
            Question.create(el);
            sql = "select q.id, t.id from questions q, themes t where q.questionName = ? and t.themeName = ?";
            int questionId = -1;
            int themeId = -1;
            try(PreparedStatement statement = conn.prepareStatement(sql)){
                statement.setString(1, el.getName());
                statement.setString(2, obj.name);
                try (ResultSet resultSet = statement.executeQuery()){
                    if (resultSet.next()){
                        questionId = resultSet.getInt(1);
                        themeId = resultSet.getInt(2);
                    }
                }
            }
            catch (SQLException e) {
                System.err.println("Ошибка при работе с БД: " + e.getMessage());
                return false;
            }

            sql = "insert into questionList (questionID, themeID) values (?, ?)";
            try(PreparedStatement statement = conn.prepareStatement(sql)){
                statement.setInt(1, questionId);
                statement.setInt(2, themeId);
                int result = statement.executeUpdate();
                if (result == 0){
                    return false;
                }
            }
            catch (SQLException e) {
                System.err.println("Ошибка при работе с БД: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    private static List<Question> getQuestionByThemeId(int id){
        var questionList = new ArrayList<Question>();
        String sql = "select questionID from questionsList where themeID = ?";
        var conn = ConnectionBD.getConnection();
        try(PreparedStatement statement = conn.prepareStatement(sql)){
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()){
                while (resultSet.next()){
                    questionList.add(Question.read(resultSet.getInt("questionID")));
                }
            }
        }
        catch (SQLException e) {
            System.err.println("Ошибка при работе с БД: " + e.getMessage());
        }
        finally {
            return questionList;
        }
    }

    static Theme read(String objName){
        Theme buff = null;
        String sql = "select * from themes where themeName = ?";
        var conn = ConnectionBD.getConnection();

        try(PreparedStatement statement = conn.prepareStatement(sql)){
            statement.setString(1, objName);
            try (ResultSet resultSet = statement.executeQuery()){
                if(resultSet.next()){
                    var id = resultSet.getInt("id");
                    buff = new Theme(objName, getQuestionByThemeId(id));
                }
            }
        }
        catch (SQLException e) {
            System.err.println("Ошибка при работе с БД: " + e.getMessage());
        }
        finally {
            return buff;
        }
    }

   static void delete(String objName){
       String sql = "delete from themes where themeName = ?";
       var conn = ConnectionBD.getConnection();

       try(PreparedStatement statement = conn.prepareStatement(sql)){
           statement.setString(1, objName);

           try (ResultSet resultSet = statement.executeQuery()){
               int result = statement.executeUpdate();
           }
       }
       catch (SQLException e) {
           System.err.println("Ошибка при работе с БД: " + e.getMessage());
       }
   }

}
