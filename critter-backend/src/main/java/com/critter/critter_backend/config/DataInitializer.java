package com.critter.critter_backend.config;

import com.critter.critter_backend.domain.CritterType;
import com.critter.critter_backend.domain.EcosystemTheme;
import com.critter.critter_backend.entity.Account;
import com.critter.critter_backend.entity.Board;
import com.critter.critter_backend.entity.Comment;
import com.critter.critter_backend.entity.CritterTemplate;
import com.critter.critter_backend.entity.Ecosystem;
import com.critter.critter_backend.entity.Guestbook;
import com.critter.critter_backend.repository.AccountRepository;
import com.critter.critter_backend.repository.BoardRepository;
import com.critter.critter_backend.repository.CommentRepository;
import com.critter.critter_backend.repository.CritterTemplateRepository;

import com.critter.critter_backend.repository.EcosystemRepository;
import com.critter.critter_backend.repository.GuestbookRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
// final로 선언된 녀석들을 자동으로 파라미터로 받는 생성자 생성
public class DataInitializer {

    private final EcosystemRepository ecosystemRepository;
    private final AccountRepository accountRepository;
    private final GuestbookRepository guestbookRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final CritterTemplateRepository critterTemplateRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            Account user1 = accountRepository.findByUserName("test").orElseGet(() -> 
                accountRepository.save(Account.builder().userName("test").password("1234").nickname("수달이").build()));
            
            Account user2 = accountRepository.findByUserName("testing").orElseGet(() -> 
                accountRepository.save(Account.builder().userName("testing").password("1234").nickname("펭귄이").build()));

            if (ecosystemRepository.count() == 0) {
                Ecosystem room1 = Ecosystem.builder()
                                            .account(user1)
                                            .roomName("나는 집으로 갈 거야~!~!~!~!")
                                            .roomTheme(EcosystemTheme.OCEAN)
                                            .build();
                ecosystemRepository.save(room1);
                
                Ecosystem room2 = Ecosystem.builder()
                                            .account(user2)
                                            .roomName("나는야 위대한 물 없이 사는 팽귄")
                                            .roomTheme(EcosystemTheme.FOREST)
                                            .build();
                ecosystemRepository.save(room2);

                // 방명록 생성
                Guestbook guestbook1 = Guestbook.builder()
                                                .ecosystem(room1)
                                                .writer(user2)
                                                .content("펭귄 평균 왤케 낮아짐;;; 펭귄계의 수치다 ;;;;")
                                                .build();
                guestbookRepository.save(guestbook1);

                Guestbook guestbook2 = Guestbook.builder()
                                                .ecosystem(room2)
                                                .writer(user1)
                                                .content("펭귄은 없네요~~^^~~")
                                                .build();
                guestbookRepository.save(guestbook2);
            }

            // 게시글 및 댓글 생성
            if (boardRepository.count() == 0) {
                Board board1 = Board.builder()
                                    .writer(user2)
                                    .title("나는 물 없이도 살아갈 수 있는")
                                    .content("고등급 펭귄님이시다~!~!~!~")
                                    .build();
                boardRepository.save(board1);

                Board board2 = Board.builder()
                                    .writer(user1)
                                    .title("글을 쓰자")
                                    .content("댓글도 쓰자")
                                    .build();
                boardRepository.save(board2);

                Comment comment1 = Comment.builder()
                                    .board(board1)
                                    .writer(user1)
                                    .content("미친놈이네")
                                    .build();
                commentRepository.save(comment1);
            }

            // 상점 데이터 생성
            if (critterTemplateRepository.count() == 0) {
                critterTemplateRepository.save(new CritterTemplate(null, "문어", CritterType.OCTOPUS, EcosystemTheme.OCEAN, 20L));
                critterTemplateRepository.save(new CritterTemplate(null, "거북이", CritterType.TURTLE, EcosystemTheme.OCEAN, 25L));
                critterTemplateRepository.save(new CritterTemplate(null, "펭귄", CritterType.PENGUIN, EcosystemTheme.OCEAN, 30L));
                critterTemplateRepository.save(new CritterTemplate(null, "다람쥐", CritterType.SQUIRREL, EcosystemTheme.FOREST, 20L));
                critterTemplateRepository.save(new CritterTemplate(null, "여우", CritterType.FOX, EcosystemTheme.FOREST, 25L));
                critterTemplateRepository.save(new CritterTemplate(null, "랫서판다", CritterType.REDPANDA, EcosystemTheme.FOREST, 30L));
                critterTemplateRepository.save(new CritterTemplate(null, "토끼", CritterType.RABBIT, EcosystemTheme.GRASSLAND, 20L));
                critterTemplateRepository.save(new CritterTemplate(null, "강아지", CritterType.DOG, EcosystemTheme.GRASSLAND, 25L));
                critterTemplateRepository.save(new CritterTemplate(null, "고양이", CritterType.CAT, EcosystemTheme.GRASSLAND, 30L));
                System.out.println("상점 데이터 생성 완료");
            }
        };
    }
}