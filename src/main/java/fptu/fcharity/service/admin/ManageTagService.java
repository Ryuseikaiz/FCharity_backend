package fptu.fcharity.service.admin;

import fptu.fcharity.dto.admindashboard.TagDTO;
import fptu.fcharity.entity.Tag;
import fptu.fcharity.repository.TagRepository;
import fptu.fcharity.utils.exception.ApiRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManageTagService {
    private final TagRepository tagRepository;

    public List<TagDTO> getAllTags() {
        return tagRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TagDTO getTagById(UUID id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ApiRequestException("Tag not found with ID: " + id));
        return convertToDTO(tag);
    }

    @Transactional
    public TagDTO createTag(TagDTO tagDto) {
        if (tagRepository.findByTagName(tagDto.getTagName()).isPresent()) {
            throw new ApiRequestException("Tag name already exists: " + tagDto.getTagName());
        }
        Tag tag = new Tag();
        tag.setTagName(tagDto.getTagName());
        return convertToDTO(tagRepository.save(tag));
    }

    @Transactional
    public TagDTO updateTag(UUID id, TagDTO tagDto) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ApiRequestException("Tag not found with ID: " + id));
        tag.setTagName(tagDto.getTagName());
        return convertToDTO(tagRepository.save(tag));
    }

    @Transactional
    public void deleteTag(UUID id) {
        if (!tagRepository.existsById(id)) {
            throw new ApiRequestException("Tag not found with ID: " + id);
        }
        tagRepository.deleteById(id);
    }

    private TagDTO convertToDTO(Tag tag) {
        TagDTO dto = new TagDTO();
        dto.setId(tag.getId());
        dto.setTagName(tag.getTagName());
        return dto;
    }
}