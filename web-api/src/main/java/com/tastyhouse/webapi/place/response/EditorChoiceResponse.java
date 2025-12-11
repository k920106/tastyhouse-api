package com.tastyhouse.webapi.place.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class EditorChoiceResponse {
    private final Long id;
    private final String name;
    private final String imageUrl;
    private final String title;
    private final String content;
    private final List<EditorChoiceProductItem> products;
}
