package horizon.taglib.controller;

import horizon.taglib.enums.ResultMessage;
import horizon.taglib.model.TaskPublisher;
import horizon.taglib.service.TaskService;
import horizon.taglib.service.UserService;
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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@RestController
public class FileController {

    private final ResourceLoader resourceLoader;
    private final UserService userService;
    private TaskService taskService;

    @Autowired
    public FileController(ResourceLoader resourceLoader, UserService userService, TaskService taskService) {
        this.resourceLoader = resourceLoader;
        this.userService = userService;
        this.taskService = taskService;
    }

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
        if (taskId == 0) {
            return "success";
        }
        try {
            File dir = new File(UPLOADED_FOLDER + fileSeparator + taskId);

            if (!dir.exists() || !dir.isDirectory()) {
                dir.mkdirs();
            }

            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + fileSeparator + taskId + fileSeparator + file.getOriginalFilename());
            Files.write(path, bytes);

            // 如果上传的文件是zip：
	        File zip = new File(path.getParent() + fileSeparator + path.getFileName());
	        if (zip.getName().contains(".zip")) {
		        List<String> imageInZipList = unZip(zip, path.getParent().toString());
                taskService.updateImageList(taskId, imageInZipList, zip.getName());
		        if (!zip.delete()) {
			        System.out.println("Delete zip " + zip.getParent() + fileSeparator + zip.getName() + " failed!");
		        }
	        }

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

    /**
     * 为TaskPublisher上传zip定制的解压：提取出所有图片到任务目录下
     * 注意：本方法只会提取zip中的图片到outputDirectory下———不解压zip中的子文件夹结构，以及非图片文件
     */
    private static List<String> unZip(File zip, String outputDirectory) throws IOException {
        List<String> imageList = new ArrayList<>();
        File folder = new File(outputDirectory);
        Files.createDirectories(folder.toPath());

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zip))) {
            ZipEntry nextEntry = zipInputStream.getNextEntry();

            while (nextEntry != null) {
                File newFile = new File(outputDirectory + fileSeparator + nextEntry.getName());
                if (newFile.getName().contains(".png") || newFile.getName().contains(".jpg")) {    // 判断待解压文件是不是文件夹
                    // 再次new File以去除中间文件夹结构 ↓
                    File image = new File(outputDirectory + fileSeparator + newFile.getName());
                    writeFile(zipInputStream, image);
                    if (!(image.getName().length() >= 2 && image.getName().substring(0, 2).equals("._"))) {
                        imageList.add(image.getName());
                    }
                }
                nextEntry = zipInputStream.getNextEntry();
            }

            zipInputStream.closeEntry();
            return imageList;
        }
    }

    private static void writeFile(ZipInputStream inputStream, File file) throws IOException {
        byte[] buffer = new byte[1024];
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, length);
            }
        }
    }
}
