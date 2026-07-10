package com.critter.critter_backend.config;

import com.critter.critter_backend.domain.BoardCategory;
import com.critter.critter_backend.domain.CritterType;
import com.critter.critter_backend.domain.EcosystemTheme;
import com.critter.critter_backend.entity.Account;
import com.critter.critter_backend.entity.Board;
import com.critter.critter_backend.entity.Comment;
import com.critter.critter_backend.entity.ShopCritter;
import com.critter.critter_backend.entity.Ecosystem;
import com.critter.critter_backend.entity.Guestbook;
import com.critter.critter_backend.repository.AccountRepository;
import com.critter.critter_backend.repository.BoardRepository;
import com.critter.critter_backend.repository.CommentRepository;
import com.critter.critter_backend.repository.ShopCritterRepository;

import com.critter.critter_backend.repository.EcosystemRepository;
import com.critter.critter_backend.repository.GuestbookRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@RequiredArgsConstructor
// final로 선언된 것을 자동으로 파라미터로 받는 생성자 생성
@Profile("local")
public class DataInitializer {

    private final EcosystemRepository ecosystemRepository;
    private final AccountRepository accountRepository;
    private final GuestbookRepository guestbookRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final ShopCritterRepository critterTemplateRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            Account user1 = accountRepository.findByUserName("test").orElseGet(() -> 
                accountRepository.save(Account.builder()
                            .userName("test")
                            .password(passwordEncoder.encode("1234"))
                            .nickname("はな")
                            .point(10000L)
                            .build()));
            
            Account user2 = accountRepository.findByUserName("testing").orElseGet(() -> 
                accountRepository.save(Account.builder().userName("testing").password(passwordEncoder.encode("1234")).nickname("ぺんぎんちゃ").point(1000L).build()));

            Account user3 = accountRepository.findByUserName("test2").orElseGet(() -> 
                accountRepository.save(Account.builder().userName("test2").password(passwordEncoder.encode("1234")).nickname("ぺん").build()));

            Account user4 = accountRepository.findByUserName("test3").orElseGet(() -> 
                accountRepository.save(Account.builder().userName("test3").password(passwordEncoder.encode("1234")).nickname("ぎんんんぎん").build()));

            if (ecosystemRepository.count() == 0) {
                Ecosystem room1 = Ecosystem.builder()
                                            .account(user1)
                                            .roomName("プラネタリウム")
                                            .roomTheme(EcosystemTheme.OCEAN)
                                            .build();
                ecosystemRepository.save(room1);
                
                Ecosystem room2 = Ecosystem.builder()
                                            .account(user2)
                                            .roomName("ぼくはいだいなる水なしペンギン")
                                            .roomTheme(EcosystemTheme.GRASSLAND)
                                            .build();
                ecosystemRepository.save(room2);

                Ecosystem room3 = Ecosystem.builder()
                                            .account(user2)
                                            .roomName("ぼくはいだいなる水なしペンギン")
                                            .roomTheme(EcosystemTheme.FOREST)
                                            .build();
                ecosystemRepository.save(room3);

                Ecosystem room4 = Ecosystem.builder()
                                            .account(user3)
                                            .roomName("오늘도 좋은 하루 되세여")
                                            .roomTheme(EcosystemTheme.FOREST)
                                            .build();
                ecosystemRepository.save(room4);

                Ecosystem room5 = Ecosystem.builder()
                                            .account(user3)
                                            .roomName("아름다운 하루를 보내고 싶은 날엔 놀러와")
                                            .roomTheme(EcosystemTheme.OCEAN)
                                            .build();
                ecosystemRepository.save(room5);

                Ecosystem room6 = Ecosystem.builder()
                                            .account(user4)
                                            .roomName("遊びに来て")
                                            .roomTheme(EcosystemTheme.OCEAN)
                                            .build();
                ecosystemRepository.save(room6);

                Ecosystem room7 = Ecosystem.builder()
                                            .account(user4)
                                            .roomName("かわいいのがいっぱい")
                                            .roomTheme(EcosystemTheme.GRASSLAND)
                                            .build();
                ecosystemRepository.save(room7);

                Ecosystem room8 = Ecosystem.builder()
                                            .account(user4)
                                            .roomName("集まれどうぶｔ")
                                            .roomTheme(EcosystemTheme.FOREST)
                                            .build();
                ecosystemRepository.save(room8);

                // 방명록 생성
                if (guestbookRepository.count() == 0 && ecosystemRepository.count() >= 5) {
                    // 저장된 방들을 순서대로 확보해서 유연하게 매핑
                    var rooms = ecosystemRepository.findAll();
                    
                    guestbookRepository.save(Guestbook.builder().ecosystem(rooms.get(0)).writer(user2).content("펭평...").build());
                    guestbookRepository.save(Guestbook.builder().ecosystem(rooms.get(1)).writer(user1).content("펭귄은 없네요~~^^~~").build());
                    guestbookRepository.save(Guestbook.builder().ecosystem(rooms.get(3)).writer(user1).content("놀러왔어요~^^").build());
                    guestbookRepository.save(Guestbook.builder().ecosystem(rooms.get(4)).writer(user4).content("좋은 하루~^^").build());
                    System.out.println("방명록 데이터 생성 완료");
                }
            }

