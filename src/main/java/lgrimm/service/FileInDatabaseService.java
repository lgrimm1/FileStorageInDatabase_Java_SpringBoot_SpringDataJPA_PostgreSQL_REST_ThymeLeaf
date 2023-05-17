package lgrimm.service;

import lgrimm.datamodel.FileInfo;
import lgrimm.datamodel.Multipart;
import lgrimm.datamodel.Payload;
import lgrimm.entity.FileInDatabaseEntity;
import lgrimm.repository.FileInDatabaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FileInDatabaseService {
    private final FileInDatabaseRepository repository;

    @Autowired
    public FileInDatabaseService(FileInDatabaseRepository repository) {
        this.repository = repository;
    }

    public Payload newFile() {
        return new Payload(
                null,
                null,
                null);
    }

    public Payload uploadFile(Multipart file, String baseUrl) {
        if (file == null) {
            return new Payload(
                    "No file was given.",
                    null,
                    convertFileNameStreamToFileInfoList(repository.findAll(), baseUrl)
            );
        }
        try {
            Paths.get(file.getOriginalFilename());
        }
        catch (Exception e) {
            return new Payload(
                    "Could not upload the file: " + file.getOriginalFilename(),
                    null,
                    convertFileNameStreamToFileInfoList(repository.findAll(), baseUrl)
            );
        }
        String message = repository.save(file)
                .map(filename -> filename + " file has been successfully uploaded.")
                .orElseGet(() -> "Could not upload the file: " + file.getOriginalFilename());
        return new Payload(
                message,
                null,
                convertFileNameStreamToFileInfoList(repository.findAll(), baseUrl)
        );
    }

    public Payload uploadFiles(List<Multipart> files, String baseUrl) {
        if (files == null || files.size() == 0) {
            return new Payload(
                    null,
                    List.of("No files were given."),
                    convertFileNameStreamToFileInfoList(repository.findAll(), baseUrl)
            );
        }
        files = files.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<String> saved = repository.saveAll(files).toList();
        List<String> messages = files.stream()
                .map(Multipart::getOriginalFilename)
                .map(filename -> {
                    if (saved.contains(filename)) {
                        return filename + ": [Success]";
                    }
                    else {
                        return filename + ": [Failed]";
                    }
                })
                .collect(Collectors.toList());
        messages.add(0, "Results:");
        return new Payload(
                null,
                messages,
                convertFileNameStreamToFileInfoList(repository.findAll(), baseUrl)
        );
    }

    public Payload getFileList(String baseUrl) {
        return new Payload(
                null,
                null,
                convertFileNameStreamToFileInfoList(repository.findAll(), baseUrl)
        );
    }

    public FileInDatabaseEntity getFile(String filename) {
        if (filename == null || filename.isBlank()) {
            return null;
        }
        return repository.getByFilename(filename)
                .orElse(null);
    }

    public Payload deleteFile(String filename, String baseUrl) {
        if (filename == null || filename.isBlank()) {
            return new Payload(
                    "No file was given.",
                    null,
                    convertFileNameStreamToFileInfoList(repository.findAll(), baseUrl)
            );
        }
        try {
            Paths.get(filename);
        }
        catch (Exception e) {
            return new Payload(
                    "Wrong file was given.",
                    null,
                    convertFileNameStreamToFileInfoList(repository.findAll(), baseUrl)
            );
        }
        String message = repository.delete(filename) ?
                filename + " file has been deleted." :
                filename + " file does not exist!";
        return new Payload(
                message,
                null,
                convertFileNameStreamToFileInfoList(repository.findAll(), baseUrl)
        );
    }

    public Payload deleteAllFiles(String baseUrl) {
        long count = repository.count();
        long deleted = repository.deleteAll();
        return new Payload(
                deleted + " of " + count + " file(s) has been deleted.",
                null,
                convertFileNameStreamToFileInfoList(repository.findAll(), baseUrl)
        );
    }

    public Payload handleMaxSizeException(String baseUrl) {
        return new Payload(
                "The selected file (or one of them) is too large!",
                null,
                convertFileNameStreamToFileInfoList(repository.findAll(), baseUrl)
        );
    }

    private List<FileInfo> convertFileNameStreamToFileInfoList(Stream<String> filenames, String baseUrl) {
        return filenames
                .map(filename -> {
                    String url = baseUrl + "/files/" + filename;
                    return new FileInfo(filename, url);
                })
                .toList();
    }
}
