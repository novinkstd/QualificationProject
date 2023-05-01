package ru.ProjectFile;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.sql.SQLException;

public class TestSystem {
    private User user;
    final static private int INIT = 0;
    final static private int ACTION_CHOICE = 1;
    final static private int ACTION_ADMIN_FUNC = 2;
    final static private int ACTION_START_TEST = 3;
    final static private int ACTION_EXIT = 4;

    private static UserFunction testStart = new UserFunction(
            "Начать тестирование",
            null,
            ACTION_START_TEST
    );
    private static UserFunction exitFunc = new UserFunction(
            "Выход",
            null,
            ACTION_EXIT
    );
    private static UserFunction[] userFunctionList = {
        new UserFunction(
                "Создать пользователя",
                new BDUserFunc() {
                    @Override
                    public boolean execute() throws Exception {

                        IOInterface.transmitMess("Введите логин нового пользователя:");
                        var login = IOInterface.receiveMess();
                        IOInterface.transmitMess("Введите пароль нового пользователя:");
                        var pass = IOInterface.receiveMess();;
                        IOInterface.transmitMess("Введите роль нового пользователя:");
                        var isAdmin = IOInterface.receiveMess().compareTo("admin") == 0;

                        String sql = "insert into users (login, password, isAdminRole) values (?, ?, ?)";
                        var conn = ConnectionBD.getConnection();

                        try(PreparedStatement statement = conn.prepareStatement(sql)){
                            statement.setString(1, login);
                            statement.setString(2, pass);
                            statement.setBoolean(3, isAdmin);
                            int result = statement.executeUpdate();
                            return result != 0;
                        }
                        catch (SQLException e) {
                            System.err.println("Ошибка при работе с БД: " + e.getMessage());
                        }
                        return false;
                    }
                },
                ACTION_ADMIN_FUNC
        ),
        new UserFunction(
                "Удалить пользователя",
                new BDUserFunc() {
                    @Override
                    public boolean execute() throws Exception {

                        IOInterface.transmitMess("Введите ID удаляемого пользователя");
                        int id = Integer.parseInt(IOInterface.receiveMess());

                        String sql = "delete from users where id = ?";
                        var conn = ConnectionBD.getConnection();

                        try(PreparedStatement statement = conn.prepareStatement(sql)){
                            statement.setInt(1, id);
                            int result = statement.executeUpdate();
                            return result != 0;
                        }
                        catch (SQLException e) {
                            System.err.println("Ошибка при работе с БД: " + e.getMessage());
                        }
                        return false;
                    }
                },
                ACTION_ADMIN_FUNC
        ),
        new UserFunction(
                "Просмотреть всех пользователей",
                new BDUserFunc() {
                    @Override
                    public boolean execute() throws Exception {
                        String sql = "select * from users";
                        var conn = ConnectionBD.getConnection();
                        try(PreparedStatement statement = conn.prepareStatement(sql)){
                            try (ResultSet resultSet = statement.executeQuery()){
                                while (resultSet.next()){
                                    var roleName = (resultSet.getBoolean("isAdminRole")) ? "admin" : "user";
                                    IOInterface.transmitMess(
                                        String.format(
                                            "ID - %d, Логин - %s, роль - %s",
                                            resultSet.getInt("id"),
                                            resultSet.getString("login"),
                                            roleName
                                        )
                                    );
                                }
                                return true;
                            }
                        }
                        catch (SQLException e) {
                            System.err.println("Ошибка при работе с БД: " + e.getMessage());
                        }
                        return false;
                    }
                },
                ACTION_ADMIN_FUNC
        )
    };


    public TestSystem(String dbURL){
        ConnectionBD.setConnection(dbURL);
    }