            // 게시글 및 댓글 생성
            if (boardRepository.count() == 0) {
                Board board1 = Board.builder()
                                    .writer(user2)
                                    .category(BoardCategory.KOREAN)
                                    .title("나는 물 없이도 살아갈 수 있는")
                                    .content("고등급 펭귄님이시다~!~!~!~")
                                    .category(BoardCategory.KOREAN)
                                    .build();
                boardRepository.save(board1);

                Board board2 = Board.builder()
                                    .writer(user1)
                                    .title("글을 쓰자")
                                    .content("댓글도 쓰자")
                                    .category(BoardCategory.KOREAN)
                                    .build();
                boardRepository.save(board2);

                Board board3 = Board.builder()
                                    .writer(user3)
                                    .title("日本語も書けるんですね")
                                    .content("そうかそうかそうかそうか")
                                    .category(BoardCategory.JAPANESE)
                                    .build();
                boardRepository.save(board3);

                Board board4 = Board.builder()
                                    .writer(user4)
                                    .title("終わらない")
                                    .content("今日もいい天気でありますように")
                                    .category(BoardCategory.JAPANESE)
                                    .build();
                boardRepository.save(board4);

                Comment comment1 = Comment.builder()
                                    .board(board1)
                                    .writer(user1)
                                    .content("미친놈이네")
                                    .build();
                commentRepository.save(comment1);
            }

            // 상점 데이터 생성
            if (critterTemplateRepository.count() == 0) {
                critterTemplateRepository.save(new ShopCritter(null, "critter.octopus", CritterType.OCTOPUS, EcosystemTheme.OCEAN, 15L));
                critterTemplateRepository.save(new ShopCritter(null, "critter.penguin", CritterType.PENGUIN, EcosystemTheme.OCEAN, 20L));
                critterTemplateRepository.save(new ShopCritter(null, "critter.turtle", CritterType.TURTLE, EcosystemTheme.OCEAN, 30L));
                critterTemplateRepository.save(new ShopCritter(null, "critter.squirrel", CritterType.SQUIRREL, EcosystemTheme.FOREST, 20L));
                critterTemplateRepository.save(new ShopCritter(null, "critter.redpanda", CritterType.REDPANDA, EcosystemTheme.FOREST, 25L));
                critterTemplateRepository.save(new ShopCritter(null, "critter.fox", CritterType.FOX, EcosystemTheme.FOREST, 35L));
                critterTemplateRepository.save(new ShopCritter(null, "critter.rabbit", CritterType.RABBIT, EcosystemTheme.GRASSLAND, 15L));
                critterTemplateRepository.save(new ShopCritter(null, "critter.cat", CritterType.CAT, EcosystemTheme.GRASSLAND, 25L));
                critterTemplateRepository.save(new ShopCritter(null, "critter.dog", CritterType.DOG, EcosystemTheme.GRASSLAND, 30L));
                System.out.println("상점 데이터 생성 완료");
            }
        };
    }
}