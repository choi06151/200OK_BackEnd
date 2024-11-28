package com._OK._OK.User;

import com._OK._OK.Story.StoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user  = UserMapper.mapToUser(userDto);
        User savedUser = userRepository.save(user);
        return UserMapper.mapToUserDto(savedUser);
    }
    // 정렬된 데이터를 반환
    @Override
    public List<User> getSortedUsers() {
        return userRepository.findAllByAliveFalseOrderByDayDescFoodDescWaterDescIdAsc();
    }
    @Override
    public boolean isAlive(StoryDto storyDto,User user) {
        int hp = user.getHp();
        int damage = storyDto.getDamage();
        int totalPer = 0; //죽을 확률
        int penalty = 0; // 패널티 확률 (데미지가 있으면 발동)
        if(user.getWater()<0||user.getFood()<0||hp<0)return false;//hp, 물, 식량값 음수면 즉시 사망
        if(damage>0){// 데미지는 즉시 패널티 확률로 적용
            penalty = damage * 10;
            totalPer += penalty;
            //데미지만큼 유저 체력감소
            user.setHp(user.getHp()-damage);
        }
        //waterAndFood값에 따른 확률, 일단 데미지 입기전을 기본확률로 함.
        System.out.println("hp : "+hp);
        switch (hp){
            case 10 :
                user.setProbability(2);
                break;
            case 9 :
                user.setProbability(5);
                break;
            case 8 :
                user.setProbability(10);
                break;
            case 7 :
                user.setProbability(15);
                break;
            case 6 :
                user.setProbability(20);
                break;
            case 5 :
                user.setProbability(30);
                break;
            case 4 :
                user.setProbability(35);
                break;
            case 3 :
                user.setProbability(40);
                break;
            case 2 :
                user.setProbability(45);
                break;
            case 1 :
                user.setProbability(51);
                break;
            case 0 :
                user.setProbability(100);
                break;
            default:
                user.setProbability(1);
                break;
        }
        totalPer += user.getProbability();

        //확률에 따른 생존 사망 판단
        Random random = new Random();
        int randomValue = random.nextInt(100) + 1; // 1부터 100까지
        if(totalPer>=randomValue)return false;
        else return true;
    }

    @Override
    public void setProbability(User user) {
        int hp = user.getHp();
        switch (hp){
            case 10 :
                user.setProbability(2);
                break;
            case 9 :
                user.setProbability(5);
                break;
            case 8 :
                user.setProbability(10);
                break;
            case 7 :
                user.setProbability(15);
                break;
            case 6 :
                user.setProbability(20);
                break;
            case 5 :
                user.setProbability(25);
                break;
            case 4 :
                user.setProbability(30);
                break;
            case 3 :
                user.setProbability(35);
                break;
            case 2 :
                user.setProbability(40);
                break;
            case 1 :
                user.setProbability(45);
                break;
            case 0 :
                user.setProbability(75);
                break;
            default:
                user.setProbability(1);
                break;
        }
    }

    @Override
    public UserDto editWater(Long userId, int dWater) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setWater(user.getWater()+dWater);
        if(dWater<0){
            user.setHp(user.getHp()-dWater); //물마시면 체력은 증가함
        }
        setProbability(user);
        userRepository.save(user);
        UserDto userDto = UserMapper.mapToUserDto(user);
        return userDto;
    }

    @Override
    public UserDto editFood(Long userId, int dFood) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setFood(user.getFood()+dFood);
        if(dFood<0){
            Random random = new Random();
            int randomNumber = random.nextInt(4) - 1; // 0부터 3까지의 랜덤 숫자 생성 후 -1을 더함
            if(randomNumber==0)randomNumber = -1;

            user.setHp(user.getHp()-(dFood*randomNumber)); //음식을 먹으면 체력 -1~2 랜덤증가 (0제외)
        }
        setProbability(user);
        userRepository.save(user);
        UserDto userDto = UserMapper.mapToUserDto(user);
        if(!userDto.isAlive()){
            userDto.setCauseOfDeath("상한 음식을 먹고 사망...");
        }
        return userDto;
    }
}
