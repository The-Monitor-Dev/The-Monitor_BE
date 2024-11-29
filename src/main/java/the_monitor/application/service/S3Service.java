package the_monitor.application.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import the_monitor.common.ApiException;
import the_monitor.common.ErrorStatus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
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
    public String uploadFileWithKey(String prefix, File file) {
        try {
            String fileName = prefix + UUID.randomUUID() + ".xlsx";
            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file));
            return amazonS3.getUrl(bucketName, fileName).toString();
        } catch (Exception e) {
            throw new ApiException(ErrorStatus._FILE_UPLOAD_FAILED);
        }
    }

//    public String getLatestFileKey(String prefix) {
//        try {
//            ListObjectsV2Request request = new ListObjectsV2Request()
//                    .withBucketName(bucketName)
//                    .withPrefix(prefix)
//                    .withMaxKeys(1000); // 최대 1000개의 객체 조회 가능
//
//            ListObjectsV2Result result = amazonS3.listObjectsV2(request);
//            List<S3ObjectSummary> objects = result.getObjectSummaries();
//
//            // 가장 최근의 파일 찾기
//            return objects.stream()
//                    .max(Comparator.comparing(S3ObjectSummary::getLastModified))
//                    .map(S3ObjectSummary::getKey)
//                    .orElse(null); // 최신 파일이 없으면 null 반환
//
//        }
//        catch (Exception e) {
//            throw new ApiException(ErrorStatus._FILE_RETRIEVE_FAILED);
//        }
//    }

    public String getLatestFileKey(String prefix) {
        try {
            ListObjectsV2Request request = new ListObjectsV2Request()
                    .withBucketName(bucketName)
                    .withPrefix(prefix)
                    .withMaxKeys(1000); // 최대 1000개의 객체 조회 가능

            ListObjectsV2Result result = amazonS3.listObjectsV2(request);
            List<S3ObjectSummary> objects = result.getObjectSummaries();

            // 로그 추가: 조회된 파일 키 목록 출력
            System.out.println("S3에서 조회된 파일들: ");
            objects.forEach(obj -> System.out.println("파일 키: " + obj.getKey() + ", 수정 시간: " + obj.getLastModified()));

            // 가장 최근의 파일 찾기
            String latestFileKey = objects.stream()
                    .max(Comparator.comparing(S3ObjectSummary::getLastModified))
                    .map(S3ObjectSummary::getKey)
                    .orElse(null); // 최신 파일이 없으면 null 반환

            // 로그 추가: 최신 파일 키 출력
            System.out.println("가장 최근의 파일 키: " + latestFileKey);

            return latestFileKey;

        } catch (Exception e) {
            e.printStackTrace(); // 예외 내용 출력
            throw new ApiException(ErrorStatus._FILE_RETRIEVE_FAILED);
        }
    }

    public File downloadFile(String fileKey) {
        try {
            S3Object s3Object = amazonS3.getObject(bucketName, fileKey);
            S3ObjectInputStream inputStream = s3Object.getObjectContent();

            // S3 키에서 파일 이름 추출
            String originalFileName = fileKey.substring(fileKey.lastIndexOf("/") + 1);
            File tempFile = File.createTempFile(originalFileName, null);

            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }

            return tempFile;
        } catch (IOException e) {
            throw new ApiException(ErrorStatus._FILE_DOWNLOAD_FAILED);
        }
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
