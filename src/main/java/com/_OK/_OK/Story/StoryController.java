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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("/amazon/story")
@Tag(name="Story", description = "Story Api") //스웨거 태그 어노테이션임
public class StoryController {
    private final String aiStoryUrl = "http://localhost:5000/generate_story";
    private final String aiImageUrl = "http://localhost:5000/generate_image";
    private final String aiMonoUrl = "http://localhost:5000/generate_monologue";
    private static final int MAX_RETRIES = 2;

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

    // 유저 day별 이미지 리스트 받기
    @GetMapping("getImages/{id}")
    @Operation(summary = "유저 day별 이미지 리스트 받기", description = "지금까지 해당 userId로 생성된 모든 이미지를 받는다.")
    public ResponseEntity< List<ImageDto>> getImages(@PathVariable("id") Long userId){
        List<Image> imageList = imageRepository.findAllByUserIdOrderByIdAsc(userId);
        List<ImageDto> imageDtoList = imageList.stream()
                .map(ImageMapper::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(imageDtoList);
    }

    @GetMapping("monologue/{id}")
    @Operation(summary = "로딩시 플레이어가 하는 독백 대사 생성", description = "10개의 독백대사와 태그를 생성합니다.<br>플레이어가 선택하기전에 호출하는 api(미리 로딩페이지 준비)" +
            "<br>태그는 [ Peaceful, Tense,  Dangerous, Scary,  Jungle Sounds, Animal Sounds, River Sounds, Battle,  Sad, Lonely ] 이 중 하나입니다.")
    @Transactional
    public ResponseEntity<MonologueDto> creatMonologue(
            @PathVariable("id") Long userId){
        Story existingStory = storyRepository.findByUserId(userId);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("content",existingStory.getContent());  // `content`라는 키 이름 사용
        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 본문을 HttpEntity로 래핑
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        // FastAPI 서버로 POST 요청 보내기
        ResponseEntity<MonologueDto> response = restTemplate.postForEntity(aiMonoUrl, requestEntity, MonologueDto.class);
        MonologueDto monologueDto = response.getBody();
        return ResponseEntity.ok(monologueDto);
    }

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

        int attempt = 0; //이미지 호출 횟수
        while(attempt++<MAX_RETRIES){
            try{
                // FastAPI 서버로 POST 요청 보내기
                ResponseEntity<byte[]> response_img = restTemplate.postForEntity(aiImageUrl, requestEntity_img, byte[].class);
                storyDto.setImage(response_img.getBody());
                // db에 이미지 저장 (서비스 클래스 따로안만들고 바로 저장)
                ImageDto imageDto = new ImageDto(null,userId,response_img.getBody());
                com._OK._OK.User.Image image = ImageMapper.mapToEntity(imageDto);
                existingStory.setImage(storyDto.getImage());
                imageRepository.save(image);
                storyRepository.save(existingStory);
                break;
            }
            catch (Exception e){
                System.err.println("AI 서버로부터 이미지 생성 실패 (시도 횟수: " + attempt + "): " + e.getMessage());
                if (attempt == MAX_RETRIES) {
                    byte[] defaultImage = getDefaultImage();
                    storyDto.setImage(defaultImage);
                    storyRepository.save(existingStory);
                    System.out.println("모든 시도 실패: 기본 이미지를 StoryDto에 설정");
                }
            }
        }

        //User 정보 변경
        user.setDay(user.getDay()+1);
        user.setFood(user.getFood()+storyDto.getFood());
        user.setWater(user.getWater()+storyDto.getWater());
        //유저의 생존 사망을 판단.
        if(!userService.isAlive(storyDto,user)){//사망
            user.setAlive(false);
        }
        if(user.getDay()<4) user.setHp(user.getHp()-2); //매일매일 체력 감소
        else if(user.getDay()<7)user.setHp(user.getHp()-3);
        else user.setHp(user.getHp()-5);

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

//이미지 호출 실패시 기본이미지 반환
    public byte[] getDefaultImage() {
        try {
            // resource.image 폴더 내 기본 이미지 파일 경로를 설정합니다.
            Path defaultImagePath = Paths.get("src/main/resources/img/SensitiveContent.png");
            // 파일을 바이트 배열로 변환하여 반환합니다.
            return Files.readAllBytes(defaultImagePath);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("기본 이미지를 로드하는 데 실패했습니다.");
            return new byte[0]; // 실패 시 빈 배열 반환
        }
    }

}
