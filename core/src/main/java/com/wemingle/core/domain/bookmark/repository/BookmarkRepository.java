package com.wemingle.core.domain.bookmark.repository;

import com.wemingle.core.domain.bookmark.entity.BookmarkedMatchingPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<BookmarkedMatchingPost,Long> {

}
