package lgrimm.repository;

import lgrimm.datamodel.*;
import lgrimm.entity.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;
import java.util.stream.*;

@Component
public class FileInDatabaseRepository {

    private final FileInDatabaseRepositoryInterface repositoryInterface;

    @Autowired
    public FileInDatabaseRepository(FileInDatabaseRepositoryInterface repositoryInterface) {
        this.repositoryInterface = repositoryInterface;
    }

    public Optional<FileInDatabaseEntity> getByFilename(String filename) {
        return repositoryInterface.findByFileName(filename);
    }

    public Stream<FileInDatabaseEntity> getAll() {
        return repositoryInterface.findAll().stream();
    }

    public Optional<String> findByFilename(String filename) {
        Optional<FileInDatabaseEntity> entity = repositoryInterface.findByFileName(filename);
        return entity.map(FileInDatabaseEntity::getFileName);
    }

    public Stream<String> findAll() {
        return repositoryInterface.findAll().stream()
                .map(FileInDatabaseEntity::getFileName);
    }

    public Optional<String> save(Multipart file) {
        if (repositoryInterface.findByFileName(file.getOriginalFilename()).isPresent()) {
            return Optional.empty();
        }
        FileInDatabaseEntity entity = this.convertMultipartToEntity(file);
        return Optional.of(repositoryInterface.save(entity).getFileName());
    }

    public Stream<String> saveAll(List<Multipart> files) {
        return files.stream()
                .map(this::save)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    public boolean delete(String filename) {
        Optional<FileInDatabaseEntity> entity = repositoryInterface.findByFileName(filename);
        if (entity.isEmpty()) {
            return false;
        }
        repositoryInterface.deleteById(entity.get().getId());
        return true;
    }

    public long deleteAll() {
        return this.findAll()
                .map(this::delete)
                .filter(success -> success)
                .count();
    }

    public long count() {
        return repositoryInterface.count();
    }

    private FileInDatabaseEntity convertMultipartToEntity(Multipart multipart) {
        return new FileInDatabaseEntity(multipart.getOriginalFilename(),
                new String(multipart.getContent()),
                multipart.getContentType());
    }
}
