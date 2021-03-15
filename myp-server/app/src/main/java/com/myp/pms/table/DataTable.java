package com.myp.pms.table;

import com.myp.pms.util.Request;
import com.myp.pms.util.Response;

public interface DataTable {
  void service(Request request, Response response) throws Exception;
}
