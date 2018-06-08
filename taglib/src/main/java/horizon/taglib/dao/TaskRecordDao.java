package horizon.taglib.dao;

import horizon.taglib.model.TaskRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TaskRecordDao extends JpaRepository<TaskRecord, Long>, BaseRepository<TaskRecord, Long> {

    List<TaskRecord> findByUserId(Long userId);

    List<TaskRecord> findByDateAndUserId(LocalDate date, Long userId);

    List<TaskRecord> findByTaskPublisherId(Long taskPublisherId);
}
