package horizon.taglib.controller;

import horizon.taglib.enums.ResultMessage;
import horizon.taglib.service.TaskService;
import horizon.taglib.service.UserService;
import horizon.taglib.vo.ResultVO;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class FileController {

    private final ResourceLoader resourceLoader;

    @Autowired
    public FileController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Autowired
    public UserService userService;

    /**
     * 定义分隔符
     */
    private static String fileSeparator = System.getProperty("file.separator");
    /**
     * 存取路径
     */
    private static String UPLOADED_FOLDER = "images";

    /**
     * 上传图片
     *
     * @param file
     * @param
     * @return
     */
    @PostMapping("/upload/task")
    public String taskUpload(@RequestParam("file") MultipartFile file, @RequestParam("id") Long taskId) {
        try {
            File dir = new File(UPLOADED_FOLDER + fileSeparator + taskId);

            if (!dir.exists() || !dir.isDirectory()) {
                dir.mkdirs();
            }

            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + fileSeparator + taskId + fileSeparator + file.getOriginalFilename());
            Files.write(path, bytes);

            return "success";

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "fail";
    }

    @PostMapping("/upload/avatar")
    public String avatarUpload(@RequestParam("file") MultipartFile file, @RequestParam("id") Long userId) {
        try {
            File dir = new File(UPLOADED_FOLDER);

            if (!dir.exists() || !dir.isDirectory()) {
                dir.mkdir();
            }

            dir = new File(UPLOADED_FOLDER + fileSeparator + "avatar");

            if (!dir.exists() || !dir.isDirectory()) {
                dir.mkdir();
            }

            dir = new File(UPLOADED_FOLDER + fileSeparator + "avatar"
                    + fileSeparator + userId);

            if (!dir.exists() || !dir.isDirectory()) {
                dir.mkdir();
            }

            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + fileSeparator + "avatar" + fileSeparator + userId + fileSeparator + file.getOriginalFilename());
            Files.write(path, bytes);

            ResultMessage re = userService.updateAvatar(userId, file.getOriginalFilename());

            if (re == ResultMessage.SUCCESS) {
                return "success";
            } else {
                return "fail";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "fail";
    }

    @RequestMapping(value = "/show/{taskId}/{filename:.+}")
    public byte[] showImage(@PathVariable Long taskId, @PathVariable String filename) throws IOException {
        final String imagePath = "images/";

        FileInputStream input = new FileInputStream(imagePath + taskId + fileSeparator + filename);
        return IOUtils.toByteArray(input);
    }

    @RequestMapping(value = "/show/{filename:.+}")
    public byte[] showImage(@PathVariable String filename) throws IOException {
        final String imagePath = "images/";

        FileInputStream input = new FileInputStream(imagePath + filename);
        return IOUtils.toByteArray(input);
    }

    @RequestMapping(value = "/show/avatar/{userId}/{filename:.+}")
    public byte[] showAvatar(@PathVariable Long userId, @PathVariable String filename) throws IOException {
        final String imagePath = "images/avatar/" + userId + "/";

        FileInputStream input = new FileInputStream(imagePath + filename);
        return IOUtils.toByteArray(input);
    }

    @GetMapping("/download/tag-data/{taskPublisherId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long taskPublisherId) {
        String dirName = "./taglib/database/tag-data/";
        File dir = new File(dirName);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }
        String filename = taskPublisherId + ".json";


        FileSystemResource file = new FileSystemResource(dirName + filename);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getFilename()));
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        try {
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentLength(file.contentLength())
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(new InputStreamResource(file.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
