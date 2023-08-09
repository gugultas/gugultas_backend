package com.serbest.magazine.backend.repository;

import com.serbest.magazine.backend.entity.Playlist;
import com.serbest.magazine.backend.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Transactional
public interface PostRepository extends JpaRepository<Post, UUID> {
    Page<Post> findByActiveTrueOrderByCreateDateTimeDesc(Pageable pageable);

    List<Post> findByActiveFalseOrderByCreateDateTimeDesc();

    @Query("select p FROM Post p WHERE p.active=true ORDER BY RANDOM() LIMIT 3")
    List<Post> findThreeActiveTrueByRandom();

    @Query("select p FROM Post p WHERE p.active=true ORDER BY p.createDateTime DESC LIMIT 5")
    List<Post> findFirstFiveActiveTrueByCreateDateTime();

    @Query("select p FROM Post p WHERE p.active=true ORDER BY p.createDateTime DESC LIMIT 4 OFFSET 5")
    List<Post> findFourPostsActiveTrueByCreateDateTime();

    @Query("select p FROM Post p WHERE p.active=true ORDER BY p.createDateTime DESC LIMIT 11 OFFSET 9")
    List<Post> findFifteenActiveTrueByCreateDateTimeOffset5();

    List<Post> findAllByAuthorUsernameAndActiveTrueOrderByCreateDateTimeDesc(String username);
    Page<Post> findAllByAuthorUsernameAndActiveTrueOrderByCreateDateTimeDesc(String username,Pageable pageable);

    List<Post> findTop5ByAuthorUsernameAndActiveTrueOrderByCreateDateTimeDesc(String username);

    Page<Post> findAllByCategoryNameAndActiveTrueOrderByCreateDateTimeDesc(String categoryName,Pageable pageable);

    List<Post> findByPlaylistsInOrderByCreateDateTimeDesc(List<Playlist> playlists);

    Page<Post> findAllBySubCategoryIdAndActiveTrueOrderByCreateDateTimeDesc(UUID categoryId,Pageable pageable);

    List<Post> findByActiveTrueAndTitleContainingIgnoreCase(String title);

    Integer countByCategoryNameAndActiveTrue(String categoryName);


}
