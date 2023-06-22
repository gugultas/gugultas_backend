package com.serbest.magazine.backend.service.impl;

import com.serbest.magazine.backend.dto.post.*;
import com.serbest.magazine.backend.entity.*;
import com.serbest.magazine.backend.exception.CustomApplicationException;
import com.serbest.magazine.backend.exception.ResourceNotFoundException;
import com.serbest.magazine.backend.repository.AuthorRepository;
import com.serbest.magazine.backend.repository.CategoryRepository;
import com.serbest.magazine.backend.repository.PostRepository;
import com.serbest.magazine.backend.repository.SubCategoryRepository;
import com.serbest.magazine.backend.service.PostService;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
class PostServiceImplIntegrationTest {

    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    SubCategoryRepository subCategoryRepository;

    @Autowired
    AuthorRepository authorRepository;

    UUID authorId;

    UUID categoryId;
    UUID subCategoryId;

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

        Category category = categoryRepository.save(new Category("Siyaset"));

        this.categoryId = category.getId();

        SubCategory subCategory =
                subCategoryRepository.save(new SubCategory("İç Siyaset", category));

        this.subCategoryId = subCategory.getId();

        postRepository.save(Post.Builder.newBuilder()
                .postId(UUID.randomUUID())
                .title("Test Title")
                .content("Test Content")
                .category(category)
                .subCategory(subCategory)
                .author(author)
                .active(false)
                .postImage(new ImageModel())
                .build());

        Post post = postRepository.save(Post.Builder.newBuilder()
                .postId(UUID.randomUUID())
                .title("Test Title")
                .content("Test Content")
                .category(category)
                .subCategory(subCategory)
                .author(author)
                .active(true)
                .postImage(new ImageModel())
                .build());

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
    public void testIntegration_createPost_authorNotFound() throws IOException {
        Authentication authentication = Mockito.mock(Authentication.class);
        // Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn("not-existed-user");
        SecurityContextHolder.setContext(securityContext);

        String imagePath = new ClassPathResource("wall1677252684787.jpg").getPath();
        Path path = Paths.get(System.getProperty("user.dir") + "/src/test/resources/" + imagePath);
        MultipartFile multipartFile = new MockMultipartFile("wall1677252684787.jpg",
                "wall1677252684787.jpg", "image/jpeg", Files.readAllBytes(path));

        PostRequestDTO requestDTO = PostRequestDTO.builder()
                .title("TestTitle")
                .subtitle("TestSubtitle")
                .content("TestContent")
                .category("Siyaset")
                .subCategory("İç Siyaset")
                .image(multipartFile)
                .build();

        assertThrows(
                ResourceNotFoundException.class,
                () -> postService.createPost(requestDTO)
        );
    }

    @Test
    public void testIntegration_createPostEditor_success() throws IOException {

        String imagePath = new ClassPathResource("wall1677252684787.jpg").getPath();
        Path path = Paths.get(System.getProperty("user.dir") + "/src/test/resources/" + imagePath);
        MultipartFile multipartFile = new MockMultipartFile("wall1677252684787.jpg",
                "wall1677252684787.jpg", "image/jpeg", Files.readAllBytes(path));

        PostCreateEditorRequestDTO requestDTO = PostCreateEditorRequestDTO.builder()
                .title("TestTitle")
                .subtitle("TestSubtitle")
                .content("TestContent")
                .author("testUser")
                .category("Siyaset")
                .subCategory("İç Siyaset")
                .image(multipartFile)
                .build();


        PostCreateResponseDTO responseDTO = postService
                .createPostEditor(requestDTO);


        assertEquals(responseDTO.getTitle(), requestDTO.getTitle());
        assertEquals(responseDTO.getContent(), requestDTO.getContent());
    }

    @Test
    public void testIntegration_createPostEditor_authorNotFound() throws IOException {
        String imagePath = new ClassPathResource("wall1677252684787.jpg").getPath();
        Path path = Paths.get(System.getProperty("user.dir") + "/src/test/resources/" + imagePath);
        MultipartFile multipartFile = new MockMultipartFile("wall1677252684787.jpg",
                "wall1677252684787.jpg", "image/jpeg", Files.readAllBytes(path));

        PostCreateEditorRequestDTO requestDTO = PostCreateEditorRequestDTO.builder()
                .title("TestTitle")
                .subtitle("TestSubtitle")
                .content("TestContent")
                .author("wrongUser")
                .category("Siyaset")
                .subCategory("İç Siyaset")
                .image(multipartFile)
                .build();

        assertThrows(
                ResourceNotFoundException.class,
                () -> postService.createPostEditor(requestDTO)
        );
    }

