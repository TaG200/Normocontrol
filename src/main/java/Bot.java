import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class Bot extends TelegramLongPollingBot {

        private String name = "name";
        private final String token = "token";

        @Override
        public void onUpdateReceived(Update update) {
            String message = update.getMessage().getText();

            if (message.equals("/start")){
                sendMsg(update.getMessage().getChatId().toString(),
                        "Стартовое сообщение Normocontrol bgpu");
            }
            System.out.println("message");
            System.out.println(update.getMessage().getDocument().getFileName());
            Document document = update.getMessage().getDocument();
            if (document != null) {
                final String fileId = document.getFileId();
                final String fileName = document.getFileName();

                System.out.println(fileId);
                System.out.println(fileName);

                //sendMsg(update.getMessage().getChatId().toString(), fileName);

                try {
                    uploadFile(fileName,fileId);
                } catch (IOException e) {
                    e.printStackTrace();
                }

              //  uploadFile(fileName,fileId);
            }

            sendMsg(update.getMessage().getChatId().toString(), message);


        }

        public synchronized void sendMsg(String chatId, String s) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.enableMarkdown(true);
            sendMessage.setChatId(chatId);
            sendMessage.setText(s);

            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        public void uploadFile(String fileName, String fileId) throws IOException{
            URL url = new URL("https://api.telegram.org/bot" + token + "/getFile?file_id=" + fileId);
            System.out.println(url.toString());

            BufferedReader in = new BufferedReader(new InputStreamReader( url.openStream()));
            String res = in.readLine();

            JSONObject jresult = new JSONObject(res);
            JSONObject path = jresult.getJSONObject("result");
            String filePath = path.getString("file_path");
            System.out.println(filePath);

            File localFile = new File("src/main/resources/syuda/" + fileName);
            InputStream is = new URL("https://api.telegram.org/file/bot" + token + "/" + filePath).openStream();

            FileUtils.copyInputStreamToFile(is, localFile);

            in.close();
            is.close();

            System.out.println("Uploaded!");
        }

    /*public void receivingFile(String file_name, String file_id) throws IOException {
        URL url = new URL("https://api.telegram.org/bot" + token + "/getFile?file_id=" + file_id);
        BufferedReader in = new BufferedReader(new InputStreamReader( url.openStream()));
        String res = in.readLine();
        JSONObject jresult = new JSONObject(res);
        JSONObject path = jresult.getJSONObject("result");
        String file_path = path.getString("file_path");
        URL downoload = new URL("https://api.telegram.org/file/bot" + token + "/" + file_path);
        FileOutputStream fos = new FileOutputStream(upPath + file_name);
        System.out.println("Start upload");
        ReadableByteChannel rbc = Channels.newChannel(downoload.openStream());
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
        //uploadFlag = 0;
        System.out.println("Uploaded!");

        
    }*/
    /*public void uploadFile(String file_name, String file_id) throws IOException{
        URL url = new URL("https://api.telegram.org/bot"+token+"/getFile?file_id="+file_id);
        BufferedReader in = new BufferedReader(new InputStreamReader( url.openStream()));
        String res = in.readLine();
        JSONObject jresult = new JSONObject(res);
        JSONObject path = jresult.getJSONObject("result");
        String file_path = path.getString("file_path");
        URL downoload = new URL("https://api.telegram.org/file/bot" + token + "/" + file_path);
        FileOutputStream fos = new FileOutputStream(upPath + file_name);
        System.out.println("Start upload");
        ReadableByteChannel rbc = Channels.newChannel(downoload.openStream());
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
        uploadFlag = 0;
        System.out.println("Uploaded!");
    }*/

        @Override
        public String getBotUsername() {
            return name;
        }

        @Override
        public String getBotToken() {
            return token;
        }



    }

