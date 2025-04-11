package fptu.fcharity.repository.manage.organization;

import fptu.fcharity.entity.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {
    @Query("SELECT uf FROM UploadedFile uf JOIN FETCH uf.uploadedBy u JOIN FETCH uf.organization o JOIN FETCH o.walletAddress JOIN FETCH o.ceo ce   WHERE uf.uploadedFileId = :id")
    UploadedFile findUploadedFileByUploadedFileId(@Param("id") UUID uploadedFileId);

    @Query("SELECT uf FROM UploadedFile uf JOIN FETCH uf.uploadedBy u JOIN FETCH uf.organization o JOIN FETCH o.walletAddress JOIN FETCH o.ceo ce  WHERE o.organizationId = :id")
    List<UploadedFile> findUploadedFileByOrganizationOrganizationId(@Param("id") UUID organizationId);

    void deleteUploadedFileByUploadedFileId(UUID uploadedFileId);
}