    @Test
    public void testIntegration_createPostEditor_categoryNotFound() throws IOException {
        String imagePath = new ClassPathResource("wall1677252684787.jpg").getPath();
        Path path = Paths.get(System.getProperty("user.dir") + "/src/test/resources/" + imagePath);
        MultipartFile multipartFile = new MockMultipartFile("wall1677252684787.jpg",
                "wall1677252684787.jpg", "image/jpeg", Files.readAllBytes(path));

        PostCreateEditorRequestDTO requestDTO = PostCreateEditorRequestDTO.builder()
                .title("TestTitle")
                .subtitle("TestSubtitle")
                .content("TestContent")
                .author("testUser")
                .category("wrongCategory")
                .subCategory("İç Siyaset")
                .image(multipartFile)
                .build();


        assertThrows(
                ResourceNotFoundException.class,
                () -> postService.createPostEditor(requestDTO)
        );
    }

    @Test
    public void testIntegration_createPostEditor_subCategoryNotFound() throws IOException {
        String imagePath = new ClassPathResource("wall1677252684787.jpg").getPath();
        Path path = Paths.get(System.getProperty("user.dir") + "/src/test/resources/" + imagePath);
        MultipartFile multipartFile = new MockMultipartFile("wall1677252684787.jpg",
                "wall1677252684787.jpg", "image/jpeg", Files.readAllBytes(path));

        PostCreateEditorRequestDTO requestDTO = PostCreateEditorRequestDTO.builder()
                .title("TestTitle")
                .subtitle("TestSubtitle")
                .content("TestContent")
                .author("testUser")
                .category("Siyaset")
                .subCategory("notExisted")
                .image(multipartFile)
                .build();


        assertThrows(
                ResourceNotFoundException.class,
                () -> postService.createPostEditor(requestDTO)
        );
    }

    @Test
    public void testIntegration_findById_success() {

        PostResponseDTO responseDTO = postService.findById(this.postId.toString());

        assertEquals("Test Title", responseDTO.getTitle());
    }

