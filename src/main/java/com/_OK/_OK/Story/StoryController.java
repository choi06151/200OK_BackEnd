package com._OK._OK.Story;

import com._OK._OK.User.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
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
    @Lazy
    private  UserService userService;
    @Autowired
    private StoryRepository storyRepository;
    @Autowired
    private ImageRepository imageRepository;

    @GetMapping("init/{id}")
    @Operation(summary = "첫 스토리 생성", description = "스토리를 생성합니다. <br>이미지는 바이트코드로 리턴합니다.")
    @Transactional
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
        stroyService.saveStory(storyDto);
        // FastAPI 서버에서 반환된 값을 리턴
        return ResponseEntity.ok(storyDto);

    }

    @PostMapping("/generate/{id}")
    @Operation(summary = "스토리 생성", description = "스토리를 생성합니다. <br>이미지는 바이트코드로 리턴합니다.")
    @Transactional
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


        existingStory.setBeforeContent(storyDto.getBeforeContent());
        existingStory.setContent(storyDto.getContent());
        existingStory.setChoice1(storyDto.getChoice1());
        existingStory.setChoice2(storyDto.getChoice2());
        existingStory.setChoice3(storyDto.getChoice3());
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
        existingStory.setImage(storyDto.getImage());
        imageRepository.save(image);
        storyRepository.save(existingStory);
        //User 정보 변경
        user.setDay(user.getDay()+1);
        user.setFood(user.getFood()+storyDto.getFood());
        user.setWater(user.getWater()+storyDto.getWater());
        //유저의 생존 사망을 판단.
        if(!userService.isAlive(storyDto,user)){//사망
            user.setAlive(false);
        }
        userRepository.save(user);
        // FastAPI 서버에서 반환된 값을 리턴
        return ResponseEntity.ok(storyDto);
    }

    //현재 스토리 필드를 리턴하는 api
    @GetMapping("/currentStory/{id}")
    @Operation(summary = "현재 플레이어 스토리", description = "현재 플레이어의 스토리와 선택지들을 리턴합니다.")
    public ResponseEntity<Map<String, Object>> currentStory(@PathVariable("id") Long userId){
        Story currentStoryEntity = storyRepository.findByUserId(userId);
        if (currentStoryEntity == null) {
            // 스토리가 없을 경우 404 응답
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Story not found for user with ID: " + userId));
        }
        Map<String, Object> response = new HashMap<>();

        StoryDto currentStoryDto = StoryMapper.mapToStoryDto(currentStoryEntity);
        System.out.println("currentStoryDto.getContent():"+currentStoryDto.getContent());
        response.put("content",currentStoryDto.getContent());
        response.put("choice1",currentStoryDto.getChoice1());
        response.put("choice2",currentStoryDto.getChoice2());
        response.put("choice3",currentStoryDto.getChoice3());
        response.put("image",currentStoryDto.getImage());
        System.out.println(response);
        return ResponseEntity.ok(response);
    }


}
