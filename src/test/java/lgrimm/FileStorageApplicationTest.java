package lgrimm;

import static org.junit.jupiter.api.Assertions.*;

import lgrimm.controller.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FileStorageApplicationTest {

    @Autowired
    FileInDatabaseController fileInDatabaseController;

    @Test
    public void contextLoads() throws Exception {
        Assertions.assertNotNull(fileInDatabaseController);
    }
}
