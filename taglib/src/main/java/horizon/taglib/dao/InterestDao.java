package horizon.taglib.dao;

import horizon.taglib.model.Interest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterestDao extends JpaRepository<Interest, Long>, BaseRepository<Interest, Long> {

    Interest findInterestByUserIdAndTopic(Long userId, String topic);

    List<Interest> findByUserId(Long userId);

}
