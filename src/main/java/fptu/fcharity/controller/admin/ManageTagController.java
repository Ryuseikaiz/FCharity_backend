package fptu.fcharity.controller.admin;

import fptu.fcharity.dto.admindashboard.TagDTO;
import fptu.fcharity.service.admin.ManageTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/tags")
@RequiredArgsConstructor
public class ManageTagController {
    private final ManageTagService tagService;

    @GetMapping
    public ResponseEntity<List<TagDTO>> getAllTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagDTO> getTagById(@PathVariable UUID id) {
        return ResponseEntity.ok(tagService.getTagById(id));
    }

    @PostMapping
    public ResponseEntity<TagDTO> createTag(@RequestBody TagDTO tagDto) {
        return ResponseEntity.ok(tagService.createTag(tagDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagDTO> updateTag(@PathVariable UUID id, @RequestBody TagDTO tagDto) {
        return ResponseEntity.ok(tagService.updateTag(id, tagDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTag(@PathVariable UUID id) {
        tagService.deleteTag(id);
        return ResponseEntity.ok("Tag deleted successfully.");
    }
}

