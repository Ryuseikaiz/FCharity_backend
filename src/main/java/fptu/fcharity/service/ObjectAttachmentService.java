package fptu.fcharity.service;

import fptu.fcharity.entity.ObjectAttachment;
import fptu.fcharity.entity.Request;
import fptu.fcharity.repository.ObjectAttachmentRepository;
import fptu.fcharity.repository.RequestRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Transactional
@Service
public class ObjectAttachmentService {
    private final ObjectAttachmentRepository objectAttachmentRepository;
    private final RequestRepository requestRepository;

    public ObjectAttachmentService(ObjectAttachmentRepository objectAttachmentRepository, RequestRepository requestRepository) {
        this.objectAttachmentRepository = objectAttachmentRepository;
        this.requestRepository = requestRepository;
    }

    @Transactional
    public void saveAttachments(UUID objectId, List<String> urls, String objectType) {
        for (String url : urls) {
            ObjectAttachment attachment = new ObjectAttachment();
            attachment.setId(UUID.randomUUID());
            attachment.setUrl(url);

            switch (objectType) {
                case "REQUEST":
                    Request request = requestRepository.findById(objectId)
                            .orElseThrow(() -> new IllegalArgumentException("Request not found"));

                    // Reattach request entity to the current Hibernate session
                    request = requestRepository.saveAndFlush(request);

                    attachment.setRequest(request);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid object type: " + objectType);
            }

            objectAttachmentRepository.save(attachment);
        }
    }


    @Transactional
    public void updateAttachments(UUID objectId, List<String> urls, String objectType) {
        List<ObjectAttachment> existingAttachments = getAttachmentsByObjectType(objectId, objectType);
        objectAttachmentRepository.deleteAll(existingAttachments);

        objectAttachmentRepository.flush(); // Ensures deletion is committed before new inserts

        saveAttachments(objectId, urls, objectType);
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
                return objectAttachmentRepository.findByProjectId(objectId);
            case "ORGANIZATION":
                return objectAttachmentRepository.findByOrganizationId(objectId);
            case "PHASE":
                return objectAttachmentRepository.findByPhaseId(objectId);
            default:
                throw new IllegalArgumentException("Invalid object type: " + objectType);
        }
    }
}