package com._OK._OK.Story;

import com._OK._OK.User.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity

public class Story {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)  // 외래키 설정
    private User user;  // User 엔티티와의 일대일 관계
    @Column
    private String beforeContent;
    @Column
    private String content;
    @Column
    private String choice1;
    @Column
    private String choice2;
    @Column
    private String choice3;
    @Column
    private String causeOfDeath;
    @Lob
    private byte[] image;



}
