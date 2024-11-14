package the_monitor.application.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import the_monitor.common.ApiException;
import the_monitor.common.ErrorStatus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public S3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public String uploadFile(MultipartFile file) {

        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File uploadFile = convertMultiPartToFile(file);

            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, uploadFile));

            String fileUrl = amazonS3.getUrl(bucketName, fileName).toString();
            uploadFile.delete();  // 로컬 임시 파일 삭제
            return fileUrl;
        } catch (Exception e) {
            throw new ApiException(ErrorStatus._FILE_UPLOAD_FAILED);
        }

    }

    public String updateFile(String existingFileKey, MultipartFile newFile) {

        deleteFile(existingFileKey);

        return uploadFile(newFile);

    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        }
        return convertedFile;
    }

    private void deleteFile(String fileKey) {
        if (amazonS3.doesObjectExist(bucketName, fileKey)) {
            amazonS3.deleteObject(bucketName, fileKey);
        } else {
            throw new ApiException(ErrorStatus._FILE_DELETE_FAILED);
        }
    }

}
