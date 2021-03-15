package com.myp.pms.table;

import java.io.File;
import java.util.List;
import com.eomcs.pms.domain.Board;
import com.myp.pms.domain.BodyBuilding;
import com.myp.pms.domain.BodyCheck;
import com.myp.pms.domain.Calisthenics;
import com.myp.pms.domain.Cardio;
import com.myp.pms.util.Request;
import com.myp.pms.util.Response;

// 1) 간단한 동작 테스트를 위해 임의의 값을 리턴한다.
// 2) JSON 포맷의 파일을 로딩한다.
public class AddTable implements DataTable {

  File calisJsonFile = new File("Calisthenics.json");
  File bodyBuildingJsonFile = new File("BodyBuilding.json");
  File cardioJsonFile = new File("Cardio.json");
  File bodyChecksonFile = new File("BodyCheck.json");

  List<Calisthenics> calisList;
  List<BodyBuilding> buildingList;
  List<Cardio> cardioList;
  List<BodyCheck> checkList;

  public AddTable() {
    calisList = JsonFileHandler.loadObject(calisJsonFile, Calisthenics.class);
    buildingList = JsonFileHandler.loadObject(bodyBuildingJsonFile, BodyBuilding.class);
    cardioList = JsonFileHandler.loadObject(cardioJsonFile, Cardio.class);
    checkList = JsonFileHandler.loadObject(bodyChecksonFile, BodyCheck.class);
  }

  @Override
  public void service(Request request, Response response) throws Exception {
    Board board = null;
    String[] fields = null;

    switch (request.getCommand()) {
      case "insert":
        Menu_AddHAnder addHandler = new Menu_AddHander();
      case "board/selectByKeyword":
        String keyword = request.getData().get(0);

        // 전체 게시글을 가져와서 검색어를 포함하는 게시글을 찾는다.
        // 찾은 게시글을 CSV 문자열로 만들어 응답할 데이터에 추가한다.
        for (Board b : list) {
          if (b.getTitle().contains(keyword) || 
              b.getContent().contains(keyword) ||
              b.getWriter().contains(keyword)) {

            response.appendData(String.format("%d,%s,%s,%s,%d", 
                b.getNo(), 
                b.getTitle(), 
                b.getWriter(), 
                b.getRegisteredDate(), 
                b.getViewCount()));
          }
        }
        break;
      case "board/update":
        fields = request.getData().get(0).split(",");

        board = getBoard(Integer.parseInt(fields[0]));
        if (board == null) {
          throw new Exception("해당 번호의 게시글이 없습니다.");
        }

        // 해당 게시물의 제목과 내용을 변경한다.
        // - List 에 보관된 객체를 꺼낸 것이기 때문에 
        //   그냥 그 객체의 값을 변경하면 된다.
        board.setTitle(fields[1]);
        board.setContent(fields[2]);

        JsonFileHandler.saveObjects(jsonFile, list);
        break;
      case "board/delete":
        no = Integer.parseInt(request.getData().get(0));
        board = getBoard(no);
        if (board == null) {
          throw new Exception("해당 번호의 게시글이 없습니다.");
        }

        list.remove(board);

        JsonFileHandler.saveObjects(jsonFile, list);
        break;
      default:
        throw new Exception("해당 명령을 처리할 수 없습니다.");
    }
  }

  private Board getBoard(int boardNo) {
    for (Board b : list) {
      if (b.getNo() == boardNo) {
        return b;
      }
    }
    return null;
  }
}
