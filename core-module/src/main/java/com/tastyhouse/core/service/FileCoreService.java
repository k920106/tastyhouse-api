package com.tastyhouse.core.service;

import com.tastyhouse.core.entity.file.UploadedFile;
import com.tastyhouse.core.repository.file.UploadedFileJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FileCoreService {

    private final UploadedFileJpaRepository uploadedFileJpaRepository;

    @Transactional
    public UploadedFile save(UploadedFile uploadedFile) {
        return uploadedFileJpaRepository.save(uploadedFile);
    }

    @Transactional(readOnly = true)
    public UploadedFile findById(Long id) {
        return uploadedFileJpaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다. ID: " + id));
    }
}
