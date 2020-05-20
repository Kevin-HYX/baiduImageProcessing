import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.baiduUtil.Base64Util;
import com.baidu.baiduUtil.FileUtil;
import com.baidu.baiduUtil.HttpUtil;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author Kevin HY
 * @version 1.0
 * @date 2020/5/15
 */
public class OCRProcess {
    public OCRProcess() throws IOException {
    }

    static {
        try {
            writer = new FileWriter(System.getProperty("user.dir") + "\\" + "OCRResult.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JFrame jFrame = new JFrame();
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setMultiSelectionEnabled(true);
        jFileChooser.setFileFilter(new FileNameExtensionFilter("图像文件(JPG)", "JPG", "JPEG"));
        jFrame.setBounds(400, 200, 600, 200);
        JButton jButton = new JButton("(自动换行)选择文件");
        JButton jButton1 = new JButton("选择文件");
        jFrame.getContentPane().setLayout(new GridLayout(1, 2));
        jFrame.getContentPane().add(jButton);
        jFrame.getContentPane().add(jButton1);
        jButton.addActionListener(e -> {
            int i = jFileChooser.showOpenDialog(jFrame);
            if (i == JFileChooser.APPROVE_OPTION) {
                File[] files = jFileChooser.getSelectedFiles();
                getResult(files, true);
            }
        });
        jButton1.addActionListener(e -> {
            int i = jFileChooser.showOpenDialog(jFrame);
            if (i == JFileChooser.APPROVE_OPTION) {
                File[] files = jFileChooser.getSelectedFiles();
                getResult(files, false);
            }
        });


        jFrame.setVisible(true);

    }

    private static void getResult(File[] files, boolean AutoLine) {
        int programSelection = 6;
        String[] URIname = new String[]{"https://aip.baidubce.com/rest/2.0/image-process/v1/dehaze", "https://aip.baidubce.com/rest/2.0/image-process/v1/contrast_enhance", "https://aip.baidubce.com/rest/2.0/image-process/v1/image_quality_enhance", "https://aip.baidubce.com/rest/2.0/image-process/v1/colourize", "https://aip.baidubce.com/rest/2.0/image-process/v1/stretch_restore", "https://aip.baidubce.com/rest/2.0/image-process/v1/selfie_anime", "https://aip.baidubce.com/rest/2.0/ocr/v1/accurate_basic", "https://aip.baidubce.com/rest/2.0/image-classify/v2/advanced_general", "https://aip.baidubce.com/rest/2.0/image-classify/v1/animal", "https://aip.baidubce.com/rest/2.0/image-classify/v1/plant", "https://aip.baidubce.com/rest/2.0/image-classify/v2/logo", "https://aip.baidubce.com/rest/2.0/image-classify/v1/classify/ingredient", "https://aip.baidubce.com/rest/2.0/image-classify/v2/dish", "https://aip.baidubce.com/rest/2.0/image-classify/v1/redwine", "https://aip.baidubce.com/rest/2.0/image-classify/v1/currency", "https://aip.baidubce.com/rest/2.0/image-classify/v1/landmark"};
        String url = URIname[programSelection];
        try {
            // 本地文件路径
            for (File file : files) {
                System.out.println("处理"+file.getName());
                byte[] imgData = FileUtil.readFileByBytes(file.getPath());
                String imgStr = Base64Util.encode(imgData);
                String imgParam = URLEncoder.encode(imgStr, StandardCharsets.UTF_8);
                String param = "image=" + imgParam;
                //得到Access Token
                String accessToken = "";
                accessToken = getToken.gettoken("2fzUVqGhtYAUiUAU9YIk8oGl", "D9mfBZtOha3jUwXUAVufrsGo8WiM2KUN");
                String result = HttpUtil.post(url, accessToken, param);
                toImage(result,file.getName(),AutoLine);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static FileWriter writer;


    private static void toImage(String result, String fileName, boolean AutoLine) throws IOException {
        JSONObject jb = JSONObject.parseObject(result);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        if (jb.getIntValue(processingTheMainProgram.WORDS_RESULT_NUM) != 0) {
            JSONArray array = jb.getJSONArray("words_result");

            bufferedWriter.write(fileName + ":");
            bufferedWriter.newLine();
            try {
                for (Object o : array) {

                    bufferedWriter.write(((JSONObject) o).getString("words"));

                    if (AutoLine) {
                        bufferedWriter.newLine();
                    }
                }

            } catch(IOException e){
                bufferedWriter.write(fileName + "识别失败");
                }
        } else {
            bufferedWriter.write("无任何结果");
        }
        bufferedWriter.newLine();
        bufferedWriter.newLine();
        bufferedWriter.newLine();
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }


}
