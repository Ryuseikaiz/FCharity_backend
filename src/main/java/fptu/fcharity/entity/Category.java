package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category {
    @Id
    @Column(name = "category_id", columnDefinition = "UNIQUEIDENTIFIER", updatable = false, nullable = false)
    private UUID categoryId;

    @Column(nullable = false)
    private String categoryName;
}