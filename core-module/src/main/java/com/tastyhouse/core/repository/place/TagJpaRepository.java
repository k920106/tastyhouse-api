package com.tastyhouse.core.repository.place;

import com.tastyhouse.core.entity.place.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagJpaRepository extends JpaRepository<Tag, Long> {

    @Query("SELECT t.tagName FROM Tag t WHERE t.id IN :tagIds")
    List<String> findTagNamesByIds(@Param("tagIds") List<Long> tagIds);
}