    @Test
    public void testIntegration_findById_postNotFound() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> postService.findById(UUID.randomUUID().toString())
        );
    }

    @Test
    public void testIntegration_activatePost_withSuccess() {
        PostResponseDTO responseDTO = postService.activatePost(this.postId.toString());

        assertEquals("Test Title", responseDTO.getTitle());
    }

    @Test
    public void testIntegration_activatePost_postNotFound() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> postService.activatePost(UUID.randomUUID().toString())
        );
    }

    @Test
    public void testIntegration_updatePostEditor_success() throws IOException {

        PostUpdateEditorRequestDTO requestDTO = PostUpdateEditorRequestDTO.builder()
                .title("TestTitle")
                .subtitle("TestSubtitle")
                .content("TestContent")
                .category("Siyaset")
                .subCategory("İç Siyaset")
                .imageProtect(true)
                .build();


        PostResponseDTO responseDTO = postService
                .updatePostEditor(postId.toString(), requestDTO);


        assertEquals(responseDTO.getTitle(), requestDTO.getTitle());
        assertEquals(responseDTO.getContent(), requestDTO.getContent());
    }

    @Test
    public void testIntegration_updatePostEditor_postNotFound() {

        PostUpdateEditorRequestDTO requestDTO = PostUpdateEditorRequestDTO.builder()
                .title("TestTitle")
                .subtitle("TestSubtitle")
                .content("TestContent")
                .category("Siyaset")
                .subCategory("İç Siyaset")
                .imageProtect(true)
                .build();

        assertThrows(
                ResourceNotFoundException.class,
                () -> postService.updatePostEditor(UUID.randomUUID().toString(), requestDTO)
        );

    }

    @Test
    public void testIntegration_updatePostEditor_categoryNotFound() {

        PostUpdateEditorRequestDTO requestDTO = PostUpdateEditorRequestDTO.builder()
                .title("TestTitle")
                .subtitle("TestSubtitle")
                .content("TestContent")
                .category("wrongCategory")
                .subCategory("İç Siyaset")
                .imageProtect(true)
                .build();

        assertThrows(
                ResourceNotFoundException.class,
                () -> postService
                        .updatePostEditor(postId.toString(), requestDTO)
        );

    }

    @Test
    public void testIntegration_updatePostEditor_subCategoryNotFound() {

        PostUpdateEditorRequestDTO requestDTO = PostUpdateEditorRequestDTO.builder()
                .title("TestTitle")
                .subtitle("TestSubtitle")
                .content("TestContent")
                .category("Siyaset")
                .subCategory("NotExisted")
                .imageProtect(true)
                .build();

        assertThrows(
                ResourceNotFoundException.class,
                () -> postService
                        .updatePostEditor(postId.toString(), requestDTO)
        );

    }

    @Test
    public void testIntegration_updatePostEditor_subCategoryNotBelongToSelectedCategory() {

        Category category = categoryRepository.save(new Category("Spor"));

        PostUpdateEditorRequestDTO requestDTO = PostUpdateEditorRequestDTO.builder()
                .title("TestTitle")
                .subtitle("TestSubtitle")
                .content("TestContent")
                .category(category.getName())
                .subCategory("İç Siyaset")
                .imageProtect(true)
                .build();


        assertThrows(
                CustomApplicationException.class,
                () -> postService
                        .updatePostEditor(postId.toString(), requestDTO)
        );
    }

    @Test
    public void testIntegration_getPostsByCategory_success() {

        List<PostResponseDTO> responseDTOS = postService.getPostsByCategory("Siyaset");

        assertEquals(responseDTOS.size(), 1);
        assertEquals(responseDTOS.get(0).getTitle(), "Test Title");
    }

    @Test
    public void testIntegration_getPostsByCategory_categoryNotFound() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> postService.getPostsByCategory("wrongCategory")
        );
    }

    @Test
    public void testIntegration_getPostsBySubCategory_success() {

        List<PostResponseDTO> responseDTOS = postService.getPostsBySubCategory(this.subCategoryId.toString());

        assertEquals(responseDTOS.size(), 1);
        assertEquals(responseDTOS.get(0).getTitle(), "Test Title");
    }

    @Test
    public void testIntegration_findByUsername_success() {

        List<PostResponseDTO> responseDTOS = postService.findByUsername("testUser");

        assertEquals(responseDTOS.size(), 1);
        assertEquals(responseDTOS.get(0).getTitle(), "Test Title");
    }

    @Test
    public void testIntegration_countsByCategoryName_success() {

        Integer responseDTOS = postService.countsByCategoryName("Siyaset");

        assertEquals(responseDTOS, 1);
    }

    @Nested
    class AuthBefore {
        @BeforeEach
        void auth() {
            Authentication authentication = Mockito.mock(Authentication.class);
            // Mockito.whens() for your authorization object
            SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
            Mockito.when(authentication.getName()).thenReturn("testUser");
            SecurityContextHolder.setContext(securityContext);
        }

        @Test
        public void testIntegration_createPost_success() throws IOException {

            String imagePath = new ClassPathResource("wall1677252684787.jpg").getPath();
            Path path = Paths.get(System.getProperty("user.dir") + "/src/test/resources/" + imagePath);
            MultipartFile multipartFile = new MockMultipartFile("wall1677252684787.jpg",
                    "wall1677252684787.jpg", "image/jpeg", Files.readAllBytes(path));

            PostRequestDTO requestDTO = PostRequestDTO.builder()
                    .title("TestTitle")
                    .subtitle("TestSubtitle")
                    .content("TestContent")
                    .category("Siyaset")
                    .subCategory("İç Siyaset")
                    .image(multipartFile)
                    .build();


            PostCreateResponseDTO responseDTO = postService
                    .createPost(requestDTO);


            assertEquals(responseDTO.getTitle(), requestDTO.getTitle());
            assertEquals(responseDTO.getContent(), requestDTO.getContent());
        }

        @Test
        public void testIntegration_createPost_categoryNotFound() throws IOException {

            String imagePath = new ClassPathResource("wall1677252684787.jpg").getPath();
            Path path = Paths.get(System.getProperty("user.dir") + "/src/test/resources/" + imagePath);
            MultipartFile multipartFile = new MockMultipartFile("wall1677252684787.jpg",
                    "wall1677252684787.jpg", "image/jpeg", Files.readAllBytes(path));

            PostRequestDTO requestDTO = PostRequestDTO.builder()
                    .title("TestTitle")
                    .subtitle("TestSubtitle")
                    .content("TestContent")
                    .category("wrongCategory")
                    .subCategory("İç Siyaset")
                    .image(multipartFile)
                    .build();

            assertThrows(
                    ResourceNotFoundException.class,
                    () -> postService.createPost(requestDTO)
            );
        }

        @Test
        public void testIntegration_createPost_subCategoryNotFound() throws IOException {

            String imagePath = new ClassPathResource("wall1677252684787.jpg").getPath();
            Path path = Paths.get(System.getProperty("user.dir") + "/src/test/resources/" + imagePath);
            MultipartFile multipartFile = new MockMultipartFile("wall1677252684787.jpg",
                    "wall1677252684787.jpg", "image/jpeg", Files.readAllBytes(path));

            PostRequestDTO requestDTO = PostRequestDTO.builder()
                    .title("TestTitle")
                    .subtitle("TestSubtitle")
                    .content("TestContent")
                    .category("Siyaset")
                    .subCategory("notExisted")
                    .image(multipartFile)
                    .build();

            assertThrows(
                    ResourceNotFoundException.class,
                    () -> postService.createPost(requestDTO)
            );
        }

        @Test
        public void testIntegration_createPost_subCategoryNotBelongToSelectedCategory() throws IOException {

            Category category = categoryRepository.save(new Category("Spor"));

            String imagePath = new ClassPathResource("wall1677252684787.jpg").getPath();
            Path path = Paths.get(System.getProperty("user.dir") + "/src/test/resources/" + imagePath);
            MultipartFile multipartFile = new MockMultipartFile("wall1677252684787.jpg",
                    "wall1677252684787.jpg", "image/jpeg", Files.readAllBytes(path));

            PostRequestDTO requestDTO = PostRequestDTO.builder()
                    .title("TestTitle")
                    .subtitle("TestSubtitle")
                    .content("TestContent")
                    .category(category.getName())
                    .subCategory("İç Siyaset")
                    .image(multipartFile)
                    .build();

            Exception exception = assertThrows(
                    CustomApplicationException.class,
                    () -> postService.createPost(requestDTO)
            );

            assertEquals("The Sub-Category is not belong to selected category.",exception.getMessage());
        }

        @Test
        public void testIntegration_getAllPosts_success() {

            List<PostResponseDTO> responseDTOS = postService.getAllPosts();

            assertEquals(responseDTOS.size(), 1);
            assertEquals(responseDTOS.get(0).getContent(), "Test Content");
        }

        @Test
        public void testIntegration_getDeactivatedPost_success() {

            List<DeactivatedPostApiResponseDTO> responseDTOS = postService.getDeactivatedPost();

            assertEquals(1, responseDTOS.size());
            assertEquals("Test Title", responseDTOS.get(0).getTitle());
        }

        @Test
        public void testIntegration_deactivatePost_withSuccess() throws IOException {
            PostResponseDTO responseDTO = postService.deactivatePost(PostServiceImplIntegrationTest.this.postId.toString());

            assertEquals("Test Title", responseDTO.getTitle());
        }

        @Test
        public void testIntegration_updatePost_success() throws IOException {

            PostUpdateRequestDTO requestDTO = PostUpdateRequestDTO.builder()
                    .title("TestTitle")
                    .subtitle("TestSubtitle")
                    .content("TestContent")
                    .category("Siyaset")
                    .subCategory("İç Siyaset")
                    .imageProtect(true)
                    .build();


            PostResponseDTO responseDTO = postService
                    .updatePost(PostServiceImplIntegrationTest.this.postId.toString(), requestDTO);


            assertEquals(responseDTO.getTitle(), requestDTO.getTitle());
            assertEquals(responseDTO.getContent(), requestDTO.getContent());
        }

        @Test
        public void testIntegration_updatePost_postNotFound() {

            PostUpdateRequestDTO requestDTO = PostUpdateRequestDTO.builder()
                    .title("TestTitle")
                    .subtitle("TestSubtitle")
                    .content("TestContent")
                    .category("Siyaset")
                    .subCategory("İç Siyaset")
                    .imageProtect(true)
                    .build();

            assertThrows(
                    ResourceNotFoundException.class,
                    () -> postService.updatePost(UUID.randomUUID().toString(), requestDTO)
            );

        }

        @Test
        public void testIntegration_updatePost_categoryNotFound() {

            PostUpdateRequestDTO requestDTO = PostUpdateRequestDTO.builder()
                    .title("TestTitle")
                    .subtitle("TestSubtitle")
                    .content("TestContent")
                    .category("wrongCategory")
                    .subCategory("İç Siyaset")
                    .imageProtect(true)
                    .build();

            assertThrows(
                    ResourceNotFoundException.class,
                    () -> postService
                            .updatePost(PostServiceImplIntegrationTest.this.postId.toString(), requestDTO)
            );

        }

        @Test
        public void testIntegration_updatePost_subCategoryNotFound() {

            PostUpdateRequestDTO requestDTO = PostUpdateRequestDTO.builder()
                    .title("TestTitle")
                    .subtitle("TestSubtitle")
                    .content("TestContent")
                    .category("Siyaset")
                    .subCategory("notExisted")
                    .imageProtect(true)
                    .build();

            assertThrows(
                    ResourceNotFoundException.class,
                    () -> postService
                            .updatePost(PostServiceImplIntegrationTest.this.postId.toString(), requestDTO)
            );

        }

        @Test
        public void testIntegration_updatePost_subCategoryNotBelongToSelectedCategory() throws IOException {

            Category category = categoryRepository.save(new Category("Spor"));

            PostUpdateRequestDTO requestDTO = PostUpdateRequestDTO.builder()
                    .title("TestTitle")
                    .subtitle("TestSubtitle")
                    .content("TestContent")
                    .category(category.getName())
                    .subCategory("İç Siyaset")
                    .imageProtect(true)
                    .build();

            assertThrows(
                    CustomApplicationException.class,
                    () -> postService
                            .updatePost(PostServiceImplIntegrationTest.this.postId.toString(), requestDTO)
            );
        }

    }

    @Nested
    class GetPosts {
        @BeforeEach
        void posts_creation() {
            Category category = categoryRepository.findByName("Siyaset").get();
            SubCategory subCategory = subCategoryRepository.findByName("İç Siyaset").get();
            Author author = authorRepository.findById(authorId).get();

            Post testPost = Post.Builder.newBuilder()
                    .postId(UUID.randomUUID())
                    .title("Test Title")
                    .content("Test Content")
                    .category(category)
                    .subCategory(subCategory)
                    .author(author)
                    .active(true)
                    .postImage(new ImageModel())
                    .build();

            postRepository.save(testPost);
            postRepository.save(testPost);
            postRepository.save(testPost);
            postRepository.save(testPost);
            postRepository.save(testPost);
            postRepository.save(testPost);
            postRepository.save(testPost);
            postRepository.save(testPost);
            postRepository.save(testPost);
            postRepository.save(testPost);

        }

        @Test
        public void testIntegration_getFirstFivePosts_success() {

            List<FirstFivePostsListDTO> responseDTOS = postService.getFirstFivePosts();

            assertEquals(responseDTOS.size(), 5);
            assertEquals(responseDTOS.get(0).getTitle(), "Test Title");
        }

        @Test
        public void testIntegration_getFourPostsForTop_success() {

            List<MainPagePostsListDTO> responseDTOS = postService.getFourPostsForTop();

            assertEquals(responseDTOS.size(), 4);
            assertEquals(responseDTOS.get(0).getTitle(), "Test Title");
        }

        @Test
        public void testIntegration_getPostsForMainPage_success() {

            List<MainPagePostsListDTO> responseDTOS = postService.getPostsForMainPage();

            assertEquals(responseDTOS.size(), 2);
            assertEquals(responseDTOS.get(0).getTitle(), "Test Title");
        }

        @Test
        public void testIntegration_getRandomThreePost_success() {

            List<PostResponseDTO> responseDTOS = postService.getRandomThreePost();

            assertEquals(responseDTOS.size(), 3);
            assertEquals(responseDTOS.get(0).getTitle(), "Test Title");
        }
    }

}