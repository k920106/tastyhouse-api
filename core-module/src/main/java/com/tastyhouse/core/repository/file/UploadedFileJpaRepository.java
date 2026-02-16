package com.tastyhouse.core.repository.file;

import com.tastyhouse.core.entity.file.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadedFileJpaRepository extends JpaRepository<UploadedFile, Long> {
}
