package fptu.fcharity.service;

import fptu.fcharity.entity.*;
import fptu.fcharity.repository.ObjectAttachmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ObjectAttachmentService {
    private final ObjectAttachmentRepository objectAttachmentRepository;

    public ObjectAttachmentService(ObjectAttachmentRepository objectAttachmentRepository) {
        this.objectAttachmentRepository = objectAttachmentRepository;
    }

    public void saveAttachments(UUID objectId, List<String> urls, String objectType) {
        for (String url : urls) {
            ObjectAttachment attachment = new ObjectAttachment(UUID.randomUUID(), url, objectId, objectType);
            objectAttachmentRepository.save(attachment);
        }
    }

    public void updateAttachments(UUID objectId, List<String> urls, String objectType) {
        // Delete existing attachments
        List<ObjectAttachment> existingAttachments = getAttachmentsByObjectType(objectId, objectType);
        objectAttachmentRepository.deleteAll(existingAttachments);

        // Save new attachments
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