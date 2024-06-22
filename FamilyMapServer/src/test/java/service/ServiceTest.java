package service;

import dataAccess.*;
import org.junit.jupiter.api.AfterEach;

import java.sql.Connection;

public class ServiceTest {
    protected Database db;
    protected EventDao eDao;
    protected PersonDao pDao;
    protected UserDao uDao;
    protected AuthtokenDao aDao;

    protected void connectToDatabase() throws DataAccessException {
        db = new Database();
        Connection conn = db.getConnection();
        eDao = new EventDao(conn);
        pDao = new PersonDao(conn);
        uDao = new UserDao(conn);
        aDao = new AuthtokenDao(conn);
    }
}