    public void startSystem(){
        int state = INIT;
        UserFunction action = null;

        while (true){
            switch (state){
                case INIT :{
                    if (getUser()){
                        IOInterface.transmitMess(String.format(
                                "Приветсвуем полбзователь %s вы успешно вошли в систем \n", user));
                        state = ACTION_CHOICE;
                    }
                    else {
                        IOInterface.transmitMess("Неверное имя пользователя или пароль");
                    }
                    break;
                }
                case ACTION_CHOICE :{
                     action = getAction();
                     if (action != null){
                         state = action.getFunctionID();
                     }
                     else {
                         IOInterface.transmitMess("Выбрано неверное действие");
                     }
                     break;
                }
                case ACTION_ADMIN_FUNC :{
                    var res = false;
                    try {
                        res = action.execute();

                    }
                    catch (Exception e){
                        IOInterface.transmitMess(e.getMessage());
                    }
                    finally {
                        if (res){
                            IOInterface.transmitMess(String.format(
                                    "Функция '%s' выполнена успешна\n", action.getFunctionName()));
                        }
                        else{
                            IOInterface.transmitMess(String.format(
                                    "Функция '%s' невыполнена\n", action.getFunctionName()));
                        }
                        state = ACTION_CHOICE;
                    }
                    break;
                }
                case ACTION_START_TEST :{
                    executeTest();
                    state = ACTION_CHOICE;
                    break;
                }
                case ACTION_EXIT :{
                    IOInterface.transmitMess("Работа с тестирующей системой завершена штатно");
                    return;
                }
                default :{
                    IOInterface.transmitMess("Работа с тестирующей системой завершена внештатно");
                    return;
                }
            }
        }
    }

    private boolean checkUser(String login, String password){
        String sql = "select login, isAdminRole from users " +
                     "where login = ? and password = ?";
        var conn = ConnectionBD.getConnection();
        try(PreparedStatement statement = conn.prepareStatement(sql)){
            statement.setString(1, login);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()){
                if (resultSet.next()){
                    List<UserFunction> buff = new ArrayList<>();
                    if (resultSet.getBoolean("isAdminRole")){
                        buff.addAll(Arrays.asList(userFunctionList));
                    }
                    buff.add(testStart);
                    buff.add(exitFunc);
                    user = new User(login, buff);
                    return true;
                }
            }
        }
        catch (SQLException e) {
            System.err.println("Ошибка при работе с БД: " + e.getMessage());
        }
        return false;
    }

    private boolean getUser(){
        IOInterface.transmitMess("Введите логин пользователя:");
        String login = IOInterface.receiveMess();
        IOInterface.transmitMess("Введите пароль пользователя:");
        String pass = IOInterface.receiveMess();
        return checkUser(login, pass);
    }

    private UserFunction getAction(){
        IOInterface.transmitMess("Выбирите действие, для этого введите его наименование:");
        user.printFunctionsList();
        String action = IOInterface.receiveMess();
        for (var func : user.getFunctionsList()) {
            if (action.compareTo(func.getFunctionName()) == 0){
                return func;
            }
        }
        return null;
    }

    private List<String> getThemeList(){
        var buff = new ArrayList<String>();
        String sql = "select * from themes";
        var conn = ConnectionBD.getConnection();
        try(PreparedStatement statement = conn.prepareStatement(sql)){
            try (ResultSet resultSet = statement.executeQuery()){
                while (resultSet.next()){
                    buff.add(resultSet.getString("themeName"));
                }
            }
        }
        catch (SQLException e) {
            System.err.println("Ошибка при работе с БД: " + e.getMessage());
        }
        return buff;
    }

    private void executeTest(){
        var themeList = getThemeList();

        while (true) {
            IOInterface.transmitMess("Выбирите тему, для тестирования, а чтобы вернуться введите назад:");
            for (var name: themeList) {
                IOInterface.transmitMess(name);
            }
            var userAns = IOInterface.receiveMess();
            if (userAns.compareTo("назад") == 0) {
                return;
            }

            var theme = Theme.read(userAns);
            theme.executeComponent();
        }
    }

}
