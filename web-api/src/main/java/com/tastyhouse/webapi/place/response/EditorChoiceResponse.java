package com.tastyhouse.webapi.place.response;

import java.util.List;

public record EditorChoiceResponse(
        Long id,
        String name,
        String imageUrl,
        String title,
        String content,
        List<EditorChoiceProductItem> products
) {
}
