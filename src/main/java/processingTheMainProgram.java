import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.baiduUtil.Base64Util;
import com.baidu.baiduUtil.FileUtil;
import com.baidu.baiduUtil.HttpUtil;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;

/**
 * 人像动漫化
 *
 * @author Kevin HY
 */

public class processingTheMainProgram {

    public static final String WORDS_RESULT_NUM = "words_result_num";

    public processingTheMainProgram() throws IOException {

    }


    public static void main(String[] args) throws IOException {
        System.out.print("选择合适的处理方式");
        String[] ProcessName = new String[]{"图像去雾", "图像对比度增强", "图像无损放大", "黑白图像上色", "拉伸图像恢复", "人像动漫化", "OCR识别", "通用图像内容识别", "指定图像内容识别"};
        for (int i = 0; i < ProcessName.length; i++) {
            System.out.print(i + 1);
            System.out.print(": " + ProcessName[i] + " ");
        }
        System.out.println();
        System.out.print("> ");
        Scanner sc = new Scanner(System.in);
        int choose = sc.nextInt();
        if (choose == 9) {
            System.out.println("请选择下面的一个");
            String[] ProcessName2 = new String[]{"动物识别", "植物识别", "logo识别", "果蔬识别", "菜品识别", "红酒识别", "货币识别", "地表识别"};
            for (int i = 0; i < ProcessName2.length; i++) {
                System.out.print(i + 1);
                System.out.print(": " + ProcessName2[i] + " ");
            }
            System.out.println();
            System.out.print("> ");
            int choose2 = sc.nextInt();
            choose = choose2 + 8;
            System.out.println("正在尝试执行程序" + ProcessName2[choose2 - 1]);
        } else {
            System.out.println("正在尝试执行程序" + ProcessName[choose - 1]);
        }
        selfieAnime(choose - 1);
        System.out.println("写入成功！程序将在3秒后退出");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }


