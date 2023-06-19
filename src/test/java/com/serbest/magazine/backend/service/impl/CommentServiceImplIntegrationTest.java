package com.serbest.magazine.backend.service.impl;

import com.serbest.magazine.backend.dto.comment.CommentRequestDTO;
import com.serbest.magazine.backend.dto.comment.CommentResponseDTO;
import com.serbest.magazine.backend.dto.general.MessageResponseDTO;
import com.serbest.magazine.backend.entity.Author;
import com.serbest.magazine.backend.entity.Category;
import com.serbest.magazine.backend.entity.Comment;
import com.serbest.magazine.backend.entity.Post;
import com.serbest.magazine.backend.exception.ResourceNotFoundException;
import com.serbest.magazine.backend.mapper.CommentMapper;
import com.serbest.magazine.backend.repository.AuthorRepository;
import com.serbest.magazine.backend.repository.CategoryRepository;
import com.serbest.magazine.backend.repository.CommentRepository;
import com.serbest.magazine.backend.repository.PostRepository;
import com.serbest.magazine.backend.service.CommentService;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
class CommentServiceImplIntegrationTest {

    @Autowired
    CommentService commentService;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CommentMapper commentMapper;

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    PostRepository postRepository;

    UUID authorId;

    UUID postId;

    @BeforeEach
    void createNecessaryModels() {

        Author testUser = Author.Builder.newBuilder()
                .username("testUser")
                .password("testpassword")
                .email("test@email.com")
                .active(true)
                .createDateTime(LocalDateTime.now())
                .build();

        Author author = authorRepository.save(testUser);

        authorId = author.getId();

        Category category = categoryRepository.save(new Category("siyaset"));

        Post testPost = Post.Builder.newBuilder()
                .postId(this.postId)
                .title("testTitle")
                .author(author)
                .category(category)
                .active(true)
                .content("testContent")
                .subtitle("testSubtitle")
                .build();

        testPost.setCreateDateTime(LocalDateTime.now());
        testPost.setUpdateDateTime(LocalDateTime.now());

        Post post = postRepository.save(testPost);

        this.postId = post.getPostId();
    }

    @AfterAll
    public static void destroy() {
        try {
            File directoryTest = new File("uploads-test");
            FileUtils.cleanDirectory(directoryTest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testIntegration_createComment_success() {
        Authentication authentication = Mockito.mock(Authentication.class);
        // Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn("testUser");
        SecurityContextHolder.setContext(securityContext);


        CommentResponseDTO responseDTO = commentService
                .createComment(new CommentRequestDTO("Content", postId.toString()));


        assertEquals(responseDTO.getPostId(), postId.toString());
        assertEquals(responseDTO.getContent(), "Content");
    }

    @Test
    public void testIntegration_createComment_wrongPostId() {
        Authentication authentication = Mockito.mock(Authentication.class);
        // Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn("testUser");
        SecurityContextHolder.setContext(securityContext);

        assertThrows(
                ResourceNotFoundException.class,
                () -> commentService.createComment(new CommentRequestDTO("Content", UUID.randomUUID().toString()))
        );
    }

    @Test
    public void testIntegration_createComment_notAuthenticated() {
        Authentication authentication = Mockito.mock(Authentication.class);
        // Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn("not-existed-user");
        SecurityContextHolder.setContext(securityContext);

        assertThrows(
                ResourceNotFoundException.class,
                () -> commentService.createComment(new CommentRequestDTO("Content", this.postId.toString()))
        );
    }

    @Tag("NeedChecking")
    @Test
    public void testIntegration_getAllComments_success() {
        Post post = postRepository.findById(postId).get();
        Author author = authorRepository.findById(authorId).get();

        commentRepository.save(new Comment("Content", post, author));


        List<CommentResponseDTO> responseDTOS = commentService
                .getAllComments(this.postId.toString());

        assertEquals(responseDTOS.size(), 1);
        assertEquals(responseDTOS.get(0).getContent(), "Content");
    }

    @Test
    public void testIntegration_findById_success() {
        Post post = postRepository.findById(postId).get();
        Author author = authorRepository.findById(authorId).get();

        Comment savedComment = commentRepository.save(new Comment("Content", post, author));

        Comment comment = commentService.findById(savedComment.getId().toString());

        assertEquals("Content", comment.getContent());
    }

    @Test
    public void testIntegration_findById_commentNotFound() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> commentService.findById(UUID.randomUUID().toString())
        );
    }

    @Test
    public void testIntegration_deleteById_withSuccess() throws AccessDeniedException {
        Post post = postRepository.findById(postId).get();
        Author author = authorRepository.findById(authorId).get();
        Comment savedComment = commentRepository.save(new Comment("Content", post, author));

        Authentication authentication = Mockito.mock(Authentication.class);
        // Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn("testUser");
        SecurityContextHolder.setContext(securityContext);

        MessageResponseDTO responseDTO = commentService.deleteById(savedComment.getId().toString());

        assertEquals("Comment with id : " + savedComment.getId() + " is deleted.",responseDTO.getMessage());
    }

    @Test
    public void testIntegration_deleteById_accessDeniedException() {
        Post post = postRepository.findById(postId).get();
        Author author = authorRepository.findById(authorId).get();
        Comment savedComment = commentRepository.save(new Comment("Content", post, author));

        Authentication authentication = Mockito.mock(Authentication.class);
        // Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn("wrongUser");
        SecurityContextHolder.setContext(securityContext);

        assertThrows(
                AccessDeniedException.class,
                () -> commentService.deleteById(savedComment.getId().toString())
        );
    }

    @Test
    public void testIntegration_deleteById_resourceNotFound(){
        assertThrows(
                ResourceNotFoundException.class,
                () -> commentService.deleteById(UUID.randomUUID().toString())
        );
    }

    @Test
    public void testIntegration_updateComment_withSuccess() throws AccessDeniedException {
        Post post = postRepository.findById(postId).get();
        Author author = authorRepository.findById(authorId).get();
        Comment savedComment = commentRepository.save(new Comment("Content", post, author));

        Authentication authentication = Mockito.mock(Authentication.class);
        // Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn("testUser");
        SecurityContextHolder.setContext(securityContext);

        CommentResponseDTO responseDTO = commentService.updateComment(
                savedComment.getId().toString(),
                "UpdatedContent"
        );

        assertEquals("UpdatedContent",responseDTO.getContent());
    }

    @Test
    public void testIntegration_updateComment_AccessDeniedException() {
        Post post = postRepository.findById(postId).get();
        Author author = authorRepository.findById(authorId).get();
        Comment savedComment = commentRepository.save(new Comment("Content", post, author));

        Authentication authentication = Mockito.mock(Authentication.class);
        // Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn("wrongUser");
        SecurityContextHolder.setContext(securityContext);

        assertThrows(
                AccessDeniedException.class,
                () -> commentService.updateComment(savedComment.getId().toString(),"UpdatedContent")
        );

    }

    @Test
    public void testIntegration_updateComment_resourceNotFound(){
        assertThrows(
                ResourceNotFoundException.class,
                () -> commentService.updateComment(UUID.randomUUID().toString(),"UpdatedContent")
        );
    }

}