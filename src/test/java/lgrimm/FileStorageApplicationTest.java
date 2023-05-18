package lgrimm;

import lgrimm.controller.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;

@SpringBootTest
class FileStorageApplicationTest {

    @Autowired
    FileInDatabaseController fileInDatabaseController;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(fileInDatabaseController);
    }
}
