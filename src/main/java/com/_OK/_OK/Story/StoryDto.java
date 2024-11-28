package com._OK._OK.Story;

import com._OK._OK.User.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoryDto {
    private Long id;
    private User user; //유저객체
    private String content;       // 현재 상황 설명
    private String choice1;       // 첫 번째 선택지
    private String choice2;       // 두 번째 선택지
    private String choice3;       // 세 번째 선택지
    private String description;   // 상황을 묘사하는 텍스트
    private byte[] image;
    private int water;            // 물의 변동량
    private int food;             // 음식의 변동량
    private int damage;             // 받은 데미지
    private int difWater;           // 물 변화량
    private int difFood;             // 음식 변화량
    private int difHp;              // 체력 변화량
    private String beforeContent; // 직전 스토리와 선택지
    private String causeOfDeath; //사망 사유
}
