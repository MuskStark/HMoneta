package fan.summer.hmoneta.util;

import cn.hutool.core.date.DateUtil;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

public class FileUtil {

    /**
     * Generates a file name with extension based on the original file name and timestamp.
     *
     * @param  multipartFile  the multipart file to generate the file name for
     * @return                the generated file name with extension
     */
    public static String generateOnlyFileNameWithExt(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        String filename = originalFilename.substring(0, originalFilename.lastIndexOf("."));
        String stamp = DateUtil.format(new Date(), "yyyy-MM-dd:HH-mm-ss");
        return filename+"_" + stamp + "." + ext;
    }

    /**
     * Generates a new filename with the given extension, incorporating a timestamp.
     *
     * @param  originalFilename   the original filename
     * @param  ext                the extension to add to the filename
     * @return                    the new filename with the timestamp and extension
     */
    public static String generateOnlyFileNameWithExt(String originalFilename, String ext) {
        String stamp = DateUtil.format(new Date(), "yyyy-MM-dd:HH-mm-ss");
        return originalFilename+"_" + stamp + "." + ext;
    }
}
