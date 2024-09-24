package goormthonuniv.swu.starcapsule.refreshToken;

import goormthonuniv.swu.starcapsule.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUser(User user);
}
