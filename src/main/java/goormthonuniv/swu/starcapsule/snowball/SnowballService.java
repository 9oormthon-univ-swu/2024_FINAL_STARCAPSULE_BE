package goormthonuniv.swu.starcapsule.snowball;

import goormthonuniv.swu.starcapsule.user.User;
import goormthonuniv.swu.starcapsule.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class SnowballService {
    private final UserService userService;
    private final SnowballRepository snowballRepository;
    @Transactional
    public Snowball makeSnowball(String email){
        User user = userService.findByEmail(email);

        String sharedLink = makeShareLink();

        Snowball snowball = new Snowball(user.getNickname(), sharedLink, user);
        snowballRepository.save(snowball);

        user.setSnowball(snowball);

        return snowball;
    }

    public String makeShareLink(){
        return "http://localhost:3000/api/capsule/" + UUID.randomUUID().toString();
    }
}
