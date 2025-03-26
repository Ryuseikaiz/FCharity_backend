package fptu.fcharity.service;

import fptu.fcharity.entity.ObjectAttachment;
import fptu.fcharity.repository.ObjectAttachmentRepository;
import fptu.fcharity.repository.manage.post.PostRepository;
import fptu.fcharity.repository.manage.project.ProjectRepository;
import fptu.fcharity.repository.manage.request.RequestRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Transactional
@Service
public class ObjectAttachmentService {
    private final ObjectAttachmentRepository objectAttachmentRepository;
private final RequestRepository requestRepository;
private final ProjectRepository projectRepository;
    private final PostRepository postRepository;

    public ObjectAttachmentService(
            ObjectAttachmentRepository objectAttachmentRepository,
            RequestRepository requestRepository,
            ProjectRepository projectRepository,
            PostRepository postRepository) {
        this.objectAttachmentRepository = objectAttachmentRepository;
        this.requestRepository = requestRepository;
        this.projectRepository = projectRepository;
        this.postRepository = postRepository;
    }

    public void takeObject(ObjectAttachment objectAttachment,UUID objectId, String type) {
        switch (type){
            case "REQUEST":
                objectAttachment.setRequest(requestRepository.findById(objectId).orElse(null));
                break;
            case "PROJECT":
//                objectAttachment.setProject(projectRepository.findById(objectId).orElse(null));
                break;
            case "POST":
                objectAttachment.setPost(postRepository.findById(objectId).orElse(null));
                break;
            case "ORGANIZATION":
//                objectAttachment.setOrganization(organizationRepository.findById(objectId).orElse(null));
                break;
            case "PHASE":
//                objectAttachment.setPhase(requestRepository.findById(objectId).orElse(null));
                break;
            default:
                throw new IllegalArgumentException("Invalid object type: " + type);
        }
    }
    public void saveAttachments(UUID objectId, List<String> urls, String objectType) {
        for (String url : urls) {
            ObjectAttachment attachment = new ObjectAttachment();
            attachment.setUrl(url);
            takeObject(attachment,objectId,objectType);
            objectAttachmentRepository.save(attachment);
        }
    }
    public void clearAttachments(UUID objectId, String objectType) {
        List<ObjectAttachment> existingAttachments = getAttachmentsByObjectType(objectId, objectType);
        objectAttachmentRepository.deleteAll(existingAttachments);
    }

    public List<String> getAttachmentsOfObject(UUID objectId, String objectType) {
        return getAttachmentsByObjectType(objectId, objectType).stream()
                .map(ObjectAttachment::getUrl)
                .toList();
    }

    private List<ObjectAttachment> getAttachmentsByObjectType(UUID objectId, String objectType) {
        switch (objectType) {
            case "REQUEST":
                return objectAttachmentRepository.findByRequestId(objectId);
            case "PROJECT":
//                return objectAttachmentRepository.findByProjectId(objectId);
            case "ORGANIZATION":
//                return objectAttachmentRepository.findObjectAttachmentByOrganizationOrganizationId(objectId);
            case "PHASE":
                return objectAttachmentRepository.findByPhaseId(objectId);
            case "POST":
                return objectAttachmentRepository.findByPostId(objectId);
            default:
                throw new IllegalArgumentException("Invalid object type: " + objectType);
        }
    }
}