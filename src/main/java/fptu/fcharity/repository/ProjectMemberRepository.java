package fptu.fcharity.repository;

import fptu.fcharity.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, UUID> {

    // Tìm danh sách các dự án mà user tham gia
    List<ProjectMember> findByUserId(UUID userId);

    // Xóa tất cả các bản ghi trong project_members liên quan đến user này
    @Transactional
    void deleteByUserId(UUID userId);
}
