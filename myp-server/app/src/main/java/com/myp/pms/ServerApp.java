package com.myp.pms;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import com.myp.pms.table.DataTable;
import com.myp.pms.util.Request;
import com.myp.pms.util.Response;

public class ServerApp {
  int port;
  HashMap<String,DataTable> tableMap = new HashMap<>();

  public ServerApp(int port) {
    this.port = port;
  }

  public static void main(String[] args) {
    ServerApp app = new ServerApp(8881);
    app.service();
  }

  public void service() {
    try (ServerSocket serverSocket = new ServerSocket(this.port)) {

      System.out.println("< 서버 실행 >");
      processRequest(serverSocket.accept());

    } catch (Exception e) {
      System.out.println("서버 실행 중 오류가 발생하였습니다.");
      e.printStackTrace();
    }

  }

  public void processRequest(Socket socket) {
    try(DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        DataInputStream in = new DataInputStream(socket.getInputStream())) {

      while (true) {
        Request request = receiveRequest(in);
        log(request);

        if (request.getCommand().equals("quit")) {
          sendResponse(out, "응답완료");
          break;
        }

        DataTable dataTable = findDataTable(request.getCommand());

        if (dataTable != null) {
          Response response = new Response();
          try {
            dataTable.service(request, response);
            sendResponse(out, "응답완료",
                response.getDataList().toArray(new String[response.getDataList().size()]));

          } catch (Exception e) {
            sendResponse(out, "Error",
                e.getMessage() != null ? e.getMessage() : e.getClass().getName());
          }
        } else {
          sendResponse(out, "Error", "해당 요청을 처리할 수 없습니다.");
        }
      }
    } catch (Exception e) {
      System.out.println("클라이언트의 요청을 처리하는 중에 오류가 발생했습니다!");
      e.printStackTrace();
    }
  }

  private DataTable findDataTable(String command) {
    Set<String> ketSet = tableMap.keySet();
    for (String key : ketSet) {
      if (command.equals(key)) {
        return tableMap.get(key);
      }
    }
    return null;
  }

  private Request receiveRequest(DataInputStream in) throws Exception {
    Request request = new Request();

    request.setCommand(in.readUTF());

    int length = in.readInt();

    ArrayList<String> data = null;
    if (length > 0) {
      data = new ArrayList<>();
      for (int i = 0; i < length; i++) {
        data.add(in.readUTF());
      }
      request.setData(data);
    }
    return request;
  }

  private void sendResponse(DataOutputStream out, String status, String... data) throws Exception {
    out.writeUTF(status);
    out.writeInt(data.length);
    for (int i =0; i < data.length; i++) {
      out.writeUTF(data[i]);
    }
    out.flush();
  }

  private void log(Request request) {
    System.out.println("------------------------------");
    System.out.printf("명령: %s\n", request);

    List<String> data = request.getData();
    System.out.printf("데이터 개수: %d\n", data == null ? 0 : data.size());
    if (data != null) {
      System.out.println("데이터:");
      for (String str : data) {
        System.out.println(str);
      }
    }
  }

}
