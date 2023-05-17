package lgrimm.repository;

import static org.junit.jupiter.api.Assertions.*;

import lgrimm.datamodel.*;
import lgrimm.entity.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileInDatabaseRepositoryTest {

    FileInDatabaseRepositoryInterface repositoryInterface;
    FileInDatabaseRepository repository;
    String filename1, filename2, content1, content2;
    FileInDatabaseEntity entityWithoutId1, entityWithoutId2, entityWithId1, entityWithId2;
    Multipart multipart1, multipart2;

    @BeforeEach
    void setUp() {
        repositoryInterface = Mockito.mock(FileInDatabaseRepositoryInterface.class);
        repository = new FileInDatabaseRepository(repositoryInterface);
        filename1 = "file1.txt";
        filename2 = "file2.txt";
        content1 = "content1";
        content2 = "content2";
        entityWithoutId1 = new FileInDatabaseEntity(filename1, content1, "");
        entityWithoutId2 = new FileInDatabaseEntity(filename2, content2, "");
        entityWithId1 = new FileInDatabaseEntity(1L, filename1, content1, "");
        entityWithId2 = new FileInDatabaseEntity(2L, filename2, content2, "");
        multipart1 = new Multipart("file", filename1, "", content1.getBytes());
        multipart2 = new Multipart("file", filename2, "", content2.getBytes());
    }

    @Test
    void getByFilename_NoSuchFile() {
        when(repositoryInterface.findByFileName(filename1))
                .thenReturn(Optional.empty());

        assertTrue(repository.getByFilename(filename1).isEmpty());
    }

    @Test
    void getByFilename_FileExists() {
        when(repositoryInterface.findByFileName(filename1))
                .thenReturn(Optional.of(entityWithId1));

        Optional<FileInDatabaseEntity> entity = repository.getByFilename(filename1);
        Assertions.assertTrue(entity.isPresent());
        Assertions.assertEquals(entityWithId1, entity.get());
    }

    @Test
    void getAll() {
        when(repositoryInterface.findAll())
                .thenReturn(List.of(entityWithId1, entityWithId2));

        List<FileInDatabaseEntity> entities = repository.getAll().toList();
        Assertions.assertEquals(2, entities.size());
        Assertions.assertEquals(entityWithId1, entities.get(0));
        Assertions.assertEquals(entityWithId2, entities.get(1));
    }

    @Test
    void findByFilename_NoSuchFile() {
        when(repositoryInterface.findByFileName(filename1))
                .thenReturn(Optional.empty());

        assertTrue(repository.findByFilename(filename1).isEmpty());
    }

    @Test
    void findByFilename_FileExists() {
        when(repositoryInterface.findByFileName(filename1))
                .thenReturn(Optional.of(entityWithId1));

        Optional<String> filename = repository.findByFilename(filename1);
        Assertions.assertTrue(filename.isPresent());
        Assertions.assertEquals(entityWithId1.getFileName(), filename.get());
    }

    @Test
    void findAll() {
        when(repositoryInterface.findAll())
                .thenReturn(List.of(entityWithId1, entityWithId2));

        List<String> filenames = repository.findAll().toList();
        Assertions.assertEquals(2, filenames.size());
        Assertions.assertEquals(filename1, filenames.get(0));
        Assertions.assertEquals(filename2, filenames.get(1));
    }

    @Test
    void save_ExistingFile() {
        when(repositoryInterface.findByFileName(filename1))
                .thenReturn(Optional.of(entityWithId1));

        Assertions.assertTrue(repository.save(multipart1).isEmpty());
    }

    @Test
    void save_NoSuchFile() {
        when(repositoryInterface.findByFileName(filename1))
                .thenReturn(Optional.empty());
        when(repositoryInterface.save(entityWithoutId1))
                .thenReturn(entityWithId1);

        Optional<String> entity = repository.save(multipart1);
        Assertions.assertTrue(entity.isPresent());
        Assertions.assertEquals(filename1, entity.get());
    }

    @Test
    void saveAll() {
        when(repositoryInterface.findByFileName(filename1))
                .thenReturn(Optional.of(entityWithId1));
        when(repositoryInterface.findByFileName(filename2))
                .thenReturn(Optional.empty());
        when(repositoryInterface.save(entityWithoutId2))
                .thenReturn(entityWithId2);

        List<String> filenames = repository.saveAll(List.of(multipart1, multipart2)).toList();
        Assertions.assertEquals(1, filenames.size());
        Assertions.assertEquals(filename2, filenames.get(0));
    }

    @Test
    void delete_NoSuchFile() {
        when(repositoryInterface.findByFileName(filename1))
                .thenReturn(Optional.empty());

        Assertions.assertFalse(repository.delete(filename1));
    }

    @Test
    void delete_ExistingFile() {
        when(repositoryInterface.findByFileName(filename1))
                .thenReturn(Optional.of(entityWithId1));
        doNothing().when(repositoryInterface).deleteById(entityWithId1.getId());

        Assertions.assertTrue(repository.delete(filename1));
    }

    @Test
    void deleteAll() {
        when(repositoryInterface.findAll())
                .thenReturn(List.of(entityWithId1, entityWithId2));
        when(repositoryInterface.findByFileName(filename1))
                .thenReturn(Optional.of(entityWithId1));
        when(repositoryInterface.findByFileName(filename2))
                .thenReturn(Optional.of(entityWithId2));
        doNothing().when(repositoryInterface).deleteById(entityWithId1.getId());
        doNothing().when(repositoryInterface).deleteById(entityWithId2.getId());

        Assertions.assertEquals(2, repository.deleteAll());
    }
}