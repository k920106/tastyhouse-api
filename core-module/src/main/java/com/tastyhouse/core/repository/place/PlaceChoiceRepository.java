package com.tastyhouse.core.repository.place;

import com.tastyhouse.core.entity.place.dto.EditorChoiceDto;

import java.util.List;

public interface PlaceChoiceRepository {

    List<EditorChoiceDto> findEditorChoice();
}
