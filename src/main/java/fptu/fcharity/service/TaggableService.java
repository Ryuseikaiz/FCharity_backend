package fptu.fcharity.service;

import fptu.fcharity.entity.Tag;
import fptu.fcharity.entity.Taggable;
import fptu.fcharity.repository.*;
import fptu.fcharity.repository.manage.post.PostRepository;
import fptu.fcharity.repository.manage.project.ProjectRepository;
import fptu.fcharity.repository.manage.request.RequestRepository;
import fptu.fcharity.utils.constants.TaggableType;
import fptu.fcharity.utils.exception.ApiRequestException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
public class TaggableService {
    private final RequestRepository requestRepository;
    private final TaggableRepository taggableRepository;
    private final TagRepository tagRepository;
    private final PostRepository postRepository;
    private final ProjectRepository projectRepository;

    public TaggableService(RequestRepository requestRepository,
                           TaggableRepository taggableRepository,
                           TagRepository tagRepository,
                           PostRepository postRepository, ProjectRepository projectRepository) {
        this.requestRepository = requestRepository;
        this.taggableRepository = taggableRepository;
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
        this.projectRepository = projectRepository;
    }

    public List<Taggable> getTagsOfObject(UUID id, String type) {
        if (TaggableType.REQUEST.equals(type)||TaggableType.POST.equals(type)||TaggableType.PROJECT.equals(type)) {
            return taggableRepository.findAllWithInclude().stream()
                    .filter(taggable -> taggable.getTaggableId().equals(id) && taggable.getTaggableType().equals(type))
                    .toList();
        } else {
            return null;
        }
    }

    public void addTaggables(UUID id, List<UUID> tagIds, String type) {
        if (requestRepository.existsById(id)||postRepository.existsById(id)||projectRepository.existsById(id)) {
            for (UUID tagId : tagIds) {
                if (tagRepository.existsById(tagId)) {
                    Tag tag = tagRepository.findById(tagId)
                            .orElseThrow(() -> new ApiRequestException("Tag not found"));
                    if (TaggableType.REQUEST.equals(type)||TaggableType.POST.equals(type)||TaggableType.PROJECT.equals(type)) {
                        Taggable taggable = new Taggable(tag, id, type);
                        taggableRepository.save(taggable);
                    }
                }
            }
        }
    }

    public void updateTaggables(UUID id, List<UUID> tagIds, String type) {
        if (TaggableType.REQUEST.equals(type)||TaggableType.POST.equals(type)||TaggableType.PROJECT.equals(type)) {
            List<Taggable> oldTags = taggableRepository.findAllWithInclude().stream()
                    .filter(taggable -> taggable.getTaggableId().equals(id) && taggable.getTaggableType().equals(type))
                    .toList();
            for (Taggable taggable : oldTags) {
                if (!tagIds.contains(taggable.getTag().getId())) {
                    taggableRepository.deleteById(taggable.getId());
                }
                tagIds.remove(taggable.getTag().getId());
            }
            addTaggables(id, tagIds, type);
        }
    }
}
