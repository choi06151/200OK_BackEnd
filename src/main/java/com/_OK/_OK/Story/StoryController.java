package com._OK._OK.Story;

import com._OK._OK.User.User;
import com._OK._OK.User.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("/amazon/story")
public class StoryController {
    private final String aiUrl = "http://localhost:5000/generate_story";

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private StroyService stroyService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("init/{id}")
    public ResponseEntity<StoryDto> initStory(@PathVariable("id") Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 요청 본문으로 보낼 데이터 생성
        // JSON 데이터를 Map으로 작성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("choice","");  // `choice`라는 키 이름 사용
        requestBody.put("before_content", "");  // `before_content`라는 키 이름 사용


        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 본문을 HttpEntity로 래핑
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        // FastAPI 서버로 POST 요청 보내기
        ResponseEntity<StoryDto> response = restTemplate.postForEntity(aiUrl, requestEntity, StoryDto.class);
        StoryDto storyDto = response.getBody();
        if(storyDto!=null){
            storyDto.setBeforeContent(storyDto.getContent()+"선택1 :"+storyDto.getChoice1()+"선택2 :"+storyDto.getChoice2()+"선택3 :"+storyDto.getChoice3());
            storyDto.setUser(user);
        }
        stroyService.saveBeforeContent(storyDto);
        // FastAPI 서버에서 반환된 값을 리턴
        return ResponseEntity.ok(storyDto);

    }


}
