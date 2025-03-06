package fptu.fcharity.service;

import fptu.fcharity.dto.admindashboard.ProjectDTO;
import fptu.fcharity.entity.Project;
//import fptu.fcharity.entity.Project.ProjectStatus;
import fptu.fcharity.repository.ManageProjectRepository;
import fptu.fcharity.utils.constants.RequestStatus;
import fptu.fcharity.utils.exception.ApiRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManageProjectService {
    private final ManageProjectRepository projectRepository;

    // Lấy danh sách dự án
    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Lấy dự án theo ID
    public ProjectDTO getProjectById(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApiRequestException("Project not found with ID: " + projectId));
        return convertToDTO(project);
    }

    // Xóa dự án
    @Transactional
    public void deleteProject(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApiRequestException("Project not found with ID: " + projectId));
        projectRepository.delete(project);
    }

    // Duyệt dự án (PENDING → ACTIVE)
//    @Transactional
//    public void approveProject(UUID projectId) {
//        Project project = projectRepository.findById(projectId)
//                .orElseThrow(() -> new ApiRequestException("Project not found with ID: " + projectId));
//
//        if (project.getProjectStatus() != Project.ProjectStatus.PENDING) {
//            throw new ApiRequestException("Project is not in PENDING status.");
//        }
//
//        project.setProjectStatus(Project.ProjectStatus.ACTIVE);
//        projectRepository.save(project);
//    }
    @Transactional
    public void approveProject(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApiRequestException("Project not found with ID: " + projectId));

        if (!RequestStatus.PENDING.equals(project.getProjectStatus())) {  // ✅ So sánh chuỗi thay vì Enum
            throw new ApiRequestException("Project is not in PENDING status");
        }

        project.setProjectStatus(RequestStatus.APPROVED); // ✅ Dùng constants
        projectRepository.save(project);
    }

    // Chuyển đổi từ entity sang DTO
//    private ProjectDTO convertToDTO(Project project) {
//        return new ProjectDTO(
//                project.getId(),
//                project.getProjectName(),
//                project.getOrganization() != null ? project.getOrganization().getId() : null,
//                project.getLeader() != null ? project.getLeader().getId() : null,
//                project.getEmail(),
//                project.getPhoneNumber(),
//                project.getProjectDescription(),
//                project.getProjectStatus(),
//                project.getReportFile(),
//                project.getPlannedStartTime(),
//                project.getPlannedEndTime(),
//                project.getActualStartTime(),
//                project.getActualEndTime(),
//                project.getShutdownReason(),
//                project.getCategory() != null ? project.getCategory().getId() : null,
//                project.getWalletAddress() != null ? project.getWalletAddress().getId() : null
//        );
//    }
    private ProjectDTO convertToDTO(Project project) {
        return new ProjectDTO(
                project.getId(),
                project.getProjectName(),
                project.getOrganization() != null ? project.getOrganization().getId() : null,
                project.getLeader() != null ? project.getLeader().getId() : null,
                project.getEmail(),
                project.getPhoneNumber(),
                project.getProjectDescription(),
                project.getProjectStatus(),
                project.getReportFile(),
                project.getPlannedStartTime(),
                project.getPlannedEndTime(),
                project.getActualStartTime(),
                project.getActualEndTime(),
                project.getShutdownReason(),
                project.getCategory() != null ? project.getCategory().getId() : null,
                project.getWalletAddress() != null ? project.getWalletAddress().getId() : null
        );
    }
}
