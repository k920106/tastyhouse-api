package com.tastyhouse.core.repository.place;

import com.tastyhouse.core.entity.place.dto.EditorChoiceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PlaceChoiceRepository {

    List<EditorChoiceDto> findEditorChoice();

    Page<EditorChoiceDto> findEditorChoice(Pageable pageable);
}
