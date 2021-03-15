package com.myp.pms;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.LinkedList;
import com.myp.pms.handler.Command;
import com.myp.util.Prompt; class ClientApp {

  ArrayDeque<String> commandStack = new ArrayDeque<>();
  LinkedList<String> commandQueue = new LinkedList<>();

  String serverAddress;
  int port;

  public static void main(String[] args) {
    ClientApp app = new ClientApp("localhost", 8881);
    app.execute();
  }

  public ClientApp(String serverAddress, int port) {
    this.serverAddress = serverAddress;
    this.port = port;
  }

  private void execute() {

    HashMap<String,Command> commandMap = new HashMap<>();

    //    commandMap.put("/add", new BoardAddHandler());
    //    commandMap.put("/board/add", new BoardAddHandler());
    //    commandMap.put("/board/add", new BoardAddHandler());
    //    commandMap.put("/board/add", new BoardAddHandler());

    try (Socket socket = new Socket(this.serverAddress, this.port);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        DataInputStream in = new DataInputStream(socket.getInputStream())) {

      while (true) {
        String command = Prompt.inputString(
            "-------------------------"
                + "\n[개인 운동 기록]"
                + "\n> /add"
                + "\n> /recode"
                + "\n> /total"
                + "\n> /Graph"
                + "\n> /history"
                + "\n> /history2"
                + "\n> quit"
                + "\n명령어> ");

        if (command.length() == 0) {
          continue;
        }

        try {
          switch (command) {
            case "quit":
              out.writeUTF("quit");
              out.writeInt(0);
              out.flush();

              in.readUTF();
              in.readInt();

              System.out.println("수고하셨습니다. 적당히하세요.");
              return;
            default:
              Command commandHandler = commandMap.get(command);

              if (commandHandler == null) {
                System.out.println("실행할 수 없는 명령입니다.");
              } else {
                commandHandler.service(in, out);
              }
          }
        } catch (Exception e) {
          System.out.println("------------------------------------------");
          System.out.printf("명령어 실행 중 오류 발생: %s\n", e.getMessage());
          System.out.println("------------------------------------------");
        }
      }
    } catch (Exception e) {
      System.out.println("서버와의 통신중 오류가 발생했습니다.");
    }
    Prompt.close();
  }

}
