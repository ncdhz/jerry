package com.github.ncdhz.jerry.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class SocketHandler implements Handler{

     String readData(ByteBuffer dataBuffer,SelectionKey key){

        SocketChannel clientChannel= (SocketChannel) key.channel();
        StringBuilder clientData =new StringBuilder();
        while (true){
            try {
                int read = clientChannel.read(dataBuffer);
                if (read<=0){
                    if (read<0){
                        key.cancel();
                        clientChannel.close();
                    }
                    break;
                }
                dataBuffer.flip();
                /**
                 * 把数据放在 StringBuilder 里面
                 */
                clientData.append(decoder.decode(dataBuffer));
                dataBuffer.clear();
            } catch (IOException e) {
                key.cancel();
                e.printStackTrace();
            }
        }
        return clientData.toString();
    }
    /**
     * 用于处理数据  $$data##$$data##$$data##$$data## 提取出里面的data
     */
    public List<String> feedbackFormatting(String data){
        List<String> dataList = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\$\\$\\{([\\S\\s]*?)}##");
        Matcher  matcher= pattern.matcher(data);
        while (matcher.find()){
            String group = matcher.group();
            if (group.endsWith("##"))
                group = group.substring(0,group.length()-2);
            if (group.startsWith("$$"))
                group = group.substring(2,group.length());
            dataList.add(group);
        }
        return dataList;
    }

    public abstract String readable(SelectionKey key);
}
