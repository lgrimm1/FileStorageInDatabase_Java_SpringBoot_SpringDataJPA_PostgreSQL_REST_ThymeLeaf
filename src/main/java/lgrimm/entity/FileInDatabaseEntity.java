package lgrimm.entity;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "files")
public class FileInDatabaseEntity {

    @Id
    @SequenceGenerator(name = "files_seq", sequenceName = "files_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "files_seq")
    @Column(name = "id")
    private long id;
    @Column(name = "filename", unique = true, nullable = false)
    String fileName;
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    String content;
    @Column(name = "filetype", nullable = false)
    private String fileType;

    public FileInDatabaseEntity() {
    }

    public FileInDatabaseEntity(String fileName, String content, String fileType) {
        this.fileName = fileName;
        this.content = content;
        this.fileType = fileType;
    }

    public FileInDatabaseEntity(long id, String fileName, String content, String fileType) {
        this.id = id;
        this.fileName = fileName;
        this.content = content;
        this.fileType = fileType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileInDatabaseEntity that = (FileInDatabaseEntity) o;
        return id == that.id &&
                Objects.equals(fileName, that.fileName) &&
                Objects.equals(content, that.content) &&
                Objects.equals(fileType, that.fileType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fileName, content, fileType);
    }

    @Override
    public String toString() {
        return "FileInDatabaseEntity{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", content='" + content + '\'' +
                ", fileType='" + fileType + '\'' +
                '}';
    }
}
