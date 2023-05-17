package lgrimm.repository;

import static org.junit.jupiter.api.Assertions.*;

import lgrimm.entity.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.jdbc.*;
import org.springframework.boot.test.autoconfigure.orm.jpa.*;

import java.util.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FileInDatabaseRepositoryInterfaceTest {

    @Autowired
    FileInDatabaseRepositoryInterface repository;

    @Test
    void findByFileName_NoSuchFile() {
        FileInDatabaseEntity entity1 = new FileInDatabaseEntity(
                "file1.txt",
                "content",
                ""
        );
        repository.save(entity1);
        FileInDatabaseEntity entity2 = new FileInDatabaseEntity(
                "file2.txt",
                "content",
                ""
        );
        repository.save(entity2);

        String filename = "file3.txt";
        Optional<FileInDatabaseEntity> entity = repository.findByFileName(filename);
        Assertions.assertTrue(entity.isEmpty());
    }

    @Test
    void findByFileName_ExistingFile() {
        FileInDatabaseEntity entity1 = new FileInDatabaseEntity(
                "file1.txt",
                "content",
                ""
        );
        repository.save(entity1);
        FileInDatabaseEntity entity2 = new FileInDatabaseEntity(
                "file2.txt",
                "content",
                ""
        );
        entity2 = repository.save(entity2);

        String filename = "file2.txt";
        Optional<FileInDatabaseEntity> entity = repository.findByFileName(filename);
        Assertions.assertTrue(entity.isPresent());
        Assertions.assertEquals(entity2, entity.get());
    }
}