    public static void selfieAnime(int programSelection) {
        String[] URIname = new String[]{"https://aip.baidubce.com/rest/2.0/image-process/v1/dehaze", "https://aip.baidubce.com/rest/2.0/image-process/v1/contrast_enhance", "https://aip.baidubce.com/rest/2.0/image-process/v1/image_quality_enhance", "https://aip.baidubce.com/rest/2.0/image-process/v1/colourize", "https://aip.baidubce.com/rest/2.0/image-process/v1/stretch_restore", "https://aip.baidubce.com/rest/2.0/image-process/v1/selfie_anime", "https://aip.baidubce.com/rest/2.0/ocr/v1/accurate_basic", "https://aip.baidubce.com/rest/2.0/image-classify/v2/advanced_general", "https://aip.baidubce.com/rest/2.0/image-classify/v1/animal", "https://aip.baidubce.com/rest/2.0/image-classify/v1/plant", "https://aip.baidubce.com/rest/2.0/image-classify/v2/logo", "https://aip.baidubce.com/rest/2.0/image-classify/v1/classify/ingredient", "https://aip.baidubce.com/rest/2.0/image-classify/v2/dish", "https://aip.baidubce.com/rest/2.0/image-classify/v1/redwine", "https://aip.baidubce.com/rest/2.0/image-classify/v1/currency", "https://aip.baidubce.com/rest/2.0/image-classify/v1/landmark"};
        // 请求url
        System.out.println(URIname[programSelection]);
        String url = URIname[programSelection];
        //noinspection AlibabaRemoveCommentedCode
        try {
            // 本地文件路径
            String filePath = System.getProperty("user.dir") + "\\" + "test.jpg";
            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, StandardCharsets.UTF_8);
            String param = "image=" + imgParam;
            //得到Access Token
            String accessToken = "";
            if (programSelection == 6) {
                accessToken = getToken.gettoken("2fzUVqGhtYAUiUAU9YIk8oGl", "D9mfBZtOha3jUwXUAVufrsGo8WiM2KUN");
            } else if (programSelection <= 5) {
                accessToken = getToken.gettoken("YVwL2SPNiI2Y0ZQdRynuLpIT", "YqLqC8lef3q9Iwca1dp0oLubuhKyolDa");
            } else if (programSelection >= 7) {
                accessToken = getToken.gettoken("FHGq6w39FB0u3S2lxOofqXjo", "3I9ZE95TcMSApghB8XzWnFZlI99E5D6u");
            }
            String result = HttpUtil.post(url, accessToken, param);
            toImage(result, programSelection);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static void toImage(String result, int programSelection) throws IOException {
        FileWriter fileWriter1 = new FileWriter(System.getProperty("user.dir") + "\\temp\\JSONResult.json");
        BufferedWriter bufferedWriter1 = new BufferedWriter(fileWriter1);
        bufferedWriter1.write(result);
        bufferedWriter1.close();
        if (programSelection >= 0 && programSelection <= 5) {
            zeroToFile(result);
        } else if (programSelection == 6) {
            six(result);
        } else if (programSelection >= 7 && programSelection <= 9) {
            sevenToNine(result, programSelection);
        } else if (programSelection == 10) {
            System.out.println("haa");
        }
    }

    private static void sevenToNine(String result, int programSelection) throws IOException {
        JSONObject jb = JSONObject.parseObject(result);
        FileWriter writer = new FileWriter(System.getProperty("user.dir") + "\\" + "ImageResult.txt");
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        JSONArray jsonArray = jb.getJSONArray("result");
        int result_num;
        if (programSelection == 7) {
            result_num = jb.getIntValue("result_num");
        } else {
            result_num = jsonArray.size();
        }
        if (result_num != 0) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
            if (programSelection == 7) {
                bufferedWriter.write("首选结果 : " + jsonObject.getString("keyword"));
            } else {
                bufferedWriter.write("首选结果 : " + jsonObject.getString("name"));

            }
            bufferedWriter.newLine();
            if (jsonObject.containsKey("description")) {
                bufferedWriter.write("首选结果描述: " + jsonObject.getString("description"));
                bufferedWriter.newLine();
            }
            if (programSelection == 7) {
                bufferedWriter.write("类别 : " + jsonObject.getString("root"));
                bufferedWriter.newLine();
            }
            bufferedWriter.write("置信度 : " + jsonObject.getDouble("score"));
            bufferedWriter.newLine();
            if (jsonObject.containsKey("baike_url")) {
                bufferedWriter.write("百度百科链接 : " + jsonObject.getString("baike_url"));
                bufferedWriter.newLine();
            }
            bufferedWriter.newLine();
        }
        if (result_num > 1) {
            for (int i = 1; i < result_num; i++) {
                JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
                if (programSelection == 7) {
                    bufferedWriter.write("备选结果" + i + " : " + jsonObject1.getString("keyword"));
                } else {
                    bufferedWriter.write("备选结果" + i + " : " + jsonObject1.getString("name"));
                }
                bufferedWriter.newLine();
                if (programSelection == 7) {
                    bufferedWriter.write("类别: " + jsonObject1.getString("root"));
                    bufferedWriter.newLine();
                }
                if (jsonObject1.containsKey("description")) {
                    bufferedWriter.write("备选结果描述: " + jsonObject1.getString("description"));
                    bufferedWriter.newLine();
                }
                if (jsonObject1.containsKey("baike_url")) {
                    bufferedWriter.write("百度百科链接 : " + jsonObject1.getString("baike_url"));
                    bufferedWriter.newLine();
                }
                bufferedWriter.write("置信度 : " + (jsonObject1.getDouble("score")).toString());
                bufferedWriter.newLine();
            }
        } else {
            bufferedWriter.write("没有备选结果");
        }
        bufferedWriter.close();
    }

    private static void six(String result) throws IOException {
        JSONObject jb = JSONObject.parseObject(result);
        FileWriter writer = new FileWriter(System.getProperty("user.dir") + "\\" + "OCRResult.txt");
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        if (jb.getIntValue(WORDS_RESULT_NUM) != 0) {
            JSONArray array = jb.getJSONArray("words_result");
            bufferedWriter.write("识别结果:");
            bufferedWriter.newLine();
            System.out.println("需要自动换行吗(yes or no)");
            String bool = new Scanner(System.in).next();

            for (Object o : array) {
                bufferedWriter.write(((JSONObject) o).getString("words"));
                if (bool.contains("yes") || bool.contains("Yes")) {
                    bufferedWriter.newLine();
                }
            }
            bufferedWriter.close();
        } else {
            bufferedWriter.write("无任何结果");
        }
    }

    private static void zeroToFile(String result) throws IOException {
        BufferedWriter temp_writer = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + "\\temp\\temp_main.txt"));
        temp_writer.write(result);
        temp_writer.close();
        JSONObject jb = JSONObject.parseObject(result);
        result = jb.getString("image");
        byte[] resultString = Base64.getDecoder().decode(result);
        File file = new File("resoult.jpg");
        FileOutputStream os = new FileOutputStream(file);
        os.write(resultString);
        os.close();
    }
}