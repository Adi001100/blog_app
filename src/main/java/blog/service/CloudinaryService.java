package blog.service;

import blog.exception.PhotoUploadFailedException;
import blog.exception.VideoUploadFailedException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Dotenv dotenv = Dotenv.load();

    private final Cloudinary cloudinary = new Cloudinary(dotenv.get("CLOUDINARY_URL"));

    private static final String USE_FILENAME = "use_filename";

    private static final String UNIQUE_FILENAME = "unique_filename";

    private static final String OVERWRITE = "overwrite";

    private static final String SECURE_URL = "secure_url";

    public String uploadPhoto(String imgUrl) {
        String photoUrl;
        cloudinary.config.secure = true;
        Map params1 = ObjectUtils.asMap(
                USE_FILENAME, true,
                UNIQUE_FILENAME, false,
                OVERWRITE, true
        );
        try {
            Map map = cloudinary.uploader().upload(new File(imgUrl), params1);
            photoUrl = map.get(SECURE_URL).toString();
        } catch (Exception e) {
            throw new PhotoUploadFailedException();
        }
        return photoUrl;
    }

    public String uploadVideo(String videoUrl) {
        String videoUrlFromCloud;
        cloudinary.config.secure = true;
        Map params1 = ObjectUtils.asMap(
                "resource_type", "video",
                USE_FILENAME, true,
                UNIQUE_FILENAME, false,
                OVERWRITE, true
        );
        try {
            Map map = cloudinary.uploader().upload(new File(videoUrl),
                    params1);
            videoUrlFromCloud = map.get(SECURE_URL).toString();
        } catch (Exception e) {
            throw new VideoUploadFailedException();
        }
        return videoUrlFromCloud;
    }

    public String uploadImgFromAI(String imgData) {
        String photoUrl;
        cloudinary.config.secure = true;
        Map params1 = ObjectUtils.asMap(
                USE_FILENAME, true,
                UNIQUE_FILENAME, false,
                OVERWRITE, true
        );
        String[] imgDataArray = imgData.split("=", 2);
        String[] imgUrlArray = imgDataArray[1].split(",");
        try {
            Map map = cloudinary.uploader().upload(imgUrlArray[0], params1);
            photoUrl = map.get(SECURE_URL).toString();
        } catch (Exception e) {
            throw new PhotoUploadFailedException();
        }
        return photoUrl;
    }
}
