package com._OK._OK.Story;

import com._OK._OK.User.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name="Story", description = "Story Api") //스웨거 태그 어노테이션임
public class StoryController {
    private final String aiStoryUrl = "http://localhost:5000/generate_story";
    private final String aiImageUrl = "http://localhost:5000/generate_image";

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private StroyService stroyService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StoryRepository storyRepository;
    @Autowired
    private ImageRepository imageRepository;

    @GetMapping("init/{id}")
    @Operation(summary = "유저 생성", description = "유저를 생성합니다.<br> id는 자동생성됩니다. <br>이미지는 바이트코드로 리턴합니다.")
    public ResponseEntity<StoryDto> initStory(
            @PathVariable("id") Long userId){
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
        ResponseEntity<StoryDto> response = restTemplate.postForEntity(aiStoryUrl, requestEntity, StoryDto.class);
        StoryDto storyDto = response.getBody();
        if(storyDto!=null){
            storyDto.setBeforeContent(storyDto.getContent()+"선택1 :"+storyDto.getChoice1()+"선택2 :"+storyDto.getChoice2()+"선택3 :"+storyDto.getChoice3());
            storyDto.setUser(user);
        }
        Map<String,Object> requestBody_img = new HashMap<>();
        requestBody_img.put("description", storyDto.getDescription());  // description라는 키 이름 사용
        // 요청 본문을 HttpEntity로 래핑
        HttpEntity<Map<String, Object>> requestEntity_img = new HttpEntity<>(requestBody_img, headers);

        // FastAPI 서버로 POST 요청 보내기
        ResponseEntity<byte[]> response_img = restTemplate.postForEntity(aiImageUrl, requestEntity_img, byte[].class);
        storyDto.setImage(response_img.getBody());
        // db에 이미지 저장 (서비스 클래스 따로안만들고 바로 저장)
        ImageDto imageDto = new ImageDto(null,userId,response_img.getBody());
        com._OK._OK.User.Image image = ImageMapper.mapToEntity(imageDto);
        imageRepository.save(image);
        stroyService.saveBeforeContent(storyDto);
        // FastAPI 서버에서 반환된 값을 리턴
        return ResponseEntity.ok(storyDto);

    }

    @PostMapping("/generate/{id}")
    @Operation(summary = "유저 생성", description = "유저를 생성합니다.<br> id는 자동생성됩니다.<br>이미지는 바이트코드로 리턴합니다.")
    public ResponseEntity<StoryDto> generateStory(
            @PathVariable("id") Long userId, @RequestBody ChoiceDto choiceDto){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Story existingStory = storyRepository.findByUserId(userId);
        String choice = choiceDto.getChoice();
        System.out.println("choice:"+choice);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("choice",choice);  // `choice`라는 키 이름 사용
        requestBody.put("before_content",existingStory.getBeforeContent());  // `before_content`라는 키 이름 사용
//        System.out.println(requestBody);
        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 본문을 HttpEntity로 래핑
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        // FastAPI 서버로 POST 요청 보내기
        ResponseEntity<StoryDto> response = restTemplate.postForEntity(aiStoryUrl, requestEntity, StoryDto.class);
        StoryDto storyDto = response.getBody();
        if(storyDto!=null){
            storyDto.setBeforeContent(storyDto.getContent()+"선택1 :"+storyDto.getChoice1()+"선택2 :"+storyDto.getChoice2()+"선택3 :"+storyDto.getChoice3());
            storyDto.setUser(user);
        }
        if(storyDto!=null){
            storyDto.setBeforeContent(storyDto.getContent()+"선택1 :"+storyDto.getChoice1()+"선택2 :"+storyDto.getChoice2()+"선택3 :"+storyDto.getChoice3());
            storyDto.setUser(user);
        }

        existingStory.setBeforeContent(storyDto.getBeforeContent());
        Map<String,Object> requestBody_img = new HashMap<>();
        requestBody_img.put("description", storyDto.getDescription());  // description라는 키 이름 사용
        // 요청 본문을 HttpEntity로 래핑
        HttpEntity<Map<String, Object>> requestEntity_img = new HttpEntity<>(requestBody_img, headers);

        // FastAPI 서버로 POST 요청 보내기
        ResponseEntity<byte[]> response_img = restTemplate.postForEntity(aiImageUrl, requestEntity_img, byte[].class);
        storyDto.setImage(response_img.getBody());
        // db에 이미지 저장 (서비스 클래스 따로안만들고 바로 저장)
        ImageDto imageDto = new ImageDto(null,userId,response_img.getBody());
        com._OK._OK.User.Image image = ImageMapper.mapToEntity(imageDto);
        imageRepository.save(image);
        stroyService.saveBeforeContent(StoryMapper.mapToStoryDto(existingStory));
        // FastAPI 서버에서 반환된 값을 리턴
        return ResponseEntity.ok(storyDto);
    }

}
