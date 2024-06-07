package personal.MapleChenX.lsp.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MinioConfiguration {

    @Value("${spring.minio.endpoint}")
    String endpoint;
    @Value("${spring.minio.username}")
    String username;
    @Value("${spring.minio.password}")
    String password;
    @Value("${spring.minio.bucket}")
    String bucket;
    @Value("${spring.minio.bucket4file}")
    String bucket4file;

    @Bean
    public MinioClient minioClient(){
        System.out.println("Init minio client...");
        log.info("Init minio client...");
        MinioClient minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(username, password)
                .build();
        // todo 更优雅的bucket初始化
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            } else {
                System.out.println("Bucket '" + bucket + "' already exists.");
            }
        } catch (Exception e) {
            System.out.println("Error occurred in creating minio bucket: " + e);
        }

        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket4file).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket4file).build());
            } else {
                System.out.println("bucket4file '" + bucket4file + "' already exists.");
            }
        } catch (Exception e) {
            System.out.println("Error occurred in creating minio bucket: " + e);
        }

        return minioClient;
    }
}
