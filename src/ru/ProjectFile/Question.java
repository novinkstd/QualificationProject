package ru.ProjectFile;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Question implements TestComponent<Question>{
    final private String name;
    final private String text;
    final private String answer;

    private boolean answerCorrect;

    public Question(String name, String text, String answer){
        this.answer = answer;
        this.text = text;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public String getAnswer() {
        return answer;
    }

    public boolean isAnswerCorrect() {
        return answerCorrect;
    }

    @Override
    public void print() {
        IOInterface.transmitMess(name);
    }

    @Override
    public void executeComponent() {
        IOInterface.transmitMess(String.format("Название вопроса: %s", name));
        IOInterface.transmitMess(String.format("Текст вопроса: %s", text));
        IOInterface.transmitMess("Введите ответ на вопрос");
        var userAns = IOInterface.receiveMess();
        answerCorrect = userAns.compareTo(answer) == 0;
    }


    public static boolean update(Question obj) {
        if (obj == null){
            return true;
        }
        String sql = "update questions set text = ?, answer = ? where questionName = ?";
        var conn = ConnectionBD.getConnection();

        try(PreparedStatement statement = conn.prepareStatement(sql)){
            statement.setString(1, obj.text);
            statement.setString(2, obj.answer);
            statement.setString(3, obj.name);
            try (ResultSet resultSet = statement.executeQuery()){
                int result = statement.executeUpdate();
                return result > 0;
            }
        }
        catch (SQLException e) {
            System.err.println("Ошибка при работе с БД: " + e.getMessage());
        }
        return false;
    }


    public static boolean create(Question obj) {
        String sql = "insert into questions (questionName, text, answer) values (?, ?, ?)";
        var conn = ConnectionBD.getConnection();

        try(PreparedStatement statement = conn.prepareStatement(sql)){
            statement.setString(1, obj.name);
            statement.setString(2, obj.text);
            statement.setString(3, obj.answer);
            int result = statement.executeUpdate();
            return result != 0;
        }
        catch (SQLException e) {
            System.err.println("Ошибка при работе с БД: " + e.getMessage());
        }
        return false;
    }

    public static Question read(int objId){
        Question buff = null;
        String sql = "select * from questions where id = ?";
        var conn = ConnectionBD.getConnection();

        try(PreparedStatement statement = conn.prepareStatement(sql)){
            statement.setInt(1, objId);
            try (ResultSet resultSet = statement.executeQuery()){
                if(resultSet.next()){
                    var name = resultSet.getString("questionName");
                    var text = resultSet.getString("text");
                    var ans =  resultSet.getString("answer");
                    buff = new Question(name, text, ans);
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

    public static void delete(String objName){
        String sql = "delete from questions where questionName = ?";
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
