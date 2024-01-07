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

    private Dotenv dotenv = Dotenv.load();

    private Cloudinary cloudinary = new Cloudinary(dotenv.get("CLOUDINARY_URL"));

    public String uploadPhoto(String imgUrl) {
        String photoUrl;
        cloudinary.config.secure = true;
        Map params1 = ObjectUtils.asMap(
                "use_filename", true,
                "unique_filename", false,
                "overwrite", true
        );
        try {
            Map map = cloudinary.uploader().upload(new File(imgUrl), params1);
            photoUrl = map.get("secure_url").toString();
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
                "use_filename", true,
                "unique_filename", false,
                "overwrite", true
        );
        try {
            Map map = cloudinary.uploader().upload(new File(videoUrl),
                    params1);
            videoUrlFromCloud = map.get("secure_url").toString();
        } catch (Exception e) {
            throw new VideoUploadFailedException();
        }
        return videoUrlFromCloud;
    }

    public String uploadImgFromAI(String imgData) {
        String photoUrl;
        cloudinary.config.secure = true;
        Map params1 = ObjectUtils.asMap(
                "use_filename", true,
                "unique_filename", false,
                "overwrite", true
        );
        String[] imgDataArray = imgData.split("=", 2);
        String[] imgUrlArray = imgDataArray[1].split(",");
        try {
            Map map = cloudinary.uploader().upload(imgUrlArray[0], params1);
            photoUrl = map.get("secure_url").toString();
        } catch (Exception e) {
            throw new PhotoUploadFailedException();
        }
        return photoUrl;
    }
}
