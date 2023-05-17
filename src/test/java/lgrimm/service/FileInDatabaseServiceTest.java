package lgrimm.service;

import lgrimm.datamodel.FileInfo;
import lgrimm.datamodel.Multipart;
import lgrimm.datamodel.Payload;
import lgrimm.entity.FileInDatabaseEntity;
import lgrimm.repository.FileInDatabaseRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;

class FileInDatabaseServiceTest {

    FileInDatabaseRepository repository;
    FileInDatabaseService service;
    String filename1, filename2, content1, content2, baseUrl;
    Multipart file1, file2;
    FileInfo fileInfo1, fileInfo2;
    List<FileInfo> fileInfoList;
    Stream<String> filenameStream;
    FileInDatabaseEntity entity1;
    FileInDatabaseEntity entity2;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(FileInDatabaseRepository.class);
        service = new FileInDatabaseService(repository);
        baseUrl = "localhost:8080";
        filename1 = "file1.txt";
        content1 = "content1";
        file1 = new Multipart("file", filename1, MediaType.TEXT_PLAIN_VALUE, content1.getBytes());
        fileInfo1 = new FileInfo(filename1, baseUrl + "/files/" + filename1);
        filename2 = "file2.txt";
        content2 = "content2";
        file2 = new Multipart("file", filename2, MediaType.TEXT_PLAIN_VALUE, content2.getBytes());
        fileInfo2 = new FileInfo(filename2, baseUrl + "/files/" + filename2);
        fileInfoList = List.of(fileInfo1, fileInfo2);
        filenameStream = Stream.of(filename1, filename2);
        entity1 = new FileInDatabaseEntity(1L, filename1, content1, "file type 1");
        entity2 = new FileInDatabaseEntity(2L, filename2, content2, "file type 2");
        when(repository.findAll())
                .thenReturn(filenameStream);
    }

    @Test
    void newFile() {
        Payload expectedPayload = new Payload(
                null,
                null,
                null
        );
        Assertions.assertEquals(expectedPayload, service.newFile());
    }

    @Test
    void uploadFile_NullFile() {
        Payload expectedPayload = new Payload(
                "No file was given.",
                null,
                fileInfoList
        );
        Assertions.assertEquals(expectedPayload, service.uploadFile(null, baseUrl));
    }

    @Test
    void uploadFile_WrongFilename() {
        String filename3 = "file3\ntxt";
        String content3 = "content3";
        Multipart file3 = new Multipart("file", filename3, MediaType.TEXT_PLAIN_VALUE, content3.getBytes());
        Payload expectedPayload = new Payload(
                "Could not upload the file: " + filename3,
                null,
                fileInfoList
        );
        Assertions.assertEquals(expectedPayload, service.uploadFile(file3, baseUrl));
    }

    @Test
    void uploadFile_FileExists() {
        when(repository.save(file1))
                .thenReturn(Optional.empty());
        Payload expectedPayload = new Payload(
                "Could not upload the file: " + filename1,
                null,
                fileInfoList
        );
        Assertions.assertEquals(expectedPayload, service.uploadFile(file1, baseUrl));
    }

    @Test
    void uploadFile_NoSuchFile() {
        when(repository.save(file1))
                .thenReturn(Optional.of(filename1));
        Payload expectedPayload = new Payload(
                filename1 + " file has been successfully uploaded.",
                null,
                fileInfoList
        );
        Assertions.assertEquals(expectedPayload, service.uploadFile(file1, baseUrl));
    }

    @Test
    void uploadFiles_NullFiles() {
        Payload expectedPayload = new Payload(
                null,
                List.of("No files were given."),
                fileInfoList
        );
        Assertions.assertEquals(expectedPayload, service.uploadFiles(null, baseUrl));
    }

    @Test
    void uploadFiles_EmptyFiles() {
        Payload expectedPayload = new Payload(
                null,
                List.of("No files were given."),
                fileInfoList
        );
        Assertions.assertEquals(expectedPayload, service.uploadFiles(new ArrayList<>(), baseUrl));
    }

    @Test
    void uploadFiles_GivenFiles() {
        List<Multipart> filesWithoutNull = new ArrayList<>();
        filesWithoutNull.add(file1);
        filesWithoutNull.add(file2);
        List<Multipart> filesWithNull = new ArrayList<>(filesWithoutNull);
        filesWithNull.add(null);
        Stream<String> filenameStream2 = Stream.of(filename1, filename2);
        when(repository.saveAll(filesWithoutNull))
                .thenReturn(filenameStream2);
        Payload expectedPayload = new Payload(
                null,
                List.of("Results:", filename1 + ": [Success]", filename2 + ": [Success]"),
                fileInfoList
        );
        Assertions.assertEquals(expectedPayload, service.uploadFiles(filesWithNull, baseUrl));
    }

    @Test
    void getFileList() {
        Payload expectedPayload = new Payload(
                null,
                null,
                fileInfoList
        );
        Assertions.assertEquals(expectedPayload, service.getFileList(baseUrl));
    }

    @Test
    void getFile_NullFilename() {
        Assertions.assertNull(service.getFile(null));
    }

    @Test
    void getFile_BlankFilename() {
        Assertions.assertNull(service.getFile("  "));
    }

    @Test
    void getFile_WrongFilename() {
        String filename3 = "file\ntxt";
        when(repository.getByFilename(filename3))
                .thenReturn(Optional.empty());
        Assertions.assertNull(service.getFile(filename3));
    }

    @Test
    void getFile_NoSuchFile() {
        when(repository.getByFilename(filename1))
                .thenReturn(Optional.empty());
        Assertions.assertNull(service.getFile(filename1));
    }

    @Test
    void getFile_ExistingFile() {
        when(repository.getByFilename(filename1))
                .thenReturn(Optional.of(entity1));
        Assertions.assertEquals(entity1, service.getFile(filename1));
    }

    @Test
    void deleteFile_NullFilename() {
        Payload expectedPayload = new Payload(
                "No file was given.",
                null,
                fileInfoList
        );
        Assertions.assertEquals(expectedPayload, service.deleteFile(null, baseUrl));
    }

    @Test
    void deleteFile_BlankFilename() {
        Payload expectedPayload = new Payload(
                "No file was given.",
                null,
                fileInfoList
        );
        Assertions.assertEquals(expectedPayload, service.deleteFile("  ", baseUrl));
    }

    @Test
    void deleteFile_WrongFilename() {
        String filename3 = "file3\ntxt";
        Payload expectedPayload = new Payload(
                "Wrong file was given.",
                null,
                fileInfoList
        );
        Assertions.assertEquals(expectedPayload, service.deleteFile(filename3, baseUrl));
    }

    @Test
    void deleteFile_NoSuchFile() {
        when(repository.delete(filename1))
                .thenReturn(false);
        Payload expectedPayload = new Payload(
                filename1 + " file does not exist!",
                null,
                fileInfoList
        );
        Assertions.assertEquals(expectedPayload, service.deleteFile(filename1, baseUrl));
    }

    @Test
    void deleteFile_FileExists() {
        when(repository.delete(filename1))
                .thenReturn(true);
        Payload expectedPayload = new Payload(
                filename1 + " file has been deleted.",
                null,
                fileInfoList
        );
        Assertions.assertEquals(expectedPayload, service.deleteFile(filename1, baseUrl));
    }

    @Test
    void deleteAllFiles() {
        when(repository.count())
                .thenReturn(2L);
        when(repository.deleteAll())
                .thenReturn(1L);
        filenameStream = Stream.of(filename1);
        when(repository.findAll())
                .thenReturn(filenameStream);
        fileInfoList = List.of(fileInfo1);
        Payload expectedPayload = new Payload(
                "1 of 2 file(s) has been deleted.",
                null,
                fileInfoList
        );
        Assertions.assertEquals(expectedPayload, service.deleteAllFiles(baseUrl));
    }

    @Test
    void handleMaxSizeException() {
        Payload expectedPayload = new Payload(
                "The selected file (or one of them) is too large!",
                null,
                fileInfoList
        );
        Assertions.assertEquals(expectedPayload, service.handleMaxSizeException(baseUrl));
    }
}
