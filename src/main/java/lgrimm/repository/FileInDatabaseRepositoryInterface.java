package lgrimm.repository;

import lgrimm.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface FileInDatabaseRepositoryInterface extends JpaRepository<FileInDatabaseEntity, Long> {

    Optional<FileInDatabaseEntity> findByFileName(String filename);
}
