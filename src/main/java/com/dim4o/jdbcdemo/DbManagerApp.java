package com.dim4o.jdbcdemo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DbManagerApp {
    private Connection conn = null;

    private Connection getConnection() {
        if (conn == null) {
            try {
                conn = DriverManager.getConnection("jdbc:h2:mem:", "sa", "");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return conn;
    }

    public boolean createPersonTable() {
        Connection conn = getConnection();

        try (Statement createTableSt = conn.createStatement()) {
            return createTableSt.execute(
                    "CREATE TABLE PERSON(Id int, FirstName varchar(255), LastName varchar(255));");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean addPerson(Person person) {
        Connection conn = getConnection();
        try (PreparedStatement insertSt = conn.prepareStatement(
                "INSERT INTO PERSON(Id, FirstName, LastName) VALUES(?, ?, ?);");) {
            insertSt.setInt(1, person.getId());
            insertSt.setString(2, person.getFirstName());
            insertSt.setString(3, person.getLastName());
            insertSt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Person> getAllPersons() {
        List<Person> person = new ArrayList<>();
        try (Statement selectAllSt = getConnection().createStatement();) {
            ResultSet res = selectAllSt.executeQuery("SELECT * FROM PERSON;");
            while (res.next())
                person.add(new Person(res.getInt(1), res.getString(2), res.getString(3)));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return person;
    }

    public void deletePersonById(int id) {
        try (PreparedStatement st = getConnection()
                .prepareStatement("DELETE FROM PERSON WHERE ID=?");) {
            st.setInt(1, id);
            st.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        Connection conn = getConnection();
        if (conn != null)
            try {
                conn.close();
            } catch (SQLException e) {}
    }

    public static void main(String[] args) {
        DbManagerApp app = new DbManagerApp();

        // Create a table Person
        app.createPersonTable();

        // Insert a data
        app.addPerson(new Person(1, "Dimcho", "Nedkov"));
        app.addPerson(new Person(2, "Bogomil", "Dimchov"));
        app.addPerson(new Person(3, "Nora", "Staykova"));

        // print the Person data
        app.getAllPersons().forEach(System.out::println);
        // Person [id=1, firstName=Dimcho, lastName=Nedkov]
        // Person [id=2, firstName=Bogomil, lastName=Dimchov]
        // Person [id=2, firstName=Nora, lastName=Staykova]
        
        app.deletePersonById(1);

        app.getAllPersons().forEach(System.out::println);
        // Person [id=2, firstName=Bogomil, lastName=Dimchov]
        // Person [id=3, firstName=Nora, lastName=Staykova]
        
        app.closeConnection();
    }
}
